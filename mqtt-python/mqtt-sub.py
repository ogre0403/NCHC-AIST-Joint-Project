#!/usr/bin/python

import sys, signal, logging
import paho.mqtt.client as mqtt

from augpake import *
from aes_cipher import AESCipher
# import syslog_client
from config import *

from thrift import Thrift
from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol

from outputClass import *


# mqtt_client = None
thrift_client = None
mqtt_looping = False

output_client = None

SESSION_KEY_CACHE = {}      # <id, key> pair
SESSION_CIPHER_CACHE = {}   # <id, cipher> pair

# logger configuration
LOGGING_FILE = 'mqtt-sub.log'
logging.basicConfig(
    # filename=LOGGING_FILE,
    level=logging.DEBUG,
    format='%(asctime)s [%(levelname)s] %(filename)s_%(lineno)d  : %(message)s')
logger = logging.getLogger('root')


def on_connect(mq, userdata, rc, _):
    # subscribe when connected.
    mq.subscribe(MQTT_TOPIC + '/#')


def on_message(mq, userdata, msg):
    global output_client
    logger.debug("payload: %s" % msg.payload)

    text = DecodePayload(msg.payload)
    if text is not None:
        output_client.write(text)
    else:
        logger.error("Decode payload {%s} fail." % msg.payload)


def DecodePayload(payload):
    global SESSION_KEY_CACHE
    global SESSION_CIPHER_CACHE

    try:
        ID = payload.decode("utf-8").split(SEP)[0]
        msg = payload.decode("utf-8").split(SEP)[1]
    except IndexError as e:
        logger.error(e)
        return None

    if ID not in SESSION_KEY_CACHE:
        logger.info("Session key for ID %s is not cached, query by thrift server." % ID)
        SESSION_KEY_CACHE[ID] = thrift_client.SearchKey(ID)

    if ID not in SESSION_CIPHER_CACHE:
        logger.info("Cipher for ID %s is not cached, create one." % ID)
        SESSION_CIPHER_CACHE[ID] = AESCipher(SESSION_KEY_CACHE[ID])

    return SESSION_CIPHER_CACHE[ID].decrypt(msg)


def instance(targetClass):
    if OUTPUTTYPE == 'default_print':
        return targetClass()
    elif OUTPUTTYPE == 'Syslog':
        return targetClass(SYSLOG_SERVER_IP)
    elif OUTPUTTYPE == 'influxdb_client':
        return targetClass(INFLUXDB_SERVER_IP, 8086, 'root', 'root')
    else:
        return targetClass()


def thrift_client_init():
    try:
        transport = TSocket.TSocket(THRIFT_SERVER_IP, THRIFT_SERVER_PORT)
        transport = TTransport.TBufferedTransport(transport)
        protocol = TBinaryProtocol.TBinaryProtocol(transport)
        client = AugPAKE.Client(protocol)
        transport.open()

    except Thrift.TException as ex:
        logger.error("%s" % ex.message)
        sys.exit(0)

    return client, transport


def mqtt_client_thread():
    global mqtt_looping, thrift_client, output_client

    thrift_client, thrift_connection = thrift_client_init()
    mqtt_client = mqtt.Client()
    mqtt_client.on_connect = on_connect
    mqtt_client.on_message = on_message

    targetClass = getattr(OutputType, OUTPUTTYPE)
    output_client = instance(targetClass)

    try:
        mqtt_client.connect(MQTT_BROKER_IP)
    except:
        logger.error("MQTT Broker is not online. Connect later.")
        sys.exit(0)

    mqtt_looping = True
    logger.info("Mqtt client thread listen on %s" % MQTT_TOPIC)

    while mqtt_looping:
        mqtt_client.loop()

    logger.info("mqtt client thread quit")
    mqtt_client.disconnect()
    thrift_connection.close()


def stop_all(*args):
    global mqtt_looping
    mqtt_looping = False

if __name__ == '__main__':
    signal.signal(signal.SIGTERM, stop_all)
    signal.signal(signal.SIGINT,  stop_all)  # Ctrl-C
    mqtt_client_thread()
    sys.exit(0)

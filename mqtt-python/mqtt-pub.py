#!/usr/bin/python

import paho.mqtt.publish as publish
import time
import random
import subprocess
import os, sys, logging
from tailf import tailf
from aes_cipher import AESCipher

SEP="\x01"

WATCH_FILE = "/var/log/auth.log"

# AugPAKE server configuration
AUGPAKE_SERVER_IP = "localhost"
AUGPAKE_SERVER_PORT = "12345"

# MQTT broker configuration
MQTT_BROKER_IP = "localhost"
MQTT_BROKER_PORT = "1883"
MQTT_TOPIC = "test"

# logger configuration
LOGGING_FILE = 'mqtt-client.log'
logging.basicConfig(#filename=LOGGING_FILE,
                    level=logging.DEBUG,
                    format='%(asctime)s [%(levelname)s] %(filename)s_%(lineno)d  : %(message)s')
logger = logging.getLogger('root')

def main():

    logger.debug("AUGPAKE_SERVER_IP: {}".format(AUGPAKE_SERVER_IP))
    logger.debug("AUGPAKE_SERVER_PORT: {}".format(AUGPAKE_SERVER_PORT))
    logger.debug("MQTT_BROKER_IP: {}".format(MQTT_BROKER_IP))
    logger.debug("MQTT_BROKER_PORT: {}".format(MQTT_BROKER_PORT))

    try:
        ID, Key = getKeyByAugPake(AUGPAKE_SERVER_IP, AUGPAKE_SERVER_PORT)
    except (OSError, IndexError) as e:
        logger.error(e)
        Key = None

    if Key is None:
	logger.error("Key is None. Exit...")
	exit()

    # create cipher
    cipher = AESCipher(Key)

    for line in tailf(WATCH_FILE):
        logger.debug("Plain Text: {}".format(line))
        encrypt_ctx = cipher.encrypt(line)
        prefix_encrypt = ID + SEP + encrypt_ctx;
        logger.debug("Encrypt Text prefixed ID: {}".format(prefix_encrypt))
        publish.single(MQTT_TOPIC, prefix_encrypt, hostname=MQTT_BROKER_IP, port=int(MQTT_BROKER_PORT))


def getKeyByAugPake(ip, port):
    """
    Return (id, key) pair
    """
    work_dir = os.path.abspath(os.path.dirname(sys.argv[0]))+"/../augpake_src/client/"
    out = subprocess.check_output(["./s_client", ip, port], cwd=work_dir)
    id = out.split("-")[0].strip()
    key = out.split("-")[1].strip()
    if id:
    	logger.info("Session ID: {}".format(id))
    	logger.info("Session KEY: {}".format(key))
	return id, key
    else:
        # If something wrong, return (None, None)
	return None, None



if __name__ == "__main__":
    main()

import paho.mqtt.publish as publish
import time
import random
import subprocess
import os, sys
import logging
from aes_cipher import AESCipher


sentance = ["Hello world",
            "hello Spark",
            "Hi Spark Streaming"
            ]

# AugPAKE server configuration
AUGPAKE_SERVER_IP = "localhost"
AUGPAKE_SERVER_PORT = "12345"

# MQTT broker configuration
MQTT_BROKER_IP = "localhost"
MQTT_BROKER_PORT = "1883"

# logger configuration
LOGGING_FILE = 'mqtt-client.log'
logging.basicConfig(#filename=LOGGING_FILE,
                    level=logging.DEBUG,
                    format='%(asctime)s [%(levelname)s] %(filename)s_%(lineno)d  : %(message)s')
logger = logging.getLogger('root')

def main():

    global AUGPAKE_SERVER_IP
    global AUGPAKE_SERVER_PORT
    global MQTT_BROKER_IP
    global MQTT_BROKER_PORT

    logger.debug("AUGPAKE_SERVER_IP: {}".format(AUGPAKE_SERVER_IP))
    logger.debug("AUGPAKE_SERVER_PORT: {}".format(AUGPAKE_SERVER_PORT))
    logger.debug("MQTT_BROKER_IP: {}".format(MQTT_BROKER_IP))
    logger.debug("MQTT_BROKER_PORT: {}".format(MQTT_BROKER_PORT))


    try:
        Key = getKeyByAugPake(AUGPAKE_SERVER_IP, AUGPAKE_SERVER_PORT)
    except OSError as e:
        logger.error(e)
        Key = None

    if Key is None:
	logger.error("Key is None. Exit...")
	exit()

    # create cipher
    cipher = AESCipher(Key)


    while True:
        j = random.randint(0, 2)
        encrypt_ctx = cipher.encrypt(sentance[j])
        print ("Sending "+ encrypt_ctx + " : " + cipher.decrypt(encrypt_ctx))
#        publish.single("test", encrypt_ctx, hostname=MQTT_BROKER_IP, port=int(MQTT_BROKER_PORT))
        time.sleep(j+1) # sleep random time


def getKeyByAugPake(ip, port):

    work_dir = os.path.abspath(os.path.dirname(sys.argv[0]))+"/../augpake_src/client/"
    out = subprocess.check_output(["./s_client", ip, port], cwd=work_dir)
    a = out.split("-")
    if a[0].strip():
    	logger.info("Session ID: {}".format(a[0].strip()))
    	logger.info("Session KEY: {}".format(a[1].strip()))
	return a[1].strip()
    else:
        # Something wrong...
	return None



if __name__ == "__main__":
    main()

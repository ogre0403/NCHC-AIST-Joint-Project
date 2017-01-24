#!/usr/bin/python

import paho.mqtt.publish as publish
import time
import random
import subprocess
import os, sys, logging
from aes_cipher import AESCipher


sentance = ["Hello world",
            "hello Spark",
            "Hi Spark Streaming"
            ]

SEP="\x01"

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


    while True:
        j = random.randint(0, 2)
        encrypt_ctx = cipher.encrypt(sentance[j])
        prefix_encrypt = ID + SEP + encrypt_ctx;
        logger.debug("Sending ID={}, encrypt_msg={}, {}".format(ID, encrypt_ctx, prefix_encrypt))
        publish.single("test", prefix_encrypt, hostname=MQTT_BROKER_IP, port=int(MQTT_BROKER_PORT))
        time.sleep(j+1) # sleep random time


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

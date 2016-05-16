import paho.mqtt.publish as publish
import time
import random
import subprocess
import os
from aes_cipher import AESCipher


sentance = ["Hello world",
            "hello Spark",
            "Hi Spark Streaming"
            ]

AUGPAKE_SERVER_IP = "140.110.141.63"
AUGPAKE_SERVER_PORT = "7777"

MQTT_BROKER_IP = "140.110.141.58"
MQTT_BROKER_PORT = "1883"


def main():

    global AUGPAKE_SERVER_IP
    global AUGPAKE_SERVER_PORT
    global MQTT_BROKER_IP
    global MQTT_BROKER_PORT

    if os.environ.has_key("AUGPAKE_SERVER_IP"):
        AUGPAKE_SERVER_IP = os.environ.get("AUGPAKE_SERVER_IP")

    if os.environ.has_key("AUGPAKE_SERVER_PORT"):
        AUGPAKE_SERVER_PORT = os.environ.get("AUGPAKE_SERVER_PORT")

    if os.environ.has_key("MQTT_BROKER_IP"):
        MQTT_BROKER_IP = os.environ.get("MQTT_BROKER_IP")

    if os.environ.has_key("MQTT_BROKER_PORT"):
        MQTT_BROKER_PORT = os.environ.get("MQTT_BROKER_PORT")

    print("AUGPAKE_SERVER_IP:" + AUGPAKE_SERVER_IP)
    print("AUGPAKE_SERVER_PORT: " + AUGPAKE_SERVER_PORT)
    print("MQTT_BROKER_IP:" + MQTT_BROKER_IP)
    print("MQTT_BROKER_PORT: " + MQTT_BROKER_PORT)

    Key = getKeyByAugPake(AUGPAKE_SERVER_IP, AUGPAKE_SERVER_PORT)
    print("encrypt key is " + Key)
    cipher = AESCipher(Key)


    while True:
        j = random.randint(0, 2)
        encrypt_ctx = cipher.encrypt(sentance[j])
        print ("Sending "+ encrypt_ctx + " : " + cipher.decrypt(encrypt_ctx))
        publish.single("test", encrypt_ctx, hostname=MQTT_BROKER_IP, port=int(MQTT_BROKER_PORT))

        time.sleep(j+1) # sleep random time


def getKeyByAugPake(ip, port):
    # Initiator's SK Value: 2AA58EAED5A04CE32B8CB65C0BD75FC2
    out = subprocess.check_output(["./augpake/test3_client", "-s", ip , "-p", port], cwd="/tmp/mqtt-python")
    return out.split(":")[1].strip()



if __name__ == "__main__":
    main()

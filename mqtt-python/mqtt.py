import paho.mqtt.publish as publish
import time
import random
import subprocess
from aes_cipher import AESCipher


sentance = ["Hello world",
            "hello Spark",
            "Hi Spark Streaming"
            ]

def main():

    #Key = "F405FDC5853EEA6589B4B6B13D2B0EE0"
    Key = getKeyByAugPake("140.110.141.63","7777")
    print("encrypt key is " + Key)
    cipher = AESCipher(Key)


    while True:
        j = random.randint(0, 2)
        encrypt_ctx = cipher.encrypt(sentance[j])
        print ("Sending "+ encrypt_ctx + " : " + cipher.decrypt(encrypt_ctx))
        #publish.single("test", encrypt_ctx, hostname="192.168.33.20", port=1883)

        time.sleep(j+1) # sleep random time


def getKeyByAugPake(ip, port):
    # Initiator's SK Value: 2AA58EAED5A04CE32B8CB65C0BD75FC2
    out = subprocess.check_output(["./augpake/test3_client", "-s", ip , "-p", port])
    return out.split(":")[1].strip()



if __name__ == "__main__":
    main()
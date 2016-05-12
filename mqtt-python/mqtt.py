import paho.mqtt.publish as publish
import time
import random
import string
from aes_cipher import AESCipher


sentance = ["Hello world",
            "hello Spark",
            "Hi Spark Streaming"
            ]

def main():

    Key = "1234567890123456"
    print("encrypt key is " + Key)
    cipher = AESCipher(Key)


    while True:
        j = random.randint(0, 2)
        encrypt_ctx = cipher.encrypt(sentance[j])
        print ("Sending "+ encrypt_ctx + " : " + cipher.decrypt(encrypt_ctx))
        publish.single("test", encrypt_ctx, hostname="192.168.33.20", port=1883)

        time.sleep(j+1) # sleep random time


def key(len=50):
    return (''.join([random.choice(string.ascii_letters + string.digits) for i in range(len)]))


if __name__ == "__main__":
    main()
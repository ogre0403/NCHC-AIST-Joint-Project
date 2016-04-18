import paho.mqtt.publish as publish
import time
import random


sentance = ["Hello world",
            "hello Spark",
            "Hi Spark Streaming"
            ]

while True:
     j = random.randint(0, 2)
     print ("Sending "+ sentance[j] )
     publish.single("test", sentance[j], hostname="140.110.141.58", port=8443)
     time.sleep(j+1)


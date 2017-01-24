#!/usr/bin/python

import sys, os, time, signal
reload(sys)
sys.setdefaultencoding('utf-8')
import paho.mqtt.client as mqtt

client = None
mqtt_looping = False

TOPIC_ROOT = "test"

def on_connect(mq, userdata, rc, _):
    # subscribe when connected.
    mq.subscribe(TOPIC_ROOT + '/#')

def on_message(mq, userdata, msg):
    print "topic: %s" % msg.topic
    print "payload: %s" % msg.payload
    print "qos: %d" % msg.qos

def mqtt_client_thread():
    global client, mqtt_looping
    client_id = "" # If broker asks client ID.
    client = mqtt.Client(client_id=client_id)

    # If broker asks user/password.
    user = ""
    password = ""
    client.username_pw_set(user, password)

    client.on_connect = on_connect
    client.on_message = on_message

    try:
        client.connect("localhost")
    except:
        print "MQTT Broker is not online. Connect later."

    mqtt_looping = True
    print "Looping..."

    #mqtt_loop.loop_forever()
    cnt = 0
    while mqtt_looping:
        client.loop()

        cnt += 1
        if cnt > 20:
            try:
                client.reconnect() # to avoid 'Broken pipe' error.
            except:
                time.sleep(1)
            cnt = 0

    print "quit mqtt thread"
    client.disconnect()

def stop_all(*args):
    global mqtt_looping
    mqtt_looping = False

if __name__ == '__main__':
    signal.signal(signal.SIGTERM, stop_all)
    signal.signal(signal.SIGQUIT, stop_all)
    signal.signal(signal.SIGINT,  stop_all)  # Ctrl-C

    mqtt_client_thread()

    print "exit program"
    sys.exit(0)

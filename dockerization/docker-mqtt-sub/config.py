# delimiter for id and key pair
SEP = "\x01"

# For mqtt-pub.py
WATCH_FILE = "/var/log/auth.log"

# AugPAKE server configuration
# For mqtt-pub.py
AUGPAKE_SERVER_IP = "augpake-server"
AUGPAKE_SERVER_PORT = "12345"

# MQTT broker configuration
# For mqtt-sub.py & mqtt-pub.py
MQTT_BROKER_IP = "broker"
MQTT_BROKER_PORT = 1883
MQTT_TOPIC = "test"

# Thrift server configuration
# For mqtt-sub.py
THRIFT_SERVER_IP = "augpake-server"
THRIFT_SERVER_PORT = 9090

# remote syslog configuration
# For mqtt-sub.py 
# usedocker bridge ip if test in docker
SYSLOG_SERVER_IP = "192.168.33.70"




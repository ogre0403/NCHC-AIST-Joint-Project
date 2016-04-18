AIST Project
================

# Start Mosquitto Docker container
    $ sudo docker run -ti -v /path/to/docker-mosquitto/mosquitto.conf:/etc/mosquitto/mosquitto.conf -p 1883:1883 -p 8443:8443 jllopis/mosquitto:v1.4.8

# Start Mqtt Python Client Docker container
    $ cd docker-python
    $ docker build -t python-mqtt .
    $ docker run -ti python-mqtt

# Run Spark-Streaming Application
    $ spark-submit --class iot.Mqtt --master yarn --queue root.MR ./aist.project-0.1-jar-with-dependencies.jar


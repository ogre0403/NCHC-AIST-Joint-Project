# NCHC-AIST joint Project

## 2016 Action Item

### Start Mosquitto Docker container
    
    $ git clone https://github.com/jllopis/docker-mosquitto.git
    $ cd docker-mosquitto
    $ modify mosquitto.conf     #if necessary
    $ sudo docker run --rm -ti -v /path/to/docker-mosquitto/mosquitto.conf:/etc/mosquitto/mosquitto.conf \
      -p 1883:1883 -p 9883:9883 jllopis/mosquitto:v1.4.8
    

### Start Mqtt Python Client Docker container
    
    $ cd mqtt-python
    $ docker build -t python-mqtt .
    $ docker run -ti --rm python-mqtt
    
### Run Spark-Streaming Application
    
    $ spark-submit --class iot.Mqtt --master yarn --queue root.MR ./aist.project-0.1-jar-with-dependencies.jar
    

## 2015 Action Item

### PUF MapReduce Job

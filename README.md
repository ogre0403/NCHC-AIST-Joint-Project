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
    $ chmod 600 augpake/augpake.conf
    $ docker build -t python-mqtt .
    $ docker run -ti --rm python-mqtt
   
### Start Augpake Server, and output session into text file
    
    $ ./test3_server -p 7777 > /home/ogre/SK.out

### Run Spark-Streaming Application, and session key from text file
    
    $ spark-submit --class iot.Launcher --master yarn --queue root.MR \
      ./aist.project-0.1-jar-with-dependencies.jar \
      --file /home/ogre/SK.out
    

## 2015 Action Item

### PUF MapReduce Job

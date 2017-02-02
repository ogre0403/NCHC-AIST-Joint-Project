
## Build MQTT Broker image

```
$ cd NCHC-AIST-Joint-Project/dockerization/docker-mosquitto
$ docker build --no-cache -t jllopis/mosquitto:v1.4.10 .

$ docker run --rm -ti -v `pwd`/mosquitto.conf:/etc/mosquitto/mosquitto.conf \
  --name broker \ 
  -p 1883:1883 -p 9883:9883 \ 
  jllopis/mosquitto:v1.4.10  
```

## Build Common images for NCHC-AIST joint project
```bash
$ cd NCHC-AIST-Joint-Project/dockerization
$ docker build -t nchc-aist:base -f ./Dockerfile-base .
```

## Build & run wrapper AugPAKE server image 
```bash
$ cd NCHC-AIST-Joint-Project/dockerization/docker-augpaker-server
$ docker build -t nchc-aist:augpake-server -f Dockerfile ../../
$ docker run --rm -p 12345:12345 -p 9090:9090 --name augpake-server nchc-aist:augpake-server
```

## Build & run mqtt publisher  
```
$ /NCHC-AIST-Joint-Project/dockerization/docker-mqtt-pub
$ docker build -t nchc-aist:mqtt-pub -f ./Dockerfile ../../

$ docker run -ti --rm --name pub \
  --link augpake-server:augpake-server \
  --link broker:broker \ 
  -v `pwd`/config.py:/tmp/mqtt-python/config.py \
  -v /var/log/auth.log:/var/log/auth.log \
  nchc-aist:mqtt-pub
```

## Build & run mqtt subscriber
  ```
  $ cd NCHC-AIST-Joint-Project/dockerization/docker-mqtt-sub
  $ docker build -t nchc-aist:mqtt-sub -f ./Dockerfile ../../
  $ docker run -ti --rm --name sub \
    --link augpake-server:augpake-server \
    --link broker:broker \
    -v `pwd`/config.py:/tmp/mqtt-python/config.py 
    nchc-aist:mqtt-sub 
  ```
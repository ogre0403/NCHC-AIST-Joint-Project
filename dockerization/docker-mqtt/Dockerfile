FROM alpine:3.3
RUN apk add --no-cache python py-pip
RUN pip install paho-mqtt
ADD ../../mqtt-python /tmp/mqtt-python

ENTRYPOINT ["python","/tmp/mqtt-python/mqtt.py"]

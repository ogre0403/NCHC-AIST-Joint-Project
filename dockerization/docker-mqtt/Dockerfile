FROM alpine:3.3
RUN apk add --no-cache python py-pip
RUN pip install paho-mqtt
ADD ../../mqtt.py /tmp/mqtt.py

ENTRYPOINT ["python","/tmp/mqtt.py"]

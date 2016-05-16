FROM ubuntu:14.04

RUN apt-get update -qq && \
    apt-get install -y python python-pip python-dev build-essential && \
    pip install paho-mqtt && \
    pip install pycrypto

COPY mqtt.py /tmp/mqtt-python/mqtt.py
COPY aes_cipher.py /tmp/mqtt-python/aes_cipher.py
COPY augpake/test3_client /tmp/mqtt-python/augpake/test3_client
COPY augpake/augpake.conf /tmp/mqtt-python/augpake.conf


RUN chmod 600 /tmp/mqtt-python/augpake.conf

ENTRYPOINT ["python","/tmp/mqtt-python/mqtt.py"]

FROM nchc-aist:base


COPY mqtt-python /tmp/mqtt-python
COPY augpake_src /tmp/augpake_src
COPY dockerization/lib//libaugpake-ubuntu-64.so /usr/local/lib/libaugpake.so

RUN ldconfig && \
    rm /tmp/mqtt-python/config.py 




ENTRYPOINT ["python","/tmp/mqtt-python/mqtt-pub.py"]

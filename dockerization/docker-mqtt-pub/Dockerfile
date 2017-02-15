FROM nchc-aist:base


COPY mqtt-python /tmp/mqtt-python
COPY augpake_src /tmp/augpake_src
COPY dockerization/lib//libaugpake-ubuntu-64.so /usr/local/lib/libaugpake.so

RUN rm /tmp/mqtt-python/config.py &&\
    cd /tmp/augpake_src  && \
    chmod 600 /tmp/augpake_src/server/augpake.conf && \
    chmod 600 /tmp/augpake_src/client/augpake.conf && \
    make clean; make && \
    ldconfig


ENTRYPOINT ["python","/tmp/mqtt-python/mqtt-pub.py"]

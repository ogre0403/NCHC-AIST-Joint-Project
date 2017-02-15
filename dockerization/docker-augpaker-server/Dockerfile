FROM nchc-aist:base

EXPOSE 12345
EXPOSE 9090

COPY mqtt-python /tmp/mqtt-python
COPY augpake_src /tmp/augpake_src
COPY dockerization/lib/libaugpake-ubuntu-64.so /usr/local/lib/libaugpake.so

RUN cd /tmp/augpake_src  && \
    make clean; make && \
    chmod 600 /tmp/augpake_src/server/augpake.conf && \
    chmod 600 /tmp/augpake_src/client/augpake.conf && \
    ldconfig


ENTRYPOINT ["python","/tmp/mqtt-python/augpake-server.py"]

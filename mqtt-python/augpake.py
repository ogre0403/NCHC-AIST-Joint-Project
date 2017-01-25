#!/usr/bin/env python

import sys
import os
import subprocess
import select
import fcntl
import signal
import logging

# Wrapper of AugPAKE server, get and store session id and key pair.

AUGPAKE_COMMAND = ["./s_server"]


# logger configuration
LOGGING_FILE = 'augpake-server.log'
logging.basicConfig(
    # filename=LOGGING_FILE,
    level=logging.DEBUG,
    format='%(asctime)s [%(levelname)s] %(filename)s_%(lineno)d  : %(message)s')
logger = logging.getLogger('root')


def augpake_init(port):
    work_dir = os.path.abspath(os.path.dirname(sys.argv[0]))+"/../augpake_src/server/"
    process = subprocess.Popen(AUGPAKE_COMMAND + [str(port)], cwd=work_dir, stdout=subprocess.PIPE, stderr=subprocess.PIPE)

    # set non-blocking mode for file if using select()
    fl = fcntl.fcntl(process.stdout, fcntl.F_GETFL)
    fcntl.fcntl(process.stdout, fcntl.F_SETFL, fl | os.O_NONBLOCK)

    fl = fcntl.fcntl(process.stderr, fcntl.F_GETFL)
    fcntl.fcntl(process.stderr, fcntl.F_SETFL, fl | os.O_NONBLOCK)

    return process

def augpake(port):
    process = augpake_init(port)

    #TODO: close process when this python program crash 
    while True:
        reads, writes, errors = select.select([process.stdout, process.stderr], [], [], 0.1)    

        if process.stderr in reads:
             err = process.stderr.readline()
             logger.error(err)
             os.killpg(os.getpgid(process.pid), signal.SIGTERM)
             exit()

        if process.stdout in reads:
             out = process.stdout.readline()
             tmp = out.split("-")
             if len(tmp) != 2:
                 logger.warning("{} is not vaild ID-KEY format.".format(out))
                 continue
             saveIdKey(tmp[0], tmp[1])



#TODO: save id and key for future query
def saveIdKey(id, key):
    logger.debug("id: "+id)
    logger.debug("key: "+key)



if __name__ == "__main__":
    if len(sys.argv) == 1:
        augpake(12345)
    elif len(sys.argv) > 1:
        augpake(int(sys.argv[1]))

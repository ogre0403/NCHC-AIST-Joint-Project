#!/usr/bin/env python


from thrift.protocol import TBinaryProtocol
from thrift.server import TServer
from thrift.transport import TSocket
from thrift.transport import TTransport

import threading
import logging


from augpake import AugPAKE

# logger configuration
LOGGING_FILE = 'ThriftServer.log'
logging.basicConfig(#filename=LOGGING_FILE,
                    level=logging.DEBUG,
                    format='%(asctime)s [%(levelname)s] %(filename)s_%(lineno)d  : %(message)s')
logger = logging.getLogger('root')


class IdKeyServer(threading.Thread):

    server = None

    def __init__(self, lookuptbl):
        threading.Thread.__init__(self)
        handler = LookupHandler(lookuptbl)
        processor = AugPAKE.Processor(handler)
        transport = TSocket.TServerSocket("localhost", 9090)
        tfactory = TTransport.TBufferedTransportFactory()
        pfactory = TBinaryProtocol.TBinaryProtocolFactory()
        self.server = TServer.TSimpleServer(processor, transport, tfactory, pfactory)


    def run(self):
        logger.info("Starting thrift server in python...")
        self.server.serve()

class LookupHandler:
    lookupTable = None

    def __init__(self, lookuptbl):
        self.lookupTable = lookuptbl


    def SearchKey(self, ID):
        key = self.lookupTable[ID]
        logger.debug("Search id = %s, Session key is %s" %(ID, key))
        return key
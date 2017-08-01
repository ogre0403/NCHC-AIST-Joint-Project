from influxdb import InfluxDBClient
import socket
from syslog_client import Facility
from syslog_client import Level



class OutputType:
    class default_print:
        def write(self, msg):
            print(msg)

    class influxdb_client:

        def __init__(self, host="localhost", port=8086, user='root', pw='root'):
            self.client = InfluxDBClient(host, port, user, pw, 'dionaeaIOT')
            self.client.create_database('dionaeaIOT')

        @staticmethod
        def convert_to_json(msg):
            data = str.split(msg, "|")
            time = data[0]
            src_ip = data[1]
            src_port = data[2]
            dest_ip = data[3]
            dest_port = data[4]
            protocol = data[5]
            status = data[6]

            json_body = [
            {
                "measurement": "threat",
                "tags": {
                    "src_ip": src_ip,
                    "src_port": src_port,
                    "dest_ip": dest_ip,
                    "dest_port": dest_port,
                    "protocol": protocol,
                    "status": status

                },
                "time": time,
                "fields": {
                    "dummy": 0
                }
            }
            ]
            return json_body

        def write(self, msg):
            self.client.write_points(self.convert_to_json(msg))

    class Syslog:
        """A syslog client that logs to a remote server.
        
        Example:
        >>> log = Syslog(host="foobar.example")
        >>> log.send("hello", Level.WARNING)
        """

        def __init__(self,
                   host="localhost",
                   port=514,
                   facility=Facility.DAEMON):
            self.host = host
            self.port = port
            self.facility = facility
            self.socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

        def send(self, message, level):
            "Send a syslog message to remote host using UDP."
            data = "<%d>%s" % (level + self.facility*8, message)
            self.socket.sendto(data.encode(), (self.host, self.port))

        def warn(self, message):
            "Send a syslog warning message."
            self.send(message, Level.WARNING)

        def notice(self, message):
            "Send a syslog notice message."
            self.send(message, Level.NOTICE)

        def error(self, message):
            "Send a syslog error message."
            self.send(message, Level.ERR)

        def write(self, message):
            self.send(message, Level.NOTICE)



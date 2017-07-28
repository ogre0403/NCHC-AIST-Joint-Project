from influxdb import InfluxDBClient

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



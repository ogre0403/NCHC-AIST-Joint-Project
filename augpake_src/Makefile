all: client/s_client server/s_server

client/s_client: s_client.c sample_initiator.c
	 cc -Wall -o client/s_client s_client.c sample_initiator.c -laugpake -lcrypto	

server/s_server: s_server.c sample_responder.c
	cc -Wall -o server/s_server s_server.c sample_responder.c -laugpake -lcrypto	

clean:
	rm server/s_server; rm client/s_client

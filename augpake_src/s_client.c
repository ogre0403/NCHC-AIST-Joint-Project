/*
 * s_client.c
 *
 *  Created on: 2016/08/15
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include "sample_header.h"
#define DEFAULT_HOST "127.0.0.1"
#define DEFAULT_PORT "12345"

extern void do_initiator(int sock);

int main(int argc, char *argv[]) {
	int sock = -1;
	struct sockaddr_in server_addr;
	char *host = DEFAULT_HOST, *port = DEFAULT_PORT;

	if (argc >= 2) {
		host = argv[1];
	}
	if (argc >= 3) {
		port = argv[2];
	}

	if ((sock = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
		perror("socket() failed.");
		goto Done;
	}

	memset(&server_addr, 0, sizeof(server_addr));
	server_addr.sin_family = AF_INET;
	server_addr.sin_port = htons(atoi(port));
	inet_pton(AF_INET, host, &server_addr.sin_addr.s_addr);
	if (connect(sock, (struct sockaddr *)&server_addr, sizeof(server_addr)) != 0) {
		perror("connect() failed.");
		goto Done;
	}

	do_initiator(sock);

Done:
	if (sock != -1)
		close(sock);

	return 0;
}




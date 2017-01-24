/*
 * s_server.c
 *
 *  Created on: 2016/08/15
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <netdb.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include "sample_header.h"
#define DEFAULT_PORT "12345"

extern void do_responder(int sock);

int main(int argc, char *argv[]) {
	int sock = -1, new_sock = -1, opt = 1;
	struct addrinfo hints, *info;
	struct sockaddr_in client_addr;
	socklen_t socklen;
	char *port = DEFAULT_PORT;

	if (argc >= 2) {
		port = argv[1];
	}

	memset(&hints, 0, sizeof(hints));
	hints.ai_family = AF_INET;
	hints.ai_socktype = SOCK_STREAM;
	hints.ai_flags = AI_PASSIVE;
	if (getaddrinfo(NULL, port, &hints, &info) != 0) {
		fprintf(stderr, "getaddrinfo() failed.\n");
		goto Done;
	}
	if ((sock = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
		perror("socket() failed.");
		goto Done;
	}
	if (setsockopt(sock, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt)) != 0) {
		perror("setsockopt() failed.");
	}
	if (bind(sock, info->ai_addr, info->ai_addrlen) != 0) {
		perror("bind() failed.");
		goto Done;
	}
	freeaddrinfo(info);

	if (listen(sock, 1) != 0) {
		perror("listen() failed.");
		goto Done;
	}
	socklen = sizeof(client_addr);
	if ((new_sock = accept(sock, (struct sockaddr *)&client_addr, &socklen)) < 0) {
		perror("accept() failed.");
		goto Done;
	}

	do_responder(new_sock);

Done:
	if (new_sock != -1)
		close(new_sock);
	if (sock != -1)
		close(sock);

	return 0;
}

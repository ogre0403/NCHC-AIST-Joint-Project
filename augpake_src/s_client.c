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
#include<errno.h> //For errno - the error number
#include<netdb.h> //hostent

extern void do_initiator(int sock);
int hostname_to_ip(char *  , char *);

int main(int argc, char *argv[]) {
	int sock = -1;
	struct sockaddr_in server_addr;
	char *host = DEFAULT_HOST, *port = DEFAULT_PORT;
	char ip[100];

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

	// Woo.. only IP is work in container
	// hostname  convert to IP first 
	hostname_to_ip(host , ip);

	memset(&server_addr, 0, sizeof(server_addr));
	server_addr.sin_family = AF_INET;
	server_addr.sin_port = htons(atoi(port));
	
	//inet_pton(AF_INET, host, &server_addr.sin_addr.s_addr);
	inet_pton(AF_INET, ip, &server_addr.sin_addr.s_addr);
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


/*
    Get ip from domain name
 */
 
int hostname_to_ip(char * hostname , char* ip)
{
    struct hostent *he;
    struct in_addr **addr_list;
    int i;
         
    if ( (he = gethostbyname( hostname ) ) == NULL) 
    {
        // get the host info
        herror("gethostbyname");
        return 1;
    }
 
    addr_list = (struct in_addr **) he->h_addr_list;
     
    for(i = 0; addr_list[i] != NULL; i++) 
    {
        //Return the first one;
        strcpy(ip , inet_ntoa(*addr_list[i]) );
        return 0;
    }
     
    return 1;
}

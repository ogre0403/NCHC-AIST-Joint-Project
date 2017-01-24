#include <stdio.h>
#include "libaugpake.h"
#include "sample_header.h"

/*** Sample Code for Responder ***/
void do_responder(int sock) {

     int receivedsize = 0, datasize = 0, SK_size = 0, i;
     unsigned char recvbuf[WORKBUFSIZE], *data = NULL, *SK = NULL;
     augpake_t *augpake = NULL;

     /* Create the autpake_t structure. */
     if ((augpake = augpake_init()) == NULL) {
          fprintf(stderr, "augpake_init() failed.\n");
          return;
     }

     /* Receive U|A|X. */
     recvOrDone(sock, recvbuf, sizeof(recvbuf), receivedsize);

     /* Make up S|A|Y. */
     data = augpake_generate_y(recvbuf, receivedsize, augpake, &datasize, NULL);
     if (data == NULL) {
          fprintf(stderr, "augpake_generate_y() failed.\n");
          goto Done;
     }
     /* Send S|A|Y. */
     sendOrDone(sock, data, datasize);
     free(data);
    
     /* Receive V_U. */
     recvOrDone(sock, recvbuf, sizeof(recvbuf), receivedsize);

     /* Verify V_U and then make up V_S and SK. */
     data = augpake_generate_v_s(recvbuf, receivedsize, augpake, &datasize);
     if (data == NULL) {
          fprintf(stderr, "augpake_generate_v_s() failed.\n");
          goto Done;
     }
     /* Send V_S. */
     sendOrDone(sock, data, datasize);
     free(data);
     
     /* Print SK. */
     SK_size = augpake_get_secret_size(augpake);
     SK = augpake_get_secret(0, SK_size, augpake);
     //fprintf(stdout, "Responder's SK = ");
     for (i = 0; i < SK_size; i++) {
  	  if(i == (SK_size -16))
	      fprintf(stdout,"-");
          fprintf(stdout, "%02X", SK[i]);
     }
     fprintf(stdout, "\n");
     fflush(stdout);

     /* Clear and free the memory. */
     memset(SK, 0, SK_size);
     free(SK);

Done:
     augpake_finish(augpake);
     return;
}


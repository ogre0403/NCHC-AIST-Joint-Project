#include <stdio.h>
#include "libaugpake.h"
#include "sample_header.h"

/*** Sample Code for Initiator ***/
void do_initiator(int sock) {

     int receivedsize = 0, datasize = 0, SK_size = 0, i;
     unsigned char recvbuf[WORKBUFSIZE], *data = NULL, *SK = NULL;
     augpake_t *augpake = NULL;

     /* Create the autpake_t structure. */
     if ((augpake = augpake_init()) == NULL) {
          fprintf(stderr, "augpake_init() failed.\n");
          return;
     }

     /* Compute X. */
     data = augpake_generate_x(augpake, &datasize, NULL, NULL, False);
     if (!data) {
          fprintf(stderr, "augpake_generate_x() failed.\n");
          goto Done;
     }
     /* Send U|A|X. */
     sendOrDone(sock,data,datasize);
     free(data);

     /* Receive S|A|Y. */
     recvOrDone(sock,recvbuf,sizeof(recvbuf),receivedsize);

     /* Make up V_U. */
     data = augpake_generate_v_u(recvbuf, receivedsize, augpake, &datasize);
     if (data == NULL) {
          fprintf(stderr, "augpake_generate_v_u() failed.\n");
          goto Done;
     }
  
      /* Send V_U. */
     sendOrDone(sock, data, datasize);
     free(data);

     /* Receive V_S. */
     recvOrDone(sock, recvbuf, sizeof(recvbuf), receivedsize);

     /* Verify V_S and then make up SK. */
     if (augpake_last_message(recvbuf, receivedsize, augpake) == False) {
          fprintf(stderr, "augpake_last_messsage() failed.\n");
          goto Done;
     }
    
     /* Print SK. */
     SK_size = augpake_get_secret_size(augpake);

     //TODO: The first (SK_size - 16) bytes is session ID
     SK = augpake_get_secret(0, SK_size, augpake); 
     //fprintf(stdout, "Initiator's SK = ");
     for (i = 0; i < SK_size; i++) {
          if(i == (SK_size - 16))
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


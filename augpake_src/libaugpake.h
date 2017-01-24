#ifndef LIBAUGPAKE_H
#define LIBAUGPAKE_H

#include <stdio.h>
#include <stdint.h>
#include <stdarg.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/ioctl.h>
#include <termio.h>
#include <unistd.h>
#include <string.h>
#include <dirent.h>
#include <openssl/dh.h>
#include <openssl/ec.h>
#include <openssl/evp.h>
#include <openssl/rsa.h>
#include <openssl/rand.h>
#include <openssl/err.h>
#include <openssl/sha.h>
#include <openssl/hmac.h>
#include <openssl/ssl.h>
#include <openssl/bn.h>
#include <openssl/engine.h>
#include <openssl/aes.h>

#define CONFFILE	"./augpake.conf"
#define PASSWDFILE	"./.passwd"

#define SECURE_PASSWORD_METHOD_AUGPAKE	2

#define AUGPAKE_ALGO_DL1	1
#define AUGPAKE_ALGO_DL2	2
#define AUGPAKE_ALGO_DL3	3
#define AUGPAKE_ALGO_SECp160r1	4
#define AUGPAKE_ALGO_SECp192r1	5
#define AUGPAKE_ALGO_SECp224r1	6
#define AUGPAKE_ALGO_SECp256r1	7
#define AUGPAKE_ALGO_SECp384r1	8
#define AUGPAKE_ALGO_SECp521r1	9
#define AUGPAKE_ALGO_SECt163r2	10
#define AUGPAKE_ALGO_SECt233r1	11
#define AUGPAKE_ALGO_SECt283r1	12
#define AUGPAKE_ALGO_SECt409r1	13
#define AUGPAKE_ALGO_SECt571r1	14

#define NULL_CHAR_LEN	1

#define PLAIN_TEXT 0
#define CIPHER_TEXT 1

typedef struct AugpakeDLPKey {
	DH *dh;
	BIGNUM *q;
} augpake_dlpkey_t;

typedef enum {
	False = 0,
	True = 1
} Bool;

typedef struct {
	int size;
	unsigned char *value;
} augpake_master_secret_t;

typedef struct {
     /*int SK_size;			20160608 */
     /*unsigned char *SK;	20160608 */
	 augpake_master_secret_t MS;
     char *myid;
     char *peerid;
     augpake_dlpkey_t *key;
     EC_KEY *eckey;
     BIGNUM *X;
     BIGNUM *Y;
     BIGNUM *K;
     EC_POINT *ecX;
     EC_POINT *ecY;
     EC_POINT *ecK;
     uint8_t used_algorithm;
     char *user;
     char *server;
     char *password;
     char *epassword;
     int algonum;
     int supported_algorithms[AUGPAKE_ALGO_SECt571r1];
     Bool ec_compressed;
     Bool responder_only;
     Bool hash_username;
} augpake_t;

#define CONF_ITEM_ID_AUGPAKE_INVALID	-1
#define CONF_ITEM_ID_AUGPAKE_USER	0
#define CONF_ITEM_ID_AUGPAKE_SERVER	1
#define CONF_ITEM_ID_AUGPAKE_PASSWORD	2
#define CONF_ITEM_ID_AUGPAKE_EPASSWORD	3
#define CONF_ITEM_ID_AUGPAKE_ALGORITHM	4
#if DB_MODE
#define CONF_ITEM_ID_AUGPAKE_DB_HOST	5
#define CONF_ITEM_ID_AUGPAKE_DB_NAME	6
#define CONF_ITEM_ID_AUGPAKE_DB_USER	7
#define CONF_ITEM_ID_AUGPAKE_DB_PASSWORD	8
#endif
#define CONF_ITEM_ID_AUGPAKE_MASTER_SECRET_SIZE	9	/* 20160608 */

#ifndef SHA1_DIGEST_LENGTH
#define SHA1_DIGEST_LENGTH      20
#endif /* !SHA1_DIGEST_LENGTH */

#ifndef SHA192_DIGEST_LENGTH
#define SHA192_DIGEST_LENGTH	24
#endif

#ifndef SHA224_DIGEST_LENGTH
#define SHA224_DIGEST_LENGTH	28
#endif

#ifndef SHA256_DIGEST_LENGTH
#define SHA256_DIGEST_LENGTH	32
#endif

#ifndef SHA384_DIGEST_LENGTH
#define SHA384_DIGEST_LENGTH	48
#endif

#ifndef SHA512_DIGEST_LENGTH
#define SHA512_DIGEST_LENGTH	64
#endif

#define WORKBUFSIZE	1024

/****************** 1024 bit p ******************/
#define DL1_P	"ffffffffffffffffffffffffffffffffffffffd100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000005b7dffffffffffffffffffffffffffffffffffef33df"
/* 160 bit q */
#define DL1_Q	"ffffffffffffffffffffffffffffffffffffffd1"
/* 1024 - 32 - 4 = 988 bit g */
#define DL1_G	"25535ca11286379d13455dfd677ffd6684873b1dc4b40bec6cc1b21033e88ade3ae559fa7ab4170589a3a8904359fb32b74c59fbfea78b3204b0c126179900469543e503fc1218f6a78967a10432f56392497ce34de38dab5ec4090e20b39b93b1fcc2403457f831047bf4fcb70d0e536a48ebfbbdffb93e15f9d348"

/****************** 2048 bit p ******************/
#define DL2_P	"ffffffffffffffffffffffffffffffffffffffffffffffffffffffc100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000003ed79dfffffffffffffffffffffffffffffffffffffffffffffffff088f01f"
/* 224 bit q */
#define DL2_Q  "ffffffffffffffffffffffffffffffffffffffffffffffffffffffc1"
/* 2048 - 32 - 3 = 2013 bit g */
#define DL2_G	"1443d32731b4fee810f6dcbf8bf0de4fd681c810759389b2907134e778087997a6fb18452ba8650fcc58447e3d2b6fbafe32bf0d22bb9b556052aa85ccc7ed4f4a64afc5b24afc762ada90e5302d73b9e23cf75249500dece5ab1a2eb555ffa86bb915e51cc868f3d3cfcafb2d389fb3cb6565e940fe8b4c2e93a7dd953c319087b66bc4a1b2259e14e6c7aaf3e79d479d99067f305b6ef51deedb4a594c48b0eca45ebb395898220b970cb9660231d4bd9ed756b8a5def7939a4e5f5bb4628225ce90128d0014ffe182a5bd7f28164f0abbcc74c3383d81dc87b3c85fea987d2af5283882b777673f879edf049c3428b0d797e0f660f7123254d231"

/****************** 3072 bit p ******************/
/* In Hex */
#define DL3_P	"ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff430000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000330a0dffffffffffffffffffffffffffffffffffffffffffffffffffffffffda5193ab"
/* 256 bit q  */
#define DL3_Q	"ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff43"
/* 3072 - 32 = 3040 bit g  */
#define DL3_G	"f1ac99884abbbbcc9baa19bf375607fd14570b3019a03871147032445ada7fa5b8bdc399c1889bbda197adb1e3939d55361241f5cd5ed529b0add921b27444bd2eb698dc962a9f7d202eab98bc0c8cc950ca13bc6b1e632d0876a4e79626fde85f06a46c9991eb02a6d6096e0df6bca2caa12e838bec47a7cb4af2b0d94107b9cdbd67327238ecaf84df292e776af0f76288b39f9d9e4ddf3a9731cc832d70f150a0f29e7a1e193d1d21cbe8a84b56b0a4692cb39d304808678285a23f08f9db402487746f7e2a19caf2171e55c76337e359217516213ff3bf616f8b20586a8b3168da444aea862bb76b9ea2bf8cb84773d29d4efe511c5395f89cb547efbbae333e0bdb22da40ce0b942a59841a12790910cc1332699d64bbf667e0df3791c4e29ceb48e8397d50c72f7765c5a18809e3497f6bd374f5d185bbc8f57e36051e11e8dd0c5dd385a9da442f22598111960cc2b83cba0a1d980745562f6c62dd6d81b7baea7650b1e6e57ab9cc4c95ef17256a79b131859e1bac81ff1e"

#define GET_HASH_PARAMS(algorithm,hashsize,hashname)	\
     {							\
     switch ((algorithm)) {				\
	  case AUGPAKE_ALGO_DL1:			\
	  case AUGPAKE_ALGO_SECp160r1:			\
	       (hashsize) = SHA1_DIGEST_LENGTH;		\
	       (hashname) = "SHA1";			\
	       break;					\
	  case AUGPAKE_ALGO_DL2:			\
	  case AUGPAKE_ALGO_SECt163r2:			\
	  case AUGPAKE_ALGO_SECp192r1:			\
	  case AUGPAKE_ALGO_SECp224r1:			\
	       (hashsize) = SHA224_DIGEST_LENGTH;	\
	       (hashname) = "SHA224";			\
	       break;					\
	  case AUGPAKE_ALGO_DL3:			\
	  case AUGPAKE_ALGO_SECt233r1:			\
	  case AUGPAKE_ALGO_SECp256r1:			\
	       (hashsize) = SHA256_DIGEST_LENGTH;	\
	       (hashname) = "SHA256";			\
	       break;					\
	  case AUGPAKE_ALGO_SECt283r1:			\
	  case AUGPAKE_ALGO_SECp384r1:			\
	       (hashsize) = SHA384_DIGEST_LENGTH;	\
	       (hashname) = "SHA384";			\
	       break;					\
	  case AUGPAKE_ALGO_SECp521r1:			\
	  case AUGPAKE_ALGO_SECt409r1:			\
	  case AUGPAKE_ALGO_SECt571r1:			\
	       (hashsize) = SHA512_DIGEST_LENGTH;	\
	       (hashname) = "SHA512";			\
	       break;					\
	  default:					\
	       (hashsize) = 0;				\
	       (hashname) = NULL;			\
	       break;					\
	  }						\
     }

#define freeIfNotNULLStr(x)		  \
	if ((x) != NULL) {		  \
		(void)free((x));	  \
		x = NULL;		  \
	}
#define freeIfNotNULLBN(x)			\
	if ((x) != NULL) {			\
		(void)BN_clear_free((x));	\
	}
#define eraseFreeIfNotNULLStr(x)			\
	if ((x) != NULL) {			\
		memset((x), 0, strlen((x)));	\
		free(x);			\
	}

#define mallocOrDone(x, y) {						\
		(x) = malloc((y));					\
		if ((x) == NULL) {						\
			goto Done;					\
		}							\
	}

#define callocOrDone(x, y) {  \
		(x) = calloc((y),1);			     \
		if ((x) == NULL) {				     \
			goto Done;					\
		}							\
	}

#define strdupOrDone(x, y) {                                            \
                if (y != NULL) {			                \
		     freeIfNotNULLStr(x);                               \
		     (x) = strdup((y));					\
		     if ((x) == NULL) {					\
			goto Done;					\
		     }							\
                }                                                       \
	}

#define BN_dupOrDone(x, y) {                                            \
                if (y != NULL) {			                \
                     freeIfNotNULLBN(x);			        \
		     (x) = BN_dup((y));					\
		     if ((x) == NULL) {					\
			goto Done;					\
		     }							\
                }                                                       \
	}

#define newBNOrDone(x) {						\
		(x) = BN_new();						\
		if (!(x))  {						\
			goto Done;					\
		}							\
	}

#define newBNCTXOrDone(x) {						\
		(x) = BN_CTX_new();					\
		if (!(x))  {						\
			goto Done;					\
		}							\
	}

#define newDHOrDone(x) {						\
		(x) = DH_new();						\
		if (!(x))  {						\
			goto Done;					\
		}							\
	}

#define clearAndFreeIfNonNULLBN(x) {		  \
		if ((x) != NULL) {		  \
			(void)BN_clear_free((x)); \
			x = NULL;                 \
		}				  \
}

#define clearAndFreeIfNonNULLstr(x, y) {	  \
		if ((x) != NULL) {		  \
		    memset((x),0,(y));		  \
			(void)free((x));	  \
			x = NULL;                 \
		}				  \
}

#define freeIfNonNULLDH(x)		    \
	if ((x) != NULL) {		    \
		(void)DH_free((x));	    \
	}

#define freeIfNonNULLBNCTX(x)		\
	if ((x) != NULL) {			\
		(void)BN_CTX_free((x));		\
	}

#define freeIfNonNULLBNMONTCTX(x)	\
	if ((x) != NULL) {			     \
		(void)BN_MONT_CTX_free((x));	     \
	}

#define removeSurroundingSpaces(x)			\
     {							\
     int sl = strlen((x));				\
     char *cpE = (x)+sl-1;				\
     while (*(x)==' ' || *(x)=='\t') {			\
	  (x)++;					\
	  sl--;						\
     }								\
     for ( ; sl > 0 && (*cpE==' ' || *cpE=='\t'); cpE--, sl--)	\
	  *cpE = '\0';						\
}

struct dlp_param
{
	char *p;
	char *q;
	char *g;
};

augpake_t *augpake_init(void);
void augpake_finish(augpake_t *conf);
unsigned char *augpake_generate_x(augpake_t *augpake, int *size, char *identity, char *password, Bool hashed);
unsigned char *augpake_generate_y(unsigned char *buffer, int receivedsize, augpake_t *augpake, int *size, char *myidentity);
unsigned char *augpake_generate_v_u(unsigned char *recvbuf, int receivedsize, augpake_t *augpake, int *sentsize);
unsigned char *augpake_generate_v_s(unsigned char *recvbuf, int receivedsize, augpake_t *augpake, int *sentsize);
Bool augpake_last_message(unsigned char *recvbuf, int receivedsize, augpake_t *augpake);
int augpake_get_secret_size(augpake_t *augpake);	/* 20160608 */
unsigned char *augpake_get_secret(int offset, int size, augpake_t *augpake);	/* 20160608 */
int augpake_hash(unsigned char *dstBuf, char *algoname, unsigned char *srcBuf, int srcBufSize);
Bool augpake_generate_dlp_key(augpake_dlpkey_t *dlpkey);
Bool augpake_dlp_range_check(BIGNUM *x, augpake_dlpkey_t *dlpkey);
augpake_dlpkey_t *DLP_new(uint8_t);
Bool augpake_algorithm_check(augpake_t *conf, uint8_t algorithm, Bool fullcheck);
void DLP_free(augpake_dlpkey_t *dlpkey);
char * augpake_generate_W(char *uid, char *sid, char *pwd, uint8_t algorithm, Bool ec_compressed);
Bool augpake_generate_server_env(augpake_t *s, augpake_t *c);
Bool augpake_search_password(augpake_t *augpake, unsigned char nametype, char *name);
BIGNUM *augpake_generate_epwd(char *uid, char *sid, char *pwd);
#endif /* LIBAUGPAKE_H */

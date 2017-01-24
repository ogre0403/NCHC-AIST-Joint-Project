#ifndef SAMPLE_HEADER_H
#define SAMPLE_HEADER_H

#define sendOrDone(s,buf,len) {					\
		int ret, size = (len);					\
		ret = write(s,(char *)(&size),sizeof(int)); \
		if (ret < 0) {						\
			perror("couldn't send a message size to the peer"); \
			goto Done;					\
		}							\
		ret = write(s,(buf),size);	\
		if (ret < 0) {						\
			perror("couldn't send a message to the peer"); \
			goto Done;					\
		}							\
	}

#define recvOrDone(s,buf,len,res) {					\
		int size, recvsize; char *sp = (char *) &size;		\
		char *bp = (char *) buf;				\
		(res) = 0;						\
		do {							\
			recvsize = read(s,sp,sizeof(int)); \
			(res) += recvsize;				\
			sp += recvsize;					\
		} while ((res) > 0 && (res) < sizeof(int));		\
		if ((res) < 0) {					\
			perror("couldn't receive a message size from the peer"); \
			goto Done;					\
		}							\
		if (size > (len)) {					\
			fprintf(stderr, "Message size is larger than the supplied buffer size\n"); \
			goto Done;					\
		}							\
		(res) = 0;						\
		do {							\
			recvsize = read(s,bp,size); \
			res += recvsize;				\
			bp += recvsize;					\
		} while (res > 0 && res < size);			\
		if (res < 0) {						\
			perror("couldn't receive a message from the peer"); \
			goto Done;					\
		}							\
	}

#endif /* SAMPLE_HEADER_H */

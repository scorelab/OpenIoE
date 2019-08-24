#ifndef CONSTANTS_H
#define	CONSTANTS_H

#ifdef errno
	#include <errno.h>
#else
	#include "errno.h"
#endif 

/* Indicates whether input is being encrypted or decrypted */
#define DIR_ENCRYPT	1
#define DIR_DECRYPT	2

/* Block location inside plaintext or ciphertext */
#define LAST_BLOCK	1

/* Supported operation modes */
#define MODE_ECB	0
#define MODE_GCM	1

/*
 * Although GCM mode is supported, it has its own set of functions.
 * Therefore, there is no need to define a GCM mode of operation here.
 */

/* Status codes */
#define SUCCESSFULL_OPERATION		0
#define INVALID_OUTPUT_SIZE		1
#define INVALID_INPUT_SIZE		2
#define INVALID_PADDING			4
#define INVALID_TAG			8
#define INVALID_PARAMETER		16
#define INVALID_STATE			32
#define DEFAULT_ERROR			64
#define INVALID_MEMSET			128

/* Simulated boolean values */
#define     TRUE            1
#define     FALSE           0

#endif /* CONSTANTS_H */ 

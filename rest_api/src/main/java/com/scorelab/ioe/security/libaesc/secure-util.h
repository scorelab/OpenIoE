#ifndef SECUREUTIL_
#define SECUREUTIL_

#include <limits.h>
#include <stdlib.h>
#include <stdint.h>
#include "constants.h"

#ifdef errno
	#include <errno.h>
#else
	#include "errno.h"
#endif

/* Secure arithmetic operations for unsigned values */
errno_t add_s(uint32_t /* op1 */, uint32_t /* op2 */, uint32_t* /* res */);
errno_t sub_s(uint32_t /* op1 */, uint32_t /* op2 */, uint32_t* /* res */);
errno_t mul_s(uint32_t /* op1 */, uint32_t /* op2 */, uint32_t* /* res */);
errno_t div_s(uint32_t /* op1 */, uint32_t /* op2 */, uint32_t* /* res */);

/* Secure parameters check */
errno_t checkIfValidParameters(const uint8_t* /* input */, uint8_t* /* output */, uint32_t* /* outputOffset */);
errno_t calculateFullBlocks(uint32_t /* blockSize */, uint32_t /* bufferOffset */, uint32_t /* inputLen */, 
					uint32_t* /* fullBlocks */);
errno_t calculateRemainingBytes(uint32_t /* blockSize */, uint32_t /* bufferOffset */, uint32_t /* inputLen */, 
					uint32_t /* fullBlocks */, uint32_t* /* remainingBytes */);

/* Secure memory manipulation */
errno_t memset_s(void* /* v */, uint32_t /* smax */, uint8_t /* c */, uint32_t /* n */);
uint8_t memcmp_s(const void* /* ptr1 */, const void* /* ptr2 */, uint32_t /* num */); 

#endif /* SECUREUTIL_ */

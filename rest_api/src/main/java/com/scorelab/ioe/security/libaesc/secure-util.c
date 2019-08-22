#include "secure-util.h"

errno_t add_s(uint32_t op1, uint32_t op2, uint32_t* res) {
	errno_t result;

	if(res == NULL) {
		result = INVALID_PARAMETER;
		goto FAIL;
	}

	if(UINT32_MAX - op1 < op2) {
		result = INVALID_PARAMETER;
		goto FAIL;
	}

	*res = op1 + op2;
	result = SUCCESSFULL_OPERATION;
FAIL:
	return result;
}

errno_t sub_s(uint32_t op1, uint32_t op2, uint32_t* res)
{
	errno_t result;

	if(res == NULL) {
		result = INVALID_PARAMETER;
		goto FAIL;
	}

	if(op1 < op2) {
		result = INVALID_PARAMETER;
		goto FAIL;
	}
	*res = op1 - op2;
	result = SUCCESSFULL_OPERATION;
FAIL:
	return result;
}

errno_t mul_s(uint32_t op1, uint32_t op2, uint32_t* res)
{
	errno_t result;
	if(res == NULL) {
		result = INVALID_PARAMETER;
		goto FAIL;
	}

	if(op2 != 0 && op1 > UINT32_MAX/op2) {
		result = INVALID_PARAMETER;
		goto FAIL;
	}
	
	*res = op1 * op2;
	result = SUCCESSFULL_OPERATION;
FAIL:
	return result;
}

errno_t div_s(uint32_t op1, uint32_t op2, uint32_t* res)
{
	errno_t result;

	if(res == NULL) {
		result = INVALID_PARAMETER;
		goto FAIL;
	}

	if(op2 == 0) {
		result = INVALID_PARAMETER;
		goto FAIL;
	}

	*res = op1 / op2;
	result = SUCCESSFULL_OPERATION;
FAIL:
	return result;
}


/* Secure parameters check */
errno_t checkIfValidParameters(const uint8_t* input, uint8_t* output, uint32_t* outputOffset)
{
	errno_t result;
	if(input == NULL || output == NULL || outputOffset == NULL) {
		result = INVALID_PARAMETER;
	} else {
		result = SUCCESSFULL_OPERATION;
	}
	return result;
}

errno_t calculateFullBlocks(uint32_t blockSize, uint32_t bufferOffset, uint32_t inputLen, uint32_t* fullBlocks)
{
	errno_t result;

	result = add_s(inputLen, bufferOffset, fullBlocks);
	if(result != SUCCESSFULL_OPERATION) {
		goto FAIL;
	}
	result = div_s(*fullBlocks, blockSize, fullBlocks);
	if(result != SUCCESSFULL_OPERATION) {
		goto FAIL;
	}
FAIL:
	return result;
}
errno_t calculateRemainingBytes(uint32_t blockSize, uint32_t bufferOffset, uint32_t inputLen, uint32_t fullBlocks, uint32_t* remainingBytes)
{
	errno_t result;
	uint32_t aux;

	result = mul_s(blockSize, fullBlocks, remainingBytes);
	if(result != SUCCESSFULL_OPERATION) {
		goto FAIL;
	}

	result = add_s(inputLen, bufferOffset, &aux);
	if(result != SUCCESSFULL_OPERATION) {
		goto FAIL;
	}

	result = sub_s(aux, *remainingBytes, remainingBytes);
	if(result != SUCCESSFULL_OPERATION) {
		goto FAIL;
	}
FAIL:
	return result;
}

/* CERT Solution for memory sanitization. It should prevent compiler optimizations, but it isn't guaranteed to work. */
errno_t memset_s(void *v, uint32_t smax, uint8_t c, uint32_t n)
{
	errno_t result;
	volatile uint8_t *p = (uint8_t*) v;

	if(v == NULL || smax > UINT32_MAX || n > smax) {
		result = INVALID_PARAMETER;
		goto FAIL;
	}

	while (smax-- && n--) {
		*p++ = c;
	}
	result = SUCCESSFULL_OPERATION;
FAIL:
	return result;
}

/* Constant-time comparison  */
uint8_t memcmp_s(const void* ptr1, const void* ptr2, uint32_t num)
{
	uint32_t i;
	uint8_t result = 0x00;	
	uint8_t* p1 = (uint8_t*) ptr1;
	uint8_t* p2 = (uint8_t*) ptr2;

	for(i = 0; i < num; i++) {
		result |= p1[i] ^ p2[i];
	}

	return result;
}

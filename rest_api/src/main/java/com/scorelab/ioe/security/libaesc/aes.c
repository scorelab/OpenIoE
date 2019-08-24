#include <string.h>
#include <stdio.h>
#include "aes.h"
#include "intel-aes.h"
#include "intel-gcm.h"

#include <limits.h>
#include <emmintrin.h>
#include <tmmintrin.h>


/*============================================================================*/
/* Private definitions                                                        */
/*============================================================================*/

/*============================================================================*/
/* Public definitions                                                         */
/*============================================================================*/
errno_t aes_init(aes_ctx_t ctx, uint8_t *iv, uint32_t iv_len, uint8_t mode) {
	errno_t result;
	
	if(ctx == NULL) {
	  result = INVALID_PARAMETER;
	  goto FAIL;
	}	

	if (iv != NULL && iv_len == BLOCK_LEN) { 
	  memcpy(ctx->iv, iv, iv_len);
	}
	
	if(mode != MODE_ECB) {
	  result = INVALID_PARAMETER;
	  goto FAIL;
	}

	ctx->mode = mode;
	ctx->pos = 0;
	result = SUCCESSFULL_OPERATION;
FAIL:
	return result;
}

errno_t aes_gcm_init(aes_gcm_ctx_t ctx, uint8_t *iv, uint32_t iv_len, uint32_t tag_len, uint8_t mode) {
    errno_t result;	
    uint8_t buff[BLOCK_LEN]; /* aux buffer */
    
    uint32_t IV_whole_len = iv_len&(~0xf);
    uint32_t IV_remainder_len = iv_len&0xf;
    
    __m128i BSWAP_MASK = _mm_setr_epi8(15,14,13,12,11,10,9,8,7,6,5,4,3,2,1,0);
    __m128i ONE = _mm_set_epi32(0,0,0,1);
    uint32_t j;
    if(ctx == NULL || iv == NULL) {
	result = INVALID_PARAMETER;
	goto FAIL;
    }

    if(iv_len > GCM_MAX_IV) {
	result = INVALID_PARAMETER;
	goto FAIL;
    }
	
    ctx->aes->mode = mode;
    ctx->aes->pos = 0;

    /* initialize context fields */
    ctx->tagBits = tag_len;
    ctx->Alen = 0;
    ctx->Mlen = 0;
    /* first prepare H and its derivatives for ghash */
    intel_aes_gcmINIT(ctx->Htbl, (uint8_t*)ctx->aes->expandedKey, ctx->aes->Nr);
    /* Initial TAG value is zero*/
    _mm_storeu_si128((__m128i*)ctx->T, _mm_setzero_si128());
    _mm_storeu_si128((__m128i*)ctx->X0, _mm_setzero_si128());
    /* Init the counter */
    if(iv_len == 12) {
        _mm_storeu_si128((__m128i*)ctx->CTR, _mm_setr_epi32(((uint32_t*)iv)[0], ((uint32_t*)iv)[1], ((uint32_t*)iv)[2], 0x01000000));
    } else {
        /* If IV size is not 96 bits, then the initial counter value is GHASH of the IV */
        intel_aes_gcmAAD(ctx->Htbl, iv, IV_whole_len, ctx->T);
        /* Partial block */
        if(IV_remainder_len) {
	    result = memset_s(buff, sizeof(uint8_t) * BLOCK_LEN, 0, sizeof(uint8_t) * BLOCK_LEN);
            if(result != SUCCESSFULL_OPERATION) {
		goto FAIL;
	    }
	    memcpy(buff, iv + IV_whole_len, IV_remainder_len);
            intel_aes_gcmAAD(ctx->Htbl, buff, BLOCK_LEN, ctx->T);
	    result = memset_s(buff, sizeof(uint8_t) *BLOCK_LEN, 0, sizeof(uint8_t) * BLOCK_LEN);
            if(result != SUCCESSFULL_OPERATION) {
		goto FAIL;
	    }
         }
         
         intel_aes_gcmTAG(ctx->Htbl, ctx->T, iv_len, 0, ctx->X0, ctx->CTR);
        /* TAG should be zero again */
        _mm_storeu_si128((__m128i*)ctx->T, _mm_setzero_si128());
    }

	/* Encrypt the initial counter, will be used to encrypt the GHASH value, in the end */
	switch(ctx->aes->keysize) {
		case AES128_KEY_LEN:
			intel_aes_encrypt_ecb_128(ctx->aes, ctx->X0, &j, BLOCK_LEN, ctx->CTR, BLOCK_LEN, BLOCK_LEN);
			break;
		case AES192_KEY_LEN:
			intel_aes_encrypt_ecb_192(ctx->aes, ctx->X0, &j, BLOCK_LEN, ctx->CTR, BLOCK_LEN, BLOCK_LEN);
			break;
		case AES256_KEY_LEN:
			intel_aes_encrypt_ecb_256(ctx->aes, ctx->X0, &j, BLOCK_LEN, ctx->CTR, BLOCK_LEN, BLOCK_LEN);
			break;
	}
    /* Promote the counter by 1 */
    _mm_storeu_si128((__m128i*)ctx->CTR, _mm_shuffle_epi8(_mm_add_epi32(ONE, _mm_shuffle_epi8(_mm_loadu_si128((__m128i*)ctx->CTR), BSWAP_MASK)), BSWAP_MASK));
    result = gcmCheckContext(ctx);
FAIL:
    return result;
}

errno_t aes_gcm_aad(aes_gcm_ctx_t ctx, uint8_t *aad, uint32_t len) {
	errno_t result;
	uint8_t buffer[BLOCK_LEN];
	uint32_t AAD_whole_len = len&(~0xf);
	uint32_t AAD_remainder_len = len&0xf;
 
        intel_aes_gcmAAD(ctx->Htbl, aad, AAD_whole_len, ctx->T);
        if(AAD_remainder_len) {
                result = memset_s(buffer, sizeof(buffer), 0, sizeof(buffer));
		if(result != SUCCESSFULL_OPERATION) {
			goto FAIL;
		}
                memcpy(buffer, aad + AAD_whole_len, AAD_remainder_len);
                intel_aes_gcmAAD(ctx->Htbl, buffer, BLOCK_LEN, ctx->T);
                result = memset_s(buffer, sizeof(buffer), 0, sizeof(buffer));
		if(result != SUCCESSFULL_OPERATION) {
			goto FAIL;
		}
        }
	/* Updates the size of the AAD */
	uint64_t aadLen = ctx->Alen + len;
	if(aadLen < ctx->Alen || aadLen > GCM_MAX_AAD) {
		result = INVALID_STATE;
        	goto FAIL;
	}
	ctx->Alen = aadLen;
	result = SUCCESSFULL_OPERATION;
FAIL:
	return result;
}

errno_t aes_gcm_encrypt(aes_gcm_ctx_t ctx, uint8_t *outbuf, const uint8_t *inbuf, uint32_t inlen, uint8_t blockIndex)
{
    errno_t result;
    uint32_t tagBytes;
    uint8_t T[BLOCK_LEN];
    int j;
    /* Calculates the size of the tag in bytes */
    if(blockIndex & LAST_BLOCK) {
	 result = add_s(ctx->tagBits, 7, &tagBytes);
	 if(result != SUCCESSFULL_OPERATION) {
	     goto FAIL;
	 }
	 result = div_s(tagBytes, 8, &tagBytes); 
	 if(result != SUCCESSFULL_OPERATION) {
	     goto FAIL;
	 }
    }
    if(inlen > 0) {
    	intel_aes_gcmENC(inbuf, outbuf, ctx, inlen);
    }
    /* Updates the size of the message */
    uint64_t messageLen = ctx->Mlen + inlen;
    if(messageLen < ctx->Mlen || messageLen > GCM_MAX_INPUT) {
	result = INVALID_STATE;
	goto FAIL;
    }
    ctx->Mlen = messageLen;
    /* Copy the tag to the output buffer */
    if(blockIndex & LAST_BLOCK) {
      intel_aes_gcmTAG(ctx->Htbl, ctx->T, ctx->Mlen, ctx->Alen, ctx->X0, T);
      for(j = 0; j < tagBytes; j++) {
          outbuf[inlen+j] = T[j]; 
      }
    }
    result = SUCCESSFULL_OPERATION;
FAIL:
    result |= memset_s(T, sizeof(T), 0, sizeof(T));
    return result;
}

errno_t aes_gcm_decrypt(aes_gcm_ctx_t ctx, uint8_t *outbuf, const uint8_t *inbuf, uint32_t inlen, uint8_t blockIndex)
{
    errno_t result;
    uint32_t tagBytes;
    uint8_t T[BLOCK_LEN];
    const uint8_t *intag;

    /* last block, it must contain the authentication tag */
    if(blockIndex & LAST_BLOCK) {
	 /* Calculates the tag size */
	 result = add_s(ctx->tagBits, 7, &tagBytes);
	 if(result != SUCCESSFULL_OPERATION) {
	     goto FAIL;
	 }
	 result = div_s(tagBytes, 8, &tagBytes); 
	 if(result != SUCCESSFULL_OPERATION) {
	     goto FAIL;
	 }
         /* check if input has enough bytes to contain the tag */
         if (inlen < tagBytes) {
            result = INVALID_PARAMETER;
	    goto FAIL;
         }
	 inlen -= tagBytes;
         /* Start index for the tag */
         intag = inbuf + inlen;
    }
    intel_aes_gcmDEC(inbuf, outbuf, ctx, inlen);
    /* Updates the size of the message */
    uint64_t messageLen = ctx->Mlen + inlen;
    if(messageLen < ctx->Mlen || messageLen > GCM_MAX_INPUT) {
	result = INVALID_STATE;
	goto FAIL;
    }
    ctx->Mlen = messageLen;

    /* last block, we must check if authentication tag is valid */
    if(blockIndex & LAST_BLOCK) {
      intel_aes_gcmTAG(ctx->Htbl, ctx->T, ctx->Mlen, ctx->Alen, ctx->X0, T);
      if (memcmp_s(T, intag, tagBytes) != 0) {
         result = INVALID_TAG;
	 goto FAIL;
      }
    }
    result = SUCCESSFULL_OPERATION;
FAIL:
    result |= memset_s(T, sizeof(T), 0, sizeof(T));
    return result;
}

void aes_ecb_enc(aes_ctx_t ctx, uint8_t *output, uint32_t max_output_size, uint8_t *input, uint32_t input_len) {
	uint32_t output_size = 0;

	switch(ctx->keysize) {
		case AES128_KEY_LEN:
			intel_aes_encrypt_ecb_128(ctx, output, &output_size, max_output_size, input, input_len, AES_BLOCK_LEN);
			break;
		case AES192_KEY_LEN:
			intel_aes_encrypt_ecb_192(ctx, output, &output_size, max_output_size, input, input_len, AES_BLOCK_LEN);
			break;
		case AES256_KEY_LEN:
			intel_aes_encrypt_ecb_256(ctx, output, &output_size, max_output_size, input, input_len, AES_BLOCK_LEN);
			break;
	}
}

void aes_ecb_dec(aes_ctx_t ctx, uint8_t *output, uint32_t max_output_size, uint8_t *input, uint32_t input_len) {
	uint32_t output_size;
	
	switch(ctx->keysize) {
		case AES128_KEY_LEN:
			intel_aes_decrypt_ecb_128(ctx, output, &output_size, max_output_size, input, input_len, AES_BLOCK_LEN);
			break;
		case AES192_KEY_LEN:
			intel_aes_decrypt_ecb_192(ctx, output, &output_size, max_output_size, input, input_len, AES_BLOCK_LEN);
			break;
		case AES256_KEY_LEN:
			intel_aes_decrypt_ecb_256(ctx, output, &output_size, max_output_size, input, input_len, AES_BLOCK_LEN);
			break;
	}
}
void aes_encrypt(aes_ctx_t ctx, uint8_t *output, uint32_t max_output_size, uint8_t *input, uint32_t input_len) {
	switch(ctx->mode) {
		case MODE_ECB:
			aes_ecb_enc(ctx, output, max_output_size, input, input_len);
			break;
	}
}


void aes_decrypt(aes_ctx_t ctx, uint8_t *output, uint32_t max_output_size, uint8_t *input, uint32_t input_len) {
	switch(ctx->mode) {
		case MODE_ECB:
			aes_ecb_dec(ctx, output, max_output_size, input, input_len);
			break;
	}
}

void aes_gcm_expand_key(aes_gcm_ctx_t ctx, uint8_t *key, uint32_t len, int direction) {
	ctx->aes->direction = direction;
	ctx->aes->keysize = len;
	switch(len) {
		case AES128_KEY_LEN:
			ctx->aes->Nb = 4;
			ctx->aes->Nr = 10;
			break;
		case AES192_KEY_LEN:
			ctx->aes->Nb = 4;
			ctx->aes->Nr = 12;
			break;
		case AES256_KEY_LEN:
			ctx->aes->Nb = 4;
			ctx->aes->Nr = 14;
			break;
	}
	// It never uses decryption
	intel_aes_init(1, len, ctx->aes);
}


void aes_expand_key(aes_ctx_t ctx, uint8_t *key, uint32_t len, uint8_t direction) {
	ctx->direction = direction;	
	ctx->keysize = len;
	switch(len) {
		case AES128_KEY_LEN:
			ctx->Nb = 4;
			ctx->Nr = 10;
			break;
		case AES192_KEY_LEN:
			ctx->Nb = 4;
			ctx->Nr = 12;
			break;
		case AES256_KEY_LEN:
			ctx->Nb = 4;
			ctx->Nr = 14;
			break;
	}
	intel_aes_init(direction == DIR_ENCRYPT ? 1 : 0, len, ctx);
}

errno_t aesCheckContext(aes_ctx_t ctx)
{
	errno_t result;

	if(ctx == NULL) {
	  result = INVALID_PARAMETER;
	  goto FAIL;
	}

	if(ctx->Nb != 4) {
	  result = INVALID_PARAMETER;
	  goto FAIL;
	}

	if(ctx->Nr != 10 && ctx->Nr != 12 && ctx->Nr != 14) {
	  result = INVALID_PARAMETER;
	  goto FAIL;
	}

	if(ctx->keysize != AES128_KEY_LEN && ctx->keysize != AES192_KEY_LEN
		&& ctx->keysize != AES256_KEY_LEN) {
	  result = INVALID_PARAMETER;
	  goto FAIL;
	}
	
	if(ctx->direction != DIR_ENCRYPT && ctx->direction != DIR_DECRYPT) {
	  result = INVALID_PARAMETER;
	  goto FAIL;
	}
	
	if(ctx->mode != MODE_ECB && ctx->mode != MODE_GCM) {
	  result = INVALID_PARAMETER;
	  goto FAIL;
	}
	result = SUCCESSFULL_OPERATION;
FAIL:
	return result;
}

errno_t gcmCheckContext(aes_gcm_ctx_t ctx)
{
	errno_t result;

	if(ctx == NULL || ctx->aes == NULL) {
	  result = INVALID_PARAMETER;
	  goto FAIL;
	}

	if(ctx->tagBits > 128) {
	  result = INVALID_PARAMETER;
	  goto FAIL;
	}
	result = aesCheckContext(ctx->aes);
FAIL:
	return result;
}	

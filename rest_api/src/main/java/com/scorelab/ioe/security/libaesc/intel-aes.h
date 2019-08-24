#include "aes.h"

void intel_aes_encrypt_init_128(const unsigned char *key, unsigned int *expanded);
void intel_aes_encrypt_init_192(const unsigned char *key, unsigned int *expanded);
void intel_aes_encrypt_init_256(const unsigned char *key, unsigned int *expanded);
void intel_aes_decrypt_init_128(const unsigned char *key, unsigned int *expanded);
void intel_aes_decrypt_init_192(const unsigned char *key, unsigned int *expanded);
void intel_aes_decrypt_init_256(const unsigned char *key, unsigned int *expanded);

int intel_aes_encrypt_ecb_128(aes_ctx_t cx, unsigned char *output,
				    unsigned int *outputLen,
				    unsigned int maxOutputLen,
				    const unsigned char *input,
				    unsigned int inputLen,
				    unsigned int blocksize);
int intel_aes_decrypt_ecb_128(aes_ctx_t cx, unsigned char *output,
				    unsigned int *outputLen,
				    unsigned int maxOutputLen,
				    const unsigned char *input,
				    unsigned int inputLen,
				    unsigned int blocksize);
int intel_aes_encrypt_cbc_128(aes_ctx_t cx, unsigned char *output,
				    unsigned int *outputLen,
				    unsigned int maxOutputLen,
				    const unsigned char *input,
				    unsigned int inputLen,
				    unsigned int blocksize);
int intel_aes_decrypt_cbc_128(aes_ctx_t cx, unsigned char *output,
				    unsigned int *outputLen,
				    unsigned int maxOutputLen,
				    const unsigned char *input,
				    unsigned int inputLen,
				    unsigned int blocksize);
int intel_aes_encrypt_ecb_192(aes_ctx_t cx, unsigned char *output,
				    unsigned int *outputLen,
				    unsigned int maxOutputLen,
				    const unsigned char *input,
				    unsigned int inputLen,
				    unsigned int blocksize);
int intel_aes_decrypt_ecb_192(aes_ctx_t cx, unsigned char *output,
				    unsigned int *outputLen,
				    unsigned int maxOutputLen,
				    const unsigned char *input,
				    unsigned int inputLen,
				    unsigned int blocksize);
int intel_aes_encrypt_cbc_192(aes_ctx_t cx, unsigned char *output,
				    unsigned int *outputLen,
				    unsigned int maxOutputLen,
				    const unsigned char *input,
				    unsigned int inputLen,
				    unsigned int blocksize);
int intel_aes_decrypt_cbc_192(aes_ctx_t cx, unsigned char *output,
				    unsigned int *outputLen,
				    unsigned int maxOutputLen,
				    const unsigned char *input,
				    unsigned int inputLen,
				    unsigned int blocksize);
int intel_aes_encrypt_ecb_256(aes_ctx_t cx, unsigned char *output,
				    unsigned int *outputLen,
				    unsigned int maxOutputLen,
				    const unsigned char *input,
				    unsigned int inputLen,
				    unsigned int blocksize);
int intel_aes_decrypt_ecb_256(aes_ctx_t cx, unsigned char *output,
				    unsigned int *outputLen,
				    unsigned int maxOutputLen,
				    const unsigned char *input,
				    unsigned int inputLen,
				    unsigned int blocksize);
int intel_aes_encrypt_cbc_256(aes_ctx_t cx, unsigned char *output,
				    unsigned int *outputLen,
				    unsigned int maxOutputLen,
				    const unsigned char *input,
				    unsigned int inputLen,
				    unsigned int blocksize);
int intel_aes_decrypt_cbc_256(aes_ctx_t cx, unsigned char *output,
				    unsigned int *outputLen,
				    unsigned int maxOutputLen,
				    const unsigned char *input,
				    unsigned int inputLen,
				    unsigned int blocksize);



#define intel_aes_ecb_worker(encrypt, keysize) \
  ((encrypt)						\
   ? ((keysize) == 16 ? intel_aes_encrypt_ecb_128 :	\
      (keysize) == 24 ? intel_aes_encrypt_ecb_192 :	\
      intel_aes_encrypt_ecb_256)			\
   : ((keysize) == 16 ? intel_aes_decrypt_ecb_128 :	\
      (keysize) == 24 ? intel_aes_decrypt_ecb_192 :	\
      intel_aes_decrypt_ecb_256))


#define intel_aes_cbc_worker(encrypt, keysize) \
  ((encrypt)						\
   ? ((keysize) == 16 ? intel_aes_encrypt_cbc_128 :	\
      (keysize) == 24 ? intel_aes_encrypt_cbc_192 :	\
      intel_aes_encrypt_cbc_256)			\
   : ((keysize) == 16 ? intel_aes_decrypt_cbc_128 :	\
      (keysize) == 24 ? intel_aes_decrypt_cbc_192 :	\
      intel_aes_decrypt_cbc_256))


#define intel_aes_init(encrypt, keysize, ctx) \
  do {					 			\
      if (encrypt) {			 			\
	  if (keysize == 16)  					\
	      intel_aes_encrypt_init_128(key, ctx->expandedKey);	\
	  else if (keysize == 24)				\
	      intel_aes_encrypt_init_192(key, ctx->expandedKey);	\
	  else							\
	      intel_aes_encrypt_init_256(key, ctx->expandedKey);	\
      } else {							\
	  if (keysize == 16) 					\
	      intel_aes_decrypt_init_128(key, ctx->expandedKey);	\
	  else if (keysize == 24)				\
	      intel_aes_decrypt_init_192(key, ctx->expandedKey);	\
	  else							\
	      intel_aes_decrypt_init_256(key, ctx->expandedKey);	\
      }								\
  } while (0)

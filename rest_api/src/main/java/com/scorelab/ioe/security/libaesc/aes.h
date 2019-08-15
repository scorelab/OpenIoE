#ifndef AES_H_
#define AES_H_

#include <stddef.h>
#include <stdint.h>
#include "constants.h"
#include "secure-util.h"

/*============================================================================*/
/* Constant definitions                                                       */
/*============================================================================*/

#define AES_BLOCK_LEN 16
#define AES128_KEY_LEN 16
#define AES192_KEY_LEN 24
#define AES256_KEY_LEN 32
#define BLOCK_LEN AES_BLOCK_LEN

/*
 * Limits the quantity of AAD and plaintext that can be processed. Actually,
 * the maximum values defined by the standard are a little bigger than the
 * values used here.
 */
#define GCM_MAX_INPUT	68719476736		// (2 << 35)
#define GCM_MAX_IV	2305843009213693952	//(2 << 60)
#define GCM_MAX_AAD	2305843009213693952	//(2 << 60)

#define MIN(A, B)			((A) < (B) ? (A) : (B))

typedef struct {
    uint8_t Nb;
    uint8_t Nr;
    uint8_t pos;
    uint8_t keysize;
    uint8_t iv[32];
    uint32_t expandedKey[8 * 15];	// Nk - Number of key words, Nr - Number of rounds
    uint8_t mode, direction;
} aes_ctx_st;

typedef aes_ctx_st aes_aes_ctx_t[1];
typedef aes_ctx_st ctx_st;
typedef ctx_st aes_ctx_t[1];

typedef struct {
    uint8_t Htbl[16*BLOCK_LEN];
    uint8_t X0[BLOCK_LEN];
    uint8_t T[BLOCK_LEN];
    uint8_t CTR[BLOCK_LEN];
    aes_ctx_t aes;
    uint8_t AADbuff[BLOCK_LEN];
    unsigned long tagBits;
    uint64_t Alen;
    uint64_t Mlen;
} aes_gcm_ctx_st;

typedef aes_gcm_ctx_st aes_aes_gcm_ctx_t[1];
typedef aes_gcm_ctx_st gcm_ctx_st;
typedef gcm_ctx_st aes_gcm_ctx_t[1];

/*============================================================================*/
/* Function prototypes                                                        */
/*============================================================================*/

errno_t aes_init(aes_ctx_t ctx, uint8_t *iv, uint32_t iv_len, uint8_t mode);
void aes_encrypt(aes_ctx_t ctx, uint8_t *output, uint32_t max_output_size, uint8_t *input, uint32_t input_len);
void aes_decrypt(aes_ctx_t ctx, uint8_t *output, uint32_t max_output_size, uint8_t *input, uint32_t input_len);

errno_t aes_gcm_init(aes_gcm_ctx_t ctx, uint8_t *iv, uint32_t iv_len, uint32_t tag_len, uint8_t mode);
errno_t aes_gcm_aad(aes_gcm_ctx_t ctx, uint8_t *aad, uint32_t len); 
void aes_gcm_expand_key(aes_gcm_ctx_t ctx, uint8_t *key, uint32_t key_len, int direction);
errno_t aes_gcm_encrypt(aes_gcm_ctx_t ctx, uint8_t *output, const uint8_t *input, uint32_t len, uint8_t blockIndex);
errno_t aes_gcm_decrypt(aes_gcm_ctx_t ctx, uint8_t *output, const uint8_t *input, uint32_t len, uint8_t blockIndex);

errno_t aesCheckContext(aes_ctx_t ctx);
errno_t gcmCheckContext(aes_gcm_ctx_t ctx);

void printAESContext(aes_ctx_t ctx);
void printGCMContext(aes_gcm_ctx_t ctx);
void printInput(uint8_t* input, uint32_t inlen);
/**
 * Sets the encryption key.
 *
 * After this call, the context becomes ready to carry encryption.
 *
 * @param[in,out] ctx		- the encryption context.
 * @param[in] key			- the key
 * @param[in] key_len		- the key length, in bytes.
 * @param[in] direction		- encrypt or decrypt
 * @throw ERR_NO_VALID		- if the key length is invalid.
 */

void aes_expand_key(aes_ctx_t ctx, uint8_t *key, uint32_t key_len, uint8_t direction);

/**
 * Sets the decryption key.
 *
 * After this call, the context becomes ready to carry decryption.
 *
 * @param[in,out] ctx		- the decryption context.
 * @param[in] key			- the key
 * @param[in] key_len		- the key length, in bytes.
 * @throw ERR_NO_VALID		- if the key length is invalid.
 */


/**
 * Starts the encryption of a block.
 *
 * Input and output blocks must be aligned and have BC_BLOCK_LEN bytes.
 *
 * @param[in,out] ctx		- the encryption context.
 * @param[out] output		- the ciphertext generated.
 * @param[in] input			- the plaintext to encrypt.
 */
void aes_enc_start(aes_ctx_t ctx, uint8_t *output, uint32_t max_output_size, uint8_t *input, uint32_t input_len);


/**
 * Starts the decryption of a block.
 *
 * Input and output blocks must be aligned and have BC_BLOCK_LEN bytes.
 *
 * @param[in,out] ctx		- the encryption context.
 * @param[out] output		- the plaintext recovered.
 * @param[in] input		- the ciphertext to decrypt.
 */
void aes_dec_start(aes_ctx_t ctx, uint8_t *output, uint32_t max_output_size, uint8_t *input, uint32_t input_len);

#define aes_enc(ctx, output, max_output_size, input, input_len) aes_enc_start(ctx, output, max_output_size, input, input_len)

/**
 * Starts the decryption of a block.
 *
 * Input and output blocks must be aligned and have BC_BLOCK_LEN bytes.
 *
 * @param[in,out] ctx		- the decryption context.
 * @param[out] output		- the plaintext generated.
 * @param[in] input			- the ciphertext to decrypt.
 */

//void bc_aes128_dec_start(bc_aes_ctx_t ctx, bc_t output, bc_t input);
/*
 *
 * Input and output blocks must be aligned and have BC_BLOCK_LEN bytes.
 *
 * @param[in,out] ctx		- the encryption context.
 * @param[out] output		- the plaintext generated.
 * @param[in] input			- the ciphertext to decrypt.
 */
#define aes_dec(ctx, output, max_output_size, input, input_len) aes_dec_start(ctx, output, max_output_size, input, input_len)

#endif /* AES_H_ */

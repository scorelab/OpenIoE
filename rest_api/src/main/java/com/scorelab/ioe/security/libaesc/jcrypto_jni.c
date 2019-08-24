/**
 * @file
 *
 * Implementation of the JNI functions.
 *
 */

#include <jni.h>
#include "jcrypto_jni.h"
#include "aes.h"
#include "constants.h"
#include <string.h>
#include <stdio.h>
#include <stdlib.h>

JNIEXPORT jint JNICALL Java_br_com_dojot_jcrypto_jni_JCrypto_aes_1size
  (JNIEnv *env, jclass clazz) {
	return sizeof(aes_ctx_st);
}


JNIEXPORT jint JNICALL Java_br_com_dojot_jcrypto_jni_JCrypto_aes_1gcm_1size
  (JNIEnv *env, jclass clazz) {
	return sizeof(aes_gcm_ctx_st);
}

JNIEXPORT errno_t JNICALL Java_br_com_dojot_jcrypto_jni_JCrypto_aes_1gcm_1aad
  (JNIEnv *env, jclass clazz, jobject ctx_buf, jbyteArray aad_buf) {
	errno_t result;
	aes_gcm_ctx_st *ctx = (aes_gcm_ctx_st *) (*env)->GetDirectBufferAddress(env, ctx_buf);
	unsigned char *aad;
	size_t aad_len;

	/* Check if context is valid */
	result = gcmCheckContext(ctx);
	if(result != SUCCESSFULL_OPERATION) {
		goto FAIL;
	}

	if(aad_buf == NULL) {
		goto FAIL;
	}
	aad_len = (int) (*env)->GetArrayLength(env, aad_buf);
        aad = (unsigned char *) (*env)->GetPrimitiveArrayCritical(env, aad_buf, NULL);

	if(aad != NULL)	{
		/* Updates the authentication tag */
		result = aes_gcm_aad(ctx, aad, aad_len);
		if(result != SUCCESSFULL_OPERATION) {
			goto FAIL_CLEAN;
		}

		result = memset_s(aad, sizeof(unsigned char) * aad_len, 0, sizeof(unsigned char) * aad_len);
		if(result != SUCCESSFULL_OPERATION) {
			goto FAIL_CLEAN;
		}
	} else {
		result = INVALID_STATE;
		goto FAIL_CLEAN;
	}
FAIL_CLEAN:
	(*env)->ReleasePrimitiveArrayCritical(env, aad_buf, aad, JNI_ABORT);
FAIL:
	return result;
}

JNIEXPORT errno_t JNICALL Java_br_com_dojot_jcrypto_jni_JCrypto_aes_1gcm_1init
  (JNIEnv *env, jclass clazz, jobject ctx_buf, jbyteArray iv_buf, jint tag_len, jint mode) {
	errno_t result;
	aes_gcm_ctx_st *ctx = (aes_gcm_ctx_st *) (*env)->GetDirectBufferAddress(env, ctx_buf);
	size_t len;
	unsigned char *iv;

	/* Check if context is valid */
	result = gcmCheckContext(ctx);
	if(result != SUCCESSFULL_OPERATION) {
		goto FAIL;
	}

	/* If there is any IV, maps it to C variable */
	if(iv_buf != NULL) {
		len = (int) (*env)->GetArrayLength(env, iv_buf);
		iv = (unsigned char *) (*env)->GetPrimitiveArrayCritical(env, iv_buf, NULL);

		result = aes_gcm_init(ctx, iv, len, tag_len, mode);

		(*env)->ReleasePrimitiveArrayCritical(env, iv_buf, iv, 0);
	/* If there is no IV then something is wrong */
	} else {
		result = INVALID_PARAMETER;
		goto FAIL;
	}

FAIL:
	if(result != SUCCESSFULL_OPERATION) {
		memset(ctx, 0, sizeof(aes_gcm_ctx_st));
	}
	return result;
}

JNIEXPORT errno_t JNICALL Java_br_com_dojot_jcrypto_jni_JCrypto_aes_1init
  (JNIEnv *env, jclass clazz, jobject ctx_buf, jbyteArray iv_buf, jint mode) {
	errno_t result;
	aes_ctx_st *ctx = (aes_ctx_st *) (*env)->GetDirectBufferAddress(env, ctx_buf);
	size_t len;
	unsigned char *iv;

	/* If there is any IV, map it to C variable. */
	if(iv_buf != NULL) {
		len = (int) (*env)->GetArrayLength(env, iv_buf);
		iv = (unsigned char *) (*env)->GetPrimitiveArrayCritical(env, iv_buf, NULL);
	} else {
		len = 0;
		iv = NULL;
	}

	result = aes_init(ctx, iv, len, mode);
	if(result != SUCCESSFULL_OPERATION) {
		memset(ctx, 0, sizeof(aes_ctx_st));
	}

	if(iv_buf != NULL) {
		(*env)->ReleasePrimitiveArrayCritical(env, iv_buf, iv, JNI_ABORT);
	}

	return result;
}


JNIEXPORT errno_t JNICALL Java_br_com_dojot_jcrypto_jni_JCrypto_aes_1enc
 (JNIEnv *env, jclass clazz, jobject ctx_buf, jbyteArray out_buf, jint out_offset, jint out_len, jbyteArray in_buf,
	jint in_offset, jint in_len, jint blockIndex) {
	errno_t result;
	aes_ctx_st *ctx = (aes_ctx_st *) (*env)->GetDirectBufferAddress(env, ctx_buf);
	unsigned char *input;
	unsigned char *output;
	int in_alloc = 0, out_alloc = 0;

	/* Check if context is valid */
	result = aesCheckContext(ctx);
	if(result != SUCCESSFULL_OPERATION) {
		goto FAIL;
	}
	/* Copies input data from in_buf to input */
	if (in_offset == 0 && in_len == (*env)->GetArrayLength(env, in_buf)) {
		input = (unsigned char *) (*env)->GetByteArrayElements(env, in_buf, NULL);
	} else {
		input = malloc(in_len);
		in_alloc = 1;
		(*env)->GetByteArrayRegion(env, in_buf, in_offset, in_len, (jbyte *) input);
	}

	if (out_offset == 0 && out_len == (*env)->GetArrayLength(env, out_buf)) {
		output = (unsigned char *) (*env)->GetByteArrayElements(env, out_buf, NULL);
	} else {
		output = malloc(out_len);
		out_alloc = 1;
	}

	/* Encrypts or decrypts input according to the mode that was set in the context */
	if (ctx->direction == DIR_ENCRYPT) {
		aes_encrypt(ctx, output, out_len, input, in_len);
	} else if(ctx->direction == DIR_DECRYPT) {
		aes_decrypt(ctx, output, out_len, input, in_len);
	}
	/* Releases variables */
	if (in_alloc == 1) {
		result |= memset_s(input, sizeof(unsigned char) * in_len, 0, sizeof(unsigned char) * in_len);
		free(input);
	} else {
		/* Release in_buf variable without copying array from C */
		(*env)->ReleaseByteArrayElements(env, in_buf, (jbyte *) input, JNI_ABORT);
	}
	/* Either way copies output array back to Java*/
	if (out_alloc) {
		(*env)->SetByteArrayRegion(env, out_buf, out_offset, out_len, (jbyte *) output);
	} else {
		(*env)->ReleaseByteArrayElements(env, out_buf, (jbyte *) output, JNI_COMMIT);
	}
	result |= memset_s(output, sizeof(unsigned char) * out_len, 0, sizeof(unsigned char) * out_len);
	free(output);
FAIL:
	/* If it is the last block being processed, clear everything */
	if(blockIndex & LAST_BLOCK) {
		memset(ctx, 0, sizeof(aes_ctx_st));
	}
	return result;
}

JNIEXPORT errno_t JNICALL Java_br_com_dojot_jcrypto_jni_JCrypto_aes_1gcm_1enc
 (JNIEnv *env, jclass clazz, jobject ctx_buf, jbyteArray out_buf, jint out_offset, jint out_len, jbyteArray in_buf,
	jint in_offset, jint in_len, jint blockIndex) {
	errno_t result;
	aes_gcm_ctx_st *ctx = (aes_gcm_ctx_st *) (*env)->GetDirectBufferAddress(env, ctx_buf);
	unsigned char *input;
	unsigned char *output;
	int in_alloc = 0, out_alloc = 0;

	/* Check if context is valid */
	result = gcmCheckContext(ctx);
	if(result != SUCCESSFULL_OPERATION) {
		goto FAIL;
	}

	/* Check if there exists the number of bytes requested to be processed*/
	if(in_len + in_offset > (*env)->GetArrayLength(env, in_buf)) {
		result = INVALID_PARAMETER;
		goto FAIL;
	}

	/* Get address from input buffer or copy the input */
	if (in_offset == 0 && in_len == (*env)->GetArrayLength(env, in_buf)) {
		if(in_len > 0) {
			input = (unsigned char *) (*env)->GetByteArrayElements(env, in_buf, NULL);
		} else {
			input = NULL;
		}
	} else {
		input = malloc(in_len);
		in_alloc = 1;
		(*env)->GetByteArrayRegion(env, in_buf, in_offset, in_len, (jbyte *) input);
	}

	if (out_offset == 0 && out_len == (*env)->GetArrayLength(env, out_buf)) {
		output = (unsigned char *) (*env)->GetByteArrayElements(env, out_buf, NULL);
	} else {
		output = malloc(out_len);
		out_alloc = 1;
	}
	/* Input processing */
	if (ctx->aes->direction == DIR_ENCRYPT) {
		result = aes_gcm_encrypt(ctx, output, input, in_len, blockIndex);
		if(result != SUCCESSFULL_OPERATION) {
			goto FAIL_CLEAN;
		}
	} else if(ctx->aes->direction == DIR_DECRYPT) {
		result = aes_gcm_decrypt(ctx, output, input, in_len, blockIndex);
		if(result != SUCCESSFULL_OPERATION) {
			goto FAIL_CLEAN;
		}
	}
FAIL_CLEAN:
	/* Release input buffer */
	if (in_alloc == 1) {
		result |= memset_s(input, sizeof(unsigned char) * in_len, 0, sizeof(unsigned char) * in_len);
		free(input);
	} else if(in_len > 0) {
		(*env)->ReleaseByteArrayElements(env, in_buf, (jbyte *) input, JNI_ABORT);
	}

	/* Copy output back to Java code */
	if (out_alloc) {
		(*env)->SetByteArrayRegion(env, out_buf, out_offset, out_len, (jbyte *) output);
	} else {
		(*env)->ReleaseByteArrayElements(env, out_buf, (jbyte *) output, JNI_COMMIT);
	}
	//result |= memset_s(output, sizeof(unsigned char) * out_len, 0, sizeof(unsigned char) * out_len);
	if(out_alloc == 1) {
		free(output);
	}
FAIL:
	/* If it is the last block, then context should be destroyed */
	if(blockIndex & LAST_BLOCK || result != SUCCESSFULL_OPERATION) {
		memset(ctx, 0, sizeof(aes_gcm_ctx_st));
	}
	return result;
}

/* Performs aes key expansion algorithm */
JNIEXPORT errno_t JNICALL Java_br_com_dojot_jcrypto_jni_JCrypto_aes_1key_1exp
  (JNIEnv *env, jclass clazz, jobject ctx_buf, jbyteArray key_buf, jint direction) {
	errno_t result;
	aes_ctx_st *ctx = (aes_ctx_st *) (*env)->GetDirectBufferAddress(env, ctx_buf);
	size_t len = (int) (*env)->GetArrayLength(env, key_buf);
	unsigned char *key = (unsigned char *) (*env)->GetPrimitiveArrayCritical(env, key_buf, NULL);

	/* Check if key has the correct length */
	if(len != AES128_KEY_LEN && len != AES192_KEY_LEN && len != AES256_KEY_LEN) {
		result = INVALID_PARAMETER;
		goto FAIL;
	}

	aes_expand_key(ctx, key, len, direction);
	result = SUCCESSFULL_OPERATION;
FAIL:
	(*env)->ReleasePrimitiveArrayCritical(env, key_buf, key, JNI_ABORT);
	return result;
}

/* Performs gcm key expansion, which is the exact same algorithm as aes key expansion but uses a different context */
JNIEXPORT errno_t JNICALL Java_br_com_dojot_jcrypto_jni_JCrypto_aes_1gcm_1key_1exp
  (JNIEnv *env, jclass clazz, jobject ctx_buf, jbyteArray key_buf, jint direction) {
	errno_t result;
	aes_gcm_ctx_st *ctx = (aes_gcm_ctx_st *) (*env)->GetDirectBufferAddress(env, ctx_buf);
	size_t len = (int) (*env)->GetArrayLength(env, key_buf);
	unsigned char *key = (unsigned char *) (*env)->GetPrimitiveArrayCritical(env, key_buf, NULL);


	/* Check if there was an error during key access */
	if(key == NULL) {
		result = INVALID_PARAMETER;
		goto FAIL;
	}

	/* Check if key has the correct length */
	if(len != AES128_KEY_LEN && len != AES192_KEY_LEN && len != AES256_KEY_LEN) {
		result = INVALID_PARAMETER;
		goto FAIL_KEY;
	}

	aes_gcm_expand_key(ctx, key, len, direction);

	result = SUCCESSFULL_OPERATION;
FAIL_KEY:
	(*env)->ReleasePrimitiveArrayCritical(env, key_buf, key, JNI_ABORT);
FAIL:
	return result;
}

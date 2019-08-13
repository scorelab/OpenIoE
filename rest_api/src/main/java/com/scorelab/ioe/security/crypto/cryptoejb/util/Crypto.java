package com.scorelab.ioe.security.crypto.cryptoejb.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {
	
	public static String getConfig(int tagLen, String key, String iv, String provider)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("\nProvider: ");
		sb.append(provider != null ? provider : "");
		sb.append("\nKey size: ");
		sb.append(key != null ? key.length() : 0);
		sb.append("\nIV size: ");
		sb.append(iv != null ? iv.length() : 0);
		sb.append("\nTag size: ");
		sb.append(tagLen);
		
		return sb.toString();
	}
	
	public static byte[] getRandomBytes(int numberOfBytes)
	{
		byte[] iv = new byte[numberOfBytes];
		SecureRandom sr = new SecureRandom();
		sr.nextBytes(iv);
		return iv;
	}
	public static byte[] encrypt(int taglen, byte[] key, byte[] iv, byte[] aad, byte[] plaintext, String provider) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException 
	{
		Cipher cipher;
		SecretKeySpec sharedKey;
		byte[] ciphertext;
		
		cipher = Cipher.getInstance("AES/GCM/NoPadding", provider);
		sharedKey = new SecretKeySpec(key, "AES");
		AlgorithmParameterSpec gcmParam = new GCMParameterSpec(taglen, iv, 0, iv.length);
		cipher.init(Cipher.ENCRYPT_MODE, sharedKey, gcmParam);

		ciphertext = doSymmetricCryptoOperation(cipher, aad, plaintext);
		
		return ciphertext;
	}
	
	public static byte[] decrypt(int taglen, byte[] key, byte[] iv, byte[] aad, byte[] ciphertext, String provider) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
	{
		Cipher cipher;
		SecretKeySpec sharedKey;
		byte[] plaintext;
		
		cipher = Cipher.getInstance("AES/GCM/NoPadding", provider);
		sharedKey = new SecretKeySpec(key, "AES");
		AlgorithmParameterSpec gcmParam = new GCMParameterSpec(taglen, iv, 0, iv.length);
		cipher.init(Cipher.DECRYPT_MODE, sharedKey, gcmParam);
		
		plaintext = doSymmetricCryptoOperation(cipher, aad, ciphertext);
		
		return plaintext;
	}
	
	private static byte[] doSymmetricCryptoOperation(Cipher cipher, byte[] aad, byte[] input) throws IllegalBlockSizeException, BadPaddingException
	{
		byte[] output;
		if(aad != null && aad.length > 0) {
			cipher.updateAAD(aad);
		}
		
		if(input != null && input.length > 0) {
			output = cipher.doFinal(input);
		} else {
			output = cipher.doFinal();
		}
		return output;		
	}
}

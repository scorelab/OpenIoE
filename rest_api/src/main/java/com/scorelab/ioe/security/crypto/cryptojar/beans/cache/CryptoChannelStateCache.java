package com.scorelab.ioe.security.crypto.cryptojar.beans.cache;

import java.io.Serializable;

public class CryptoChannelStateCache implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5602276967529713133L;

	private String provider;
	
	private byte[] keyServerToComponent;
	
	private byte[] keyComponentToServer;
	
	private byte[] ivServerToComponent;
	
	private byte[] ivComponentToServer;
	
	private int tagLen;

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public byte[] getKeyServerToComponent() {
		return keyServerToComponent;
	}

	public void setKeyServerToComponent(byte[] keyServerToComponent) {
		this.keyServerToComponent = keyServerToComponent;
	}

	public byte[] getKeyComponentToServer() {
		return keyComponentToServer;
	}

	public void setKeyComponentToServer(byte[] keyComponentToServer) {
		this.keyComponentToServer = keyComponentToServer;
	}

	public byte[] getIvServerToComponent() {
		return ivServerToComponent;
	}

	public void setIvServerToComponent(byte[] ivServerToComponent) {
		this.ivServerToComponent = ivServerToComponent;
	}

	public byte[] getIvComponentToServer() {
		return ivComponentToServer;
	}

	public void setIvComponentToServer(byte[] ivComponentToServer) {
		this.ivComponentToServer = ivComponentToServer;
	}

	public int getTagLen() {
		return tagLen;
	}

	public void setTagLen(int tagLen) {
		this.tagLen = tagLen;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("\nProvider: ");
		sb.append(provider != null ? provider : "");
		sb.append("\nServer to component key size: ");
		sb.append(keyServerToComponent != null ? keyServerToComponent.length : 0);
		sb.append("\nComponent to server key size: ");
		sb.append(keyComponentToServer != null ? keyComponentToServer.length : 0);
		sb.append("\nServer to component iv size: ");
		sb.append(ivServerToComponent != null ? ivServerToComponent.length : 0);
		sb.append("\nComponent to server iv size: ");
		sb.append(ivComponentToServer != null ? ivComponentToServer.length : 0);
		sb.append("\nTag size: ");
		sb.append(tagLen);
		
		return sb.toString();
	}

}

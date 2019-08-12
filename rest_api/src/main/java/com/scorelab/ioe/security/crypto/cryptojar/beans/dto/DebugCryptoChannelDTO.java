package com.scorelab.ioe.security.crypto.cryptojar.beans.dto;

import java.io.Serializable;

public class DebugCryptoChannelDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6468230161989290272L;

	private String sessionId;
	
	private String transactionId;
	
	private String provider;
	
	private Integer tagLen;
	
	private String keyServerToComponent;
	
	private String keyComponentToServer;
	
	private String ivServerToComponent;
	
	private String ivComponentToServer;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public Integer getTagLen() {
		return tagLen;
	}

	public void setTagLen(Integer tagLen) {
		this.tagLen = tagLen;
	}

	public String getKeyServerToComponent() {
		return keyServerToComponent;
	}

	public void setKeyServerToComponent(String keyServerToComponent) {
		this.keyServerToComponent = keyServerToComponent;
	}

	public String getKeyComponentToServer() {
		return keyComponentToServer;
	}

	public void setKeyComponentToServer(String keyComponentToServer) {
		this.keyComponentToServer = keyComponentToServer;
	}

	public String getIvServerToComponent() {
		return ivServerToComponent;
	}

	public void setIvServerToComponent(String ivServerToComponent) {
		this.ivServerToComponent = ivServerToComponent;
	}

	public String getIvComponentToServer() {
		return ivComponentToServer;
	}

	public void setIvComponentToServer(String ivComponentToServer) {
		this.ivComponentToServer = ivComponentToServer;
	}

}
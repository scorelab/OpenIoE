package com.scorelab.ioe.security.crypto.cryptojar.beans.dto;

import java.io.Serializable;

public class LoggingDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4810136567369883571L;

	private String level;
	
	private String component;
	
	private String username;
	
	private String details;
	
	private String node;

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

}
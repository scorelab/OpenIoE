package com.scorelab.ioe.security.communication.constants;

public interface CommunicationKeysConstants {

	// Keys to Service Registry query
	String KEY_MICROSERVICE = "microservice";
	String KEY_RESTFUL = "restful";
	String KEY_VERSION = "version";
	String KEY_METHOD = "method";
	
	String SERVICE_REGISTRY_TARGET = "SERVICE_REGISTRY_TARGET_LOAD_BALANCER";	
	String SERVICE_REGISTRY_PATH_REGISTER = "SERVICE_REGISTRY_PATH_REGISTER_LOAD_BALANCER";	
	String SERVICE_REGISTRY_PATH_UNREGISTER = "SERVICE_REGISTRY_PATH_UNREGISTER_LOAD_BALANCER";
	String SERVICE_REGISTRY_PATH_FIND_SERVICE = "SERVICE_REGISTRY_PATH_FIND_SERVICE_LOAD_BALANCER";
	
	// Other node.properties keys
	String KEY_NODE = "node";
}

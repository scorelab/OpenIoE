package com.scorelab.ioe.security.crypto.cryptoejb.util;

public interface CryptoIntegrationConstants {
	
	// Topics Kafka Cluster
	String TOPIC_DECRYPT = "decrypttopic";
	String TOPIC_DECRYPT_WITH_CC = "decryptwithcctopic";
	String TOPIC_ENCRYPT = "encrypttopic";
	String TOPIC_ENCRYPT_WITH_CC = "encryptwithcctopic";
	String TOPIC_REGISTER = "registertopic";
	String TOPIC_UNREGISTER = "unregistertopic";
	String TOPIC_CRYPTO_RELEASE_DECRYPT_CC = "cryptoreleasedecryptcctopic";
	String TOPIC_LOGGING_PROCESSING = "logprocessingtopic";
	String TOPIC_DEBUG_CRYPTO_CHANNEL = "debugcryptochanneltopic";
	String TOPIC_SAVE_CRYPTO_CHANNEL = "savecryptochanneltopic";
	
	String REDIS_CLUSTER_MASTER_NAME = "redis-cs-cluster";
	
	String PARAM_KAFKA_BOOTSTRAP_SERVERS = "PARAM_KAFKA_BOOTSTRAP_SERVERS";
	String PARAM_KAFKA_NUMBER_OF_CONSUMERS = "PARAM_KAFKA_NUMBER_OF_CONSUMERS";
}

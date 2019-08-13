package com.scorelab.ioe.security.crypto.cryptoejb.messaging.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import com.scorelab.ioe.security.crypto.cryptoejb.messaging.api.ReleaseDecryptConsumerService;
import com.scorelab.ioe.security.crypto.cryptoejb.service.api.CryptoService;
import com.scorelab.ioe.security.crypto.cryptoejb.util.CryptoIntegrationConstants;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

@Startup
@Singleton
public class ReleaseDecryptConsumerServiceImpl extends Thread implements ReleaseDecryptConsumerService {

    private ConsumerConnector consumerConnector;

	@EJB
	private CryptoService cryptoService; 
	
	@PostConstruct
    public void init() {
        /*Properties properties = new Properties();
        properties.put("zookeeper.connect","localhost:2181");
        properties.put("group.id","testgroup");
        ConsumerConfig consumerConfig = new ConsumerConfig(properties);
        consumerConnector = Consumer.createJavaConsumerConnector(consumerConfig);
        this.start();*/
    }

    @Override
    public void run() {
        Map<String, Integer> topicMap = new HashMap<String, Integer>();
        topicMap.put(CryptoIntegrationConstants.TOPIC_CRYPTO_RELEASE_DECRYPT_CC, new Integer(1));
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumerConnector.createMessageStreams(topicMap);
        KafkaStream<byte[], byte[]> stream =  consumerMap.get(CryptoIntegrationConstants.TOPIC_CRYPTO_RELEASE_DECRYPT_CC).get(0);
        ConsumerIterator<byte[], byte[]> it = stream.iterator();
        while(it.hasNext()) {
        	String sessionId = new String(it.next().message());
        	cryptoService.releaseDecryptCCVolatile(sessionId);
        }
    }

}
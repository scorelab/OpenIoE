package com.scorelab.ioe.security.crypto.cryptoejb.messaging.consumer;

import java.util.Arrays;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

public abstract class ConsumerLoopGeneric implements Runnable {

	private KafkaConsumer<String, String> consumer;
	
	private String topic;
	
	private String clientId;
	
	private String version;
	
	private Properties props;
	
	public ConsumerLoopGeneric(String topic, String groupId, String clientId) {
		props = new Properties();
		props.put("group.id", groupId);
		props.put("enable.auto.commit", "true");
	    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
	    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		this.topic = topic;
		this.clientId = clientId;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public void setBootstrapServers(String bootstrapServers) {
		props.put("bootstrap.servers", bootstrapServers);
	}
	
	public void setUniqueIdentifier(String node, int index) {
		props.put("client.id", clientId + node + index);
	}

	@Override
	public void run() {
		try {
			this.consumer = new KafkaConsumer<>(props);

			consumer.subscribe(Arrays.asList(topic + "." + version));
			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);
				for (ConsumerRecord<String, String> record : records) {
					String json = new String(record.value());
					process(json);
				}
			}
		} catch (WakeupException e) {
			System.out.println("Consumer " + props.getProperty("client.id") + " from group " + props.getProperty("group.id") + " on topic " + topic + "." + version + " was waken up.");
		} finally {
			System.out.println("Consumer " + props.getProperty("client.id") + " from group " + props.getProperty("group.id") + " on topic " + topic + "." + version + " is being terminated.");
			consumer.close();
		}
	}
	
	public abstract void process(String json);
	
	public void shutdown() {
		consumer.wakeup();
	}

}
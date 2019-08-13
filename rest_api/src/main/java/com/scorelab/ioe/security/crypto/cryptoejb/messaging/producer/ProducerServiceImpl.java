package com.scorelab.ioe.security.crypto.cryptoejb.messaging.producer;

import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ProducerServiceImpl {

	private Producer<String, String> producer;

	private String topic;



	public ProducerServiceImpl(String bootstrapServers, String topic, String version, String clientId) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		Properties producerProps = new Properties();
		producerProps.put("bootstrap.servers", bootstrapServers);
		producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		producerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		producerProps.put("acks", "1");
		producerProps.put("client.id", clientId + calendar.getTimeInMillis());
		producer = new KafkaProducer<>(producerProps);
		this.topic = topic + "." + version;

	}

	public void close() {
		producer.close();
	}

	public void produce(Object dto) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(dto);
			ProducerRecord<String, String> data = new ProducerRecord<String, String>(topic, json);
			producer.send(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

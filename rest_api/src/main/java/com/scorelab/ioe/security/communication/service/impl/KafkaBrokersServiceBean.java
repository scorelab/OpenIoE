package com.scorelab.ioe.security.communication.service.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.scorelab.ioe.security.communication.service.api.KafkaBrokersService;

public class KafkaBrokersServiceBean implements KafkaBrokersService {
	
	@Override
	public String requestKafkaBrokers() {
		String brokers = null;
		FileInputStream fis = null;
		Properties properties = new Properties();
		try {
			String fileName = System.getProperty("jboss.server.config.dir") + "/kafka.brokers";
			fis = new FileInputStream(fileName);
			properties.load(fis);
			brokers = properties.getProperty("kafkabootstrap");
		} catch (IOException ex) {
			System.out.println("ERROR: Redis Sentinels Start: " + ex.getMessage());
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					System.out.println("WARN: Redis Sentinels Start: " + e.getMessage());
				}
			}
		}
		return brokers;
	}

}

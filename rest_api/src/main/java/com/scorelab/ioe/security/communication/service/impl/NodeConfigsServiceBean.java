package com.scorelab.ioe.security.communication.service.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Properties;

import com.scorelab.ioe.security.communication.service.api.NodeConfigsService;

public class NodeConfigsServiceBean implements NodeConfigsService {

	@Override
	public LinkedHashMap<String, Object> requestNodeConfigs() {
		LinkedHashMap<String, Object> configs = new LinkedHashMap<String, Object>();
		FileInputStream fis = null;
		Properties properties = new Properties();
		try {
			String fileName = System.getProperty("jboss.server.config.dir") + "/node.config";
			fis = new FileInputStream(fileName);
			properties.load(fis);
			for(String key : properties.stringPropertyNames()) {
				configs.put(key, properties.getProperty(key));
			}
		} catch (IOException ex) {
			System.out.println("ERROR: Request Node Configs: " + ex.getMessage());
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					System.out.println("WARN: Request Node Configs: " + e.getMessage());
				}
			}
		}
		return configs;
	}

}
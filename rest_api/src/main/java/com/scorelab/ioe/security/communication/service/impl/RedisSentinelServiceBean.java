package com.scorelab.ioe.security.communication.service.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import com.scorelab.ioe.security.communication.service.api.RedisSentinelService;

public class RedisSentinelServiceBean implements RedisSentinelService {

	@Override
	public Set<String> requestRedisSentinelsHosts() {
		Set<String> sentinels = new HashSet<String>();
		FileInputStream fis = null;
		Properties properties = new Properties();
		try {
			String fileName = System.getProperty("jboss.server.config.dir") + "/redis.sentinels";
			fis = new FileInputStream(fileName);
			properties.load(fis);
			String result = properties.getProperty("sentinels");
			StringTokenizer token = new StringTokenizer(result, ",");
			while (token.hasMoreTokens()) {
				String partial = token.nextToken();
				sentinels.add(partial);
			}
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
		return sentinels;
	}

}

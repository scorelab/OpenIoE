package com.scorelab.ioe.security.crypto.cryptoejb.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Security;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.scorelab.ioe.jcrypto.JCryptoProvider;
import com.scorelab.ioe.security.crypto.cryptoejb.service.api.AESConfigService;

@Startup
@Singleton
public class AESConfigServiceImpl implements AESConfigService {
	
	private String library;

	@Override
	@PostConstruct
	public void addCryptographicProvider() {
		if(hasHardwareSupportForAES()) {
			this.library = "JCrypto";
			Security.addProvider(new JCryptoProvider());
		} else {
			this.library = "BC";
			Security.addProvider(new BouncyCastleProvider());
		}
	}
	
	private boolean hasHardwareSupportForAES() {
		String[] dependencies = {"aes", "avx",  "ssse3", "pclmulqdq"};
		boolean hasHardwareSupportForAES = true;
		try {
			String processorDescription = new String(Files.readAllBytes(Paths.get("/proc/cpuinfo")));
			Pattern pattern = Pattern.compile("^flags\\s*:(.*)$");
			Matcher matcher = pattern.matcher(processorDescription);
			if(matcher.matches()) {
				processorDescription = matcher.group(0);
			}
			for(int i = 0; i < dependencies.length; i++) {
				hasHardwareSupportForAES &= processorDescription.contains(dependencies[i]);
			}
		} catch (IOException e) {
			hasHardwareSupportForAES = false;
		}
		return hasHardwareSupportForAES;
	}
	
	@Override
	public String getLibrary() {
		return this.library;
	}
	
}

package com.scorelab.ioe.security.crypto.cryptoejb.messaging.consumer;

import javax.ejb.EJB;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.scorelab.ioe.security.crypto.cryptoejb.service.api.CryptoService;
import com.scorelab.ioe.security.crypto.cryptoejb.service.api.LoggingService;
import com.scorelab.ioe.security.crypto.cryptoejb.service.api.LoggingService.Level;
import com.scorelab.ioe.security.crypto.cryptoejb.util.CryptoIntegrationConstants;

public class ConsumerLoopReleaseDecrypt extends ConsumerLoopGeneric {
	@EJB
	private CryptoService cryptoService;  
	
	@EJB
	private LoggingService loggingService;

	public ConsumerLoopReleaseDecrypt() {
		super(CryptoIntegrationConstants.TOPIC_CRYPTO_RELEASE_DECRYPT_CC, "cryptointegrationgroup", "cryptointegrationrelease");
	}
	
	@Override
	public void process(String json) {
		try {
			cryptoService.releaseDecryptCCVolatile(json);
		} catch (Exception ex) {
			loggingService.saveLogging(Level.ERROR, "CRYPTO_INTEGRATION", "SISTEMA", ex.getMessage() + ".\n" + ExceptionUtils.getStackTrace(ex));
		}
	}
	
}
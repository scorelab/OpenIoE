package com.scorelab.ioe.security.crypto.cryptoejb.messaging.consumer;

import javax.ejb.EJB;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;

import com.scorelab.ioe.security.crypto.cryptoejb.service.api.CryptoManagerService;
import com.scorelab.ioe.security.crypto.cryptoejb.service.api.CryptoService;
import com.scorelab.ioe.security.crypto.cryptoejb.service.api.LoggingService;
import com.scorelab.ioe.security.crypto.cryptoejb.service.api.LoggingService.Level;
import com.scorelab.ioe.security.crypto.cryptoejb.util.CryptoIntegrationConstants;

public class ConsumerLoopRegister extends ConsumerLoopGeneric {
	@EJB
	private CryptoService cryptoService;

	@EJB
	private CryptoManagerService cryptoManager;

	@EJB
	private LoggingService loggingService;

	public ConsumerLoopRegister() {
		super(CryptoIntegrationConstants.TOPIC_REGISTER, "cryptointegrationgroup", "cryptointegrationregister");
	}

	@Override
	public void process(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			String transactionId = jsonObject.getString("transactionId");
			String sessionId = jsonObject.getString("sessionId");
			String provider = jsonObject.getString("provider");
			int tagLen = jsonObject.getInt("tagLen");
			String keyServerToComponent = jsonObject.getString("keyServerToComponent");
			String keyComponentToServer = jsonObject.getString("keyComponentToServer");
			String ivServerToComponent = jsonObject.getString("ivServerToComponent");
			String ivComponentToServer = jsonObject.getString("ivComponentToServer");
			long lifespan = jsonObject.getLong("lifespan");
			Boolean result = cryptoService.registerCC(sessionId, transactionId, provider, tagLen, keyServerToComponent,
					keyComponentToServer, ivServerToComponent, ivComponentToServer, lifespan);
			cryptoManager.put("register." + sessionId, result);
		} catch (Exception ex) {
			loggingService.saveLogging(Level.ERROR, "CRYPTO_INTEGRATION", "SISTEMA", ex.getMessage() + ".\n" + ExceptionUtils.getStackTrace(ex));
		}
	}

}
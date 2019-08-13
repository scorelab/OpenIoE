package com.scorelab.ioe.security.crypto.cryptoejb.messaging.consumer;

import java.util.Map;
import javax.ejb.EJB;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;

import com.scorelab.ioe.security.crypto.cryptoejb.service.api.CryptoManagerService;
import com.scorelab.ioe.security.crypto.cryptoejb.service.api.CryptoService;
import com.scorelab.ioe.security.crypto.cryptoejb.service.api.LoggingService;
import com.scorelab.ioe.security.crypto.cryptoejb.service.api.LoggingService.Level;
import com.scorelab.ioe.security.crypto.cryptoejb.util.Constants;
import com.scorelab.ioe.security.crypto.cryptoejb.util.CryptoIntegrationConstants;

public class ConsumerLoopDecrypt extends ConsumerLoopGeneric {
	@EJB
	private CryptoService cryptoService;  

	@EJB
	private CryptoManagerService cryptoManager;
	
	@EJB
	private LoggingService loggingService;

	public ConsumerLoopDecrypt() {
		super(CryptoIntegrationConstants.TOPIC_DECRYPT, "cryptointegrationgroup", "cryptointegrationdecrypt");
	}

	@Override
	public void process(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
	    	String sessionId = jsonObject.getString("sessionId");
	    	String key = jsonObject.getString("key");
	    	String iv = jsonObject.getString("iv");
	    	int tagSize = jsonObject.getInt("tagLen");
	    	String data = jsonObject.getString("data");
	    	Map<String, String> tmp = cryptoService.decrypt(key, iv, tagSize, data);
			cryptoManager.put("decrypt." + sessionId, (String) tmp.getOrDefault(Constants.PLAINTEXT, ""), null,
					(String) tmp.getOrDefault(Constants.OPERATION_RESULT, Constants.FAILURE));
		} catch (Exception ex) {
			loggingService.saveLogging(Level.ERROR, "CRYPTO_INTEGRATION", "SISTEMA", ex.getMessage() + ".\n" + ExceptionUtils.getStackTrace(ex));
		}
	}
	
}
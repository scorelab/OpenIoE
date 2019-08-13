package com.scorelab.ioe.security.crypto.cryptoejb.messaging.producer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import com.scorelab.ioe.security.communication.constants.CommunicationKeysConstants;
import com.scorelab.ioe.security.communication.facade.api.CommunicationFacade;
import com.scorelab.ioe.security.communication.facade.impl.CommunicationFacadeBean;
import com.scorelab.ioe.security.crypto.cryptoejb.beans.dto.DebugCryptoChannelDTO;
import com.scorelab.ioe.security.crypto.cryptoejb.dao.api.ConfigDAO;
import com.scorelab.ioe.security.crypto.cryptoejb.messaging.api.DebugCryptoChannelProducerService;
import com.scorelab.ioe.security.crypto.cryptoejb.util.CryptoIntegrationConstants;

@Startup
@Singleton
public class DebugCryptoChannelProducerServiceImpl extends Thread implements DebugCryptoChannelProducerService {
	private ProducerServiceImpl producer;
	
	@EJB
	private ConfigDAO configDAO;
    
	@PostConstruct
	public void init() {
		CommunicationFacade facade = new CommunicationFacadeBean();
		
		producer = new ProducerServiceImpl(configDAO.findParameterByKey(CryptoIntegrationConstants.PARAM_KAFKA_BOOTSTRAP_SERVERS),
				CryptoIntegrationConstants.TOPIC_DEBUG_CRYPTO_CHANNEL,
				(String) facade.requestNodeConfigs().get(CommunicationKeysConstants.KEY_VERSION), "cry.debugcryptochannel.");
	}
	
	@PreDestroy
	public void close() {
		producer.close();
	}

	@Override
	public void produce(DebugCryptoChannelDTO dto) {
		producer.produce(dto);
	}

}
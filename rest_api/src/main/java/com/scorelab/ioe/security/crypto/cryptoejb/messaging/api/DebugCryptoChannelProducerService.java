package com.scorelab.ioe.security.crypto.cryptoejb.messaging.api;

import com.scorelab.ioe.security.crypto.cryptoejb.beans.dto.DebugCryptoChannelDTO;

public interface DebugCryptoChannelProducerService {
	
	void produce(DebugCryptoChannelDTO dto);
}

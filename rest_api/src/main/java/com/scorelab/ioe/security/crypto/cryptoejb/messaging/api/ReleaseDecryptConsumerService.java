package com.scorelab.ioe.security.crypto.cryptoejb.messaging.api;

import javax.ejb.Local;

@Local
public interface ReleaseDecryptConsumerService {
	
	void init();
	
	void start();
}

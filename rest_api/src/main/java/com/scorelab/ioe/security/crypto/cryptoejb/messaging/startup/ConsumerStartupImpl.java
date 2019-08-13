package com.scorelab.ioe.security.crypto.cryptoejb.messaging.startup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.scorelab.ioe.security.communication.constants.CommunicationKeysConstants;
import com.scorelab.ioe.security.communication.facade.api.CommunicationFacade;
import com.scorelab.ioe.security.communication.facade.impl.CommunicationFacadeBean;
import com.scorelab.ioe.security.crypto.cryptoejb.dao.api.ConfigDAO;
import com.scorelab.ioe.security.crypto.cryptoejb.messaging.api.ConsumerStartup;
import com.scorelab.ioe.security.crypto.cryptoejb.messaging.consumer.ConsumerLoopDecrypt;
import com.scorelab.ioe.security.crypto.cryptoejb.messaging.consumer.ConsumerLoopDecryptWithCC;
import com.scorelab.ioe.security.crypto.cryptoejb.messaging.consumer.ConsumerLoopEncrypt;
import com.scorelab.ioe.security.crypto.cryptoejb.messaging.consumer.ConsumerLoopEncryptWithCC;
import com.scorelab.ioe.security.crypto.cryptoejb.messaging.consumer.ConsumerLoopGeneric;
import com.scorelab.ioe.security.crypto.cryptoejb.messaging.consumer.ConsumerLoopRegister;
import com.scorelab.ioe.security.crypto.cryptoejb.messaging.consumer.ConsumerLoopReleaseDecrypt;
import com.scorelab.ioe.security.crypto.cryptoejb.messaging.consumer.ConsumerLoopSaveCryptoChannel;
import com.scorelab.ioe.security.crypto.cryptoejb.messaging.consumer.ConsumerLoopUnregister;
import com.scorelab.ioe.security.crypto.cryptoejb.util.CryptoIntegrationConstants;

@Startup
@Singleton
public class ConsumerStartupImpl implements ConsumerStartup {
	
    @Resource
    ManagedExecutorService managedExecutorService;
    
    @EJB
    private ConfigDAO configDAO;
    
    @Inject
    Instance<ConsumerLoopDecrypt> consumerLoopDecrypt;
    
    @Inject
    Instance<ConsumerLoopDecryptWithCC> consumerLoopDecryptWithCC;
    
    @Inject
    Instance<ConsumerLoopEncrypt> consumerLoopEncrypt;
    
    @Inject
    Instance<ConsumerLoopEncryptWithCC> consumerLoopEncryptWithCC;
    
    @Inject
    Instance<ConsumerLoopRegister> consumerLoopRegister;
    
    @Inject
    Instance<ConsumerLoopReleaseDecrypt> consumerLoopReleaseDecrypt;
    
    @Inject
    Instance<ConsumerLoopSaveCryptoChannel> consumerLoopSaveCryptoChannel;
    
    @Inject 
    Instance<ConsumerLoopUnregister> consumerLoopUnregister;
    
    private List<ConsumerLoopGeneric> tasks;

    public void executeAsync() throws ExecutionException, InterruptedException {
    	tasks = new ArrayList<>();
    	CommunicationFacade facade = new CommunicationFacadeBean();
    	String node = (String) facade.requestNodeConfigs().get(CommunicationKeysConstants.KEY_NODE);
    	String version = (String) facade.requestNodeConfigs().get(CommunicationKeysConstants.KEY_VERSION);
    	String bootstrapServers = configDAO.findParameterByKey(CryptoIntegrationConstants.PARAM_KAFKA_BOOTSTRAP_SERVERS);
    	int numberOfConsumers = Integer.valueOf(configDAO.findParameterByKey(CryptoIntegrationConstants.PARAM_KAFKA_NUMBER_OF_CONSUMERS));
	    for(int i=0; i<numberOfConsumers; i++) {
	    	ConsumerLoopDecrypt cld = consumerLoopDecrypt.get();
	    	cld.setUniqueIdentifier(node, i);
	    	cld.setVersion(version);
	    	cld.setBootstrapServers(bootstrapServers);
	    	tasks.add(cld);
	        this.managedExecutorService.submit(cld);
	        
	        ConsumerLoopDecryptWithCC cldcc = consumerLoopDecryptWithCC.get();
	        cldcc.setUniqueIdentifier(node, i);
	        cldcc.setVersion(version);
	        cldcc.setBootstrapServers(bootstrapServers);
	        tasks.add(cldcc);
	        this.managedExecutorService.submit(cldcc);
	        
	        ConsumerLoopEncrypt cle = consumerLoopEncrypt.get();
	        cle.setUniqueIdentifier(node, i);
	        cle.setVersion(version);
	        cle.setBootstrapServers(bootstrapServers);
	        tasks.add(cle);
	        this.managedExecutorService.submit(cle);
	        
	        ConsumerLoopEncryptWithCC clecc = consumerLoopEncryptWithCC.get();
	        clecc.setUniqueIdentifier(node, i);
	        clecc.setVersion(version);
	        clecc.setBootstrapServers(bootstrapServers);
	        tasks.add(clecc);
	        this.managedExecutorService.submit(clecc);
	        
	        ConsumerLoopRegister clr = consumerLoopRegister.get();
	        clr.setUniqueIdentifier(node, i);
	        clr.setVersion(version);
	        clr.setBootstrapServers(bootstrapServers);
	        tasks.add(clr);
	        this.managedExecutorService.submit(clr);
	        
	        ConsumerLoopReleaseDecrypt clrd = consumerLoopReleaseDecrypt.get();
	        clrd.setUniqueIdentifier(node, i);
	        clrd.setVersion(version);
	        clrd.setBootstrapServers(bootstrapServers);
	        tasks.add(clrd);
	        this.managedExecutorService.submit(clrd);
	        
	        ConsumerLoopSaveCryptoChannel clscc = consumerLoopSaveCryptoChannel.get();
	        clscc.setUniqueIdentifier(node, i);
	        clscc.setVersion(version);
	        clscc.setBootstrapServers(bootstrapServers);
	        tasks.add(clscc);
	        this.managedExecutorService.submit(clscc);
	        
	        ConsumerLoopUnregister clu = consumerLoopUnregister.get();
	        clu.setUniqueIdentifier(node, i);
	        clu.setVersion(version);
	        clu.setBootstrapServers(bootstrapServers);
	        tasks.add(clu);
	        this.managedExecutorService.submit(clu);
	    }
    }

	@PostConstruct
	public void init() {
		try {
			this.executeAsync();
			System.out.println("Started all CryptoIntegration consumers");
		} catch (ExecutionException e) {
			System.out.println("ExecutionException" + e.getMessage());

		} catch (InterruptedException e) {
			System.out.println("InterruptedException" + e.getMessage());
		}
	}
	
	@PreDestroy
	public void destroy() {
		System.out.println("Removing all CryptoIntegration consumers");
		for (ConsumerLoopGeneric task : tasks) {
			task.shutdown();
		}
		System.out.println("All CryptoIntegration consumers were terminated.");
	}

}
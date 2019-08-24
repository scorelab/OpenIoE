package com.scorelab.ioe.security.crypto.cryptows.listener;

import java.util.LinkedHashMap;

import javax.ejb.EJB;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.scorelab.ioe.security.communication.constants.CommunicationKeysConstants;
import com.scorelab.ioe.security.communication.facade.api.CommunicationFacade;
import com.scorelab.ioe.security.communication.facade.impl.CommunicationFacadeBean;
import com.scorelab.ioe.security.registry.facade.api.RegistryFacade;
import com.scorelab.ioe.security.registry.facade.impl.RegistryFacadeBean;
import com.scorelab.ioe.security.crypto.cryptows.facade.api.CryptoIntegrationFacade;

@WebListener
public class ServiceRegistryMicroserviceListener implements ServletContextListener {

	@EJB
	private CryptoIntegrationFacade cryptoIntegrationFacade;

	private LinkedHashMap<String, Object> params;

	private String targetServiceRegistry;

	private String pathServiceRegistryRegister;

	private String pathServiceRegistryUnregister;

	@Override
	public void contextInitialized(ServletContextEvent arg0) {		
		CommunicationFacade facade = new CommunicationFacadeBean();
		params = facade.requestNodeConfigs();
		params.put("microservice", arg0.getServletContext().getInitParameter("microserviceName"));
		params.put("context", arg0.getServletContext().getContextPath());
		targetServiceRegistry = cryptoIntegrationFacade.findParameterByKey(CommunicationKeysConstants.SERVICE_REGISTRY_TARGET);
		pathServiceRegistryRegister = cryptoIntegrationFacade.findParameterByKey(CommunicationKeysConstants.SERVICE_REGISTRY_PATH_REGISTER);
		pathServiceRegistryUnregister = cryptoIntegrationFacade.findParameterByKey(CommunicationKeysConstants.SERVICE_REGISTRY_PATH_UNREGISTER);
        RegistryFacade registryFacade = new RegistryFacadeBean();
		registryFacade.register(targetServiceRegistry, pathServiceRegistryRegister, params);
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
        RegistryFacade registryFacade = new RegistryFacadeBean();
		registryFacade.unregister(targetServiceRegistry, pathServiceRegistryUnregister, params);
	}

}
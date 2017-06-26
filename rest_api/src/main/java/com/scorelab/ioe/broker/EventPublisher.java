package com.scorelab.ioe.broker;

import com.scorelab.ioe.config.IoeConfiguration;
import com.scorelab.ioe.repository.SensorRepository;
import com.scorelab.ioe.service.SensorDataRepositoryService;
import com.scorelab.ioe.repository.PublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

/**
 * Created by priya on 22/6/17.
 */

@Component
public class EventPublisher implements ApplicationEventPublisherAware{

    @Autowired
    @Qualifier("taskExecutor")
    private TaskExecutor taskExecutor;

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private IoeConfiguration ioeConfiguration;

    @Autowired
    private SensorDataRepositoryService databaseService;

    @Autowired
    private PublicationRepository publicationRepository;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        taskExecutor.execute(new BrokerProducerThread(sensorRepository, databaseService, ioeConfiguration, publicationRepository));
    }
}

package com.scorelab.ioe.broker;

import com.scorelab.ioe.config.IoeConfiguration;
import com.scorelab.ioe.repository.SensorRepository;
import com.scorelab.ioe.service.noSqlRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

/**
 * Created by tharidu on 8/3/16.
 */

@Component
public class ContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    @Qualifier("taskExecutor")
    private TaskExecutor taskExecutor;

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private IoeConfiguration ioeConfiguration;

    @Autowired
    private noSqlRepositoryService databaseService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        taskExecutor.execute(new BrokerConsumerThread(sensorRepository, databaseService, ioeConfiguration));
    }
}

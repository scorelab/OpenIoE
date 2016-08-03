package com.scorelab.ioe.broker;

import com.scorelab.ioe.repository.SensorRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Created by tharidu on 8/3/16.
 */

@Component
public class ContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent>{

    @Inject
    private SensorRepository sensorRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        BrokerConsumerThread brokerConsumer = new BrokerConsumerThread(sensorRepository);
        try {
            brokerConsumer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

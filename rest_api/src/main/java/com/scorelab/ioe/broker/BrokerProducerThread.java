package com.scorelab.ioe.broker;

import javax.jms.*;

import com.scorelab.ioe.config.IoeConfiguration;
import com.scorelab.ioe.repository.SensorRepository;
import com.scorelab.ioe.service.SensorDataRepositoryService;
import com.scorelab.ioe.repository.PublicationRepository;
import com.scorelab.ioe.domain.Publication;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import java.util.List;

/**
 * Created by priya on 22/6/17.
 */

public class BrokerProducerThread implements Runnable {
    private Thread t;

    private SensorRepository sensorRepository;
    private SensorDataRepositoryService databaseService;
    private IoeConfiguration ioeConfiguration;
    private PublicationRepository publicationRepository;

    public BrokerProducerThread(SensorRepository sensorRepository, SensorDataRepositoryService databaseService, IoeConfiguration ioeConfiguration, PublicationRepository publicationRepository) {
        this.sensorRepository = sensorRepository;
        this.databaseService = databaseService;
        this.ioeConfiguration = ioeConfiguration;
        this.publicationRepository = publicationRepository;
    }

    public void StartBrokerProducer() throws JMSException {

        Connection connection = null;
        try {
            ConnectionFactory cf = new ActiveMQConnectionFactory(ioeConfiguration.getTopic().getTopicUrl());
            connection = cf.createConnection(ioeConfiguration.getTopic().getUsername(), ioeConfiguration.getTopic().getPassword());
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // TODO - Read message payload from cassandra
            String payload = "{\"data\": \"36\",\"description\": \"test\",\"sensorId\": 1, \"timestamp\": \"2015-06-19T11:07:44.526Z\", \"topic\": \"topic\"}";
            Message msg = session.createTextMessage(payload);

            //TODO Read topic value from the sensor data or publication entity
            Topic topic = ActiveMQJMSClient.createTopic("ioe");
            MessageProducer messageProducer = session.createProducer(null);
            messageProducer.send(topic,msg);
            connection.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            StartBrokerProducer();
            while (true) {
                Thread.sleep(1000);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("Message broker thread interrupted.");
        }
    }

    public void start() {
        if (t == null) {
            t = new Thread(this);
            t.start();
        }
    }
}

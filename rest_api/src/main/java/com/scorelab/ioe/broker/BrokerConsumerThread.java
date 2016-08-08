package com.scorelab.ioe.broker;

import javax.jms.*;

import com.scorelab.ioe.config.IoeConfiguration;
import com.scorelab.ioe.repository.SensorRepository;
import com.scorelab.ioe.service.noSqlRepositoryService;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

/**
 * Created by tharidu on 8/1/16.
 */

//@Component
//@Scope("prototype")
public class BrokerConsumerThread implements Runnable {
    private Thread t;

//    @Inject
//    private IoeConfiguration ioeConfiguration;

//    public BrokerConsumerThread() {
//    }

//    public BrokerConsumerThread(SensorRepository sensorRepository) {
//        this.sensorRepository = sensorRepository;
//    }

    private SensorRepository sensorRepository;
    private noSqlRepositoryService databaseService;
    private IoeConfiguration ioeConfiguration;

    public BrokerConsumerThread(SensorRepository sensorRepository, noSqlRepositoryService databaseService, IoeConfiguration ioeConfiguration) {
        this.sensorRepository = sensorRepository;
        this.databaseService = databaseService;
        this.ioeConfiguration = ioeConfiguration;
    }

    public void StartBrokerConsumer() throws JMSException {
//        ApplicationContext context = new AnnotationConfigApplicationContext(IoeConfiguration.class);
//        IoeConfiguration ioeConfiguration = context.getBean(IoeConfiguration.class);

        Connection connection = null;
        try {
            Queue queue = ActiveMQJMSClient.createQueue(ioeConfiguration.getQueue().getQueueName());
            ConnectionFactory cf = new ActiveMQConnectionFactory(ioeConfiguration.getQueue().getQueueUrl());
            connection = cf.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer messageConsumer = session.createConsumer(queue);
            messageConsumer.setMessageListener(new BrokerMessageListener(sensorRepository, databaseService));

            connection.start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            StartBrokerConsumer();
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

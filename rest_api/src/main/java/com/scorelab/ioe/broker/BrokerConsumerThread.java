package com.scorelab.ioe.broker;

import javax.jms.*;

import com.scorelab.ioe.config.IoeConfiguration;
import com.scorelab.ioe.repository.SensorRepository;
import com.scorelab.ioe.service.SensorDataRepositoryService;
import com.scorelab.ioe.repository.SubscriptionRepository;
import com.scorelab.ioe.domain.Subscription;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import java.util.List;
import javax.validation.Valid;
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
    private SensorDataRepositoryService databaseService;
    private IoeConfiguration ioeConfiguration;
    private SubscriptionRepository subscriptionRepository;

    public BrokerConsumerThread(SensorRepository sensorRepository, SensorDataRepositoryService databaseService, IoeConfiguration ioeConfiguration, SubscriptionRepository subscriptionRepository) {
        this.sensorRepository = sensorRepository;
        this.databaseService = databaseService;
        this.ioeConfiguration = ioeConfiguration;
        this.subscriptionRepository = subscriptionRepository;
    }

    public void StartBrokerConsumer() throws JMSException {
//        ApplicationContext context = new AnnotationConfigApplicationContext(IoeConfiguration.class);
//        IoeConfiguration ioeConfiguration = context.getBean(IoeConfiguration.class);

        Connection connection = null;
        try {
            ConnectionFactory cf = new ActiveMQConnectionFactory(ioeConfiguration.getTopic().getTopicUrl());
            connection = cf.createConnection(ioeConfiguration.getTopic().getUsername(), ioeConfiguration.getTopic().getPassword());
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            List<Subscription> subscriptions = subscriptionRepository.findAll();
            for(Subscription s:subscriptions) {
                Topic topic = ActiveMQJMSClient.createTopic(s.getTopicFilter());
                MessageConsumer messageConsumer = session.createConsumer(topic);
                messageConsumer.setMessageListener(new BrokerMessageListener(sensorRepository, databaseService));
            }
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

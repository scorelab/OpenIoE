package com.scorelab.ioe.broker;

import javax.jms.*;

import com.scorelab.ioe.config.Constants;
import com.scorelab.ioe.repository.SensorRepository;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

/**
 * Created by tharidu on 8/1/16.
 */
public class BrokerConsumerThread implements Runnable {
    private Thread t;
    private SensorRepository sensorRepository;

    public BrokerConsumerThread(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    public void StartBrokerConsumer() throws JMSException {
        Connection connection = null;
        try {
            Queue queue = ActiveMQJMSClient.createQueue(Constants.QUEUE_NAME);
            ConnectionFactory cf = new ActiveMQConnectionFactory(Constants.QUEUE_URL);
            connection = cf.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer messageConsumer = session.createConsumer(queue);
            messageConsumer.setMessageListener(new BrokerMessageListener(sensorRepository));

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

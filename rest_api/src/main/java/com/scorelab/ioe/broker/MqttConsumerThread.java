package com.scorelab.ioe.broker;

import com.scorelab.ioe.config.Constants;
import com.scorelab.ioe.config.IoeConfiguration;
import com.scorelab.ioe.repository.SubscriptionRepository;
import com.scorelab.ioe.domain.Subscription;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.ArrayList;

import java.net.URISyntaxException;

/**
 * Created by tharidu on 8/4/16.
 */
public class MqttConsumerThread {

    @Autowired
    private IoeConfiguration ioeConfiguration;
    private SubscriptionRepository subscriptionRepository;

    public void StartBrokerConsumer() throws Exception {
        MQTT mqtt = new MQTT();
        mqtt.setHost(ioeConfiguration.getTopic().getMqttUrl());
        BlockingConnection connection = mqtt.blockingConnection();
        connection.connect();

        List<Topic> topic_list = new ArrayList<>();
        List<Subscription> subscriptions = subscriptionRepository.findAll();
        for (Subscription s : subscriptions) {
            topic_list.add(new Topic(s.getTopicFilter(), QoS.AT_LEAST_ONCE));
        }
        Topic[] topics = topic_list.toArray(new Topic[topic_list.size()]);
        connection.subscribe(topics);
    }
}

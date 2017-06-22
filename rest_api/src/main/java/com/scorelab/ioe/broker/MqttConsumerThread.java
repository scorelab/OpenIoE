package com.scorelab.ioe.broker;

import com.scorelab.ioe.config.Constants;
import com.scorelab.ioe.config.IoeConfiguration;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URISyntaxException;

/**
 * Created by tharidu on 8/4/16.
 */
public class MqttConsumerThread {

    @Autowired
    private IoeConfiguration ioeConfiguration;

    public void StartBrokerConsumer() throws Exception {
        MQTT mqtt = new MQTT();
        mqtt.setHost(ioeConfiguration.getTopic().getMqttUrl());
        BlockingConnection connection = mqtt.blockingConnection();
        connection.connect();

    }
}

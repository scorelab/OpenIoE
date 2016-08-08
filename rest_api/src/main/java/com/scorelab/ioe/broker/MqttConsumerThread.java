package com.scorelab.ioe.broker;

import com.scorelab.ioe.config.Constants;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;

import java.net.URISyntaxException;

/**
 * Created by tharidu on 8/4/16.
 */
public class MqttConsumerThread {

    public void StartBrokerConsumer() throws Exception {
        MQTT mqtt = new MQTT();
        mqtt.setHost(Constants.MQTT_URL);
        BlockingConnection connection = mqtt.blockingConnection();
        connection.connect();

    }
}

package com.scorelab.ioe.broker;


import com.scorelab.ioe.config.Constants;
import com.scorelab.ioe.config.IoeConfiguration;
import com.scorelab.ioe.repository.SubscriptionRepository;
import com.scorelab.ioe.domain.Subscription;
import com.scorelab.ioe.domain.Sensor;
import com.scorelab.ioe.domain.SensorData;
import com.scorelab.ioe.nosql.StoreTypes;
import com.scorelab.ioe.repository.SensorRepository;
import com.scorelab.ioe.service.SensorDataRepositoryService;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.fusesource.mqtt.client.Message;
import org.springframework.beans.factory.annotation.Autowired;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;


import java.util.List;
import java.util.ArrayList;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import java.net.URISyntaxException;

/**
 * Created by tharidu on 8/4/16.
 */
public class MqttConsumerThread implements Runnable{

    @Autowired
    private IoeConfiguration ioeConfiguration;
    private SubscriptionRepository subscriptionRepository;
    private Gson gson;
    private SensorRepository sensorRepository;
    private SensorDataRepositoryService databaseService;

    public MqttConsumerThread(SensorRepository sensorRepository, SensorDataRepositoryService databaseService, IoeConfiguration ioeConfiguration, SubscriptionRepository subscriptionRepository) {
        this.ioeConfiguration = ioeConfiguration;
        this.subscriptionRepository = subscriptionRepository;
        this.sensorRepository = sensorRepository;
        this.databaseService = databaseService;
        gson = Converters.registerAll(new GsonBuilder()).create();
    }

    public void StartBrokerConsumer() throws Exception {
        MQTT mqtt = new MQTT();
        mqtt.setHost(ioeConfiguration.getTopic().getMqttUrl());
        BlockingConnection connection = mqtt.blockingConnection();
        connection.connect();
        List<Topic> topic_list = new ArrayList<>();

        while (true) {
            List<Subscription> subscriptions = subscriptionRepository.findAll();
            for (Subscription s : subscriptions) {
                topic_list.add(new Topic(s.getTopicFilter(), QoS.AT_LEAST_ONCE));
            }
            Topic[] topics = topic_list.toArray(new Topic[topic_list.size()]);
            connection.subscribe(topics);

            Message message = connection.receive();
            byte[] payload = message.getPayload();
            String messageContent = new String(payload);
            SensorData sensorData = gson.fromJson(messageContent, SensorData.class);
            ZonedDateTime utcTime = sensorData.getTimestamp().withZoneSameInstant(ZoneOffset.UTC);
            Sensor sensor = sensorRepository.findBySensorId(sensorData.getSensorId());
            // TODO - Read TTL value
            databaseService.insertData(sensorData.getSensorId(), sensorData.getData(), sensorData.getDescription(), utcTime, StoreTypes.valueOf(sensor.getStoreType()), sensorData.getTopic(), 0);
            System.out.println("Received message: " + messageContent);
            message.ack();
        }
    }

    @Override
    public void run() {
        try {
            StartBrokerConsumer();
            while (true) {
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

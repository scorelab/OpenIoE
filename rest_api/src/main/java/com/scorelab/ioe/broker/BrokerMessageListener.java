package com.scorelab.ioe.broker;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.scorelab.ioe.config.CassandraConfiguration;
import com.scorelab.ioe.domain.Sensor;
import com.scorelab.ioe.domain.SensorData;
import com.scorelab.ioe.nosql.StoreTypes;
import com.scorelab.ioe.repository.SensorRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Created by tharidu on 8/2/16.
 */
public class BrokerMessageListener implements MessageListener {

    @Inject
    private SensorRepository sensorRepository;

    private Gson gson;

    public BrokerMessageListener(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
        gson = Converters.registerAll(new GsonBuilder()).create();
    }

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            ApplicationContext ctx =
                new AnnotationConfigApplicationContext(CassandraConfiguration.class);

            CassandraConfiguration cc = ctx.getBean(CassandraConfiguration.class);
            SensorData sensorData = gson.fromJson(textMessage.getText(), SensorData.class);
            ZonedDateTime utcTime = sensorData.getTimestamp().withZoneSameInstant(ZoneOffset.UTC);

            Sensor sensor = sensorRepository.findBySensorId(sensorData.getSensorId());
//            // TODO - Read TTL value
            cc.insertData(sensorData.getSensorId(), sensorData.getData(), sensorData.getDescription(), utcTime, StoreTypes.valueOf(sensor.getStoreType()), 0);

            System.out.println("Received message: " + textMessage.getText());
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException | JsonIOException e) {
            e.printStackTrace();
        }
    }
}

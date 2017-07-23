package com.scorelab.ioe.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Strings;
import com.scorelab.ioe.domain.Sensor;
import com.scorelab.ioe.domain.SensorData;
import com.scorelab.ioe.nosql.StoreTypes;
import com.scorelab.ioe.repository.SensorRepository;
import com.scorelab.ioe.domain.Publication;
import com.scorelab.ioe.repository.PublicationRepository;
import com.scorelab.ioe.service.SensorDataRepositoryService;
import com.scorelab.ioe.config.IoeConfiguration;

import com.scorelab.ioe.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.fusesource.mqtt.client.Message;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * REST controller for managing Sensor.
 */
@RestController
@RequestMapping("/api")
public class SensorResource {

    private final Logger log = LoggerFactory.getLogger(SensorResource.class);

    @Inject
    private SensorRepository sensorRepository;

    @Autowired
    private SensorDataRepositoryService databaseService;

    @Autowired
    private IoeConfiguration ioeConfiguration;

    @Inject
    private PublicationRepository publicationRepository;

    private Gson gson;

    /**
     * POST  /sensors : Create a new sensor.
     *
     * @param sensor the sensor to create
     * @return the ResponseEntity with status 201 (Created) and with body the new sensor, or with status 400 (Bad Request) if the sensor has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/sensors",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Sensor> createSensor(@Valid @RequestBody Sensor sensor) throws URISyntaxException {
        log.debug("REST request to save Sensor : {}", sensor);
        if (sensor.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("sensor", "idexists", "A new sensor cannot already have an ID")).body(null);
        }
        Sensor result = sensorRepository.save(sensor);
        databaseService.createSensorTable(result.getSensorId(), StoreTypes.valueOf(result.getStoreType()));
        return ResponseEntity.created(new URI("/api/sensors/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("sensor", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /sensors : Updates an existing sensor.
     *
     * @param sensor the sensor to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated sensor,
     * or with status 400 (Bad Request) if the sensor is not valid,
     * or with status 500 (Internal Server Error) if the sensor couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/sensors",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Sensor> updateSensor(@Valid @RequestBody Sensor sensor) throws URISyntaxException {
        log.debug("REST request to update Sensor : {}", sensor);
        if (sensor.getId() == null) {
            return createSensor(sensor);
        }
        Sensor result = sensorRepository.save(sensor);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("sensor", sensor.getId().toString()))
            .body(result);
    }

    /**
     * GET  /sensors : get all the sensors.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of sensors in body
     */
    @RequestMapping(value = "/sensors",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Sensor> getAllSensors() {
        log.debug("REST request to get all Sensors");
        List<Sensor> sensors = sensorRepository.findAll();
        return sensors;
    }

    /**
     * GET  /sensors/:id : get the "id" sensor.
     *
     * @param id the id of the sensor to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the sensor, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/sensors/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Sensor> getSensor(@PathVariable Long id) {
        log.debug("REST request to get Sensor : {}", id);
        Sensor sensor = sensorRepository.findOne(id);
        return Optional.ofNullable(sensor)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /sensors/:id : delete the "id" sensor.
     *
     * @param id the id of the sensor to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/sensors/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteSensor(@PathVariable Long id) {
        log.debug("REST request to delete Sensor : {}", id);
        sensorRepository.delete(id);

        // TODO - Clear up the sensor data?

        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("sensor", id.toString())).build();
    }

    /**
     * POST /sensors/:id
     * Insert sensor data to Cassandra storage
     *
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/sensors/{id}",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void insertSensorPayload(@Valid @RequestBody SensorData sensorData) throws Exception{
        ZonedDateTime utcTime = sensorData.getTimestamp().withZoneSameInstant(ZoneOffset.UTC);

        Sensor sensor = sensorRepository.findBySensorId(sensorData.getSensorId());

        Publication publication = publicationRepository.findByTopic(sensorData.getSensorId(),sensorData.getTopic());
            if(publication != null){
                // TODO - Read TTL value
                databaseService.insertData(sensorData.getSensorId(), sensorData.getData(), sensorData.getDescription(), utcTime, StoreTypes.valueOf(sensor.getStoreType()), sensorData.getTopic(), 0);
                String topic = sensorData.getTopic();
                MQTT mqtt = new MQTT();
                mqtt.setHost(ioeConfiguration.getTopic().getMqttUrl());
                BlockingConnection connection = mqtt.blockingConnection();
                connection.connect();

                // TODO - use gson.toJson(sensordata)
                // String jsonInString = gson.toJson(sensordata);

                String payload = "{\"data\": \""+sensorData.getData()+"\",\"description\": \""+sensorData.getDescription()+"\",\"sensorId\": "+sensorData.getSensorId()+", \"timestamp\": \""+utcTime+"\", \"topic\": \""+sensorData.getTopic()+"\"}";
                connection.publish(topic, payload.getBytes(), QoS.AT_LEAST_ONCE, false);
            }
    }

    /**
     * GET /sensors/:id
     * Return all sensor data
     *
     * @param id of the sensor to insert payload
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/sensors/{id}/data",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<String> GetSensorPayload(@PathVariable Long id) {
        return databaseService.readData(id);
    }

    /**
     * GET /sensors/:id
     * Return all sensor data by topic
     *
     * @param id of the sensor to insert payload
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/sensors/{id}/{topic}/data",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<String> GetSensorPayloadbyTopic(@PathVariable Long id, @PathVariable String topic){
        return databaseService.readData(id, topic);
    }

    /**
     * GET /sensors/:id/data
     * Return all sensor data by given dates
     *
     * @param id of the sensor to insert payload
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/sensors/{id}/data",
        method = RequestMethod.GET,
        params = "dates",
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<String> GetSensorPayloadByDate(@PathVariable Long id, @RequestParam("dates") String dates) {
        if (Strings.isNullOrEmpty(dates)) {
            return null;
        }

        List<LocalDate> dateTimeList = new ArrayList<>();
        String[] dateArr = dates.split(",");
        for (String date :
            dateArr) {
            try {
                dateTimeList.add(LocalDate.parse(date));
            } catch (DateTimeParseException ex) {
            }
        }

        return databaseService.readData(id, dateTimeList);
    }
}

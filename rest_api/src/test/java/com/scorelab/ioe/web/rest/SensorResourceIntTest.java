package com.scorelab.ioe.web.rest;

import com.scorelab.ioe.IoeApp;
import com.scorelab.ioe.domain.Sensor;
import com.scorelab.ioe.repository.SensorRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the SensorResource REST controller.
 *
 * @see SensorResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IoeApp.class)
@WebAppConfiguration
@IntegrationTest
public class SensorResourceIntTest {


    private static final Long DEFAULT_SENSOR_ID = 1L;
    private static final Long UPDATED_SENSOR_ID = 2L;
    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";
    private static final String DEFAULT_TYPE = "AAAAA";
    private static final String UPDATED_TYPE = "BBBBB";
    private static final String DEFAULT_SERIAL = "AAAAA";
    private static final String UPDATED_SERIAL = "BBBBB";
    private static final String DEFAULT_STORE_TYPE = "AAAAA";
    private static final String UPDATED_STORE_TYPE = "BBBBB";
    private static final String DEFAULT_TABLE_ID = "AAAAA";
    private static final String UPDATED_TABLE_ID = "BBBBB";

    @Inject
    private SensorRepository sensorRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restSensorMockMvc;

    private Sensor sensor;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SensorResource sensorResource = new SensorResource();
        ReflectionTestUtils.setField(sensorResource, "sensorRepository", sensorRepository);
        this.restSensorMockMvc = MockMvcBuilders.standaloneSetup(sensorResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        sensor = new Sensor();
        sensor.setSensorId(DEFAULT_SENSOR_ID);
        sensor.setName(DEFAULT_NAME);
        sensor.setDescription(DEFAULT_DESCRIPTION);
        sensor.setType(DEFAULT_TYPE);
        sensor.setSerial(DEFAULT_SERIAL);
        sensor.setStoreType(DEFAULT_STORE_TYPE);
        sensor.setTableId(DEFAULT_TABLE_ID);
    }

    @Test
    @Transactional
    public void createSensor() throws Exception {
        int databaseSizeBeforeCreate = sensorRepository.findAll().size();

        // Create the Sensor

        restSensorMockMvc.perform(post("/api/sensors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(sensor)))
                .andExpect(status().isCreated());

        // Validate the Sensor in the database
        List<Sensor> sensors = sensorRepository.findAll();
        assertThat(sensors).hasSize(databaseSizeBeforeCreate + 1);
        Sensor testSensor = sensors.get(sensors.size() - 1);
        assertThat(testSensor.getSensorId()).isEqualTo(DEFAULT_SENSOR_ID);
        assertThat(testSensor.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSensor.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testSensor.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testSensor.getSerial()).isEqualTo(DEFAULT_SERIAL);
        assertThat(testSensor.getStoreType()).isEqualTo(DEFAULT_STORE_TYPE);
        assertThat(testSensor.getTableId()).isEqualTo(DEFAULT_TABLE_ID);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = sensorRepository.findAll().size();
        // set the field null
        sensor.setName(null);

        // Create the Sensor, which fails.

        restSensorMockMvc.perform(post("/api/sensors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(sensor)))
                .andExpect(status().isBadRequest());

        List<Sensor> sensors = sensorRepository.findAll();
        assertThat(sensors).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSensors() throws Exception {
        // Initialize the database
        sensorRepository.saveAndFlush(sensor);

        // Get all the sensors
        restSensorMockMvc.perform(get("/api/sensors?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(sensor.getId().intValue())))
                .andExpect(jsonPath("$.[*].sensorId").value(hasItem(DEFAULT_SENSOR_ID.intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
                .andExpect(jsonPath("$.[*].serial").value(hasItem(DEFAULT_SERIAL.toString())))
                .andExpect(jsonPath("$.[*].storeType").value(hasItem(DEFAULT_STORE_TYPE.toString())))
                .andExpect(jsonPath("$.[*].tableId").value(hasItem(DEFAULT_TABLE_ID.toString())));
    }

    @Test
    @Transactional
    public void getSensor() throws Exception {
        // Initialize the database
        sensorRepository.saveAndFlush(sensor);

        // Get the sensor
        restSensorMockMvc.perform(get("/api/sensors/{id}", sensor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(sensor.getId().intValue()))
            .andExpect(jsonPath("$.sensorId").value(DEFAULT_SENSOR_ID.intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.serial").value(DEFAULT_SERIAL.toString()))
            .andExpect(jsonPath("$.storeType").value(DEFAULT_STORE_TYPE.toString()))
            .andExpect(jsonPath("$.tableId").value(DEFAULT_TABLE_ID.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSensor() throws Exception {
        // Get the sensor
        restSensorMockMvc.perform(get("/api/sensors/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSensor() throws Exception {
        // Initialize the database
        sensorRepository.saveAndFlush(sensor);
        int databaseSizeBeforeUpdate = sensorRepository.findAll().size();

        // Update the sensor
        Sensor updatedSensor = new Sensor();
        updatedSensor.setId(sensor.getId());
        updatedSensor.setSensorId(UPDATED_SENSOR_ID);
        updatedSensor.setName(UPDATED_NAME);
        updatedSensor.setDescription(UPDATED_DESCRIPTION);
        updatedSensor.setType(UPDATED_TYPE);
        updatedSensor.setSerial(UPDATED_SERIAL);
        updatedSensor.setStoreType(UPDATED_STORE_TYPE);
        updatedSensor.setTableId(UPDATED_TABLE_ID);

        restSensorMockMvc.perform(put("/api/sensors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedSensor)))
                .andExpect(status().isOk());

        // Validate the Sensor in the database
        List<Sensor> sensors = sensorRepository.findAll();
        assertThat(sensors).hasSize(databaseSizeBeforeUpdate);
        Sensor testSensor = sensors.get(sensors.size() - 1);
        assertThat(testSensor.getSensorId()).isEqualTo(UPDATED_SENSOR_ID);
        assertThat(testSensor.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSensor.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSensor.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testSensor.getSerial()).isEqualTo(UPDATED_SERIAL);
        assertThat(testSensor.getStoreType()).isEqualTo(UPDATED_STORE_TYPE);
        assertThat(testSensor.getTableId()).isEqualTo(UPDATED_TABLE_ID);
    }

    @Test
    @Transactional
    public void deleteSensor() throws Exception {
        // Initialize the database
        sensorRepository.saveAndFlush(sensor);
        int databaseSizeBeforeDelete = sensorRepository.findAll().size();

        // Get the sensor
        restSensorMockMvc.perform(delete("/api/sensors/{id}", sensor.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Sensor> sensors = sensorRepository.findAll();
        assertThat(sensors).hasSize(databaseSizeBeforeDelete - 1);
    }
}

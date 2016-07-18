package com.scorelab.ioe.web.rest;

import com.scorelab.ioe.IoeApp;
import com.scorelab.ioe.domain.SensorData;
import com.scorelab.ioe.repository.SensorDataRepository;

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
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the SensorDataResource REST controller.
 *
 * @see SensorDataResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IoeApp.class)
@WebAppConfiguration
@IntegrationTest
public class SensorDataResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));


    private static final Long DEFAULT_SENSOR_ID = 1L;
    private static final Long UPDATED_SENSOR_ID = 2L;
    private static final String DEFAULT_DATA = "AAAAA";
    private static final String UPDATED_DATA = "BBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    private static final ZonedDateTime DEFAULT_TIMESTAMP = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_TIMESTAMP = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_TIMESTAMP_STR = dateTimeFormatter.format(DEFAULT_TIMESTAMP);

    @Inject
    private SensorDataRepository sensorDataRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restSensorDataMockMvc;

    private SensorData sensorData;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SensorDataResource sensorDataResource = new SensorDataResource();
        ReflectionTestUtils.setField(sensorDataResource, "sensorDataRepository", sensorDataRepository);
        this.restSensorDataMockMvc = MockMvcBuilders.standaloneSetup(sensorDataResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        sensorData = new SensorData();
        sensorData.setSensorId(DEFAULT_SENSOR_ID);
        sensorData.setData(DEFAULT_DATA);
        sensorData.setDescription(DEFAULT_DESCRIPTION);
        sensorData.setTimestamp(DEFAULT_TIMESTAMP);
    }

    @Test
    @Transactional
    public void createSensorData() throws Exception {
        int databaseSizeBeforeCreate = sensorDataRepository.findAll().size();

        // Create the SensorData

        restSensorDataMockMvc.perform(post("/api/sensor-data")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(sensorData)))
                .andExpect(status().isCreated());

        // Validate the SensorData in the database
        List<SensorData> sensorData = sensorDataRepository.findAll();
        assertThat(sensorData).hasSize(databaseSizeBeforeCreate + 1);
        SensorData testSensorData = sensorData.get(sensorData.size() - 1);
        assertThat(testSensorData.getSensorId()).isEqualTo(DEFAULT_SENSOR_ID);
        assertThat(testSensorData.getData()).isEqualTo(DEFAULT_DATA);
        assertThat(testSensorData.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testSensorData.getTimestamp()).isEqualTo(DEFAULT_TIMESTAMP);
    }

    @Test
    @Transactional
    public void getAllSensorData() throws Exception {
        // Initialize the database
        sensorDataRepository.saveAndFlush(sensorData);

        // Get all the sensorData
        restSensorDataMockMvc.perform(get("/api/sensor-data?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(sensorData.getId().intValue())))
                .andExpect(jsonPath("$.[*].sensorId").value(hasItem(DEFAULT_SENSOR_ID.intValue())))
                .andExpect(jsonPath("$.[*].data").value(hasItem(DEFAULT_DATA.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP_STR)));
    }

    @Test
    @Transactional
    public void getSensorData() throws Exception {
        // Initialize the database
        sensorDataRepository.saveAndFlush(sensorData);

        // Get the sensorData
        restSensorDataMockMvc.perform(get("/api/sensor-data/{id}", sensorData.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(sensorData.getId().intValue()))
            .andExpect(jsonPath("$.sensorId").value(DEFAULT_SENSOR_ID.intValue()))
            .andExpect(jsonPath("$.data").value(DEFAULT_DATA.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.timestamp").value(DEFAULT_TIMESTAMP_STR));
    }

    @Test
    @Transactional
    public void getNonExistingSensorData() throws Exception {
        // Get the sensorData
        restSensorDataMockMvc.perform(get("/api/sensor-data/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSensorData() throws Exception {
        // Initialize the database
        sensorDataRepository.saveAndFlush(sensorData);
        int databaseSizeBeforeUpdate = sensorDataRepository.findAll().size();

        // Update the sensorData
        SensorData updatedSensorData = new SensorData();
        updatedSensorData.setId(sensorData.getId());
        updatedSensorData.setSensorId(UPDATED_SENSOR_ID);
        updatedSensorData.setData(UPDATED_DATA);
        updatedSensorData.setDescription(UPDATED_DESCRIPTION);
        updatedSensorData.setTimestamp(UPDATED_TIMESTAMP);

        restSensorDataMockMvc.perform(put("/api/sensor-data")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedSensorData)))
                .andExpect(status().isOk());

        // Validate the SensorData in the database
        List<SensorData> sensorData = sensorDataRepository.findAll();
        assertThat(sensorData).hasSize(databaseSizeBeforeUpdate);
        SensorData testSensorData = sensorData.get(sensorData.size() - 1);
        assertThat(testSensorData.getSensorId()).isEqualTo(UPDATED_SENSOR_ID);
        assertThat(testSensorData.getData()).isEqualTo(UPDATED_DATA);
        assertThat(testSensorData.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSensorData.getTimestamp()).isEqualTo(UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    public void deleteSensorData() throws Exception {
        // Initialize the database
        sensorDataRepository.saveAndFlush(sensorData);
        int databaseSizeBeforeDelete = sensorDataRepository.findAll().size();

        // Get the sensorData
        restSensorDataMockMvc.perform(delete("/api/sensor-data/{id}", sensorData.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<SensorData> sensorData = sensorDataRepository.findAll();
        assertThat(sensorData).hasSize(databaseSizeBeforeDelete - 1);
    }
}

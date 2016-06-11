package com.scorelab.ioe.web.rest;

import com.scorelab.ioe.IoeApp;
import com.scorelab.ioe.domain.Device;
import com.scorelab.ioe.repository.DeviceRepository;

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
 * Test class for the DeviceResource REST controller.
 *
 * @see DeviceResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IoeApp.class)
@WebAppConfiguration
@IntegrationTest
public class DeviceResourceIntTest {


    private static final Long DEFAULT_DEVICE_ID = 1L;
    private static final Long UPDATED_DEVICE_ID = 2L;
    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";
    private static final String DEFAULT_TYPE = "AAAAA";
    private static final String UPDATED_TYPE = "BBBBB";
    private static final String DEFAULT_SERIAL = "AAAAA";
    private static final String UPDATED_SERIAL = "BBBBB";

    @Inject
    private DeviceRepository deviceRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restDeviceMockMvc;

    private Device device;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        DeviceResource deviceResource = new DeviceResource();
        ReflectionTestUtils.setField(deviceResource, "deviceRepository", deviceRepository);
        this.restDeviceMockMvc = MockMvcBuilders.standaloneSetup(deviceResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        device = new Device();
        device.setDeviceId(DEFAULT_DEVICE_ID);
        device.setName(DEFAULT_NAME);
        device.setDescription(DEFAULT_DESCRIPTION);
        device.setType(DEFAULT_TYPE);
        device.setSerial(DEFAULT_SERIAL);
    }

    @Test
    @Transactional
    public void createDevice() throws Exception {
        int databaseSizeBeforeCreate = deviceRepository.findAll().size();

        // Create the Device

        restDeviceMockMvc.perform(post("/api/devices")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(device)))
                .andExpect(status().isCreated());

        // Validate the Device in the database
        List<Device> devices = deviceRepository.findAll();
        assertThat(devices).hasSize(databaseSizeBeforeCreate + 1);
        Device testDevice = devices.get(devices.size() - 1);
        assertThat(testDevice.getDeviceId()).isEqualTo(DEFAULT_DEVICE_ID);
        assertThat(testDevice.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDevice.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testDevice.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testDevice.getSerial()).isEqualTo(DEFAULT_SERIAL);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = deviceRepository.findAll().size();
        // set the field null
        device.setName(null);

        // Create the Device, which fails.

        restDeviceMockMvc.perform(post("/api/devices")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(device)))
                .andExpect(status().isBadRequest());

        List<Device> devices = deviceRepository.findAll();
        assertThat(devices).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllDevices() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the devices
        restDeviceMockMvc.perform(get("/api/devices?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(device.getId().intValue())))
                .andExpect(jsonPath("$.[*].deviceId").value(hasItem(DEFAULT_DEVICE_ID.intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
                .andExpect(jsonPath("$.[*].serial").value(hasItem(DEFAULT_SERIAL.toString())));
    }

    @Test
    @Transactional
    public void getDevice() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get the device
        restDeviceMockMvc.perform(get("/api/devices/{id}", device.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(device.getId().intValue()))
            .andExpect(jsonPath("$.deviceId").value(DEFAULT_DEVICE_ID.intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.serial").value(DEFAULT_SERIAL.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingDevice() throws Exception {
        // Get the device
        restDeviceMockMvc.perform(get("/api/devices/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDevice() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);
        int databaseSizeBeforeUpdate = deviceRepository.findAll().size();

        // Update the device
        Device updatedDevice = new Device();
        updatedDevice.setId(device.getId());
        updatedDevice.setDeviceId(UPDATED_DEVICE_ID);
        updatedDevice.setName(UPDATED_NAME);
        updatedDevice.setDescription(UPDATED_DESCRIPTION);
        updatedDevice.setType(UPDATED_TYPE);
        updatedDevice.setSerial(UPDATED_SERIAL);

        restDeviceMockMvc.perform(put("/api/devices")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedDevice)))
                .andExpect(status().isOk());

        // Validate the Device in the database
        List<Device> devices = deviceRepository.findAll();
        assertThat(devices).hasSize(databaseSizeBeforeUpdate);
        Device testDevice = devices.get(devices.size() - 1);
        assertThat(testDevice.getDeviceId()).isEqualTo(UPDATED_DEVICE_ID);
        assertThat(testDevice.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDevice.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testDevice.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testDevice.getSerial()).isEqualTo(UPDATED_SERIAL);
    }

    @Test
    @Transactional
    public void deleteDevice() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);
        int databaseSizeBeforeDelete = deviceRepository.findAll().size();

        // Get the device
        restDeviceMockMvc.perform(delete("/api/devices/{id}", device.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Device> devices = deviceRepository.findAll();
        assertThat(devices).hasSize(databaseSizeBeforeDelete - 1);
    }
}

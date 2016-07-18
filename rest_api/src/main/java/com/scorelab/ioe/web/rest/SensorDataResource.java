package com.scorelab.ioe.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.scorelab.ioe.domain.SensorData;
import com.scorelab.ioe.repository.SensorDataRepository;
import com.scorelab.ioe.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing SensorData.
 */
@RestController
@RequestMapping("/api")
public class SensorDataResource {

    private final Logger log = LoggerFactory.getLogger(SensorDataResource.class);
        
    @Inject
    private SensorDataRepository sensorDataRepository;
    
    /**
     * POST  /sensor-data : Create a new sensorData.
     *
     * @param sensorData the sensorData to create
     * @return the ResponseEntity with status 201 (Created) and with body the new sensorData, or with status 400 (Bad Request) if the sensorData has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/sensor-data",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SensorData> createSensorData(@RequestBody SensorData sensorData) throws URISyntaxException {
        log.debug("REST request to save SensorData : {}", sensorData);
        if (sensorData.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("sensorData", "idexists", "A new sensorData cannot already have an ID")).body(null);
        }
        SensorData result = sensorDataRepository.save(sensorData);
        return ResponseEntity.created(new URI("/api/sensor-data/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("sensorData", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /sensor-data : Updates an existing sensorData.
     *
     * @param sensorData the sensorData to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated sensorData,
     * or with status 400 (Bad Request) if the sensorData is not valid,
     * or with status 500 (Internal Server Error) if the sensorData couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/sensor-data",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SensorData> updateSensorData(@RequestBody SensorData sensorData) throws URISyntaxException {
        log.debug("REST request to update SensorData : {}", sensorData);
        if (sensorData.getId() == null) {
            return createSensorData(sensorData);
        }
        SensorData result = sensorDataRepository.save(sensorData);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("sensorData", sensorData.getId().toString()))
            .body(result);
    }

    /**
     * GET  /sensor-data : get all the sensorData.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of sensorData in body
     */
    @RequestMapping(value = "/sensor-data",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<SensorData> getAllSensorData() {
        log.debug("REST request to get all SensorData");
        List<SensorData> sensorData = sensorDataRepository.findAll();
        return sensorData;
    }

    /**
     * GET  /sensor-data/:id : get the "id" sensorData.
     *
     * @param id the id of the sensorData to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the sensorData, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/sensor-data/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SensorData> getSensorData(@PathVariable Long id) {
        log.debug("REST request to get SensorData : {}", id);
        SensorData sensorData = sensorDataRepository.findOne(id);
        return Optional.ofNullable(sensorData)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /sensor-data/:id : delete the "id" sensorData.
     *
     * @param id the id of the sensorData to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/sensor-data/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteSensorData(@PathVariable Long id) {
        log.debug("REST request to delete SensorData : {}", id);
        sensorDataRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("sensorData", id.toString())).build();
    }

}

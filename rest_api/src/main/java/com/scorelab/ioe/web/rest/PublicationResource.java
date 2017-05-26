package com.scorelab.ioe.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.scorelab.ioe.domain.Publication;
import com.scorelab.ioe.domain.Sensor;
import com.scorelab.ioe.domain.SensorData;
import com.scorelab.ioe.nosql.StoreTypes;
import com.scorelab.ioe.repository.PublicationRepository;
import com.scorelab.ioe.repository.SensorRepository;
import com.scorelab.ioe.service.SensorDataRepositoryService;
import com.scorelab.ioe.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Null;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Publication.
 */
@RestController
@RequestMapping("/api")
public class PublicationResource {

    private final Logger log = LoggerFactory.getLogger(PublicationResource.class);

    @Inject
    private PublicationRepository publicationRepository;

   @Inject
    private SensorRepository sensorRepository;

    @Autowired
    private SensorDataRepositoryService databaseService;


    /**
     * POST  /publications : Create a new publication.
     *
     * @param publication the publication to create
     * @return the ResponseEntity with status 201 (Created) and with body the new publication, or with status 400 (Bad Request) if the publication has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/publications",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Publication> createPublication(@RequestBody Publication publication) throws URISyntaxException {
        log.debug("REST request to save Publication : {}", publication);
        if (publication.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("publication", "idexists", "A new publication cannot already have an ID")).body(null);
        }
        Publication result = publicationRepository.save(publication);
        return ResponseEntity.created(new URI("/api/publications/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("publication", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /publications : Updates an existing publication.
     *
     * @param publication the publication to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated publication,
     * or with status 400 (Bad Request) if the publication is not valid,
     * or with status 500 (Internal Server Error) if the publication couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/publications",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Publication> updatePublication(@RequestBody Publication publication) throws URISyntaxException {
        log.debug("REST request to update Publication : {}", publication);
        if (publication.getId() == null) {
            return createPublication(publication);
        }
        Publication result = publicationRepository.save(publication);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("publication", publication.getId().toString()))
            .body(result);
    }

    /**
     * GET  /publications : get all the publications.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of publications in body
     */
    @RequestMapping(value = "/publications",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Publication> getAllPublications() {
        log.debug("REST request to get all Publications");
        List<Publication> publications = publicationRepository.findAll();

        return publications;
    }

    /**
     * POST /publish/:sensorid
     * Insert sensor data to Cassandra storage
     *
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */

    @RequestMapping(value = "/publish/{sensorid}",
    	method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)

    @Timed
    public void publishData (@Valid @RequestBody SensorData sensorData){
        List<Publication> publications = publicationRepository.findAll();
        for(Publication p:publications) {
            if(p.getSensor().getSensorId()==sensorData.getSensorId()){
                ZonedDateTime utcTime = sensorData.getTimestamp().withZoneSameInstant(ZoneOffset.UTC);
                Sensor sensor = sensorRepository.findBySensorId(sensorData.getSensorId());
                // TODO - Read TTL value
                databaseService.insertData(sensorData.getSensorId(), sensorData.getData(), sensorData.getDescription(), utcTime, StoreTypes.valueOf(sensor.getStoreType()), sensorData.getTopic(), 0);
            }
        }
    }

    /**
     * GET  /publications/:id : get the "id" publication.
     *
     * @param id the id of the publication to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the publication, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/publications/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Publication> getPublication(@PathVariable Long id) {
        log.debug("REST request to get Publication : {}", id);
        Publication publication = publicationRepository.findOne(id);
        return Optional.ofNullable(publication)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /publications/:id : delete the "id" publication.
     *
     * @param id the id of the publication to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/publications/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deletePublication(@PathVariable Long id) {
        log.debug("REST request to delete Publication : {}", id);
        publicationRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("publication", id.toString())).build();
    }

}

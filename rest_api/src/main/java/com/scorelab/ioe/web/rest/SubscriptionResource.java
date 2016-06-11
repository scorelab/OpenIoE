package com.scorelab.ioe.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.scorelab.ioe.domain.Subscription;
import com.scorelab.ioe.repository.SubscriptionRepository;
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
 * REST controller for managing Subscription.
 */
@RestController
@RequestMapping("/api")
public class SubscriptionResource {

    private final Logger log = LoggerFactory.getLogger(SubscriptionResource.class);
        
    @Inject
    private SubscriptionRepository subscriptionRepository;
    
    /**
     * POST  /subscriptions : Create a new subscription.
     *
     * @param subscription the subscription to create
     * @return the ResponseEntity with status 201 (Created) and with body the new subscription, or with status 400 (Bad Request) if the subscription has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/subscriptions",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Subscription> createSubscription(@RequestBody Subscription subscription) throws URISyntaxException {
        log.debug("REST request to save Subscription : {}", subscription);
        if (subscription.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("subscription", "idexists", "A new subscription cannot already have an ID")).body(null);
        }
        Subscription result = subscriptionRepository.save(subscription);
        return ResponseEntity.created(new URI("/api/subscriptions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("subscription", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /subscriptions : Updates an existing subscription.
     *
     * @param subscription the subscription to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated subscription,
     * or with status 400 (Bad Request) if the subscription is not valid,
     * or with status 500 (Internal Server Error) if the subscription couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/subscriptions",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Subscription> updateSubscription(@RequestBody Subscription subscription) throws URISyntaxException {
        log.debug("REST request to update Subscription : {}", subscription);
        if (subscription.getId() == null) {
            return createSubscription(subscription);
        }
        Subscription result = subscriptionRepository.save(subscription);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("subscription", subscription.getId().toString()))
            .body(result);
    }

    /**
     * GET  /subscriptions : get all the subscriptions.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of subscriptions in body
     */
    @RequestMapping(value = "/subscriptions",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Subscription> getAllSubscriptions() {
        log.debug("REST request to get all Subscriptions");
        List<Subscription> subscriptions = subscriptionRepository.findAll();
        return subscriptions;
    }

    /**
     * GET  /subscriptions/:id : get the "id" subscription.
     *
     * @param id the id of the subscription to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the subscription, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/subscriptions/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Subscription> getSubscription(@PathVariable Long id) {
        log.debug("REST request to get Subscription : {}", id);
        Subscription subscription = subscriptionRepository.findOne(id);
        return Optional.ofNullable(subscription)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /subscriptions/:id : delete the "id" subscription.
     *
     * @param id the id of the subscription to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/subscriptions/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteSubscription(@PathVariable Long id) {
        log.debug("REST request to delete Subscription : {}", id);
        subscriptionRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("subscription", id.toString())).build();
    }

}

package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Activitymbr;
import com.innvo.repository.ActivitymbrRepository;
import com.innvo.repository.search.ActivitymbrSearchRepository;
import com.innvo.web.rest.util.HeaderUtil;
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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Activitymbr.
 */
@RestController
@RequestMapping("/api")
public class ActivitymbrResource {

    private final Logger log = LoggerFactory.getLogger(ActivitymbrResource.class);
        
    @Inject
    private ActivitymbrRepository activitymbrRepository;
    
    @Inject
    private ActivitymbrSearchRepository activitymbrSearchRepository;
    
    /**
     * POST  /activitymbrs : Create a new activitymbr.
     *
     * @param activitymbr the activitymbr to create
     * @return the ResponseEntity with status 201 (Created) and with body the new activitymbr, or with status 400 (Bad Request) if the activitymbr has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/activitymbrs",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Activitymbr> createActivitymbr(@RequestBody Activitymbr activitymbr) throws URISyntaxException {
        log.debug("REST request to save Activitymbr : {}", activitymbr);
        if (activitymbr.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("activitymbr", "idexists", "A new activitymbr cannot already have an ID")).body(null);
        }
        Activitymbr result = activitymbrRepository.save(activitymbr);
        activitymbrSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/activitymbrs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("activitymbr", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /activitymbrs : Updates an existing activitymbr.
     *
     * @param activitymbr the activitymbr to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated activitymbr,
     * or with status 400 (Bad Request) if the activitymbr is not valid,
     * or with status 500 (Internal Server Error) if the activitymbr couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/activitymbrs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Activitymbr> updateActivitymbr(@RequestBody Activitymbr activitymbr) throws URISyntaxException {
        log.debug("REST request to update Activitymbr : {}", activitymbr);
        if (activitymbr.getId() == null) {
            return createActivitymbr(activitymbr);
        }
        Activitymbr result = activitymbrRepository.save(activitymbr);
        activitymbrSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("activitymbr", activitymbr.getId().toString()))
            .body(result);
    }

    /**
     * GET  /activitymbrs : get all the activitymbrs.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of activitymbrs in body
     */
    @RequestMapping(value = "/activitymbrs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Activitymbr> getAllActivitymbrs() {
        log.debug("REST request to get all Activitymbrs");
        List<Activitymbr> activitymbrs = activitymbrRepository.findAll();
        return activitymbrs;
    }

    /**
     * GET  /activitymbrs/:id : get the "id" activitymbr.
     *
     * @param id the id of the activitymbr to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the activitymbr, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/activitymbrs/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Activitymbr> getActivitymbr(@PathVariable Long id) {
        log.debug("REST request to get Activitymbr : {}", id);
        Activitymbr activitymbr = activitymbrRepository.findOne(id);
        return Optional.ofNullable(activitymbr)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /activitymbrs/:id : delete the "id" activitymbr.
     *
     * @param id the id of the activitymbr to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/activitymbrs/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteActivitymbr(@PathVariable Long id) {
        log.debug("REST request to delete Activitymbr : {}", id);
        activitymbrRepository.delete(id);
        activitymbrSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("activitymbr", id.toString())).build();
    }

    /**
     * SEARCH  /_search/activitymbrs?query=:query : search for the activitymbr corresponding
     * to the query.
     *
     * @param query the query of the activitymbr search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/activitymbrs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Activitymbr> searchActivitymbrs(@RequestParam String query) {
        log.debug("REST request to search Activitymbrs for query {}", query);
        return StreamSupport
            .stream(activitymbrSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}

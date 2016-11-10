package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Requestprojectmbr;
import com.innvo.repository.RequestprojectmbrRepository;
import com.innvo.repository.search.RequestprojectmbrSearchRepository;
import com.innvo.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Requestprojectmbr.
 */
@RestController
@RequestMapping("/api")
public class RequestprojectmbrResource {

    private final Logger log = LoggerFactory.getLogger(RequestprojectmbrResource.class);
        
    @Inject
    private RequestprojectmbrRepository requestprojectmbrRepository;
    
    @Inject
    private RequestprojectmbrSearchRepository requestprojectmbrSearchRepository;
    
    /**
     * POST  /requestprojectmbrs : Create a new requestprojectmbr.
     *
     * @param requestprojectmbr the requestprojectmbr to create
     * @return the ResponseEntity with status 201 (Created) and with body the new requestprojectmbr, or with status 400 (Bad Request) if the requestprojectmbr has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/requestprojectmbrs",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Requestprojectmbr> createRequestprojectmbr(@Valid @RequestBody Requestprojectmbr requestprojectmbr) throws URISyntaxException {
        log.debug("REST request to save Requestprojectmbr : {}", requestprojectmbr);
        if (requestprojectmbr.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("requestprojectmbr", "idexists", "A new requestprojectmbr cannot already have an ID")).body(null);
        }
        Requestprojectmbr result = requestprojectmbrRepository.save(requestprojectmbr);
        requestprojectmbrSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/requestprojectmbrs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("requestprojectmbr", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /requestprojectmbrs : Updates an existing requestprojectmbr.
     *
     * @param requestprojectmbr the requestprojectmbr to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated requestprojectmbr,
     * or with status 400 (Bad Request) if the requestprojectmbr is not valid,
     * or with status 500 (Internal Server Error) if the requestprojectmbr couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/requestprojectmbrs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Requestprojectmbr> updateRequestprojectmbr(@Valid @RequestBody Requestprojectmbr requestprojectmbr) throws URISyntaxException {
        log.debug("REST request to update Requestprojectmbr : {}", requestprojectmbr);
        if (requestprojectmbr.getId() == null) {
            return createRequestprojectmbr(requestprojectmbr);
        }
        Requestprojectmbr result = requestprojectmbrRepository.save(requestprojectmbr);
        requestprojectmbrSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("requestprojectmbr", requestprojectmbr.getId().toString()))
            .body(result);
    }

    /**
     * GET  /requestprojectmbrs : get all the requestprojectmbrs.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of requestprojectmbrs in body
     */
    @RequestMapping(value = "/requestprojectmbrs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Requestprojectmbr> getAllRequestprojectmbrs() {
        log.debug("REST request to get all Requestprojectmbrs");
        List<Requestprojectmbr> requestprojectmbrs = requestprojectmbrRepository.findAll();
        return requestprojectmbrs;
    }

    /**
     * GET  /requestprojectmbrs/:id : get the "id" requestprojectmbr.
     *
     * @param id the id of the requestprojectmbr to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the requestprojectmbr, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/requestprojectmbrs/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Requestprojectmbr> getRequestprojectmbr(@PathVariable Long id) {
        log.debug("REST request to get Requestprojectmbr : {}", id);
        Requestprojectmbr requestprojectmbr = requestprojectmbrRepository.findOne(id);
        return Optional.ofNullable(requestprojectmbr)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /requestprojectmbrs/:id : delete the "id" requestprojectmbr.
     *
     * @param id the id of the requestprojectmbr to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/requestprojectmbrs/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteRequestprojectmbr(@PathVariable Long id) {
        log.debug("REST request to delete Requestprojectmbr : {}", id);
        requestprojectmbrRepository.delete(id);
        requestprojectmbrSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("requestprojectmbr", id.toString())).build();
    }

    /**
     * SEARCH  /_search/requestprojectmbrs?query=:query : search for the requestprojectmbr corresponding
     * to the query.
     *
     * @param query the query of the requestprojectmbr search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/requestprojectmbrs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Requestprojectmbr> searchRequestprojectmbrs(@RequestParam String query) {
        log.debug("REST request to search Requestprojectmbrs for query {}", query);
        return StreamSupport
            .stream(requestprojectmbrSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}

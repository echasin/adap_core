package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Strategymbr;
import com.innvo.repository.StrategymbrRepository;
import com.innvo.repository.search.StrategymbrSearchRepository;
import com.innvo.web.rest.util.HeaderUtil;
import com.innvo.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
 * REST controller for managing Strategymbr.
 */
@RestController
@RequestMapping("/api")
public class StrategymbrResource {

    private final Logger log = LoggerFactory.getLogger(StrategymbrResource.class);
        
    @Inject
    private StrategymbrRepository strategymbrRepository;
    
    @Inject
    private StrategymbrSearchRepository strategymbrSearchRepository;
    
    /**
     * POST  /strategymbrs : Create a new strategymbr.
     *
     * @param strategymbr the strategymbr to create
     * @return the ResponseEntity with status 201 (Created) and with body the new strategymbr, or with status 400 (Bad Request) if the strategymbr has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/strategymbrs",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Strategymbr> createStrategymbr(@Valid @RequestBody Strategymbr strategymbr) throws URISyntaxException {
        log.debug("REST request to save Strategymbr : {}", strategymbr);
        if (strategymbr.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("strategymbr", "idexists", "A new strategymbr cannot already have an ID")).body(null);
        }
        Strategymbr result = strategymbrRepository.save(strategymbr);
        strategymbrSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/strategymbrs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("strategymbr", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /strategymbrs : Updates an existing strategymbr.
     *
     * @param strategymbr the strategymbr to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated strategymbr,
     * or with status 400 (Bad Request) if the strategymbr is not valid,
     * or with status 500 (Internal Server Error) if the strategymbr couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/strategymbrs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Strategymbr> updateStrategymbr(@Valid @RequestBody Strategymbr strategymbr) throws URISyntaxException {
        log.debug("REST request to update Strategymbr : {}", strategymbr);
        if (strategymbr.getId() == null) {
            return createStrategymbr(strategymbr);
        }
        Strategymbr result = strategymbrRepository.save(strategymbr);
        strategymbrSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("strategymbr", strategymbr.getId().toString()))
            .body(result);
    }

    /**
     * GET  /strategymbrs : get all the strategymbrs.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of strategymbrs in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/strategymbrs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Strategymbr>> getAllStrategymbrs(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Strategymbrs");
        Page<Strategymbr> page = strategymbrRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/strategymbrs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /strategymbrs/:id : get the "id" strategymbr.
     *
     * @param id the id of the strategymbr to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the strategymbr, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/strategymbrs/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Strategymbr> getStrategymbr(@PathVariable Long id) {
        log.debug("REST request to get Strategymbr : {}", id);
        Strategymbr strategymbr = strategymbrRepository.findOne(id);
        return Optional.ofNullable(strategymbr)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /strategymbrs/:id : delete the "id" strategymbr.
     *
     * @param id the id of the strategymbr to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/strategymbrs/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteStrategymbr(@PathVariable Long id) {
        log.debug("REST request to delete Strategymbr : {}", id);
        strategymbrRepository.delete(id);
        strategymbrSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("strategymbr", id.toString())).build();
    }

    /**
     * SEARCH  /_search/strategymbrs?query=:query : search for the strategymbr corresponding
     * to the query.
     *
     * @param query the query of the strategymbr search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/strategymbrs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Strategymbr>> searchStrategymbrs(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Strategymbrs for query {}", query);
        Page<Strategymbr> page = strategymbrSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/strategymbrs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}

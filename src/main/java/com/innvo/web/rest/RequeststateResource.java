package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Requeststate;
import com.innvo.repository.RequeststateRepository;
import com.innvo.repository.search.RequeststateSearchRepository;
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
 * REST controller for managing Requeststate.
 */
@RestController
@RequestMapping("/api")
public class RequeststateResource {

    private final Logger log = LoggerFactory.getLogger(RequeststateResource.class);
        
    @Inject
    private RequeststateRepository requeststateRepository;
    
    @Inject
    private RequeststateSearchRepository requeststateSearchRepository;
    
    /**
     * POST  /requeststates : Create a new requeststate.
     *
     * @param requeststate the requeststate to create
     * @return the ResponseEntity with status 201 (Created) and with body the new requeststate, or with status 400 (Bad Request) if the requeststate has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/requeststates",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Requeststate> createRequeststate(@Valid @RequestBody Requeststate requeststate) throws URISyntaxException {
        log.debug("REST request to save Requeststate : {}", requeststate);
        if (requeststate.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("requeststate", "idexists", "A new requeststate cannot already have an ID")).body(null);
        }
        Requeststate result = requeststateRepository.save(requeststate);
        requeststateSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/requeststates/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("requeststate", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /requeststates : Updates an existing requeststate.
     *
     * @param requeststate the requeststate to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated requeststate,
     * or with status 400 (Bad Request) if the requeststate is not valid,
     * or with status 500 (Internal Server Error) if the requeststate couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/requeststates",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Requeststate> updateRequeststate(@Valid @RequestBody Requeststate requeststate) throws URISyntaxException {
        log.debug("REST request to update Requeststate : {}", requeststate);
        if (requeststate.getId() == null) {
            return createRequeststate(requeststate);
        }
        Requeststate result = requeststateRepository.save(requeststate);
        requeststateSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("requeststate", requeststate.getId().toString()))
            .body(result);
    }

    /**
     * GET  /requeststates : get all the requeststates.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of requeststates in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/requeststates",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Requeststate>> getAllRequeststates(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Requeststates");
        Page<Requeststate> page = requeststateRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/requeststates");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /requeststates/:id : get the "id" requeststate.
     *
     * @param id the id of the requeststate to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the requeststate, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/requeststates/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Requeststate> getRequeststate(@PathVariable Long id) {
        log.debug("REST request to get Requeststate : {}", id);
        Requeststate requeststate = requeststateRepository.findOne(id);
        return Optional.ofNullable(requeststate)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /requeststates/:id : delete the "id" requeststate.
     *
     * @param id the id of the requeststate to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/requeststates/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteRequeststate(@PathVariable Long id) {
        log.debug("REST request to delete Requeststate : {}", id);
        requeststateRepository.delete(id);
        requeststateSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("requeststate", id.toString())).build();
    }

    /**
     * SEARCH  /_search/requeststates?query=:query : search for the requeststate corresponding
     * to the query.
     *
     * @param query the query of the requeststate search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/requeststates",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Requeststate>> searchRequeststates(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Requeststates for query {}", query);
        Page<Requeststate> page = requeststateSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/requeststates");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}

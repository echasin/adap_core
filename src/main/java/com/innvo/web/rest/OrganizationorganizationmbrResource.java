package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Organizationorganizationmbr;
import com.innvo.repository.OrganizationorganizationmbrRepository;
import com.innvo.repository.search.OrganizationorganizationmbrSearchRepository;
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
 * REST controller for managing Organizationorganizationmbr.
 */
@RestController
@RequestMapping("/api")
public class OrganizationorganizationmbrResource {

    private final Logger log = LoggerFactory.getLogger(OrganizationorganizationmbrResource.class);
        
    @Inject
    private OrganizationorganizationmbrRepository organizationorganizationmbrRepository;
    
    @Inject
    private OrganizationorganizationmbrSearchRepository organizationorganizationmbrSearchRepository;
    
    /**
     * POST  /organizationorganizationmbrs : Create a new organizationorganizationmbr.
     *
     * @param organizationorganizationmbr the organizationorganizationmbr to create
     * @return the ResponseEntity with status 201 (Created) and with body the new organizationorganizationmbr, or with status 400 (Bad Request) if the organizationorganizationmbr has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/organizationorganizationmbrs",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Organizationorganizationmbr> createOrganizationorganizationmbr(@Valid @RequestBody Organizationorganizationmbr organizationorganizationmbr) throws URISyntaxException {
        log.debug("REST request to save Organizationorganizationmbr : {}", organizationorganizationmbr);
        if (organizationorganizationmbr.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("organizationorganizationmbr", "idexists", "A new organizationorganizationmbr cannot already have an ID")).body(null);
        }
        Organizationorganizationmbr result = organizationorganizationmbrRepository.save(organizationorganizationmbr);
        organizationorganizationmbrSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/organizationorganizationmbrs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("organizationorganizationmbr", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /organizationorganizationmbrs : Updates an existing organizationorganizationmbr.
     *
     * @param organizationorganizationmbr the organizationorganizationmbr to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated organizationorganizationmbr,
     * or with status 400 (Bad Request) if the organizationorganizationmbr is not valid,
     * or with status 500 (Internal Server Error) if the organizationorganizationmbr couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/organizationorganizationmbrs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Organizationorganizationmbr> updateOrganizationorganizationmbr(@Valid @RequestBody Organizationorganizationmbr organizationorganizationmbr) throws URISyntaxException {
        log.debug("REST request to update Organizationorganizationmbr : {}", organizationorganizationmbr);
        if (organizationorganizationmbr.getId() == null) {
            return createOrganizationorganizationmbr(organizationorganizationmbr);
        }
        Organizationorganizationmbr result = organizationorganizationmbrRepository.save(organizationorganizationmbr);
        organizationorganizationmbrSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("organizationorganizationmbr", organizationorganizationmbr.getId().toString()))
            .body(result);
    }

    /**
     * GET  /organizationorganizationmbrs : get all the organizationorganizationmbrs.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of organizationorganizationmbrs in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/organizationorganizationmbrs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Organizationorganizationmbr>> getAllOrganizationorganizationmbrs(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Organizationorganizationmbrs");
        Page<Organizationorganizationmbr> page = organizationorganizationmbrRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/organizationorganizationmbrs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /organizationorganizationmbrs/:id : get the "id" organizationorganizationmbr.
     *
     * @param id the id of the organizationorganizationmbr to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the organizationorganizationmbr, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/organizationorganizationmbrs/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Organizationorganizationmbr> getOrganizationorganizationmbr(@PathVariable Long id) {
        log.debug("REST request to get Organizationorganizationmbr : {}", id);
        Organizationorganizationmbr organizationorganizationmbr = organizationorganizationmbrRepository.findOneWithEagerRelationships(id);
        return Optional.ofNullable(organizationorganizationmbr)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /organizationorganizationmbrs/:id : delete the "id" organizationorganizationmbr.
     *
     * @param id the id of the organizationorganizationmbr to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/organizationorganizationmbrs/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteOrganizationorganizationmbr(@PathVariable Long id) {
        log.debug("REST request to delete Organizationorganizationmbr : {}", id);
        organizationorganizationmbrRepository.delete(id);
        organizationorganizationmbrSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("organizationorganizationmbr", id.toString())).build();
    }

    /**
     * SEARCH  /_search/organizationorganizationmbrs?query=:query : search for the organizationorganizationmbr corresponding
     * to the query.
     *
     * @param query the query of the organizationorganizationmbr search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/organizationorganizationmbrs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Organizationorganizationmbr>> searchOrganizationorganizationmbrs(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Organizationorganizationmbrs for query {}", query);
        Page<Organizationorganizationmbr> page = organizationorganizationmbrSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/organizationorganizationmbrs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}

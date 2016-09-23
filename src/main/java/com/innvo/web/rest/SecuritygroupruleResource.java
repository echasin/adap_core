package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Securitygrouprule;
import com.innvo.repository.SecuritygroupruleRepository;
import com.innvo.repository.search.SecuritygroupruleSearchRepository;
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
 * REST controller for managing Securitygrouprule.
 */
@RestController
@RequestMapping("/api")
public class SecuritygroupruleResource {

    private final Logger log = LoggerFactory.getLogger(SecuritygroupruleResource.class);
        
    @Inject
    private SecuritygroupruleRepository securitygroupruleRepository;
    
    @Inject
    private SecuritygroupruleSearchRepository securitygroupruleSearchRepository;
    
    /**
     * POST  /securitygrouprules : Create a new securitygrouprule.
     *
     * @param securitygrouprule the securitygrouprule to create
     * @return the ResponseEntity with status 201 (Created) and with body the new securitygrouprule, or with status 400 (Bad Request) if the securitygrouprule has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/securitygrouprules",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Securitygrouprule> createSecuritygrouprule(@Valid @RequestBody Securitygrouprule securitygrouprule) throws URISyntaxException {
        log.debug("REST request to save Securitygrouprule : {}", securitygrouprule);
        if (securitygrouprule.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("securitygrouprule", "idexists", "A new securitygrouprule cannot already have an ID")).body(null);
        }
        Securitygrouprule result = securitygroupruleRepository.save(securitygrouprule);
        securitygroupruleSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/securitygrouprules/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("securitygrouprule", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /securitygrouprules : Updates an existing securitygrouprule.
     *
     * @param securitygrouprule the securitygrouprule to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated securitygrouprule,
     * or with status 400 (Bad Request) if the securitygrouprule is not valid,
     * or with status 500 (Internal Server Error) if the securitygrouprule couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/securitygrouprules",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Securitygrouprule> updateSecuritygrouprule(@Valid @RequestBody Securitygrouprule securitygrouprule) throws URISyntaxException {
        log.debug("REST request to update Securitygrouprule : {}", securitygrouprule);
        if (securitygrouprule.getId() == null) {
            return createSecuritygrouprule(securitygrouprule);
        }
        Securitygrouprule result = securitygroupruleRepository.save(securitygrouprule);
        securitygroupruleSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("securitygrouprule", securitygrouprule.getId().toString()))
            .body(result);
    }

    /**
     * GET  /securitygrouprules : get all the securitygrouprules.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of securitygrouprules in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/securitygrouprules",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Securitygrouprule>> getAllSecuritygrouprules(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Securitygrouprules");
        Page<Securitygrouprule> page = securitygroupruleRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/securitygrouprules");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /securitygrouprules/:id : get the "id" securitygrouprule.
     *
     * @param id the id of the securitygrouprule to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the securitygrouprule, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/securitygrouprules/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Securitygrouprule> getSecuritygrouprule(@PathVariable Long id) {
        log.debug("REST request to get Securitygrouprule : {}", id);
        Securitygrouprule securitygrouprule = securitygroupruleRepository.findOne(id);
        return Optional.ofNullable(securitygrouprule)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /securitygrouprules/:id : delete the "id" securitygrouprule.
     *
     * @param id the id of the securitygrouprule to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/securitygrouprules/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteSecuritygrouprule(@PathVariable Long id) {
        log.debug("REST request to delete Securitygrouprule : {}", id);
        securitygroupruleRepository.delete(id);
        securitygroupruleSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("securitygrouprule", id.toString())).build();
    }

    /**
     * SEARCH  /_search/securitygrouprules?query=:query : search for the securitygrouprule corresponding
     * to the query.
     *
     * @param query the query of the securitygrouprule search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/securitygrouprules",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Securitygrouprule>> searchSecuritygrouprules(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Securitygrouprules for query {}", query);
        Page<Securitygrouprule> page = securitygroupruleSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/securitygrouprules");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}

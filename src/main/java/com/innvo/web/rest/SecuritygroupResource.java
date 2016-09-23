package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Securitygroup;
import com.innvo.repository.SecuritygroupRepository;
import com.innvo.repository.search.SecuritygroupSearchRepository;
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
 * REST controller for managing Securitygroup.
 */
@RestController
@RequestMapping("/api")
public class SecuritygroupResource {

    private final Logger log = LoggerFactory.getLogger(SecuritygroupResource.class);
        
    @Inject
    private SecuritygroupRepository securitygroupRepository;
    
    @Inject
    private SecuritygroupSearchRepository securitygroupSearchRepository;
    
    /**
     * POST  /securitygroups : Create a new securitygroup.
     *
     * @param securitygroup the securitygroup to create
     * @return the ResponseEntity with status 201 (Created) and with body the new securitygroup, or with status 400 (Bad Request) if the securitygroup has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/securitygroups",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Securitygroup> createSecuritygroup(@Valid @RequestBody Securitygroup securitygroup) throws URISyntaxException {
        log.debug("REST request to save Securitygroup : {}", securitygroup);
        if (securitygroup.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("securitygroup", "idexists", "A new securitygroup cannot already have an ID")).body(null);
        }
        Securitygroup result = securitygroupRepository.save(securitygroup);
        securitygroupSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/securitygroups/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("securitygroup", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /securitygroups : Updates an existing securitygroup.
     *
     * @param securitygroup the securitygroup to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated securitygroup,
     * or with status 400 (Bad Request) if the securitygroup is not valid,
     * or with status 500 (Internal Server Error) if the securitygroup couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/securitygroups",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Securitygroup> updateSecuritygroup(@Valid @RequestBody Securitygroup securitygroup) throws URISyntaxException {
        log.debug("REST request to update Securitygroup : {}", securitygroup);
        if (securitygroup.getId() == null) {
            return createSecuritygroup(securitygroup);
        }
        Securitygroup result = securitygroupRepository.save(securitygroup);
        securitygroupSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("securitygroup", securitygroup.getId().toString()))
            .body(result);
    }

    /**
     * GET  /securitygroups : get all the securitygroups.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of securitygroups in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/securitygroups",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Securitygroup>> getAllSecuritygroups(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Securitygroups");
        Page<Securitygroup> page = securitygroupRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/securitygroups");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /securitygroups/:id : get the "id" securitygroup.
     *
     * @param id the id of the securitygroup to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the securitygroup, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/securitygroups/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Securitygroup> getSecuritygroup(@PathVariable Long id) {
        log.debug("REST request to get Securitygroup : {}", id);
        Securitygroup securitygroup = securitygroupRepository.findOne(id);
        return Optional.ofNullable(securitygroup)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /securitygroups/:id : delete the "id" securitygroup.
     *
     * @param id the id of the securitygroup to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/securitygroups/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteSecuritygroup(@PathVariable Long id) {
        log.debug("REST request to delete Securitygroup : {}", id);
        securitygroupRepository.delete(id);
        securitygroupSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("securitygroup", id.toString())).build();
    }

    /**
     * SEARCH  /_search/securitygroups?query=:query : search for the securitygroup corresponding
     * to the query.
     *
     * @param query the query of the securitygroup search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/securitygroups",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Securitygroup>> searchSecuritygroups(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Securitygroups for query {}", query);
        Page<Securitygroup> page = securitygroupSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/securitygroups");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}

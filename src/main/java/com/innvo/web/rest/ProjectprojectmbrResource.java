package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Projectprojectmbr;
import com.innvo.repository.ProjectprojectmbrRepository;
import com.innvo.repository.search.ProjectprojectmbrSearchRepository;
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
 * REST controller for managing Projectprojectmbr.
 */
@RestController
@RequestMapping("/api")
public class ProjectprojectmbrResource {

    private final Logger log = LoggerFactory.getLogger(ProjectprojectmbrResource.class);
        
    @Inject
    private ProjectprojectmbrRepository projectprojectmbrRepository;
    
    @Inject
    private ProjectprojectmbrSearchRepository projectprojectmbrSearchRepository;
    
    /**
     * POST  /projectprojectmbrs : Create a new projectprojectmbr.
     *
     * @param projectprojectmbr the projectprojectmbr to create
     * @return the ResponseEntity with status 201 (Created) and with body the new projectprojectmbr, or with status 400 (Bad Request) if the projectprojectmbr has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/projectprojectmbrs",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Projectprojectmbr> createProjectprojectmbr(@Valid @RequestBody Projectprojectmbr projectprojectmbr) throws URISyntaxException {
        log.debug("REST request to save Projectprojectmbr : {}", projectprojectmbr);
        if (projectprojectmbr.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("projectprojectmbr", "idexists", "A new projectprojectmbr cannot already have an ID")).body(null);
        }
        Projectprojectmbr result = projectprojectmbrRepository.save(projectprojectmbr);
        projectprojectmbrSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/projectprojectmbrs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("projectprojectmbr", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /projectprojectmbrs : Updates an existing projectprojectmbr.
     *
     * @param projectprojectmbr the projectprojectmbr to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated projectprojectmbr,
     * or with status 400 (Bad Request) if the projectprojectmbr is not valid,
     * or with status 500 (Internal Server Error) if the projectprojectmbr couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/projectprojectmbrs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Projectprojectmbr> updateProjectprojectmbr(@Valid @RequestBody Projectprojectmbr projectprojectmbr) throws URISyntaxException {
        log.debug("REST request to update Projectprojectmbr : {}", projectprojectmbr);
        if (projectprojectmbr.getId() == null) {
            return createProjectprojectmbr(projectprojectmbr);
        }
        Projectprojectmbr result = projectprojectmbrRepository.save(projectprojectmbr);
        projectprojectmbrSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("projectprojectmbr", projectprojectmbr.getId().toString()))
            .body(result);
    }

    /**
     * GET  /projectprojectmbrs : get all the projectprojectmbrs.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of projectprojectmbrs in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/projectprojectmbrs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Projectprojectmbr>> getAllProjectprojectmbrs(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Projectprojectmbrs");
        Page<Projectprojectmbr> page = projectprojectmbrRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/projectprojectmbrs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /projectprojectmbrs/:id : get the "id" projectprojectmbr.
     *
     * @param id the id of the projectprojectmbr to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the projectprojectmbr, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/projectprojectmbrs/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Projectprojectmbr> getProjectprojectmbr(@PathVariable Long id) {
        log.debug("REST request to get Projectprojectmbr : {}", id);
        Projectprojectmbr projectprojectmbr = projectprojectmbrRepository.findOneWithEagerRelationships(id);
        return Optional.ofNullable(projectprojectmbr)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /projectprojectmbrs/:id : delete the "id" projectprojectmbr.
     *
     * @param id the id of the projectprojectmbr to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/projectprojectmbrs/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteProjectprojectmbr(@PathVariable Long id) {
        log.debug("REST request to delete Projectprojectmbr : {}", id);
        projectprojectmbrRepository.delete(id);
        projectprojectmbrSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("projectprojectmbr", id.toString())).build();
    }

    /**
     * SEARCH  /_search/projectprojectmbrs?query=:query : search for the projectprojectmbr corresponding
     * to the query.
     *
     * @param query the query of the projectprojectmbr search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/projectprojectmbrs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Projectprojectmbr>> searchProjectprojectmbrs(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Projectprojectmbrs for query {}", query);
        Page<Projectprojectmbr> page = projectprojectmbrSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/projectprojectmbrs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}

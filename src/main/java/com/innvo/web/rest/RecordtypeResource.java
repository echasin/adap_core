package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Recordtype;
import com.innvo.repository.RecordtypeRepository;
import com.innvo.repository.search.RecordtypeSearchRepository;
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
 * REST controller for managing Recordtype.
 */
@RestController
@RequestMapping("/api")
public class RecordtypeResource {

    private final Logger log = LoggerFactory.getLogger(RecordtypeResource.class);
        
    @Inject
    private RecordtypeRepository recordtypeRepository;
    
    @Inject
    private RecordtypeSearchRepository recordtypeSearchRepository;
    
    /**
     * POST  /recordtypes : Create a new recordtype.
     *
     * @param recordtype the recordtype to create
     * @return the ResponseEntity with status 201 (Created) and with body the new recordtype, or with status 400 (Bad Request) if the recordtype has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/recordtypes",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Recordtype> createRecordtype(@Valid @RequestBody Recordtype recordtype) throws URISyntaxException {
        log.debug("REST request to save Recordtype : {}", recordtype);
        if (recordtype.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("recordtype", "idexists", "A new recordtype cannot already have an ID")).body(null);
        }
        Recordtype result = recordtypeRepository.save(recordtype);
        recordtypeSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/recordtypes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("recordtype", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /recordtypes : Updates an existing recordtype.
     *
     * @param recordtype the recordtype to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated recordtype,
     * or with status 400 (Bad Request) if the recordtype is not valid,
     * or with status 500 (Internal Server Error) if the recordtype couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/recordtypes",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Recordtype> updateRecordtype(@Valid @RequestBody Recordtype recordtype) throws URISyntaxException {
        log.debug("REST request to update Recordtype : {}", recordtype);
        if (recordtype.getId() == null) {
            return createRecordtype(recordtype);
        }
        Recordtype result = recordtypeRepository.save(recordtype);
        recordtypeSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("recordtype", recordtype.getId().toString()))
            .body(result);
    }

    /**
     * GET  /recordtypes : get all the recordtypes.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of recordtypes in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/recordtypes",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Recordtype>> getAllRecordtypes(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Recordtypes");
        Page<Recordtype> page = recordtypeRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/recordtypes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /recordtypes/:id : get the "id" recordtype.
     *
     * @param id the id of the recordtype to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the recordtype, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/recordtypes/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Recordtype> getRecordtype(@PathVariable Long id) {
        log.debug("REST request to get Recordtype : {}", id);
        Recordtype recordtype = recordtypeRepository.findOne(id);
        return Optional.ofNullable(recordtype)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /recordtypes/:id : delete the "id" recordtype.
     *
     * @param id the id of the recordtype to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/recordtypes/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteRecordtype(@PathVariable Long id) {
        log.debug("REST request to delete Recordtype : {}", id);
        recordtypeRepository.delete(id);
        recordtypeSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("recordtype", id.toString())).build();
    }

    /**
     * SEARCH  /_search/recordtypes?query=:query : search for the recordtype corresponding
     * to the query.
     *
     * @param query the query of the recordtype search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/recordtypes",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Recordtype>> searchRecordtypes(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Recordtypes for query {}", query);
        Page<Recordtype> page = recordtypeSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/recordtypes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}

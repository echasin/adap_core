package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Tagmbr;
import com.innvo.repository.TagmbrRepository;
import com.innvo.repository.search.TagmbrSearchRepository;
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
 * REST controller for managing Tagmbr.
 */
@RestController
@RequestMapping("/api")
public class TagmbrResource {

    private final Logger log = LoggerFactory.getLogger(TagmbrResource.class);
        
    @Inject
    private TagmbrRepository tagmbrRepository;
    
    @Inject
    private TagmbrSearchRepository tagmbrSearchRepository;
    
    /**
     * POST  /tagmbrs : Create a new tagmbr.
     *
     * @param tagmbr the tagmbr to create
     * @return the ResponseEntity with status 201 (Created) and with body the new tagmbr, or with status 400 (Bad Request) if the tagmbr has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/tagmbrs",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Tagmbr> createTagmbr(@Valid @RequestBody Tagmbr tagmbr) throws URISyntaxException {
        log.debug("REST request to save Tagmbr : {}", tagmbr);
        if (tagmbr.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("tagmbr", "idexists", "A new tagmbr cannot already have an ID")).body(null);
        }
        Tagmbr result = tagmbrRepository.save(tagmbr);
        tagmbrSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/tagmbrs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("tagmbr", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /tagmbrs : Updates an existing tagmbr.
     *
     * @param tagmbr the tagmbr to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated tagmbr,
     * or with status 400 (Bad Request) if the tagmbr is not valid,
     * or with status 500 (Internal Server Error) if the tagmbr couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/tagmbrs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Tagmbr> updateTagmbr(@Valid @RequestBody Tagmbr tagmbr) throws URISyntaxException {
        log.debug("REST request to update Tagmbr : {}", tagmbr);
        if (tagmbr.getId() == null) {
            return createTagmbr(tagmbr);
        }
        Tagmbr result = tagmbrRepository.save(tagmbr);
        tagmbrSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("tagmbr", tagmbr.getId().toString()))
            .body(result);
    }

    /**
     * GET  /tagmbrs : get all the tagmbrs.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of tagmbrs in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/tagmbrs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Tagmbr>> getAllTagmbrs(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Tagmbrs");
        Page<Tagmbr> page = tagmbrRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/tagmbrs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /tagmbrs/:id : get the "id" tagmbr.
     *
     * @param id the id of the tagmbr to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the tagmbr, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/tagmbrs/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Tagmbr> getTagmbr(@PathVariable Long id) {
        log.debug("REST request to get Tagmbr : {}", id);
        Tagmbr tagmbr = tagmbrRepository.findOne(id);
        return Optional.ofNullable(tagmbr)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /tagmbrs/:id : delete the "id" tagmbr.
     *
     * @param id the id of the tagmbr to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/tagmbrs/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteTagmbr(@PathVariable Long id) {
        log.debug("REST request to delete Tagmbr : {}", id);
        tagmbrRepository.delete(id);
        tagmbrSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("tagmbr", id.toString())).build();
    }

    /**
     * SEARCH  /_search/tagmbrs?query=:query : search for the tagmbr corresponding
     * to the query.
     *
     * @param query the query of the tagmbr search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/tagmbrs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Tagmbr>> searchTagmbrs(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Tagmbrs for query {}", query);
        Page<Tagmbr> page = tagmbrSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/tagmbrs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}

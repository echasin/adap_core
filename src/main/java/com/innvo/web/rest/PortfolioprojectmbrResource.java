package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Portfolioprojectmbr;
import com.innvo.repository.PortfolioprojectmbrRepository;
import com.innvo.repository.search.PortfolioprojectmbrSearchRepository;
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
 * REST controller for managing Portfolioprojectmbr.
 */
@RestController
@RequestMapping("/api")
public class PortfolioprojectmbrResource {

    private final Logger log = LoggerFactory.getLogger(PortfolioprojectmbrResource.class);
        
    @Inject
    private PortfolioprojectmbrRepository portfolioprojectmbrRepository;
    
    @Inject
    private PortfolioprojectmbrSearchRepository portfolioprojectmbrSearchRepository;
    
    /**
     * POST  /portfolioprojectmbrs : Create a new portfolioprojectmbr.
     *
     * @param portfolioprojectmbr the portfolioprojectmbr to create
     * @return the ResponseEntity with status 201 (Created) and with body the new portfolioprojectmbr, or with status 400 (Bad Request) if the portfolioprojectmbr has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/portfolioprojectmbrs",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Portfolioprojectmbr> createPortfolioprojectmbr(@Valid @RequestBody Portfolioprojectmbr portfolioprojectmbr) throws URISyntaxException {
        log.debug("REST request to save Portfolioprojectmbr : {}", portfolioprojectmbr);
        if (portfolioprojectmbr.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("portfolioprojectmbr", "idexists", "A new portfolioprojectmbr cannot already have an ID")).body(null);
        }
        Portfolioprojectmbr result = portfolioprojectmbrRepository.save(portfolioprojectmbr);
        portfolioprojectmbrSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/portfolioprojectmbrs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("portfolioprojectmbr", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /portfolioprojectmbrs : Updates an existing portfolioprojectmbr.
     *
     * @param portfolioprojectmbr the portfolioprojectmbr to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated portfolioprojectmbr,
     * or with status 400 (Bad Request) if the portfolioprojectmbr is not valid,
     * or with status 500 (Internal Server Error) if the portfolioprojectmbr couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/portfolioprojectmbrs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Portfolioprojectmbr> updatePortfolioprojectmbr(@Valid @RequestBody Portfolioprojectmbr portfolioprojectmbr) throws URISyntaxException {
        log.debug("REST request to update Portfolioprojectmbr : {}", portfolioprojectmbr);
        if (portfolioprojectmbr.getId() == null) {
            return createPortfolioprojectmbr(portfolioprojectmbr);
        }
        Portfolioprojectmbr result = portfolioprojectmbrRepository.save(portfolioprojectmbr);
        portfolioprojectmbrSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("portfolioprojectmbr", portfolioprojectmbr.getId().toString()))
            .body(result);
    }

    /**
     * GET  /portfolioprojectmbrs : get all the portfolioprojectmbrs.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of portfolioprojectmbrs in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/portfolioprojectmbrs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Portfolioprojectmbr>> getAllPortfolioprojectmbrs(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Portfolioprojectmbrs");
        Page<Portfolioprojectmbr> page = portfolioprojectmbrRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/portfolioprojectmbrs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /portfolioprojectmbrs/:id : get the "id" portfolioprojectmbr.
     *
     * @param id the id of the portfolioprojectmbr to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the portfolioprojectmbr, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/portfolioprojectmbrs/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Portfolioprojectmbr> getPortfolioprojectmbr(@PathVariable Long id) {
        log.debug("REST request to get Portfolioprojectmbr : {}", id);
        Portfolioprojectmbr portfolioprojectmbr = portfolioprojectmbrRepository.findOne(id);
        return Optional.ofNullable(portfolioprojectmbr)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /portfolioprojectmbrs/:id : delete the "id" portfolioprojectmbr.
     *
     * @param id the id of the portfolioprojectmbr to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/portfolioprojectmbrs/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deletePortfolioprojectmbr(@PathVariable Long id) {
        log.debug("REST request to delete Portfolioprojectmbr : {}", id);
        portfolioprojectmbrRepository.delete(id);
        portfolioprojectmbrSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("portfolioprojectmbr", id.toString())).build();
    }

    /**
     * SEARCH  /_search/portfolioprojectmbrs?query=:query : search for the portfolioprojectmbr corresponding
     * to the query.
     *
     * @param query the query of the portfolioprojectmbr search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/portfolioprojectmbrs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Portfolioprojectmbr>> searchPortfolioprojectmbrs(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Portfolioprojectmbrs for query {}", query);
        Page<Portfolioprojectmbr> page = portfolioprojectmbrSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/portfolioprojectmbrs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    

    

}

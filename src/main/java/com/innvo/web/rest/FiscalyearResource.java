package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Fiscalyear;
import com.innvo.repository.FiscalyearRepository;
import com.innvo.repository.search.FiscalyearSearchRepository;
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
 * REST controller for managing Fiscalyear.
 */
@RestController
@RequestMapping("/api")
public class FiscalyearResource {

    private final Logger log = LoggerFactory.getLogger(FiscalyearResource.class);
        
    @Inject
    private FiscalyearRepository fiscalyearRepository;
    
    @Inject
    private FiscalyearSearchRepository fiscalyearSearchRepository;
    
    /**
     * POST  /fiscalyears : Create a new fiscalyear.
     *
     * @param fiscalyear the fiscalyear to create
     * @return the ResponseEntity with status 201 (Created) and with body the new fiscalyear, or with status 400 (Bad Request) if the fiscalyear has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/fiscalyears",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Fiscalyear> createFiscalyear(@Valid @RequestBody Fiscalyear fiscalyear) throws URISyntaxException {
        log.debug("REST request to save Fiscalyear : {}", fiscalyear);
        if (fiscalyear.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("fiscalyear", "idexists", "A new fiscalyear cannot already have an ID")).body(null);
        }
        Fiscalyear result = fiscalyearRepository.save(fiscalyear);
        fiscalyearSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/fiscalyears/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("fiscalyear", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /fiscalyears : Updates an existing fiscalyear.
     *
     * @param fiscalyear the fiscalyear to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated fiscalyear,
     * or with status 400 (Bad Request) if the fiscalyear is not valid,
     * or with status 500 (Internal Server Error) if the fiscalyear couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/fiscalyears",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Fiscalyear> updateFiscalyear(@Valid @RequestBody Fiscalyear fiscalyear) throws URISyntaxException {
        log.debug("REST request to update Fiscalyear : {}", fiscalyear);
        if (fiscalyear.getId() == null) {
            return createFiscalyear(fiscalyear);
        }
        Fiscalyear result = fiscalyearRepository.save(fiscalyear);
        fiscalyearSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("fiscalyear", fiscalyear.getId().toString()))
            .body(result);
    }

    /**
     * GET  /fiscalyears : get all the fiscalyears.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of fiscalyears in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/fiscalyears",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Fiscalyear>> getAllFiscalyears(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Fiscalyears");
        Page<Fiscalyear> page = fiscalyearRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/fiscalyears");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /fiscalyears/:id : get the "id" fiscalyear.
     *
     * @param id the id of the fiscalyear to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the fiscalyear, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/fiscalyears/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Fiscalyear> getFiscalyear(@PathVariable Long id) {
        log.debug("REST request to get Fiscalyear : {}", id);
        Fiscalyear fiscalyear = fiscalyearRepository.findOne(id);
        return Optional.ofNullable(fiscalyear)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /fiscalyears/:id : delete the "id" fiscalyear.
     *
     * @param id the id of the fiscalyear to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/fiscalyears/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteFiscalyear(@PathVariable Long id) {
        log.debug("REST request to delete Fiscalyear : {}", id);
        fiscalyearRepository.delete(id);
        fiscalyearSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("fiscalyear", id.toString())).build();
    }

    /**
     * SEARCH  /_search/fiscalyears?query=:query : search for the fiscalyear corresponding
     * to the query.
     *
     * @param query the query of the fiscalyear search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/fiscalyears",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Fiscalyear>> searchFiscalyears(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Fiscalyears for query {}", query);
        Page<Fiscalyear> page = fiscalyearSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/fiscalyears");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}

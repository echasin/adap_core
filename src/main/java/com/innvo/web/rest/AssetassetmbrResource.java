package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Assetassetmbr;
import com.innvo.repository.AssetassetmbrRepository;
import com.innvo.repository.search.AssetassetmbrSearchRepository;
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
 * REST controller for managing Assetassetmbr.
 */
@RestController
@RequestMapping("/api")
public class AssetassetmbrResource {

    private final Logger log = LoggerFactory.getLogger(AssetassetmbrResource.class);
        
    @Inject
    private AssetassetmbrRepository assetassetmbrRepository;
    
    @Inject
    private AssetassetmbrSearchRepository assetassetmbrSearchRepository;
    
    /**
     * POST  /assetassetmbrs : Create a new assetassetmbr.
     *
     * @param assetassetmbr the assetassetmbr to create
     * @return the ResponseEntity with status 201 (Created) and with body the new assetassetmbr, or with status 400 (Bad Request) if the assetassetmbr has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/assetassetmbrs",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Assetassetmbr> createAssetassetmbr(@Valid @RequestBody Assetassetmbr assetassetmbr) throws URISyntaxException {
        log.debug("REST request to save Assetassetmbr : {}", assetassetmbr);
        if (assetassetmbr.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("assetassetmbr", "idexists", "A new assetassetmbr cannot already have an ID")).body(null);
        }
        Assetassetmbr result = assetassetmbrRepository.save(assetassetmbr);
        assetassetmbrSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/assetassetmbrs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("assetassetmbr", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /assetassetmbrs : Updates an existing assetassetmbr.
     *
     * @param assetassetmbr the assetassetmbr to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated assetassetmbr,
     * or with status 400 (Bad Request) if the assetassetmbr is not valid,
     * or with status 500 (Internal Server Error) if the assetassetmbr couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/assetassetmbrs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Assetassetmbr> updateAssetassetmbr(@Valid @RequestBody Assetassetmbr assetassetmbr) throws URISyntaxException {
        log.debug("REST request to update Assetassetmbr : {}", assetassetmbr);
        if (assetassetmbr.getId() == null) {
            return createAssetassetmbr(assetassetmbr);
        }
        Assetassetmbr result = assetassetmbrRepository.save(assetassetmbr);
        assetassetmbrSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("assetassetmbr", assetassetmbr.getId().toString()))
            .body(result);
    }

    /**
     * GET  /assetassetmbrs : get all the assetassetmbrs.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of assetassetmbrs in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/assetassetmbrs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Assetassetmbr>> getAllAssetassetmbrs(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Assetassetmbrs");
        Page<Assetassetmbr> page = assetassetmbrRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/assetassetmbrs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /assetassetmbrs/:id : get the "id" assetassetmbr.
     *
     * @param id the id of the assetassetmbr to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the assetassetmbr, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/assetassetmbrs/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Assetassetmbr> getAssetassetmbr(@PathVariable Long id) {
        log.debug("REST request to get Assetassetmbr : {}", id);
        Assetassetmbr assetassetmbr = assetassetmbrRepository.findOne(id);
        return Optional.ofNullable(assetassetmbr)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /assetassetmbrs/:id : delete the "id" assetassetmbr.
     *
     * @param id the id of the assetassetmbr to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/assetassetmbrs/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteAssetassetmbr(@PathVariable Long id) {
        log.debug("REST request to delete Assetassetmbr : {}", id);
        assetassetmbrRepository.delete(id);
        assetassetmbrSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("assetassetmbr", id.toString())).build();
    }

    /**
     * SEARCH  /_search/assetassetmbrs?query=:query : search for the assetassetmbr corresponding
     * to the query.
     *
     * @param query the query of the assetassetmbr search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/assetassetmbrs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Assetassetmbr>> searchAssetassetmbrs(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Assetassetmbrs for query {}", query);
        Page<Assetassetmbr> page = assetassetmbrSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/assetassetmbrs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}

package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Strategy;
import com.innvo.repository.StrategyRepository;
import com.innvo.repository.search.StrategySearchRepository;
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
 * REST controller for managing Strategy.
 */
@RestController
@RequestMapping("/api")
public class StrategyResource {

    private final Logger log = LoggerFactory.getLogger(StrategyResource.class);
        
    @Inject
    private StrategyRepository strategyRepository;
    
    @Inject
    private StrategySearchRepository strategySearchRepository;
    
    /**
     * POST  /strategies : Create a new strategy.
     *
     * @param strategy the strategy to create
     * @return the ResponseEntity with status 201 (Created) and with body the new strategy, or with status 400 (Bad Request) if the strategy has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/strategies",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Strategy> createStrategy(@Valid @RequestBody Strategy strategy) throws URISyntaxException {
        log.debug("REST request to save Strategy : {}", strategy);
        if (strategy.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("strategy", "idexists", "A new strategy cannot already have an ID")).body(null);
        }
        Strategy result = strategyRepository.save(strategy);
        strategySearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/strategies/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("strategy", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /strategies : Updates an existing strategy.
     *
     * @param strategy the strategy to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated strategy,
     * or with status 400 (Bad Request) if the strategy is not valid,
     * or with status 500 (Internal Server Error) if the strategy couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/strategies",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Strategy> updateStrategy(@Valid @RequestBody Strategy strategy) throws URISyntaxException {
        log.debug("REST request to update Strategy : {}", strategy);
        if (strategy.getId() == null) {
            return createStrategy(strategy);
        }
        Strategy result = strategyRepository.save(strategy);
        strategySearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("strategy", strategy.getId().toString()))
            .body(result);
    }

    /**
     * GET  /strategies : get all the strategies.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of strategies in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/strategies",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Strategy>> getAllStrategies(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Strategies");
        Page<Strategy> page = strategyRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/strategies");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /strategies/:id : get the "id" strategy.
     *
     * @param id the id of the strategy to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the strategy, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/strategies/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Strategy> getStrategy(@PathVariable Long id) {
        log.debug("REST request to get Strategy : {}", id);
        Strategy strategy = strategyRepository.findOne(id);
        return Optional.ofNullable(strategy)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /strategies/:id : delete the "id" strategy.
     *
     * @param id the id of the strategy to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/strategies/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteStrategy(@PathVariable Long id) {
        log.debug("REST request to delete Strategy : {}", id);
        strategyRepository.delete(id);
        strategySearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("strategy", id.toString())).build();
    }

    /**
     * SEARCH  /_search/strategies?query=:query : search for the strategy corresponding
     * to the query.
     *
     * @param query the query of the strategy search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/strategies",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Strategy>> searchStrategies(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Strategies for query {}", query);
        Page<Strategy> page = strategySearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/strategies");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}

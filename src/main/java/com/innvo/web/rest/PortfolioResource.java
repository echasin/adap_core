package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Portfolio;
import com.innvo.repository.PortfolioRepository;
import com.innvo.repository.search.PortfolioSearchRepository;
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
 * REST controller for managing Portfolio.
 */
@RestController
@RequestMapping("/api")
public class PortfolioResource {

    private final Logger log = LoggerFactory.getLogger(PortfolioResource.class);
        
    @Inject
    private PortfolioRepository portfolioRepository;
    
    @Inject
    private PortfolioSearchRepository portfolioSearchRepository;
    
    /**
     * POST  /portfolios : Create a new portfolio.
     *
     * @param portfolio the portfolio to create
     * @return the ResponseEntity with status 201 (Created) and with body the new portfolio, or with status 400 (Bad Request) if the portfolio has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/portfolios",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Portfolio> createPortfolio(@Valid @RequestBody Portfolio portfolio) throws URISyntaxException {
        log.debug("REST request to save Portfolio : {}", portfolio);
        if (portfolio.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("portfolio", "idexists", "A new portfolio cannot already have an ID")).body(null);
        }
        Portfolio result = portfolioRepository.save(portfolio);
        portfolioSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/portfolios/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("portfolio", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /portfolios : Updates an existing portfolio.
     *
     * @param portfolio the portfolio to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated portfolio,
     * or with status 400 (Bad Request) if the portfolio is not valid,
     * or with status 500 (Internal Server Error) if the portfolio couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/portfolios",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Portfolio> updatePortfolio(@Valid @RequestBody Portfolio portfolio) throws URISyntaxException {
        log.debug("REST request to update Portfolio : {}", portfolio);
        if (portfolio.getId() == null) {
            return createPortfolio(portfolio);
        }
        Portfolio result = portfolioRepository.save(portfolio);
        portfolioSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("portfolio", portfolio.getId().toString()))
            .body(result);
    }

    /**
     * GET  /portfolios : get all the portfolios.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of portfolios in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/portfolios",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Portfolio>> getAllPortfolios(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Portfolios");
        Page<Portfolio> page = portfolioRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/portfolios");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /portfolios/:id : get the "id" portfolio.
     *
     * @param id the id of the portfolio to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the portfolio, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/portfolios/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Portfolio> getPortfolio(@PathVariable Long id) {
        log.debug("REST request to get Portfolio : {}", id);
        Portfolio portfolio = portfolioRepository.findOneWithEagerRelationships(id);
        return Optional.ofNullable(portfolio)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /portfolios/:id : delete the "id" portfolio.
     *
     * @param id the id of the portfolio to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/portfolios/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deletePortfolio(@PathVariable Long id) {
        log.debug("REST request to delete Portfolio : {}", id);
        portfolioRepository.delete(id);
        portfolioSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("portfolio", id.toString())).build();
    }

    /**
     * SEARCH  /_search/portfolios?query=:query : search for the portfolio corresponding
     * to the query.
     *
     * @param query the query of the portfolio search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/portfolios",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Portfolio>> searchPortfolios(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Portfolios for query {}", query);
        Page<Portfolio> page = portfolioSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/portfolios");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}

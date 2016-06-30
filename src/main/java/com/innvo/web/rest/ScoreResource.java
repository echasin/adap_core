package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Score;
import com.innvo.repository.ScoreRepository;
import com.innvo.repository.search.ScoreSearchRepository;
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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Score.
 */
@RestController
@RequestMapping("/api")
public class ScoreResource {

    private final Logger log = LoggerFactory.getLogger(ScoreResource.class);
        
    @Inject
    private ScoreRepository scoreRepository;
    
    @Inject
    private ScoreSearchRepository scoreSearchRepository;
    
    /**
     * POST  /scores : Create a new score.
     *
     * @param score the score to create
     * @return the ResponseEntity with status 201 (Created) and with body the new score, or with status 400 (Bad Request) if the score has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/scores",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Score> createScore(@Valid @RequestBody Score score) throws URISyntaxException {
        log.debug("REST request to save Score : {}", score);
        if (score.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("score", "idexists", "A new score cannot already have an ID")).body(null);
        }
        ZonedDateTime lastmodifieddate = ZonedDateTime.now(ZoneId.systemDefault());
        score.setLastmodifieddatetime(lastmodifieddate);
        Score result = scoreRepository.save(score);
        scoreSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/scores/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("score", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /scores : Updates an existing score.
     *
     * @param score the score to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated score,
     * or with status 400 (Bad Request) if the score is not valid,
     * or with status 500 (Internal Server Error) if the score couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/scores",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Score> updateScore(@Valid @RequestBody Score score) throws URISyntaxException {
        log.debug("REST request to update Score : {}", score);
        if (score.getId() == null) {
            return createScore(score);
        }
        Score result = scoreRepository.save(score);
        scoreSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("score", score.getId().toString()))
            .body(result);
    }

    /**
     * GET  /scores : get all the scores.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of scores in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/scores",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Score>> getAllScores(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Scores");
        Page<Score> page = scoreRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/scores");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /scores/:id : get the "id" score.
     *
     * @param id the id of the score to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the score, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/scores/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Score> getScore(@PathVariable Long id) {
        log.debug("REST request to get Score : {}", id);
        Score score = scoreRepository.findOne(id);
        return Optional.ofNullable(score)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /scores/:id : delete the "id" score.
     *
     * @param id the id of the score to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/scores/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteScore(@PathVariable Long id) {
        log.debug("REST request to delete Score : {}", id);
        scoreRepository.delete(id);
        scoreSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("score", id.toString())).build();
    }

    /**
     * SEARCH  /_search/scores?query=:query : search for the score corresponding
     * to the query.
     *
     * @param query the query of the score search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/scores",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Score>> searchScores(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Scores for query {}", query);
        Page<Score> page = scoreSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/scores");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}

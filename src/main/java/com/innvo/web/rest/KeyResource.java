package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Key;
import com.innvo.repository.KeyRepository;
import com.innvo.repository.search.KeySearchRepository;
import com.innvo.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * REST controller for managing Key.
 */
@RestController
@RequestMapping("/api")
public class KeyResource {

    private final Logger log = LoggerFactory.getLogger(KeyResource.class);
        
    @Inject
    private KeyRepository keyRepository;
    
    @Inject
    private KeySearchRepository keySearchRepository;
    
    /**
     * POST  /keys : Create a new key.
     *
     * @param key the key to create
     * @return the ResponseEntity with status 201 (Created) and with body the new key, or with status 400 (Bad Request) if the key has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/keys",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Key> createKey(@Valid @RequestBody Key key) throws URISyntaxException {
        log.debug("REST request to save Key : {}", key);
        if (key.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("key", "idexists", "A new key cannot already have an ID")).body(null);
        }
        Key result = keyRepository.save(key);
        keySearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/keys/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("key", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /keys : Updates an existing key.
     *
     * @param key the key to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated key,
     * or with status 400 (Bad Request) if the key is not valid,
     * or with status 500 (Internal Server Error) if the key couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/keys",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Key> updateKey(@Valid @RequestBody Key key) throws URISyntaxException {
        log.debug("REST request to update Key : {}", key);
        if (key.getId() == null) {
            return createKey(key);
        }
        Key result = keyRepository.save(key);
        keySearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("key", key.getId().toString()))
            .body(result);
    }

    /**
     * GET  /keys : get all the keys.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of keys in body
     */
    @RequestMapping(value = "/keys",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Key> getAllKeys() {
        log.debug("REST request to get all Keys");
        List<Key> keys = keyRepository.findAll();
        return keys;
    }

    /**
     * GET  /keys/:id : get the "id" key.
     *
     * @param id the id of the key to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the key, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/keys/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Key> getKey(@PathVariable Long id) {
        log.debug("REST request to get Key : {}", id);
        Key key = keyRepository.findOne(id);
        return Optional.ofNullable(key)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /keys/:id : delete the "id" key.
     *
     * @param id the id of the key to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/keys/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteKey(@PathVariable Long id) {
        log.debug("REST request to delete Key : {}", id);
        keyRepository.delete(id);
        keySearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("key", id.toString())).build();
    }

    /**
     * SEARCH  /_search/keys?query=:query : search for the key corresponding
     * to the query.
     *
     * @param query the query of the key search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/keys",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Key> searchKeys(@RequestParam String query) {
        log.debug("REST request to search Keys for query {}", query);
        return StreamSupport
            .stream(keySearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}

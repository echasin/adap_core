package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Asset;
import com.innvo.domain.Location;
import com.innvo.repository.AssetRepository;
import com.innvo.repository.search.AssetSearchRepository;
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
import java.util.Set;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Asset.
 */
@RestController
@RequestMapping("/api")
public class AssetResource {

    private final Logger log = LoggerFactory.getLogger(AssetResource.class);
        
    @Inject
    private AssetRepository assetRepository;
    
    @Inject
    private AssetSearchRepository assetSearchRepository;
    
    /**
     * POST  /assets : Create a new asset.
     *
     * @param asset the asset to create
     * @return the ResponseEntity with status 201 (Created) and with body the new asset, or with status 400 (Bad Request) if the asset has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/assets",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Asset> createAsset(@Valid @RequestBody Asset asset) throws URISyntaxException {
        log.debug("REST request to save Asset : {}", asset);
        if (asset.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("asset", "idexists", "A new asset cannot already have an ID")).body(null);
        }
        Asset result = assetRepository.save(asset);
        assetSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/assets/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("asset", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /assets : Updates an existing asset.
     *
     * @param asset the asset to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated asset,
     * or with status 400 (Bad Request) if the asset is not valid,
     * or with status 500 (Internal Server Error) if the asset couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/assets",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Asset> updateAsset(@Valid @RequestBody Asset asset) throws URISyntaxException {
        log.debug("REST request to update Asset : {}", asset);
        if (asset.getId() == null) {
            return createAsset(asset);
        }
        Asset result = assetRepository.save(asset);
        assetSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("asset", asset.getId().toString()))
            .body(result);
    }

    /**
     * GET  /assets : get all the assets.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of assets in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/assets",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Asset>> getAllAssets(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Assets");
        Page<Asset> page = assetRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/assets");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /assets/:id : get the "id" asset.
     *
     * @param id the id of the asset to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the asset, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/assets/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Asset> getAsset(@PathVariable Long id) {
        log.debug("REST request to get Asset : {}", id);
        Asset asset = assetRepository.findOne(id);
        return Optional.ofNullable(asset)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /assets/:id : delete the "id" asset.
     *
     * @param id the id of the asset to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/assets/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteAsset(@PathVariable Long id) {
        log.debug("REST request to delete Asset : {}", id);
        assetRepository.delete(id);
        assetSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("asset", id.toString())).build();
    }

    /**
     * SEARCH  /_search/assets?query=:query : search for the asset corresponding
     * to the query.
     *
     * @param query the query of the asset search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/assets",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Asset>> searchAssets(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Assets for query {}", query);
        Page<Asset> page = assetSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/assets");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    /**
     * GET  /assetlocations/:assetid : get the "assetid" location.
     *
     * @param id the id of the location to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the location, or with status 404 (Not Found)
     */
   @RequestMapping(value = "/assetobject/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Asset> getAssetLocation(@PathVariable Long id) {
        log.debug("REST request to get Location by AssetId : {}", id);
        Asset asset = assetRepository.findOne(id);
        Set<Location> locations = assetRepository.findByAssetId(asset);
        asset.setLocations(locations);
        return Optional.ofNullable(asset)
                .map(result -> new ResponseEntity<>(
                    result,
                    HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}

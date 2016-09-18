package com.innvo.web.rest;

import com.innvo.AdapCoreApp;
import com.innvo.domain.Asset;
import com.innvo.repository.AssetRepository;
import com.innvo.repository.search.AssetSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the AssetResource REST controller.
 *
 * @see AssetResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AdapCoreApp.class)
@WebAppConfiguration
@IntegrationTest
public class AssetResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));

    private static final String DEFAULT_NAME = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_STATUS = "AAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_LASTMODIFIEDBY = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_LASTMODIFIEDBY = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_LASTMODIFIEDDATETIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_LASTMODIFIEDDATETIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_LASTMODIFIEDDATETIME_STR = dateTimeFormatter.format(DEFAULT_LASTMODIFIEDDATETIME);
    private static final String DEFAULT_DOMAIN = "AAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_DOMAIN = "BBBBBBBBBBBBBBBBBBBBBBBBB";

    @Inject
    private AssetRepository assetRepository;

    @Inject
    private AssetSearchRepository assetSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restAssetMockMvc;

    private Asset asset;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AssetResource assetResource = new AssetResource();
        ReflectionTestUtils.setField(assetResource, "assetSearchRepository", assetSearchRepository);
        ReflectionTestUtils.setField(assetResource, "assetRepository", assetRepository);
        this.restAssetMockMvc = MockMvcBuilders.standaloneSetup(assetResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        assetSearchRepository.deleteAll();
        asset = new Asset();
        asset.setName(DEFAULT_NAME);
        asset.setStatus(DEFAULT_STATUS);
        asset.setLastmodifiedby(DEFAULT_LASTMODIFIEDBY);
        asset.setLastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME);
        asset.setDomain(DEFAULT_DOMAIN);
    }

    @Test
    @Transactional
    public void createAsset() throws Exception {
        int databaseSizeBeforeCreate = assetRepository.findAll().size();

        // Create the Asset

        restAssetMockMvc.perform(post("/api/assets")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(asset)))
                .andExpect(status().isCreated());

        // Validate the Asset in the database
        List<Asset> assets = assetRepository.findAll();
        assertThat(assets).hasSize(databaseSizeBeforeCreate + 1);
        Asset testAsset = assets.get(assets.size() - 1);
        assertThat(testAsset.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAsset.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testAsset.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testAsset.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testAsset.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Asset in ElasticSearch
        Asset assetEs = assetSearchRepository.findOne(testAsset.getId());
        assertThat(assetEs).isEqualToComparingFieldByField(testAsset);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = assetRepository.findAll().size();
        // set the field null
        asset.setName(null);

        // Create the Asset, which fails.

        restAssetMockMvc.perform(post("/api/assets")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(asset)))
                .andExpect(status().isBadRequest());

        List<Asset> assets = assetRepository.findAll();
        assertThat(assets).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = assetRepository.findAll().size();
        // set the field null
        asset.setStatus(null);

        // Create the Asset, which fails.

        restAssetMockMvc.perform(post("/api/assets")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(asset)))
                .andExpect(status().isBadRequest());

        List<Asset> assets = assetRepository.findAll();
        assertThat(assets).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = assetRepository.findAll().size();
        // set the field null
        asset.setLastmodifiedby(null);

        // Create the Asset, which fails.

        restAssetMockMvc.perform(post("/api/assets")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(asset)))
                .andExpect(status().isBadRequest());

        List<Asset> assets = assetRepository.findAll();
        assertThat(assets).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = assetRepository.findAll().size();
        // set the field null
        asset.setLastmodifieddatetime(null);

        // Create the Asset, which fails.

        restAssetMockMvc.perform(post("/api/assets")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(asset)))
                .andExpect(status().isBadRequest());

        List<Asset> assets = assetRepository.findAll();
        assertThat(assets).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = assetRepository.findAll().size();
        // set the field null
        asset.setDomain(null);

        // Create the Asset, which fails.

        restAssetMockMvc.perform(post("/api/assets")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(asset)))
                .andExpect(status().isBadRequest());

        List<Asset> assets = assetRepository.findAll();
        assertThat(assets).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAssets() throws Exception {
        // Initialize the database
        assetRepository.saveAndFlush(asset);

        // Get all the assets
        restAssetMockMvc.perform(get("/api/assets?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(asset.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
                .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
                .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void getAsset() throws Exception {
        // Initialize the database
        assetRepository.saveAndFlush(asset);

        // Get the asset
        restAssetMockMvc.perform(get("/api/assets/{id}", asset.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(asset.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(DEFAULT_LASTMODIFIEDDATETIME_STR))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingAsset() throws Exception {
        // Get the asset
        restAssetMockMvc.perform(get("/api/assets/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAsset() throws Exception {
        // Initialize the database
        assetRepository.saveAndFlush(asset);
        assetSearchRepository.save(asset);
        int databaseSizeBeforeUpdate = assetRepository.findAll().size();

        // Update the asset
        Asset updatedAsset = new Asset();
        updatedAsset.setId(asset.getId());
        updatedAsset.setName(UPDATED_NAME);
        updatedAsset.setStatus(UPDATED_STATUS);
        updatedAsset.setLastmodifiedby(UPDATED_LASTMODIFIEDBY);
        updatedAsset.setLastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME);
        updatedAsset.setDomain(UPDATED_DOMAIN);

        restAssetMockMvc.perform(put("/api/assets")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedAsset)))
                .andExpect(status().isOk());

        // Validate the Asset in the database
        List<Asset> assets = assetRepository.findAll();
        assertThat(assets).hasSize(databaseSizeBeforeUpdate);
        Asset testAsset = assets.get(assets.size() - 1);
        assertThat(testAsset.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAsset.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testAsset.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testAsset.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testAsset.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Asset in ElasticSearch
        Asset assetEs = assetSearchRepository.findOne(testAsset.getId());
        assertThat(assetEs).isEqualToComparingFieldByField(testAsset);
    }

    @Test
    @Transactional
    public void deleteAsset() throws Exception {
        // Initialize the database
        assetRepository.saveAndFlush(asset);
        assetSearchRepository.save(asset);
        int databaseSizeBeforeDelete = assetRepository.findAll().size();

        // Get the asset
        restAssetMockMvc.perform(delete("/api/assets/{id}", asset.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean assetExistsInEs = assetSearchRepository.exists(asset.getId());
        assertThat(assetExistsInEs).isFalse();

        // Validate the database is empty
        List<Asset> assets = assetRepository.findAll();
        assertThat(assets).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchAsset() throws Exception {
        // Initialize the database
        assetRepository.saveAndFlush(asset);
        assetSearchRepository.save(asset);

        // Search the asset
        restAssetMockMvc.perform(get("/api/_search/assets?query=id:" + asset.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(asset.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }
}

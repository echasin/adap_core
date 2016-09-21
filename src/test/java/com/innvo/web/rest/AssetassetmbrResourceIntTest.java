package com.innvo.web.rest;

import com.innvo.AdapCoreApp;
import com.innvo.domain.Assetassetmbr;
import com.innvo.repository.AssetassetmbrRepository;
import com.innvo.repository.search.AssetassetmbrSearchRepository;

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
 * Test class for the AssetassetmbrResource REST controller.
 *
 * @see AssetassetmbrResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AdapCoreApp.class)
@WebAppConfiguration
@IntegrationTest
public class AssetassetmbrResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
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
    private AssetassetmbrRepository assetassetmbrRepository;

    @Inject
    private AssetassetmbrSearchRepository assetassetmbrSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restAssetassetmbrMockMvc;

    private Assetassetmbr assetassetmbr;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AssetassetmbrResource assetassetmbrResource = new AssetassetmbrResource();
        ReflectionTestUtils.setField(assetassetmbrResource, "assetassetmbrSearchRepository", assetassetmbrSearchRepository);
        ReflectionTestUtils.setField(assetassetmbrResource, "assetassetmbrRepository", assetassetmbrRepository);
        this.restAssetassetmbrMockMvc = MockMvcBuilders.standaloneSetup(assetassetmbrResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        assetassetmbrSearchRepository.deleteAll();
        assetassetmbr = new Assetassetmbr();
        assetassetmbr.setDescription(DEFAULT_DESCRIPTION);
        assetassetmbr.setStatus(DEFAULT_STATUS);
        assetassetmbr.setLastmodifiedby(DEFAULT_LASTMODIFIEDBY);
        assetassetmbr.setLastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME);
        assetassetmbr.setDomain(DEFAULT_DOMAIN);
    }

    @Test
    @Transactional
    public void createAssetassetmbr() throws Exception {
        int databaseSizeBeforeCreate = assetassetmbrRepository.findAll().size();

        // Create the Assetassetmbr

        restAssetassetmbrMockMvc.perform(post("/api/assetassetmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(assetassetmbr)))
                .andExpect(status().isCreated());

        // Validate the Assetassetmbr in the database
        List<Assetassetmbr> assetassetmbrs = assetassetmbrRepository.findAll();
        assertThat(assetassetmbrs).hasSize(databaseSizeBeforeCreate + 1);
        Assetassetmbr testAssetassetmbr = assetassetmbrs.get(assetassetmbrs.size() - 1);
        assertThat(testAssetassetmbr.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testAssetassetmbr.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testAssetassetmbr.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testAssetassetmbr.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testAssetassetmbr.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Assetassetmbr in ElasticSearch
        Assetassetmbr assetassetmbrEs = assetassetmbrSearchRepository.findOne(testAssetassetmbr.getId());
        assertThat(assetassetmbrEs).isEqualToComparingFieldByField(testAssetassetmbr);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = assetassetmbrRepository.findAll().size();
        // set the field null
        assetassetmbr.setStatus(null);

        // Create the Assetassetmbr, which fails.

        restAssetassetmbrMockMvc.perform(post("/api/assetassetmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(assetassetmbr)))
                .andExpect(status().isBadRequest());

        List<Assetassetmbr> assetassetmbrs = assetassetmbrRepository.findAll();
        assertThat(assetassetmbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = assetassetmbrRepository.findAll().size();
        // set the field null
        assetassetmbr.setLastmodifiedby(null);

        // Create the Assetassetmbr, which fails.

        restAssetassetmbrMockMvc.perform(post("/api/assetassetmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(assetassetmbr)))
                .andExpect(status().isBadRequest());

        List<Assetassetmbr> assetassetmbrs = assetassetmbrRepository.findAll();
        assertThat(assetassetmbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = assetassetmbrRepository.findAll().size();
        // set the field null
        assetassetmbr.setLastmodifieddatetime(null);

        // Create the Assetassetmbr, which fails.

        restAssetassetmbrMockMvc.perform(post("/api/assetassetmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(assetassetmbr)))
                .andExpect(status().isBadRequest());

        List<Assetassetmbr> assetassetmbrs = assetassetmbrRepository.findAll();
        assertThat(assetassetmbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = assetassetmbrRepository.findAll().size();
        // set the field null
        assetassetmbr.setDomain(null);

        // Create the Assetassetmbr, which fails.

        restAssetassetmbrMockMvc.perform(post("/api/assetassetmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(assetassetmbr)))
                .andExpect(status().isBadRequest());

        List<Assetassetmbr> assetassetmbrs = assetassetmbrRepository.findAll();
        assertThat(assetassetmbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAssetassetmbrs() throws Exception {
        // Initialize the database
        assetassetmbrRepository.saveAndFlush(assetassetmbr);

        // Get all the assetassetmbrs
        restAssetassetmbrMockMvc.perform(get("/api/assetassetmbrs?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(assetassetmbr.getId().intValue())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
                .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
                .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void getAssetassetmbr() throws Exception {
        // Initialize the database
        assetassetmbrRepository.saveAndFlush(assetassetmbr);

        // Get the assetassetmbr
        restAssetassetmbrMockMvc.perform(get("/api/assetassetmbrs/{id}", assetassetmbr.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(assetassetmbr.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(DEFAULT_LASTMODIFIEDDATETIME_STR))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingAssetassetmbr() throws Exception {
        // Get the assetassetmbr
        restAssetassetmbrMockMvc.perform(get("/api/assetassetmbrs/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAssetassetmbr() throws Exception {
        // Initialize the database
        assetassetmbrRepository.saveAndFlush(assetassetmbr);
        assetassetmbrSearchRepository.save(assetassetmbr);
        int databaseSizeBeforeUpdate = assetassetmbrRepository.findAll().size();

        // Update the assetassetmbr
        Assetassetmbr updatedAssetassetmbr = new Assetassetmbr();
        updatedAssetassetmbr.setId(assetassetmbr.getId());
        updatedAssetassetmbr.setDescription(UPDATED_DESCRIPTION);
        updatedAssetassetmbr.setStatus(UPDATED_STATUS);
        updatedAssetassetmbr.setLastmodifiedby(UPDATED_LASTMODIFIEDBY);
        updatedAssetassetmbr.setLastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME);
        updatedAssetassetmbr.setDomain(UPDATED_DOMAIN);

        restAssetassetmbrMockMvc.perform(put("/api/assetassetmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedAssetassetmbr)))
                .andExpect(status().isOk());

        // Validate the Assetassetmbr in the database
        List<Assetassetmbr> assetassetmbrs = assetassetmbrRepository.findAll();
        assertThat(assetassetmbrs).hasSize(databaseSizeBeforeUpdate);
        Assetassetmbr testAssetassetmbr = assetassetmbrs.get(assetassetmbrs.size() - 1);
        assertThat(testAssetassetmbr.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testAssetassetmbr.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testAssetassetmbr.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testAssetassetmbr.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testAssetassetmbr.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Assetassetmbr in ElasticSearch
        Assetassetmbr assetassetmbrEs = assetassetmbrSearchRepository.findOne(testAssetassetmbr.getId());
        assertThat(assetassetmbrEs).isEqualToComparingFieldByField(testAssetassetmbr);
    }

    @Test
    @Transactional
    public void deleteAssetassetmbr() throws Exception {
        // Initialize the database
        assetassetmbrRepository.saveAndFlush(assetassetmbr);
        assetassetmbrSearchRepository.save(assetassetmbr);
        int databaseSizeBeforeDelete = assetassetmbrRepository.findAll().size();

        // Get the assetassetmbr
        restAssetassetmbrMockMvc.perform(delete("/api/assetassetmbrs/{id}", assetassetmbr.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean assetassetmbrExistsInEs = assetassetmbrSearchRepository.exists(assetassetmbr.getId());
        assertThat(assetassetmbrExistsInEs).isFalse();

        // Validate the database is empty
        List<Assetassetmbr> assetassetmbrs = assetassetmbrRepository.findAll();
        assertThat(assetassetmbrs).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchAssetassetmbr() throws Exception {
        // Initialize the database
        assetassetmbrRepository.saveAndFlush(assetassetmbr);
        assetassetmbrSearchRepository.save(assetassetmbr);

        // Search the assetassetmbr
        restAssetassetmbrMockMvc.perform(get("/api/_search/assetassetmbrs?query=id:" + assetassetmbr.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assetassetmbr.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }
}

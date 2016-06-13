package com.innvo.web.rest;

import com.innvo.AdapCoreApp;
import com.innvo.domain.Strategymbr;
import com.innvo.repository.StrategymbrRepository;
import com.innvo.repository.search.StrategymbrSearchRepository;

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
 * Test class for the StrategymbrResource REST controller.
 *
 * @see StrategymbrResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AdapCoreApp.class)
@WebAppConfiguration
@IntegrationTest
public class StrategymbrResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));

    private static final String DEFAULT_NAME = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
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
    private StrategymbrRepository strategymbrRepository;

    @Inject
    private StrategymbrSearchRepository strategymbrSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restStrategymbrMockMvc;

    private Strategymbr strategymbr;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        StrategymbrResource strategymbrResource = new StrategymbrResource();
        ReflectionTestUtils.setField(strategymbrResource, "strategymbrSearchRepository", strategymbrSearchRepository);
        ReflectionTestUtils.setField(strategymbrResource, "strategymbrRepository", strategymbrRepository);
        this.restStrategymbrMockMvc = MockMvcBuilders.standaloneSetup(strategymbrResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        strategymbrSearchRepository.deleteAll();
        strategymbr = new Strategymbr();
        strategymbr.setName(DEFAULT_NAME);
        strategymbr.setDescription(DEFAULT_DESCRIPTION);
        strategymbr.setStatus(DEFAULT_STATUS);
        strategymbr.setLastmodifiedby(DEFAULT_LASTMODIFIEDBY);
        strategymbr.setLastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME);
        strategymbr.setDomain(DEFAULT_DOMAIN);
    }

    @Test
    @Transactional
    public void createStrategymbr() throws Exception {
        int databaseSizeBeforeCreate = strategymbrRepository.findAll().size();

        // Create the Strategymbr

        restStrategymbrMockMvc.perform(post("/api/strategymbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(strategymbr)))
                .andExpect(status().isCreated());

        // Validate the Strategymbr in the database
        List<Strategymbr> strategymbrs = strategymbrRepository.findAll();
        assertThat(strategymbrs).hasSize(databaseSizeBeforeCreate + 1);
        Strategymbr testStrategymbr = strategymbrs.get(strategymbrs.size() - 1);
        assertThat(testStrategymbr.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testStrategymbr.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testStrategymbr.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testStrategymbr.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testStrategymbr.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testStrategymbr.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Strategymbr in ElasticSearch
        Strategymbr strategymbrEs = strategymbrSearchRepository.findOne(testStrategymbr.getId());
        assertThat(strategymbrEs).isEqualToComparingFieldByField(testStrategymbr);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = strategymbrRepository.findAll().size();
        // set the field null
        strategymbr.setName(null);

        // Create the Strategymbr, which fails.

        restStrategymbrMockMvc.perform(post("/api/strategymbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(strategymbr)))
                .andExpect(status().isBadRequest());

        List<Strategymbr> strategymbrs = strategymbrRepository.findAll();
        assertThat(strategymbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = strategymbrRepository.findAll().size();
        // set the field null
        strategymbr.setDescription(null);

        // Create the Strategymbr, which fails.

        restStrategymbrMockMvc.perform(post("/api/strategymbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(strategymbr)))
                .andExpect(status().isBadRequest());

        List<Strategymbr> strategymbrs = strategymbrRepository.findAll();
        assertThat(strategymbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = strategymbrRepository.findAll().size();
        // set the field null
        strategymbr.setStatus(null);

        // Create the Strategymbr, which fails.

        restStrategymbrMockMvc.perform(post("/api/strategymbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(strategymbr)))
                .andExpect(status().isBadRequest());

        List<Strategymbr> strategymbrs = strategymbrRepository.findAll();
        assertThat(strategymbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = strategymbrRepository.findAll().size();
        // set the field null
        strategymbr.setLastmodifiedby(null);

        // Create the Strategymbr, which fails.

        restStrategymbrMockMvc.perform(post("/api/strategymbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(strategymbr)))
                .andExpect(status().isBadRequest());

        List<Strategymbr> strategymbrs = strategymbrRepository.findAll();
        assertThat(strategymbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = strategymbrRepository.findAll().size();
        // set the field null
        strategymbr.setLastmodifieddatetime(null);

        // Create the Strategymbr, which fails.

        restStrategymbrMockMvc.perform(post("/api/strategymbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(strategymbr)))
                .andExpect(status().isBadRequest());

        List<Strategymbr> strategymbrs = strategymbrRepository.findAll();
        assertThat(strategymbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = strategymbrRepository.findAll().size();
        // set the field null
        strategymbr.setDomain(null);

        // Create the Strategymbr, which fails.

        restStrategymbrMockMvc.perform(post("/api/strategymbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(strategymbr)))
                .andExpect(status().isBadRequest());

        List<Strategymbr> strategymbrs = strategymbrRepository.findAll();
        assertThat(strategymbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllStrategymbrs() throws Exception {
        // Initialize the database
        strategymbrRepository.saveAndFlush(strategymbr);

        // Get all the strategymbrs
        restStrategymbrMockMvc.perform(get("/api/strategymbrs?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(strategymbr.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
                .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
                .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void getStrategymbr() throws Exception {
        // Initialize the database
        strategymbrRepository.saveAndFlush(strategymbr);

        // Get the strategymbr
        restStrategymbrMockMvc.perform(get("/api/strategymbrs/{id}", strategymbr.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(strategymbr.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(DEFAULT_LASTMODIFIEDDATETIME_STR))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingStrategymbr() throws Exception {
        // Get the strategymbr
        restStrategymbrMockMvc.perform(get("/api/strategymbrs/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateStrategymbr() throws Exception {
        // Initialize the database
        strategymbrRepository.saveAndFlush(strategymbr);
        strategymbrSearchRepository.save(strategymbr);
        int databaseSizeBeforeUpdate = strategymbrRepository.findAll().size();

        // Update the strategymbr
        Strategymbr updatedStrategymbr = new Strategymbr();
        updatedStrategymbr.setId(strategymbr.getId());
        updatedStrategymbr.setName(UPDATED_NAME);
        updatedStrategymbr.setDescription(UPDATED_DESCRIPTION);
        updatedStrategymbr.setStatus(UPDATED_STATUS);
        updatedStrategymbr.setLastmodifiedby(UPDATED_LASTMODIFIEDBY);
        updatedStrategymbr.setLastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME);
        updatedStrategymbr.setDomain(UPDATED_DOMAIN);

        restStrategymbrMockMvc.perform(put("/api/strategymbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedStrategymbr)))
                .andExpect(status().isOk());

        // Validate the Strategymbr in the database
        List<Strategymbr> strategymbrs = strategymbrRepository.findAll();
        assertThat(strategymbrs).hasSize(databaseSizeBeforeUpdate);
        Strategymbr testStrategymbr = strategymbrs.get(strategymbrs.size() - 1);
        assertThat(testStrategymbr.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testStrategymbr.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testStrategymbr.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testStrategymbr.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testStrategymbr.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testStrategymbr.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Strategymbr in ElasticSearch
        Strategymbr strategymbrEs = strategymbrSearchRepository.findOne(testStrategymbr.getId());
        assertThat(strategymbrEs).isEqualToComparingFieldByField(testStrategymbr);
    }

    @Test
    @Transactional
    public void deleteStrategymbr() throws Exception {
        // Initialize the database
        strategymbrRepository.saveAndFlush(strategymbr);
        strategymbrSearchRepository.save(strategymbr);
        int databaseSizeBeforeDelete = strategymbrRepository.findAll().size();

        // Get the strategymbr
        restStrategymbrMockMvc.perform(delete("/api/strategymbrs/{id}", strategymbr.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean strategymbrExistsInEs = strategymbrSearchRepository.exists(strategymbr.getId());
        assertThat(strategymbrExistsInEs).isFalse();

        // Validate the database is empty
        List<Strategymbr> strategymbrs = strategymbrRepository.findAll();
        assertThat(strategymbrs).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchStrategymbr() throws Exception {
        // Initialize the database
        strategymbrRepository.saveAndFlush(strategymbr);
        strategymbrSearchRepository.save(strategymbr);

        // Search the strategymbr
        restStrategymbrMockMvc.perform(get("/api/_search/strategymbrs?query=id:" + strategymbr.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(strategymbr.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }
}

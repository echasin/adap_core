package com.innvo.web.rest;

import com.innvo.AdapCoreApp;
import com.innvo.domain.Fiscalyear;
import com.innvo.repository.FiscalyearRepository;
import com.innvo.repository.search.FiscalyearSearchRepository;

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
 * Test class for the FiscalyearResource REST controller.
 *
 * @see FiscalyearResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AdapCoreApp.class)
@WebAppConfiguration
@IntegrationTest
public class FiscalyearResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));

    private static final String DEFAULT_VALUE = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
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
    private FiscalyearRepository fiscalyearRepository;

    @Inject
    private FiscalyearSearchRepository fiscalyearSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restFiscalyearMockMvc;

    private Fiscalyear fiscalyear;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        FiscalyearResource fiscalyearResource = new FiscalyearResource();
        ReflectionTestUtils.setField(fiscalyearResource, "fiscalyearSearchRepository", fiscalyearSearchRepository);
        ReflectionTestUtils.setField(fiscalyearResource, "fiscalyearRepository", fiscalyearRepository);
        this.restFiscalyearMockMvc = MockMvcBuilders.standaloneSetup(fiscalyearResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        fiscalyearSearchRepository.deleteAll();
        fiscalyear = new Fiscalyear();
        fiscalyear.setValue(DEFAULT_VALUE);
        fiscalyear.setDescription(DEFAULT_DESCRIPTION);
        fiscalyear.setStatus(DEFAULT_STATUS);
        fiscalyear.setLastmodifiedby(DEFAULT_LASTMODIFIEDBY);
        fiscalyear.setLastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME);
        fiscalyear.setDomain(DEFAULT_DOMAIN);
    }

    @Test
    @Transactional
    public void createFiscalyear() throws Exception {
        int databaseSizeBeforeCreate = fiscalyearRepository.findAll().size();

        // Create the Fiscalyear

        restFiscalyearMockMvc.perform(post("/api/fiscalyears")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(fiscalyear)))
                .andExpect(status().isCreated());

        // Validate the Fiscalyear in the database
        List<Fiscalyear> fiscalyears = fiscalyearRepository.findAll();
        assertThat(fiscalyears).hasSize(databaseSizeBeforeCreate + 1);
        Fiscalyear testFiscalyear = fiscalyears.get(fiscalyears.size() - 1);
        assertThat(testFiscalyear.getValue()).isEqualTo(DEFAULT_VALUE);
        assertThat(testFiscalyear.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testFiscalyear.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testFiscalyear.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testFiscalyear.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testFiscalyear.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Fiscalyear in ElasticSearch
        Fiscalyear fiscalyearEs = fiscalyearSearchRepository.findOne(testFiscalyear.getId());
        assertThat(fiscalyearEs).isEqualToComparingFieldByField(testFiscalyear);
    }

    @Test
    @Transactional
    public void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = fiscalyearRepository.findAll().size();
        // set the field null
        fiscalyear.setValue(null);

        // Create the Fiscalyear, which fails.

        restFiscalyearMockMvc.perform(post("/api/fiscalyears")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(fiscalyear)))
                .andExpect(status().isBadRequest());

        List<Fiscalyear> fiscalyears = fiscalyearRepository.findAll();
        assertThat(fiscalyears).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = fiscalyearRepository.findAll().size();
        // set the field null
        fiscalyear.setStatus(null);

        // Create the Fiscalyear, which fails.

        restFiscalyearMockMvc.perform(post("/api/fiscalyears")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(fiscalyear)))
                .andExpect(status().isBadRequest());

        List<Fiscalyear> fiscalyears = fiscalyearRepository.findAll();
        assertThat(fiscalyears).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = fiscalyearRepository.findAll().size();
        // set the field null
        fiscalyear.setLastmodifiedby(null);

        // Create the Fiscalyear, which fails.

        restFiscalyearMockMvc.perform(post("/api/fiscalyears")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(fiscalyear)))
                .andExpect(status().isBadRequest());

        List<Fiscalyear> fiscalyears = fiscalyearRepository.findAll();
        assertThat(fiscalyears).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = fiscalyearRepository.findAll().size();
        // set the field null
        fiscalyear.setLastmodifieddatetime(null);

        // Create the Fiscalyear, which fails.

        restFiscalyearMockMvc.perform(post("/api/fiscalyears")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(fiscalyear)))
                .andExpect(status().isBadRequest());

        List<Fiscalyear> fiscalyears = fiscalyearRepository.findAll();
        assertThat(fiscalyears).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = fiscalyearRepository.findAll().size();
        // set the field null
        fiscalyear.setDomain(null);

        // Create the Fiscalyear, which fails.

        restFiscalyearMockMvc.perform(post("/api/fiscalyears")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(fiscalyear)))
                .andExpect(status().isBadRequest());

        List<Fiscalyear> fiscalyears = fiscalyearRepository.findAll();
        assertThat(fiscalyears).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllFiscalyears() throws Exception {
        // Initialize the database
        fiscalyearRepository.saveAndFlush(fiscalyear);

        // Get all the fiscalyears
        restFiscalyearMockMvc.perform(get("/api/fiscalyears?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(fiscalyear.getId().intValue())))
                .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
                .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
                .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void getFiscalyear() throws Exception {
        // Initialize the database
        fiscalyearRepository.saveAndFlush(fiscalyear);

        // Get the fiscalyear
        restFiscalyearMockMvc.perform(get("/api/fiscalyears/{id}", fiscalyear.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(fiscalyear.getId().intValue()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(DEFAULT_LASTMODIFIEDDATETIME_STR))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingFiscalyear() throws Exception {
        // Get the fiscalyear
        restFiscalyearMockMvc.perform(get("/api/fiscalyears/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFiscalyear() throws Exception {
        // Initialize the database
        fiscalyearRepository.saveAndFlush(fiscalyear);
        fiscalyearSearchRepository.save(fiscalyear);
        int databaseSizeBeforeUpdate = fiscalyearRepository.findAll().size();

        // Update the fiscalyear
        Fiscalyear updatedFiscalyear = new Fiscalyear();
        updatedFiscalyear.setId(fiscalyear.getId());
        updatedFiscalyear.setValue(UPDATED_VALUE);
        updatedFiscalyear.setDescription(UPDATED_DESCRIPTION);
        updatedFiscalyear.setStatus(UPDATED_STATUS);
        updatedFiscalyear.setLastmodifiedby(UPDATED_LASTMODIFIEDBY);
        updatedFiscalyear.setLastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME);
        updatedFiscalyear.setDomain(UPDATED_DOMAIN);

        restFiscalyearMockMvc.perform(put("/api/fiscalyears")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedFiscalyear)))
                .andExpect(status().isOk());

        // Validate the Fiscalyear in the database
        List<Fiscalyear> fiscalyears = fiscalyearRepository.findAll();
        assertThat(fiscalyears).hasSize(databaseSizeBeforeUpdate);
        Fiscalyear testFiscalyear = fiscalyears.get(fiscalyears.size() - 1);
        assertThat(testFiscalyear.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testFiscalyear.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testFiscalyear.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testFiscalyear.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testFiscalyear.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testFiscalyear.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Fiscalyear in ElasticSearch
        Fiscalyear fiscalyearEs = fiscalyearSearchRepository.findOne(testFiscalyear.getId());
        assertThat(fiscalyearEs).isEqualToComparingFieldByField(testFiscalyear);
    }

    @Test
    @Transactional
    public void deleteFiscalyear() throws Exception {
        // Initialize the database
        fiscalyearRepository.saveAndFlush(fiscalyear);
        fiscalyearSearchRepository.save(fiscalyear);
        int databaseSizeBeforeDelete = fiscalyearRepository.findAll().size();

        // Get the fiscalyear
        restFiscalyearMockMvc.perform(delete("/api/fiscalyears/{id}", fiscalyear.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean fiscalyearExistsInEs = fiscalyearSearchRepository.exists(fiscalyear.getId());
        assertThat(fiscalyearExistsInEs).isFalse();

        // Validate the database is empty
        List<Fiscalyear> fiscalyears = fiscalyearRepository.findAll();
        assertThat(fiscalyears).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchFiscalyear() throws Exception {
        // Initialize the database
        fiscalyearRepository.saveAndFlush(fiscalyear);
        fiscalyearSearchRepository.save(fiscalyear);

        // Search the fiscalyear
        restFiscalyearMockMvc.perform(get("/api/_search/fiscalyears?query=id:" + fiscalyear.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fiscalyear.getId().intValue())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }
}

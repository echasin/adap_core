package com.innvo.web.rest;

import com.innvo.AdapCoreApp;
import com.innvo.domain.Requeststate;
import com.innvo.repository.RequeststateRepository;
import com.innvo.repository.search.RequeststateSearchRepository;

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
 * Test class for the RequeststateResource REST controller.
 *
 * @see RequeststateResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AdapCoreApp.class)
@WebAppConfiguration
@IntegrationTest
public class RequeststateResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));

    private static final String DEFAULT_NAME = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
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
    private RequeststateRepository requeststateRepository;

    @Inject
    private RequeststateSearchRepository requeststateSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restRequeststateMockMvc;

    private Requeststate requeststate;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        RequeststateResource requeststateResource = new RequeststateResource();
        ReflectionTestUtils.setField(requeststateResource, "requeststateSearchRepository", requeststateSearchRepository);
        ReflectionTestUtils.setField(requeststateResource, "requeststateRepository", requeststateRepository);
        this.restRequeststateMockMvc = MockMvcBuilders.standaloneSetup(requeststateResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        requeststateSearchRepository.deleteAll();
        requeststate = new Requeststate();
        requeststate.setName(DEFAULT_NAME);
        requeststate.setDescription(DEFAULT_DESCRIPTION);
        requeststate.setStatus(DEFAULT_STATUS);
        requeststate.setLastmodifiedby(DEFAULT_LASTMODIFIEDBY);
        requeststate.setLastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME);
        requeststate.setDomain(DEFAULT_DOMAIN);
    }

    @Test
    @Transactional
    public void createRequeststate() throws Exception {
        int databaseSizeBeforeCreate = requeststateRepository.findAll().size();

        // Create the Requeststate

        restRequeststateMockMvc.perform(post("/api/requeststates")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(requeststate)))
                .andExpect(status().isCreated());

        // Validate the Requeststate in the database
        List<Requeststate> requeststates = requeststateRepository.findAll();
        assertThat(requeststates).hasSize(databaseSizeBeforeCreate + 1);
        Requeststate testRequeststate = requeststates.get(requeststates.size() - 1);
        assertThat(testRequeststate.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRequeststate.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRequeststate.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testRequeststate.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testRequeststate.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testRequeststate.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Requeststate in ElasticSearch
        Requeststate requeststateEs = requeststateSearchRepository.findOne(testRequeststate.getId());
        assertThat(requeststateEs).isEqualToComparingFieldByField(testRequeststate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = requeststateRepository.findAll().size();
        // set the field null
        requeststate.setName(null);

        // Create the Requeststate, which fails.

        restRequeststateMockMvc.perform(post("/api/requeststates")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(requeststate)))
                .andExpect(status().isBadRequest());

        List<Requeststate> requeststates = requeststateRepository.findAll();
        assertThat(requeststates).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = requeststateRepository.findAll().size();
        // set the field null
        requeststate.setStatus(null);

        // Create the Requeststate, which fails.

        restRequeststateMockMvc.perform(post("/api/requeststates")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(requeststate)))
                .andExpect(status().isBadRequest());

        List<Requeststate> requeststates = requeststateRepository.findAll();
        assertThat(requeststates).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = requeststateRepository.findAll().size();
        // set the field null
        requeststate.setLastmodifiedby(null);

        // Create the Requeststate, which fails.

        restRequeststateMockMvc.perform(post("/api/requeststates")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(requeststate)))
                .andExpect(status().isBadRequest());

        List<Requeststate> requeststates = requeststateRepository.findAll();
        assertThat(requeststates).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = requeststateRepository.findAll().size();
        // set the field null
        requeststate.setLastmodifieddatetime(null);

        // Create the Requeststate, which fails.

        restRequeststateMockMvc.perform(post("/api/requeststates")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(requeststate)))
                .andExpect(status().isBadRequest());

        List<Requeststate> requeststates = requeststateRepository.findAll();
        assertThat(requeststates).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = requeststateRepository.findAll().size();
        // set the field null
        requeststate.setDomain(null);

        // Create the Requeststate, which fails.

        restRequeststateMockMvc.perform(post("/api/requeststates")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(requeststate)))
                .andExpect(status().isBadRequest());

        List<Requeststate> requeststates = requeststateRepository.findAll();
        assertThat(requeststates).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllRequeststates() throws Exception {
        // Initialize the database
        requeststateRepository.saveAndFlush(requeststate);

        // Get all the requeststates
        restRequeststateMockMvc.perform(get("/api/requeststates?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(requeststate.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
                .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
                .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void getRequeststate() throws Exception {
        // Initialize the database
        requeststateRepository.saveAndFlush(requeststate);

        // Get the requeststate
        restRequeststateMockMvc.perform(get("/api/requeststates/{id}", requeststate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(requeststate.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(DEFAULT_LASTMODIFIEDDATETIME_STR))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingRequeststate() throws Exception {
        // Get the requeststate
        restRequeststateMockMvc.perform(get("/api/requeststates/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRequeststate() throws Exception {
        // Initialize the database
        requeststateRepository.saveAndFlush(requeststate);
        requeststateSearchRepository.save(requeststate);
        int databaseSizeBeforeUpdate = requeststateRepository.findAll().size();

        // Update the requeststate
        Requeststate updatedRequeststate = new Requeststate();
        updatedRequeststate.setId(requeststate.getId());
        updatedRequeststate.setName(UPDATED_NAME);
        updatedRequeststate.setDescription(UPDATED_DESCRIPTION);
        updatedRequeststate.setStatus(UPDATED_STATUS);
        updatedRequeststate.setLastmodifiedby(UPDATED_LASTMODIFIEDBY);
        updatedRequeststate.setLastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME);
        updatedRequeststate.setDomain(UPDATED_DOMAIN);

        restRequeststateMockMvc.perform(put("/api/requeststates")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedRequeststate)))
                .andExpect(status().isOk());

        // Validate the Requeststate in the database
        List<Requeststate> requeststates = requeststateRepository.findAll();
        assertThat(requeststates).hasSize(databaseSizeBeforeUpdate);
        Requeststate testRequeststate = requeststates.get(requeststates.size() - 1);
        assertThat(testRequeststate.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRequeststate.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testRequeststate.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testRequeststate.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testRequeststate.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testRequeststate.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Requeststate in ElasticSearch
        Requeststate requeststateEs = requeststateSearchRepository.findOne(testRequeststate.getId());
        assertThat(requeststateEs).isEqualToComparingFieldByField(testRequeststate);
    }

    @Test
    @Transactional
    public void deleteRequeststate() throws Exception {
        // Initialize the database
        requeststateRepository.saveAndFlush(requeststate);
        requeststateSearchRepository.save(requeststate);
        int databaseSizeBeforeDelete = requeststateRepository.findAll().size();

        // Get the requeststate
        restRequeststateMockMvc.perform(delete("/api/requeststates/{id}", requeststate.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean requeststateExistsInEs = requeststateSearchRepository.exists(requeststate.getId());
        assertThat(requeststateExistsInEs).isFalse();

        // Validate the database is empty
        List<Requeststate> requeststates = requeststateRepository.findAll();
        assertThat(requeststates).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchRequeststate() throws Exception {
        // Initialize the database
        requeststateRepository.saveAndFlush(requeststate);
        requeststateSearchRepository.save(requeststate);

        // Search the requeststate
        restRequeststateMockMvc.perform(get("/api/_search/requeststates?query=id:" + requeststate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(requeststate.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }
}

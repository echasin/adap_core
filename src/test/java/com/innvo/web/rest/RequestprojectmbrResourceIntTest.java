package com.innvo.web.rest;

import com.innvo.AdapCoreApp;
import com.innvo.domain.Requestprojectmbr;
import com.innvo.repository.RequestprojectmbrRepository;
import com.innvo.repository.search.RequestprojectmbrSearchRepository;

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
 * Test class for the RequestprojectmbrResource REST controller.
 *
 * @see RequestprojectmbrResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AdapCoreApp.class)
@WebAppConfiguration
@IntegrationTest
public class RequestprojectmbrResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));

    private static final String DEFAULT_COMMENT = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
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
    private RequestprojectmbrRepository requestprojectmbrRepository;

    @Inject
    private RequestprojectmbrSearchRepository requestprojectmbrSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restRequestprojectmbrMockMvc;

    private Requestprojectmbr requestprojectmbr;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        RequestprojectmbrResource requestprojectmbrResource = new RequestprojectmbrResource();
        ReflectionTestUtils.setField(requestprojectmbrResource, "requestprojectmbrSearchRepository", requestprojectmbrSearchRepository);
        ReflectionTestUtils.setField(requestprojectmbrResource, "requestprojectmbrRepository", requestprojectmbrRepository);
        this.restRequestprojectmbrMockMvc = MockMvcBuilders.standaloneSetup(requestprojectmbrResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        requestprojectmbrSearchRepository.deleteAll();
        requestprojectmbr = new Requestprojectmbr();
        requestprojectmbr.setComment(DEFAULT_COMMENT);
        requestprojectmbr.setStatus(DEFAULT_STATUS);
        requestprojectmbr.setLastmodifiedby(DEFAULT_LASTMODIFIEDBY);
        requestprojectmbr.setLastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME);
        requestprojectmbr.setDomain(DEFAULT_DOMAIN);
    }

    @Test
    @Transactional
    public void createRequestprojectmbr() throws Exception {
        int databaseSizeBeforeCreate = requestprojectmbrRepository.findAll().size();

        // Create the Requestprojectmbr

        restRequestprojectmbrMockMvc.perform(post("/api/requestprojectmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(requestprojectmbr)))
                .andExpect(status().isCreated());

        // Validate the Requestprojectmbr in the database
        List<Requestprojectmbr> requestprojectmbrs = requestprojectmbrRepository.findAll();
        assertThat(requestprojectmbrs).hasSize(databaseSizeBeforeCreate + 1);
        Requestprojectmbr testRequestprojectmbr = requestprojectmbrs.get(requestprojectmbrs.size() - 1);
        assertThat(testRequestprojectmbr.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testRequestprojectmbr.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testRequestprojectmbr.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testRequestprojectmbr.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testRequestprojectmbr.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Requestprojectmbr in ElasticSearch
        Requestprojectmbr requestprojectmbrEs = requestprojectmbrSearchRepository.findOne(testRequestprojectmbr.getId());
        assertThat(requestprojectmbrEs).isEqualToComparingFieldByField(testRequestprojectmbr);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = requestprojectmbrRepository.findAll().size();
        // set the field null
        requestprojectmbr.setStatus(null);

        // Create the Requestprojectmbr, which fails.

        restRequestprojectmbrMockMvc.perform(post("/api/requestprojectmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(requestprojectmbr)))
                .andExpect(status().isBadRequest());

        List<Requestprojectmbr> requestprojectmbrs = requestprojectmbrRepository.findAll();
        assertThat(requestprojectmbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = requestprojectmbrRepository.findAll().size();
        // set the field null
        requestprojectmbr.setLastmodifiedby(null);

        // Create the Requestprojectmbr, which fails.

        restRequestprojectmbrMockMvc.perform(post("/api/requestprojectmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(requestprojectmbr)))
                .andExpect(status().isBadRequest());

        List<Requestprojectmbr> requestprojectmbrs = requestprojectmbrRepository.findAll();
        assertThat(requestprojectmbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = requestprojectmbrRepository.findAll().size();
        // set the field null
        requestprojectmbr.setLastmodifieddatetime(null);

        // Create the Requestprojectmbr, which fails.

        restRequestprojectmbrMockMvc.perform(post("/api/requestprojectmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(requestprojectmbr)))
                .andExpect(status().isBadRequest());

        List<Requestprojectmbr> requestprojectmbrs = requestprojectmbrRepository.findAll();
        assertThat(requestprojectmbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = requestprojectmbrRepository.findAll().size();
        // set the field null
        requestprojectmbr.setDomain(null);

        // Create the Requestprojectmbr, which fails.

        restRequestprojectmbrMockMvc.perform(post("/api/requestprojectmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(requestprojectmbr)))
                .andExpect(status().isBadRequest());

        List<Requestprojectmbr> requestprojectmbrs = requestprojectmbrRepository.findAll();
        assertThat(requestprojectmbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllRequestprojectmbrs() throws Exception {
        // Initialize the database
        requestprojectmbrRepository.saveAndFlush(requestprojectmbr);

        // Get all the requestprojectmbrs
        restRequestprojectmbrMockMvc.perform(get("/api/requestprojectmbrs?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(requestprojectmbr.getId().intValue())))
                .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
                .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
                .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void getRequestprojectmbr() throws Exception {
        // Initialize the database
        requestprojectmbrRepository.saveAndFlush(requestprojectmbr);

        // Get the requestprojectmbr
        restRequestprojectmbrMockMvc.perform(get("/api/requestprojectmbrs/{id}", requestprojectmbr.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(requestprojectmbr.getId().intValue()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(DEFAULT_LASTMODIFIEDDATETIME_STR))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingRequestprojectmbr() throws Exception {
        // Get the requestprojectmbr
        restRequestprojectmbrMockMvc.perform(get("/api/requestprojectmbrs/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRequestprojectmbr() throws Exception {
        // Initialize the database
        requestprojectmbrRepository.saveAndFlush(requestprojectmbr);
        requestprojectmbrSearchRepository.save(requestprojectmbr);
        int databaseSizeBeforeUpdate = requestprojectmbrRepository.findAll().size();

        // Update the requestprojectmbr
        Requestprojectmbr updatedRequestprojectmbr = new Requestprojectmbr();
        updatedRequestprojectmbr.setId(requestprojectmbr.getId());
        updatedRequestprojectmbr.setComment(UPDATED_COMMENT);
        updatedRequestprojectmbr.setStatus(UPDATED_STATUS);
        updatedRequestprojectmbr.setLastmodifiedby(UPDATED_LASTMODIFIEDBY);
        updatedRequestprojectmbr.setLastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME);
        updatedRequestprojectmbr.setDomain(UPDATED_DOMAIN);

        restRequestprojectmbrMockMvc.perform(put("/api/requestprojectmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedRequestprojectmbr)))
                .andExpect(status().isOk());

        // Validate the Requestprojectmbr in the database
        List<Requestprojectmbr> requestprojectmbrs = requestprojectmbrRepository.findAll();
        assertThat(requestprojectmbrs).hasSize(databaseSizeBeforeUpdate);
        Requestprojectmbr testRequestprojectmbr = requestprojectmbrs.get(requestprojectmbrs.size() - 1);
        assertThat(testRequestprojectmbr.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testRequestprojectmbr.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testRequestprojectmbr.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testRequestprojectmbr.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testRequestprojectmbr.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Requestprojectmbr in ElasticSearch
        Requestprojectmbr requestprojectmbrEs = requestprojectmbrSearchRepository.findOne(testRequestprojectmbr.getId());
        assertThat(requestprojectmbrEs).isEqualToComparingFieldByField(testRequestprojectmbr);
    }

    @Test
    @Transactional
    public void deleteRequestprojectmbr() throws Exception {
        // Initialize the database
        requestprojectmbrRepository.saveAndFlush(requestprojectmbr);
        requestprojectmbrSearchRepository.save(requestprojectmbr);
        int databaseSizeBeforeDelete = requestprojectmbrRepository.findAll().size();

        // Get the requestprojectmbr
        restRequestprojectmbrMockMvc.perform(delete("/api/requestprojectmbrs/{id}", requestprojectmbr.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean requestprojectmbrExistsInEs = requestprojectmbrSearchRepository.exists(requestprojectmbr.getId());
        assertThat(requestprojectmbrExistsInEs).isFalse();

        // Validate the database is empty
        List<Requestprojectmbr> requestprojectmbrs = requestprojectmbrRepository.findAll();
        assertThat(requestprojectmbrs).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchRequestprojectmbr() throws Exception {
        // Initialize the database
        requestprojectmbrRepository.saveAndFlush(requestprojectmbr);
        requestprojectmbrSearchRepository.save(requestprojectmbr);

        // Search the requestprojectmbr
        restRequestprojectmbrMockMvc.perform(get("/api/_search/requestprojectmbrs?query=id:" + requestprojectmbr.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(requestprojectmbr.getId().intValue())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }
}

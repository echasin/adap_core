package com.innvo.web.rest;

import com.innvo.AdapCoreApp;
import com.innvo.domain.Organizationorganizationmbr;
import com.innvo.repository.OrganizationorganizationmbrRepository;
import com.innvo.repository.search.OrganizationorganizationmbrSearchRepository;

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
 * Test class for the OrganizationorganizationmbrResource REST controller.
 *
 * @see OrganizationorganizationmbrResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AdapCoreApp.class)
@WebAppConfiguration
@IntegrationTest
public class OrganizationorganizationmbrResourceIntTest {

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
    private OrganizationorganizationmbrRepository organizationorganizationmbrRepository;

    @Inject
    private OrganizationorganizationmbrSearchRepository organizationorganizationmbrSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restOrganizationorganizationmbrMockMvc;

    private Organizationorganizationmbr organizationorganizationmbr;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        OrganizationorganizationmbrResource organizationorganizationmbrResource = new OrganizationorganizationmbrResource();
        ReflectionTestUtils.setField(organizationorganizationmbrResource, "organizationorganizationmbrSearchRepository", organizationorganizationmbrSearchRepository);
        ReflectionTestUtils.setField(organizationorganizationmbrResource, "organizationorganizationmbrRepository", organizationorganizationmbrRepository);
        this.restOrganizationorganizationmbrMockMvc = MockMvcBuilders.standaloneSetup(organizationorganizationmbrResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        organizationorganizationmbrSearchRepository.deleteAll();
        organizationorganizationmbr = new Organizationorganizationmbr();
        organizationorganizationmbr.setComment(DEFAULT_COMMENT);
        organizationorganizationmbr.setStatus(DEFAULT_STATUS);
        organizationorganizationmbr.setLastmodifiedby(DEFAULT_LASTMODIFIEDBY);
        organizationorganizationmbr.setLastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME);
        organizationorganizationmbr.setDomain(DEFAULT_DOMAIN);
    }

    @Test
    @Transactional
    public void createOrganizationorganizationmbr() throws Exception {
        int databaseSizeBeforeCreate = organizationorganizationmbrRepository.findAll().size();

        // Create the Organizationorganizationmbr

        restOrganizationorganizationmbrMockMvc.perform(post("/api/organizationorganizationmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(organizationorganizationmbr)))
                .andExpect(status().isCreated());

        // Validate the Organizationorganizationmbr in the database
        List<Organizationorganizationmbr> organizationorganizationmbrs = organizationorganizationmbrRepository.findAll();
        assertThat(organizationorganizationmbrs).hasSize(databaseSizeBeforeCreate + 1);
        Organizationorganizationmbr testOrganizationorganizationmbr = organizationorganizationmbrs.get(organizationorganizationmbrs.size() - 1);
        assertThat(testOrganizationorganizationmbr.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testOrganizationorganizationmbr.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testOrganizationorganizationmbr.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testOrganizationorganizationmbr.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testOrganizationorganizationmbr.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Organizationorganizationmbr in ElasticSearch
        Organizationorganizationmbr organizationorganizationmbrEs = organizationorganizationmbrSearchRepository.findOne(testOrganizationorganizationmbr.getId());
        assertThat(organizationorganizationmbrEs).isEqualToComparingFieldByField(testOrganizationorganizationmbr);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = organizationorganizationmbrRepository.findAll().size();
        // set the field null
        organizationorganizationmbr.setStatus(null);

        // Create the Organizationorganizationmbr, which fails.

        restOrganizationorganizationmbrMockMvc.perform(post("/api/organizationorganizationmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(organizationorganizationmbr)))
                .andExpect(status().isBadRequest());

        List<Organizationorganizationmbr> organizationorganizationmbrs = organizationorganizationmbrRepository.findAll();
        assertThat(organizationorganizationmbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = organizationorganizationmbrRepository.findAll().size();
        // set the field null
        organizationorganizationmbr.setLastmodifiedby(null);

        // Create the Organizationorganizationmbr, which fails.

        restOrganizationorganizationmbrMockMvc.perform(post("/api/organizationorganizationmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(organizationorganizationmbr)))
                .andExpect(status().isBadRequest());

        List<Organizationorganizationmbr> organizationorganizationmbrs = organizationorganizationmbrRepository.findAll();
        assertThat(organizationorganizationmbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = organizationorganizationmbrRepository.findAll().size();
        // set the field null
        organizationorganizationmbr.setLastmodifieddatetime(null);

        // Create the Organizationorganizationmbr, which fails.

        restOrganizationorganizationmbrMockMvc.perform(post("/api/organizationorganizationmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(organizationorganizationmbr)))
                .andExpect(status().isBadRequest());

        List<Organizationorganizationmbr> organizationorganizationmbrs = organizationorganizationmbrRepository.findAll();
        assertThat(organizationorganizationmbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = organizationorganizationmbrRepository.findAll().size();
        // set the field null
        organizationorganizationmbr.setDomain(null);

        // Create the Organizationorganizationmbr, which fails.

        restOrganizationorganizationmbrMockMvc.perform(post("/api/organizationorganizationmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(organizationorganizationmbr)))
                .andExpect(status().isBadRequest());

        List<Organizationorganizationmbr> organizationorganizationmbrs = organizationorganizationmbrRepository.findAll();
        assertThat(organizationorganizationmbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllOrganizationorganizationmbrs() throws Exception {
        // Initialize the database
        organizationorganizationmbrRepository.saveAndFlush(organizationorganizationmbr);

        // Get all the organizationorganizationmbrs
        restOrganizationorganizationmbrMockMvc.perform(get("/api/organizationorganizationmbrs?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(organizationorganizationmbr.getId().intValue())))
                .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
                .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
                .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void getOrganizationorganizationmbr() throws Exception {
        // Initialize the database
        organizationorganizationmbrRepository.saveAndFlush(organizationorganizationmbr);

        // Get the organizationorganizationmbr
        restOrganizationorganizationmbrMockMvc.perform(get("/api/organizationorganizationmbrs/{id}", organizationorganizationmbr.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(organizationorganizationmbr.getId().intValue()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(DEFAULT_LASTMODIFIEDDATETIME_STR))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingOrganizationorganizationmbr() throws Exception {
        // Get the organizationorganizationmbr
        restOrganizationorganizationmbrMockMvc.perform(get("/api/organizationorganizationmbrs/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOrganizationorganizationmbr() throws Exception {
        // Initialize the database
        organizationorganizationmbrRepository.saveAndFlush(organizationorganizationmbr);
        organizationorganizationmbrSearchRepository.save(organizationorganizationmbr);
        int databaseSizeBeforeUpdate = organizationorganizationmbrRepository.findAll().size();

        // Update the organizationorganizationmbr
        Organizationorganizationmbr updatedOrganizationorganizationmbr = new Organizationorganizationmbr();
        updatedOrganizationorganizationmbr.setId(organizationorganizationmbr.getId());
        updatedOrganizationorganizationmbr.setComment(UPDATED_COMMENT);
        updatedOrganizationorganizationmbr.setStatus(UPDATED_STATUS);
        updatedOrganizationorganizationmbr.setLastmodifiedby(UPDATED_LASTMODIFIEDBY);
        updatedOrganizationorganizationmbr.setLastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME);
        updatedOrganizationorganizationmbr.setDomain(UPDATED_DOMAIN);

        restOrganizationorganizationmbrMockMvc.perform(put("/api/organizationorganizationmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedOrganizationorganizationmbr)))
                .andExpect(status().isOk());

        // Validate the Organizationorganizationmbr in the database
        List<Organizationorganizationmbr> organizationorganizationmbrs = organizationorganizationmbrRepository.findAll();
        assertThat(organizationorganizationmbrs).hasSize(databaseSizeBeforeUpdate);
        Organizationorganizationmbr testOrganizationorganizationmbr = organizationorganizationmbrs.get(organizationorganizationmbrs.size() - 1);
        assertThat(testOrganizationorganizationmbr.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testOrganizationorganizationmbr.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testOrganizationorganizationmbr.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testOrganizationorganizationmbr.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testOrganizationorganizationmbr.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Organizationorganizationmbr in ElasticSearch
        Organizationorganizationmbr organizationorganizationmbrEs = organizationorganizationmbrSearchRepository.findOne(testOrganizationorganizationmbr.getId());
        assertThat(organizationorganizationmbrEs).isEqualToComparingFieldByField(testOrganizationorganizationmbr);
    }

    @Test
    @Transactional
    public void deleteOrganizationorganizationmbr() throws Exception {
        // Initialize the database
        organizationorganizationmbrRepository.saveAndFlush(organizationorganizationmbr);
        organizationorganizationmbrSearchRepository.save(organizationorganizationmbr);
        int databaseSizeBeforeDelete = organizationorganizationmbrRepository.findAll().size();

        // Get the organizationorganizationmbr
        restOrganizationorganizationmbrMockMvc.perform(delete("/api/organizationorganizationmbrs/{id}", organizationorganizationmbr.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean organizationorganizationmbrExistsInEs = organizationorganizationmbrSearchRepository.exists(organizationorganizationmbr.getId());
        assertThat(organizationorganizationmbrExistsInEs).isFalse();

        // Validate the database is empty
        List<Organizationorganizationmbr> organizationorganizationmbrs = organizationorganizationmbrRepository.findAll();
        assertThat(organizationorganizationmbrs).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchOrganizationorganizationmbr() throws Exception {
        // Initialize the database
        organizationorganizationmbrRepository.saveAndFlush(organizationorganizationmbr);
        organizationorganizationmbrSearchRepository.save(organizationorganizationmbr);

        // Search the organizationorganizationmbr
        restOrganizationorganizationmbrMockMvc.perform(get("/api/_search/organizationorganizationmbrs?query=id:" + organizationorganizationmbr.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(organizationorganizationmbr.getId().intValue())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }
}

package com.innvo.web.rest;

import com.innvo.AdapCoreApp;
import com.innvo.domain.Projectprojectmbr;
import com.innvo.repository.ProjectprojectmbrRepository;
import com.innvo.repository.search.ProjectprojectmbrSearchRepository;

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
 * Test class for the ProjectprojectmbrResource REST controller.
 *
 * @see ProjectprojectmbrResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AdapCoreApp.class)
@WebAppConfiguration
@IntegrationTest
public class ProjectprojectmbrResourceIntTest {

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
    private ProjectprojectmbrRepository projectprojectmbrRepository;

    @Inject
    private ProjectprojectmbrSearchRepository projectprojectmbrSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restProjectprojectmbrMockMvc;

    private Projectprojectmbr projectprojectmbr;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ProjectprojectmbrResource projectprojectmbrResource = new ProjectprojectmbrResource();
        ReflectionTestUtils.setField(projectprojectmbrResource, "projectprojectmbrSearchRepository", projectprojectmbrSearchRepository);
        ReflectionTestUtils.setField(projectprojectmbrResource, "projectprojectmbrRepository", projectprojectmbrRepository);
        this.restProjectprojectmbrMockMvc = MockMvcBuilders.standaloneSetup(projectprojectmbrResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        projectprojectmbrSearchRepository.deleteAll();
        projectprojectmbr = new Projectprojectmbr();
        projectprojectmbr.setComment(DEFAULT_COMMENT);
        projectprojectmbr.setStatus(DEFAULT_STATUS);
        projectprojectmbr.setLastmodifiedby(DEFAULT_LASTMODIFIEDBY);
        projectprojectmbr.setLastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME);
        projectprojectmbr.setDomain(DEFAULT_DOMAIN);
    }

    @Test
    @Transactional
    public void createProjectprojectmbr() throws Exception {
        int databaseSizeBeforeCreate = projectprojectmbrRepository.findAll().size();

        // Create the Projectprojectmbr

        restProjectprojectmbrMockMvc.perform(post("/api/projectprojectmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectprojectmbr)))
                .andExpect(status().isCreated());

        // Validate the Projectprojectmbr in the database
        List<Projectprojectmbr> projectprojectmbrs = projectprojectmbrRepository.findAll();
        assertThat(projectprojectmbrs).hasSize(databaseSizeBeforeCreate + 1);
        Projectprojectmbr testProjectprojectmbr = projectprojectmbrs.get(projectprojectmbrs.size() - 1);
        assertThat(testProjectprojectmbr.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testProjectprojectmbr.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testProjectprojectmbr.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testProjectprojectmbr.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testProjectprojectmbr.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Projectprojectmbr in ElasticSearch
        Projectprojectmbr projectprojectmbrEs = projectprojectmbrSearchRepository.findOne(testProjectprojectmbr.getId());
        assertThat(projectprojectmbrEs).isEqualToComparingFieldByField(testProjectprojectmbr);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectprojectmbrRepository.findAll().size();
        // set the field null
        projectprojectmbr.setStatus(null);

        // Create the Projectprojectmbr, which fails.

        restProjectprojectmbrMockMvc.perform(post("/api/projectprojectmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectprojectmbr)))
                .andExpect(status().isBadRequest());

        List<Projectprojectmbr> projectprojectmbrs = projectprojectmbrRepository.findAll();
        assertThat(projectprojectmbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectprojectmbrRepository.findAll().size();
        // set the field null
        projectprojectmbr.setLastmodifiedby(null);

        // Create the Projectprojectmbr, which fails.

        restProjectprojectmbrMockMvc.perform(post("/api/projectprojectmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectprojectmbr)))
                .andExpect(status().isBadRequest());

        List<Projectprojectmbr> projectprojectmbrs = projectprojectmbrRepository.findAll();
        assertThat(projectprojectmbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectprojectmbrRepository.findAll().size();
        // set the field null
        projectprojectmbr.setLastmodifieddatetime(null);

        // Create the Projectprojectmbr, which fails.

        restProjectprojectmbrMockMvc.perform(post("/api/projectprojectmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectprojectmbr)))
                .andExpect(status().isBadRequest());

        List<Projectprojectmbr> projectprojectmbrs = projectprojectmbrRepository.findAll();
        assertThat(projectprojectmbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectprojectmbrRepository.findAll().size();
        // set the field null
        projectprojectmbr.setDomain(null);

        // Create the Projectprojectmbr, which fails.

        restProjectprojectmbrMockMvc.perform(post("/api/projectprojectmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectprojectmbr)))
                .andExpect(status().isBadRequest());

        List<Projectprojectmbr> projectprojectmbrs = projectprojectmbrRepository.findAll();
        assertThat(projectprojectmbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllProjectprojectmbrs() throws Exception {
        // Initialize the database
        projectprojectmbrRepository.saveAndFlush(projectprojectmbr);

        // Get all the projectprojectmbrs
        restProjectprojectmbrMockMvc.perform(get("/api/projectprojectmbrs?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(projectprojectmbr.getId().intValue())))
                .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
                .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
                .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void getProjectprojectmbr() throws Exception {
        // Initialize the database
        projectprojectmbrRepository.saveAndFlush(projectprojectmbr);

        // Get the projectprojectmbr
        restProjectprojectmbrMockMvc.perform(get("/api/projectprojectmbrs/{id}", projectprojectmbr.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(projectprojectmbr.getId().intValue()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(DEFAULT_LASTMODIFIEDDATETIME_STR))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingProjectprojectmbr() throws Exception {
        // Get the projectprojectmbr
        restProjectprojectmbrMockMvc.perform(get("/api/projectprojectmbrs/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProjectprojectmbr() throws Exception {
        // Initialize the database
        projectprojectmbrRepository.saveAndFlush(projectprojectmbr);
        projectprojectmbrSearchRepository.save(projectprojectmbr);
        int databaseSizeBeforeUpdate = projectprojectmbrRepository.findAll().size();

        // Update the projectprojectmbr
        Projectprojectmbr updatedProjectprojectmbr = new Projectprojectmbr();
        updatedProjectprojectmbr.setId(projectprojectmbr.getId());
        updatedProjectprojectmbr.setComment(UPDATED_COMMENT);
        updatedProjectprojectmbr.setStatus(UPDATED_STATUS);
        updatedProjectprojectmbr.setLastmodifiedby(UPDATED_LASTMODIFIEDBY);
        updatedProjectprojectmbr.setLastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME);
        updatedProjectprojectmbr.setDomain(UPDATED_DOMAIN);

        restProjectprojectmbrMockMvc.perform(put("/api/projectprojectmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedProjectprojectmbr)))
                .andExpect(status().isOk());

        // Validate the Projectprojectmbr in the database
        List<Projectprojectmbr> projectprojectmbrs = projectprojectmbrRepository.findAll();
        assertThat(projectprojectmbrs).hasSize(databaseSizeBeforeUpdate);
        Projectprojectmbr testProjectprojectmbr = projectprojectmbrs.get(projectprojectmbrs.size() - 1);
        assertThat(testProjectprojectmbr.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testProjectprojectmbr.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testProjectprojectmbr.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testProjectprojectmbr.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testProjectprojectmbr.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Projectprojectmbr in ElasticSearch
        Projectprojectmbr projectprojectmbrEs = projectprojectmbrSearchRepository.findOne(testProjectprojectmbr.getId());
        assertThat(projectprojectmbrEs).isEqualToComparingFieldByField(testProjectprojectmbr);
    }

    @Test
    @Transactional
    public void deleteProjectprojectmbr() throws Exception {
        // Initialize the database
        projectprojectmbrRepository.saveAndFlush(projectprojectmbr);
        projectprojectmbrSearchRepository.save(projectprojectmbr);
        int databaseSizeBeforeDelete = projectprojectmbrRepository.findAll().size();

        // Get the projectprojectmbr
        restProjectprojectmbrMockMvc.perform(delete("/api/projectprojectmbrs/{id}", projectprojectmbr.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean projectprojectmbrExistsInEs = projectprojectmbrSearchRepository.exists(projectprojectmbr.getId());
        assertThat(projectprojectmbrExistsInEs).isFalse();

        // Validate the database is empty
        List<Projectprojectmbr> projectprojectmbrs = projectprojectmbrRepository.findAll();
        assertThat(projectprojectmbrs).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchProjectprojectmbr() throws Exception {
        // Initialize the database
        projectprojectmbrRepository.saveAndFlush(projectprojectmbr);
        projectprojectmbrSearchRepository.save(projectprojectmbr);

        // Search the projectprojectmbr
        restProjectprojectmbrMockMvc.perform(get("/api/_search/projectprojectmbrs?query=id:" + projectprojectmbr.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectprojectmbr.getId().intValue())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }
}

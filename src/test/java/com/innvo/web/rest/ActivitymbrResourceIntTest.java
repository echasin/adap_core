package com.innvo.web.rest;

import com.innvo.AdapCoreApp;
import com.innvo.domain.Activitymbr;
import com.innvo.repository.ActivitymbrRepository;
import com.innvo.repository.search.ActivitymbrSearchRepository;

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
 * Test class for the ActivitymbrResource REST controller.
 *
 * @see ActivitymbrResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AdapCoreApp.class)
@WebAppConfiguration
@IntegrationTest
public class ActivitymbrResourceIntTest {

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
    private ActivitymbrRepository activitymbrRepository;

    @Inject
    private ActivitymbrSearchRepository activitymbrSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restActivitymbrMockMvc;

    private Activitymbr activitymbr;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ActivitymbrResource activitymbrResource = new ActivitymbrResource();
        ReflectionTestUtils.setField(activitymbrResource, "activitymbrSearchRepository", activitymbrSearchRepository);
        ReflectionTestUtils.setField(activitymbrResource, "activitymbrRepository", activitymbrRepository);
        this.restActivitymbrMockMvc = MockMvcBuilders.standaloneSetup(activitymbrResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        activitymbrSearchRepository.deleteAll();
        activitymbr = new Activitymbr();
        activitymbr.setComment(DEFAULT_COMMENT);
        activitymbr.setStatus(DEFAULT_STATUS);
        activitymbr.setLastmodifiedby(DEFAULT_LASTMODIFIEDBY);
        activitymbr.setLastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME);
        activitymbr.setDomain(DEFAULT_DOMAIN);
    }

    @Test
    @Transactional
    public void createActivitymbr() throws Exception {
        int databaseSizeBeforeCreate = activitymbrRepository.findAll().size();

        // Create the Activitymbr

        restActivitymbrMockMvc.perform(post("/api/activitymbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(activitymbr)))
                .andExpect(status().isCreated());

        // Validate the Activitymbr in the database
        List<Activitymbr> activitymbrs = activitymbrRepository.findAll();
        assertThat(activitymbrs).hasSize(databaseSizeBeforeCreate + 1);
        Activitymbr testActivitymbr = activitymbrs.get(activitymbrs.size() - 1);
        assertThat(testActivitymbr.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testActivitymbr.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testActivitymbr.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testActivitymbr.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testActivitymbr.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Activitymbr in ElasticSearch
        Activitymbr activitymbrEs = activitymbrSearchRepository.findOne(testActivitymbr.getId());
        assertThat(activitymbrEs).isEqualToComparingFieldByField(testActivitymbr);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = activitymbrRepository.findAll().size();
        // set the field null
        activitymbr.setStatus(null);

        // Create the Activitymbr, which fails.

        restActivitymbrMockMvc.perform(post("/api/activitymbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(activitymbr)))
                .andExpect(status().isBadRequest());

        List<Activitymbr> activitymbrs = activitymbrRepository.findAll();
        assertThat(activitymbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = activitymbrRepository.findAll().size();
        // set the field null
        activitymbr.setLastmodifiedby(null);

        // Create the Activitymbr, which fails.

        restActivitymbrMockMvc.perform(post("/api/activitymbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(activitymbr)))
                .andExpect(status().isBadRequest());

        List<Activitymbr> activitymbrs = activitymbrRepository.findAll();
        assertThat(activitymbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = activitymbrRepository.findAll().size();
        // set the field null
        activitymbr.setLastmodifieddatetime(null);

        // Create the Activitymbr, which fails.

        restActivitymbrMockMvc.perform(post("/api/activitymbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(activitymbr)))
                .andExpect(status().isBadRequest());

        List<Activitymbr> activitymbrs = activitymbrRepository.findAll();
        assertThat(activitymbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = activitymbrRepository.findAll().size();
        // set the field null
        activitymbr.setDomain(null);

        // Create the Activitymbr, which fails.

        restActivitymbrMockMvc.perform(post("/api/activitymbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(activitymbr)))
                .andExpect(status().isBadRequest());

        List<Activitymbr> activitymbrs = activitymbrRepository.findAll();
        assertThat(activitymbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllActivitymbrs() throws Exception {
        // Initialize the database
        activitymbrRepository.saveAndFlush(activitymbr);

        // Get all the activitymbrs
        restActivitymbrMockMvc.perform(get("/api/activitymbrs?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(activitymbr.getId().intValue())))
                .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
                .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
                .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void getActivitymbr() throws Exception {
        // Initialize the database
        activitymbrRepository.saveAndFlush(activitymbr);

        // Get the activitymbr
        restActivitymbrMockMvc.perform(get("/api/activitymbrs/{id}", activitymbr.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(activitymbr.getId().intValue()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(DEFAULT_LASTMODIFIEDDATETIME_STR))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingActivitymbr() throws Exception {
        // Get the activitymbr
        restActivitymbrMockMvc.perform(get("/api/activitymbrs/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateActivitymbr() throws Exception {
        // Initialize the database
        activitymbrRepository.saveAndFlush(activitymbr);
        activitymbrSearchRepository.save(activitymbr);
        int databaseSizeBeforeUpdate = activitymbrRepository.findAll().size();

        // Update the activitymbr
        Activitymbr updatedActivitymbr = new Activitymbr();
        updatedActivitymbr.setId(activitymbr.getId());
        updatedActivitymbr.setComment(UPDATED_COMMENT);
        updatedActivitymbr.setStatus(UPDATED_STATUS);
        updatedActivitymbr.setLastmodifiedby(UPDATED_LASTMODIFIEDBY);
        updatedActivitymbr.setLastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME);
        updatedActivitymbr.setDomain(UPDATED_DOMAIN);

        restActivitymbrMockMvc.perform(put("/api/activitymbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedActivitymbr)))
                .andExpect(status().isOk());

        // Validate the Activitymbr in the database
        List<Activitymbr> activitymbrs = activitymbrRepository.findAll();
        assertThat(activitymbrs).hasSize(databaseSizeBeforeUpdate);
        Activitymbr testActivitymbr = activitymbrs.get(activitymbrs.size() - 1);
        assertThat(testActivitymbr.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testActivitymbr.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testActivitymbr.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testActivitymbr.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testActivitymbr.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Activitymbr in ElasticSearch
        Activitymbr activitymbrEs = activitymbrSearchRepository.findOne(testActivitymbr.getId());
        assertThat(activitymbrEs).isEqualToComparingFieldByField(testActivitymbr);
    }

    @Test
    @Transactional
    public void deleteActivitymbr() throws Exception {
        // Initialize the database
        activitymbrRepository.saveAndFlush(activitymbr);
        activitymbrSearchRepository.save(activitymbr);
        int databaseSizeBeforeDelete = activitymbrRepository.findAll().size();

        // Get the activitymbr
        restActivitymbrMockMvc.perform(delete("/api/activitymbrs/{id}", activitymbr.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean activitymbrExistsInEs = activitymbrSearchRepository.exists(activitymbr.getId());
        assertThat(activitymbrExistsInEs).isFalse();

        // Validate the database is empty
        List<Activitymbr> activitymbrs = activitymbrRepository.findAll();
        assertThat(activitymbrs).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchActivitymbr() throws Exception {
        // Initialize the database
        activitymbrRepository.saveAndFlush(activitymbr);
        activitymbrSearchRepository.save(activitymbr);

        // Search the activitymbr
        restActivitymbrMockMvc.perform(get("/api/_search/activitymbrs?query=id:" + activitymbr.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(activitymbr.getId().intValue())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }
}

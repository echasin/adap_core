package com.innvo.web.rest;

import com.innvo.AdapCoreApp;
import com.innvo.domain.Tagmbr;
import com.innvo.repository.TagmbrRepository;
import com.innvo.repository.search.TagmbrSearchRepository;

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
 * Test class for the TagmbrResource REST controller.
 *
 * @see TagmbrResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AdapCoreApp.class)
@WebAppConfiguration
@IntegrationTest
public class TagmbrResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));

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
    private TagmbrRepository tagmbrRepository;

    @Inject
    private TagmbrSearchRepository tagmbrSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restTagmbrMockMvc;

    private Tagmbr tagmbr;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TagmbrResource tagmbrResource = new TagmbrResource();
        ReflectionTestUtils.setField(tagmbrResource, "tagmbrSearchRepository", tagmbrSearchRepository);
        ReflectionTestUtils.setField(tagmbrResource, "tagmbrRepository", tagmbrRepository);
        this.restTagmbrMockMvc = MockMvcBuilders.standaloneSetup(tagmbrResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        tagmbrSearchRepository.deleteAll();
        tagmbr = new Tagmbr();
        tagmbr.setStatus(DEFAULT_STATUS);
        tagmbr.setLastmodifiedby(DEFAULT_LASTMODIFIEDBY);
        tagmbr.setLastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME);
        tagmbr.setDomain(DEFAULT_DOMAIN);
    }

    @Test
    @Transactional
    public void createTagmbr() throws Exception {
        int databaseSizeBeforeCreate = tagmbrRepository.findAll().size();

        // Create the Tagmbr

        restTagmbrMockMvc.perform(post("/api/tagmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tagmbr)))
                .andExpect(status().isCreated());

        // Validate the Tagmbr in the database
        List<Tagmbr> tagmbrs = tagmbrRepository.findAll();
        assertThat(tagmbrs).hasSize(databaseSizeBeforeCreate + 1);
        Tagmbr testTagmbr = tagmbrs.get(tagmbrs.size() - 1);
        assertThat(testTagmbr.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testTagmbr.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testTagmbr.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testTagmbr.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Tagmbr in ElasticSearch
        Tagmbr tagmbrEs = tagmbrSearchRepository.findOne(testTagmbr.getId());
        assertThat(tagmbrEs).isEqualToComparingFieldByField(testTagmbr);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = tagmbrRepository.findAll().size();
        // set the field null
        tagmbr.setStatus(null);

        // Create the Tagmbr, which fails.

        restTagmbrMockMvc.perform(post("/api/tagmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tagmbr)))
                .andExpect(status().isBadRequest());

        List<Tagmbr> tagmbrs = tagmbrRepository.findAll();
        assertThat(tagmbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = tagmbrRepository.findAll().size();
        // set the field null
        tagmbr.setLastmodifiedby(null);

        // Create the Tagmbr, which fails.

        restTagmbrMockMvc.perform(post("/api/tagmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tagmbr)))
                .andExpect(status().isBadRequest());

        List<Tagmbr> tagmbrs = tagmbrRepository.findAll();
        assertThat(tagmbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = tagmbrRepository.findAll().size();
        // set the field null
        tagmbr.setLastmodifieddatetime(null);

        // Create the Tagmbr, which fails.

        restTagmbrMockMvc.perform(post("/api/tagmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tagmbr)))
                .andExpect(status().isBadRequest());

        List<Tagmbr> tagmbrs = tagmbrRepository.findAll();
        assertThat(tagmbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = tagmbrRepository.findAll().size();
        // set the field null
        tagmbr.setDomain(null);

        // Create the Tagmbr, which fails.

        restTagmbrMockMvc.perform(post("/api/tagmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tagmbr)))
                .andExpect(status().isBadRequest());

        List<Tagmbr> tagmbrs = tagmbrRepository.findAll();
        assertThat(tagmbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTagmbrs() throws Exception {
        // Initialize the database
        tagmbrRepository.saveAndFlush(tagmbr);

        // Get all the tagmbrs
        restTagmbrMockMvc.perform(get("/api/tagmbrs?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(tagmbr.getId().intValue())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
                .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
                .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void getTagmbr() throws Exception {
        // Initialize the database
        tagmbrRepository.saveAndFlush(tagmbr);

        // Get the tagmbr
        restTagmbrMockMvc.perform(get("/api/tagmbrs/{id}", tagmbr.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(tagmbr.getId().intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(DEFAULT_LASTMODIFIEDDATETIME_STR))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTagmbr() throws Exception {
        // Get the tagmbr
        restTagmbrMockMvc.perform(get("/api/tagmbrs/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTagmbr() throws Exception {
        // Initialize the database
        tagmbrRepository.saveAndFlush(tagmbr);
        tagmbrSearchRepository.save(tagmbr);
        int databaseSizeBeforeUpdate = tagmbrRepository.findAll().size();

        // Update the tagmbr
        Tagmbr updatedTagmbr = new Tagmbr();
        updatedTagmbr.setId(tagmbr.getId());
        updatedTagmbr.setStatus(UPDATED_STATUS);
        updatedTagmbr.setLastmodifiedby(UPDATED_LASTMODIFIEDBY);
        updatedTagmbr.setLastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME);
        updatedTagmbr.setDomain(UPDATED_DOMAIN);

        restTagmbrMockMvc.perform(put("/api/tagmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedTagmbr)))
                .andExpect(status().isOk());

        // Validate the Tagmbr in the database
        List<Tagmbr> tagmbrs = tagmbrRepository.findAll();
        assertThat(tagmbrs).hasSize(databaseSizeBeforeUpdate);
        Tagmbr testTagmbr = tagmbrs.get(tagmbrs.size() - 1);
        assertThat(testTagmbr.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testTagmbr.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testTagmbr.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testTagmbr.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Tagmbr in ElasticSearch
        Tagmbr tagmbrEs = tagmbrSearchRepository.findOne(testTagmbr.getId());
        assertThat(tagmbrEs).isEqualToComparingFieldByField(testTagmbr);
    }

    @Test
    @Transactional
    public void deleteTagmbr() throws Exception {
        // Initialize the database
        tagmbrRepository.saveAndFlush(tagmbr);
        tagmbrSearchRepository.save(tagmbr);
        int databaseSizeBeforeDelete = tagmbrRepository.findAll().size();

        // Get the tagmbr
        restTagmbrMockMvc.perform(delete("/api/tagmbrs/{id}", tagmbr.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean tagmbrExistsInEs = tagmbrSearchRepository.exists(tagmbr.getId());
        assertThat(tagmbrExistsInEs).isFalse();

        // Validate the database is empty
        List<Tagmbr> tagmbrs = tagmbrRepository.findAll();
        assertThat(tagmbrs).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchTagmbr() throws Exception {
        // Initialize the database
        tagmbrRepository.saveAndFlush(tagmbr);
        tagmbrSearchRepository.save(tagmbr);

        // Search the tagmbr
        restTagmbrMockMvc.perform(get("/api/_search/tagmbrs?query=id:" + tagmbr.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tagmbr.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }
}

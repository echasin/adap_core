package com.innvo.web.rest;

import com.innvo.AdapCoreApp;
import com.innvo.domain.Portfolioprojectmbr;
import com.innvo.repository.PortfolioprojectmbrRepository;
import com.innvo.repository.search.PortfolioprojectmbrSearchRepository;

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
 * Test class for the PortfolioprojectmbrResource REST controller.
 *
 * @see PortfolioprojectmbrResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AdapCoreApp.class)
@WebAppConfiguration
@IntegrationTest
public class PortfolioprojectmbrResourceIntTest {

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
    private PortfolioprojectmbrRepository portfolioprojectmbrRepository;

    @Inject
    private PortfolioprojectmbrSearchRepository portfolioprojectmbrSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restPortfolioprojectmbrMockMvc;

    private Portfolioprojectmbr portfolioprojectmbr;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PortfolioprojectmbrResource portfolioprojectmbrResource = new PortfolioprojectmbrResource();
        ReflectionTestUtils.setField(portfolioprojectmbrResource, "portfolioprojectmbrSearchRepository", portfolioprojectmbrSearchRepository);
        ReflectionTestUtils.setField(portfolioprojectmbrResource, "portfolioprojectmbrRepository", portfolioprojectmbrRepository);
        this.restPortfolioprojectmbrMockMvc = MockMvcBuilders.standaloneSetup(portfolioprojectmbrResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        portfolioprojectmbrSearchRepository.deleteAll();
        portfolioprojectmbr = new Portfolioprojectmbr();
        portfolioprojectmbr.setComment(DEFAULT_COMMENT);
        portfolioprojectmbr.setStatus(DEFAULT_STATUS);
        portfolioprojectmbr.setLastmodifiedby(DEFAULT_LASTMODIFIEDBY);
        portfolioprojectmbr.setLastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME);
        portfolioprojectmbr.setDomain(DEFAULT_DOMAIN);
    }

    @Test
    @Transactional
    public void createPortfolioprojectmbr() throws Exception {
        int databaseSizeBeforeCreate = portfolioprojectmbrRepository.findAll().size();

        // Create the Portfolioprojectmbr

        restPortfolioprojectmbrMockMvc.perform(post("/api/portfolioprojectmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(portfolioprojectmbr)))
                .andExpect(status().isCreated());

        // Validate the Portfolioprojectmbr in the database
        List<Portfolioprojectmbr> portfolioprojectmbrs = portfolioprojectmbrRepository.findAll();
        assertThat(portfolioprojectmbrs).hasSize(databaseSizeBeforeCreate + 1);
        Portfolioprojectmbr testPortfolioprojectmbr = portfolioprojectmbrs.get(portfolioprojectmbrs.size() - 1);
        assertThat(testPortfolioprojectmbr.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testPortfolioprojectmbr.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testPortfolioprojectmbr.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testPortfolioprojectmbr.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testPortfolioprojectmbr.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Portfolioprojectmbr in ElasticSearch
        Portfolioprojectmbr portfolioprojectmbrEs = portfolioprojectmbrSearchRepository.findOne(testPortfolioprojectmbr.getId());
        assertThat(portfolioprojectmbrEs).isEqualToComparingFieldByField(testPortfolioprojectmbr);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = portfolioprojectmbrRepository.findAll().size();
        // set the field null
        portfolioprojectmbr.setStatus(null);

        // Create the Portfolioprojectmbr, which fails.

        restPortfolioprojectmbrMockMvc.perform(post("/api/portfolioprojectmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(portfolioprojectmbr)))
                .andExpect(status().isBadRequest());

        List<Portfolioprojectmbr> portfolioprojectmbrs = portfolioprojectmbrRepository.findAll();
        assertThat(portfolioprojectmbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = portfolioprojectmbrRepository.findAll().size();
        // set the field null
        portfolioprojectmbr.setLastmodifiedby(null);

        // Create the Portfolioprojectmbr, which fails.

        restPortfolioprojectmbrMockMvc.perform(post("/api/portfolioprojectmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(portfolioprojectmbr)))
                .andExpect(status().isBadRequest());

        List<Portfolioprojectmbr> portfolioprojectmbrs = portfolioprojectmbrRepository.findAll();
        assertThat(portfolioprojectmbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = portfolioprojectmbrRepository.findAll().size();
        // set the field null
        portfolioprojectmbr.setLastmodifieddatetime(null);

        // Create the Portfolioprojectmbr, which fails.

        restPortfolioprojectmbrMockMvc.perform(post("/api/portfolioprojectmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(portfolioprojectmbr)))
                .andExpect(status().isBadRequest());

        List<Portfolioprojectmbr> portfolioprojectmbrs = portfolioprojectmbrRepository.findAll();
        assertThat(portfolioprojectmbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = portfolioprojectmbrRepository.findAll().size();
        // set the field null
        portfolioprojectmbr.setDomain(null);

        // Create the Portfolioprojectmbr, which fails.

        restPortfolioprojectmbrMockMvc.perform(post("/api/portfolioprojectmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(portfolioprojectmbr)))
                .andExpect(status().isBadRequest());

        List<Portfolioprojectmbr> portfolioprojectmbrs = portfolioprojectmbrRepository.findAll();
        assertThat(portfolioprojectmbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPortfolioprojectmbrs() throws Exception {
        // Initialize the database
        portfolioprojectmbrRepository.saveAndFlush(portfolioprojectmbr);

        // Get all the portfolioprojectmbrs
        restPortfolioprojectmbrMockMvc.perform(get("/api/portfolioprojectmbrs?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(portfolioprojectmbr.getId().intValue())))
                .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
                .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
                .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void getPortfolioprojectmbr() throws Exception {
        // Initialize the database
        portfolioprojectmbrRepository.saveAndFlush(portfolioprojectmbr);

        // Get the portfolioprojectmbr
        restPortfolioprojectmbrMockMvc.perform(get("/api/portfolioprojectmbrs/{id}", portfolioprojectmbr.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(portfolioprojectmbr.getId().intValue()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(DEFAULT_LASTMODIFIEDDATETIME_STR))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPortfolioprojectmbr() throws Exception {
        // Get the portfolioprojectmbr
        restPortfolioprojectmbrMockMvc.perform(get("/api/portfolioprojectmbrs/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePortfolioprojectmbr() throws Exception {
        // Initialize the database
        portfolioprojectmbrRepository.saveAndFlush(portfolioprojectmbr);
        portfolioprojectmbrSearchRepository.save(portfolioprojectmbr);
        int databaseSizeBeforeUpdate = portfolioprojectmbrRepository.findAll().size();

        // Update the portfolioprojectmbr
        Portfolioprojectmbr updatedPortfolioprojectmbr = new Portfolioprojectmbr();
        updatedPortfolioprojectmbr.setId(portfolioprojectmbr.getId());
        updatedPortfolioprojectmbr.setComment(UPDATED_COMMENT);
        updatedPortfolioprojectmbr.setStatus(UPDATED_STATUS);
        updatedPortfolioprojectmbr.setLastmodifiedby(UPDATED_LASTMODIFIEDBY);
        updatedPortfolioprojectmbr.setLastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME);
        updatedPortfolioprojectmbr.setDomain(UPDATED_DOMAIN);

        restPortfolioprojectmbrMockMvc.perform(put("/api/portfolioprojectmbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedPortfolioprojectmbr)))
                .andExpect(status().isOk());

        // Validate the Portfolioprojectmbr in the database
        List<Portfolioprojectmbr> portfolioprojectmbrs = portfolioprojectmbrRepository.findAll();
        assertThat(portfolioprojectmbrs).hasSize(databaseSizeBeforeUpdate);
        Portfolioprojectmbr testPortfolioprojectmbr = portfolioprojectmbrs.get(portfolioprojectmbrs.size() - 1);
        assertThat(testPortfolioprojectmbr.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testPortfolioprojectmbr.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testPortfolioprojectmbr.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testPortfolioprojectmbr.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testPortfolioprojectmbr.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Portfolioprojectmbr in ElasticSearch
        Portfolioprojectmbr portfolioprojectmbrEs = portfolioprojectmbrSearchRepository.findOne(testPortfolioprojectmbr.getId());
        assertThat(portfolioprojectmbrEs).isEqualToComparingFieldByField(testPortfolioprojectmbr);
    }

    @Test
    @Transactional
    public void deletePortfolioprojectmbr() throws Exception {
        // Initialize the database
        portfolioprojectmbrRepository.saveAndFlush(portfolioprojectmbr);
        portfolioprojectmbrSearchRepository.save(portfolioprojectmbr);
        int databaseSizeBeforeDelete = portfolioprojectmbrRepository.findAll().size();

        // Get the portfolioprojectmbr
        restPortfolioprojectmbrMockMvc.perform(delete("/api/portfolioprojectmbrs/{id}", portfolioprojectmbr.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean portfolioprojectmbrExistsInEs = portfolioprojectmbrSearchRepository.exists(portfolioprojectmbr.getId());
        assertThat(portfolioprojectmbrExistsInEs).isFalse();

        // Validate the database is empty
        List<Portfolioprojectmbr> portfolioprojectmbrs = portfolioprojectmbrRepository.findAll();
        assertThat(portfolioprojectmbrs).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPortfolioprojectmbr() throws Exception {
        // Initialize the database
        portfolioprojectmbrRepository.saveAndFlush(portfolioprojectmbr);
        portfolioprojectmbrSearchRepository.save(portfolioprojectmbr);

        // Search the portfolioprojectmbr
        restPortfolioprojectmbrMockMvc.perform(get("/api/_search/portfolioprojectmbrs?query=id:" + portfolioprojectmbr.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(portfolioprojectmbr.getId().intValue())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }
}

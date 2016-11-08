package com.innvo.web.rest;

import com.innvo.AdapCoreApp;
import com.innvo.domain.Portfolio;
import com.innvo.repository.PortfolioRepository;
import com.innvo.repository.search.PortfolioSearchRepository;

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
 * Test class for the PortfolioResource REST controller.
 *
 * @see PortfolioResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AdapCoreApp.class)
@WebAppConfiguration
@IntegrationTest
public class PortfolioResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));

    private static final String DEFAULT_NAME = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_NAMESHORT = "AAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_NAMESHORT = "BBBBBBBBBBBBBBBBBBBB";
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
    private PortfolioRepository portfolioRepository;

    @Inject
    private PortfolioSearchRepository portfolioSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restPortfolioMockMvc;

    private Portfolio portfolio;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PortfolioResource portfolioResource = new PortfolioResource();
        ReflectionTestUtils.setField(portfolioResource, "portfolioSearchRepository", portfolioSearchRepository);
        ReflectionTestUtils.setField(portfolioResource, "portfolioRepository", portfolioRepository);
        this.restPortfolioMockMvc = MockMvcBuilders.standaloneSetup(portfolioResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        portfolioSearchRepository.deleteAll();
        portfolio = new Portfolio();
        portfolio.setName(DEFAULT_NAME);
        portfolio.setNameshort(DEFAULT_NAMESHORT);
        portfolio.setDescription(DEFAULT_DESCRIPTION);
        portfolio.setStatus(DEFAULT_STATUS);
        portfolio.setLastmodifiedby(DEFAULT_LASTMODIFIEDBY);
        portfolio.setLastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME);
        portfolio.setDomain(DEFAULT_DOMAIN);
    }

    @Test
    @Transactional
    public void createPortfolio() throws Exception {
        int databaseSizeBeforeCreate = portfolioRepository.findAll().size();

        // Create the Portfolio

        restPortfolioMockMvc.perform(post("/api/portfolios")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(portfolio)))
                .andExpect(status().isCreated());

        // Validate the Portfolio in the database
        List<Portfolio> portfolios = portfolioRepository.findAll();
        assertThat(portfolios).hasSize(databaseSizeBeforeCreate + 1);
        Portfolio testPortfolio = portfolios.get(portfolios.size() - 1);
        assertThat(testPortfolio.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPortfolio.getNameshort()).isEqualTo(DEFAULT_NAMESHORT);
        assertThat(testPortfolio.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPortfolio.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testPortfolio.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testPortfolio.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testPortfolio.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Portfolio in ElasticSearch
        Portfolio portfolioEs = portfolioSearchRepository.findOne(testPortfolio.getId());
        assertThat(portfolioEs).isEqualToComparingFieldByField(testPortfolio);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = portfolioRepository.findAll().size();
        // set the field null
        portfolio.setName(null);

        // Create the Portfolio, which fails.

        restPortfolioMockMvc.perform(post("/api/portfolios")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(portfolio)))
                .andExpect(status().isBadRequest());

        List<Portfolio> portfolios = portfolioRepository.findAll();
        assertThat(portfolios).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameshortIsRequired() throws Exception {
        int databaseSizeBeforeTest = portfolioRepository.findAll().size();
        // set the field null
        portfolio.setNameshort(null);

        // Create the Portfolio, which fails.

        restPortfolioMockMvc.perform(post("/api/portfolios")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(portfolio)))
                .andExpect(status().isBadRequest());

        List<Portfolio> portfolios = portfolioRepository.findAll();
        assertThat(portfolios).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = portfolioRepository.findAll().size();
        // set the field null
        portfolio.setStatus(null);

        // Create the Portfolio, which fails.

        restPortfolioMockMvc.perform(post("/api/portfolios")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(portfolio)))
                .andExpect(status().isBadRequest());

        List<Portfolio> portfolios = portfolioRepository.findAll();
        assertThat(portfolios).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = portfolioRepository.findAll().size();
        // set the field null
        portfolio.setLastmodifiedby(null);

        // Create the Portfolio, which fails.

        restPortfolioMockMvc.perform(post("/api/portfolios")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(portfolio)))
                .andExpect(status().isBadRequest());

        List<Portfolio> portfolios = portfolioRepository.findAll();
        assertThat(portfolios).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = portfolioRepository.findAll().size();
        // set the field null
        portfolio.setLastmodifieddatetime(null);

        // Create the Portfolio, which fails.

        restPortfolioMockMvc.perform(post("/api/portfolios")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(portfolio)))
                .andExpect(status().isBadRequest());

        List<Portfolio> portfolios = portfolioRepository.findAll();
        assertThat(portfolios).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = portfolioRepository.findAll().size();
        // set the field null
        portfolio.setDomain(null);

        // Create the Portfolio, which fails.

        restPortfolioMockMvc.perform(post("/api/portfolios")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(portfolio)))
                .andExpect(status().isBadRequest());

        List<Portfolio> portfolios = portfolioRepository.findAll();
        assertThat(portfolios).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPortfolios() throws Exception {
        // Initialize the database
        portfolioRepository.saveAndFlush(portfolio);

        // Get all the portfolios
        restPortfolioMockMvc.perform(get("/api/portfolios?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(portfolio.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].nameshort").value(hasItem(DEFAULT_NAMESHORT.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
                .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
                .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void getPortfolio() throws Exception {
        // Initialize the database
        portfolioRepository.saveAndFlush(portfolio);

        // Get the portfolio
        restPortfolioMockMvc.perform(get("/api/portfolios/{id}", portfolio.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(portfolio.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.nameshort").value(DEFAULT_NAMESHORT.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(DEFAULT_LASTMODIFIEDDATETIME_STR))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPortfolio() throws Exception {
        // Get the portfolio
        restPortfolioMockMvc.perform(get("/api/portfolios/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePortfolio() throws Exception {
        // Initialize the database
        portfolioRepository.saveAndFlush(portfolio);
        portfolioSearchRepository.save(portfolio);
        int databaseSizeBeforeUpdate = portfolioRepository.findAll().size();

        // Update the portfolio
        Portfolio updatedPortfolio = new Portfolio();
        updatedPortfolio.setId(portfolio.getId());
        updatedPortfolio.setName(UPDATED_NAME);
        updatedPortfolio.setNameshort(UPDATED_NAMESHORT);
        updatedPortfolio.setDescription(UPDATED_DESCRIPTION);
        updatedPortfolio.setStatus(UPDATED_STATUS);
        updatedPortfolio.setLastmodifiedby(UPDATED_LASTMODIFIEDBY);
        updatedPortfolio.setLastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME);
        updatedPortfolio.setDomain(UPDATED_DOMAIN);

        restPortfolioMockMvc.perform(put("/api/portfolios")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedPortfolio)))
                .andExpect(status().isOk());

        // Validate the Portfolio in the database
        List<Portfolio> portfolios = portfolioRepository.findAll();
        assertThat(portfolios).hasSize(databaseSizeBeforeUpdate);
        Portfolio testPortfolio = portfolios.get(portfolios.size() - 1);
        assertThat(testPortfolio.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPortfolio.getNameshort()).isEqualTo(UPDATED_NAMESHORT);
        assertThat(testPortfolio.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPortfolio.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testPortfolio.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testPortfolio.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testPortfolio.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Portfolio in ElasticSearch
        Portfolio portfolioEs = portfolioSearchRepository.findOne(testPortfolio.getId());
        assertThat(portfolioEs).isEqualToComparingFieldByField(testPortfolio);
    }

    @Test
    @Transactional
    public void deletePortfolio() throws Exception {
        // Initialize the database
        portfolioRepository.saveAndFlush(portfolio);
        portfolioSearchRepository.save(portfolio);
        int databaseSizeBeforeDelete = portfolioRepository.findAll().size();

        // Get the portfolio
        restPortfolioMockMvc.perform(delete("/api/portfolios/{id}", portfolio.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean portfolioExistsInEs = portfolioSearchRepository.exists(portfolio.getId());
        assertThat(portfolioExistsInEs).isFalse();

        // Validate the database is empty
        List<Portfolio> portfolios = portfolioRepository.findAll();
        assertThat(portfolios).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPortfolio() throws Exception {
        // Initialize the database
        portfolioRepository.saveAndFlush(portfolio);
        portfolioSearchRepository.save(portfolio);

        // Search the portfolio
        restPortfolioMockMvc.perform(get("/api/_search/portfolios?query=id:" + portfolio.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(portfolio.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].nameshort").value(hasItem(DEFAULT_NAMESHORT.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }
}

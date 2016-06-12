package com.innvo.web.rest;

import com.innvo.AdapCoreApp;
import com.innvo.domain.Strategy;
import com.innvo.repository.StrategyRepository;
import com.innvo.repository.search.StrategySearchRepository;

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
 * Test class for the StrategyResource REST controller.
 *
 * @see StrategyResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AdapCoreApp.class)
@WebAppConfiguration
@IntegrationTest
public class StrategyResourceIntTest {

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
    private StrategyRepository strategyRepository;

    @Inject
    private StrategySearchRepository strategySearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restStrategyMockMvc;

    private Strategy strategy;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        StrategyResource strategyResource = new StrategyResource();
        ReflectionTestUtils.setField(strategyResource, "strategySearchRepository", strategySearchRepository);
        ReflectionTestUtils.setField(strategyResource, "strategyRepository", strategyRepository);
        this.restStrategyMockMvc = MockMvcBuilders.standaloneSetup(strategyResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        strategySearchRepository.deleteAll();
        strategy = new Strategy();
        strategy.setName(DEFAULT_NAME);
        strategy.setDescription(DEFAULT_DESCRIPTION);
        strategy.setStatus(DEFAULT_STATUS);
        strategy.setLastmodifiedby(DEFAULT_LASTMODIFIEDBY);
        strategy.setLastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME);
        strategy.setDomain(DEFAULT_DOMAIN);
    }

    @Test
    @Transactional
    public void createStrategy() throws Exception {
        int databaseSizeBeforeCreate = strategyRepository.findAll().size();

        // Create the Strategy

        restStrategyMockMvc.perform(post("/api/strategies")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(strategy)))
                .andExpect(status().isCreated());

        // Validate the Strategy in the database
        List<Strategy> strategies = strategyRepository.findAll();
        assertThat(strategies).hasSize(databaseSizeBeforeCreate + 1);
        Strategy testStrategy = strategies.get(strategies.size() - 1);
        assertThat(testStrategy.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testStrategy.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testStrategy.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testStrategy.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testStrategy.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testStrategy.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Strategy in ElasticSearch
        Strategy strategyEs = strategySearchRepository.findOne(testStrategy.getId());
        assertThat(strategyEs).isEqualToComparingFieldByField(testStrategy);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = strategyRepository.findAll().size();
        // set the field null
        strategy.setName(null);

        // Create the Strategy, which fails.

        restStrategyMockMvc.perform(post("/api/strategies")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(strategy)))
                .andExpect(status().isBadRequest());

        List<Strategy> strategies = strategyRepository.findAll();
        assertThat(strategies).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = strategyRepository.findAll().size();
        // set the field null
        strategy.setDescription(null);

        // Create the Strategy, which fails.

        restStrategyMockMvc.perform(post("/api/strategies")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(strategy)))
                .andExpect(status().isBadRequest());

        List<Strategy> strategies = strategyRepository.findAll();
        assertThat(strategies).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = strategyRepository.findAll().size();
        // set the field null
        strategy.setStatus(null);

        // Create the Strategy, which fails.

        restStrategyMockMvc.perform(post("/api/strategies")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(strategy)))
                .andExpect(status().isBadRequest());

        List<Strategy> strategies = strategyRepository.findAll();
        assertThat(strategies).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = strategyRepository.findAll().size();
        // set the field null
        strategy.setLastmodifiedby(null);

        // Create the Strategy, which fails.

        restStrategyMockMvc.perform(post("/api/strategies")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(strategy)))
                .andExpect(status().isBadRequest());

        List<Strategy> strategies = strategyRepository.findAll();
        assertThat(strategies).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = strategyRepository.findAll().size();
        // set the field null
        strategy.setLastmodifieddatetime(null);

        // Create the Strategy, which fails.

        restStrategyMockMvc.perform(post("/api/strategies")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(strategy)))
                .andExpect(status().isBadRequest());

        List<Strategy> strategies = strategyRepository.findAll();
        assertThat(strategies).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = strategyRepository.findAll().size();
        // set the field null
        strategy.setDomain(null);

        // Create the Strategy, which fails.

        restStrategyMockMvc.perform(post("/api/strategies")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(strategy)))
                .andExpect(status().isBadRequest());

        List<Strategy> strategies = strategyRepository.findAll();
        assertThat(strategies).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllStrategies() throws Exception {
        // Initialize the database
        strategyRepository.saveAndFlush(strategy);

        // Get all the strategies
        restStrategyMockMvc.perform(get("/api/strategies?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(strategy.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
                .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
                .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void getStrategy() throws Exception {
        // Initialize the database
        strategyRepository.saveAndFlush(strategy);

        // Get the strategy
        restStrategyMockMvc.perform(get("/api/strategies/{id}", strategy.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(strategy.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(DEFAULT_LASTMODIFIEDDATETIME_STR))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingStrategy() throws Exception {
        // Get the strategy
        restStrategyMockMvc.perform(get("/api/strategies/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateStrategy() throws Exception {
        // Initialize the database
        strategyRepository.saveAndFlush(strategy);
        strategySearchRepository.save(strategy);
        int databaseSizeBeforeUpdate = strategyRepository.findAll().size();

        // Update the strategy
        Strategy updatedStrategy = new Strategy();
        updatedStrategy.setId(strategy.getId());
        updatedStrategy.setName(UPDATED_NAME);
        updatedStrategy.setDescription(UPDATED_DESCRIPTION);
        updatedStrategy.setStatus(UPDATED_STATUS);
        updatedStrategy.setLastmodifiedby(UPDATED_LASTMODIFIEDBY);
        updatedStrategy.setLastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME);
        updatedStrategy.setDomain(UPDATED_DOMAIN);

        restStrategyMockMvc.perform(put("/api/strategies")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedStrategy)))
                .andExpect(status().isOk());

        // Validate the Strategy in the database
        List<Strategy> strategies = strategyRepository.findAll();
        assertThat(strategies).hasSize(databaseSizeBeforeUpdate);
        Strategy testStrategy = strategies.get(strategies.size() - 1);
        assertThat(testStrategy.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testStrategy.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testStrategy.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testStrategy.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testStrategy.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testStrategy.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Strategy in ElasticSearch
        Strategy strategyEs = strategySearchRepository.findOne(testStrategy.getId());
        assertThat(strategyEs).isEqualToComparingFieldByField(testStrategy);
    }

    @Test
    @Transactional
    public void deleteStrategy() throws Exception {
        // Initialize the database
        strategyRepository.saveAndFlush(strategy);
        strategySearchRepository.save(strategy);
        int databaseSizeBeforeDelete = strategyRepository.findAll().size();

        // Get the strategy
        restStrategyMockMvc.perform(delete("/api/strategies/{id}", strategy.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean strategyExistsInEs = strategySearchRepository.exists(strategy.getId());
        assertThat(strategyExistsInEs).isFalse();

        // Validate the database is empty
        List<Strategy> strategies = strategyRepository.findAll();
        assertThat(strategies).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchStrategy() throws Exception {
        // Initialize the database
        strategyRepository.saveAndFlush(strategy);
        strategySearchRepository.save(strategy);

        // Search the strategy
        restStrategyMockMvc.perform(get("/api/_search/strategies?query=id:" + strategy.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(strategy.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }
}

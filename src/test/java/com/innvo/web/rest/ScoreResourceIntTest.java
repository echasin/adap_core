package com.innvo.web.rest;

import com.innvo.AdapCoreApp;
import com.innvo.domain.Score;
import com.innvo.repository.ScoreRepository;
import com.innvo.repository.search.ScoreSearchRepository;

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
 * Test class for the ScoreResource REST controller.
 *
 * @see ScoreResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AdapCoreApp.class)
@WebAppConfiguration
@IntegrationTest
public class ScoreResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));

    private static final String DEFAULT_RECORDTYPE = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_RECORDTYPE = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_STATUS = "AAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_LASTMODIFIEDBY = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_LASTMODIFIEDBY = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_LASTMODIFIEDDATETIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_LASTMODIFIEDDATETIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_LASTMODIFIEDDATETIME_STR = dateTimeFormatter.format(DEFAULT_LASTMODIFIEDDATETIME);
    private static final String DEFAULT_DOMAIN = "AAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_DOMAIN = "BBBBBBBBBBBBBBBBBBBBBBBBB";

    private static final Long DEFAULT_VALUE = 1L;
    private static final Long UPDATED_VALUE = 2L;

    @Inject
    private ScoreRepository scoreRepository;

    @Inject
    private ScoreSearchRepository scoreSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restScoreMockMvc;

    private Score score;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ScoreResource scoreResource = new ScoreResource();
        ReflectionTestUtils.setField(scoreResource, "scoreSearchRepository", scoreSearchRepository);
        ReflectionTestUtils.setField(scoreResource, "scoreRepository", scoreRepository);
        this.restScoreMockMvc = MockMvcBuilders.standaloneSetup(scoreResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        scoreSearchRepository.deleteAll();
        score = new Score();
        score.setRecordtype(DEFAULT_RECORDTYPE);
        score.setStatus(DEFAULT_STATUS);
        score.setLastmodifiedby(DEFAULT_LASTMODIFIEDBY);
        score.setLastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME);
        score.setDomain(DEFAULT_DOMAIN);
        score.setValue(DEFAULT_VALUE);
    }

    @Test
    @Transactional
    public void createScore() throws Exception {
        int databaseSizeBeforeCreate = scoreRepository.findAll().size();

        // Create the Score

        restScoreMockMvc.perform(post("/api/scores")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(score)))
                .andExpect(status().isCreated());

        // Validate the Score in the database
        List<Score> scores = scoreRepository.findAll();
        assertThat(scores).hasSize(databaseSizeBeforeCreate + 1);
        Score testScore = scores.get(scores.size() - 1);
        assertThat(testScore.getRecordtype()).isEqualTo(DEFAULT_RECORDTYPE);
        assertThat(testScore.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testScore.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testScore.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testScore.getDomain()).isEqualTo(DEFAULT_DOMAIN);
        assertThat(testScore.getValue()).isEqualTo(DEFAULT_VALUE);

        // Validate the Score in ElasticSearch
        Score scoreEs = scoreSearchRepository.findOne(testScore.getId());
        assertThat(scoreEs).isEqualToComparingFieldByField(testScore);
    }

    @Test
    @Transactional
    public void checkRecordtypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = scoreRepository.findAll().size();
        // set the field null
        score.setRecordtype(null);

        // Create the Score, which fails.

        restScoreMockMvc.perform(post("/api/scores")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(score)))
                .andExpect(status().isBadRequest());

        List<Score> scores = scoreRepository.findAll();
        assertThat(scores).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = scoreRepository.findAll().size();
        // set the field null
        score.setStatus(null);

        // Create the Score, which fails.

        restScoreMockMvc.perform(post("/api/scores")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(score)))
                .andExpect(status().isBadRequest());

        List<Score> scores = scoreRepository.findAll();
        assertThat(scores).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = scoreRepository.findAll().size();
        // set the field null
        score.setLastmodifiedby(null);

        // Create the Score, which fails.

        restScoreMockMvc.perform(post("/api/scores")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(score)))
                .andExpect(status().isBadRequest());

        List<Score> scores = scoreRepository.findAll();
        assertThat(scores).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = scoreRepository.findAll().size();
        // set the field null
        score.setLastmodifieddatetime(null);

        // Create the Score, which fails.

        restScoreMockMvc.perform(post("/api/scores")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(score)))
                .andExpect(status().isBadRequest());

        List<Score> scores = scoreRepository.findAll();
        assertThat(scores).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = scoreRepository.findAll().size();
        // set the field null
        score.setDomain(null);

        // Create the Score, which fails.

        restScoreMockMvc.perform(post("/api/scores")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(score)))
                .andExpect(status().isBadRequest());

        List<Score> scores = scoreRepository.findAll();
        assertThat(scores).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = scoreRepository.findAll().size();
        // set the field null
        score.setValue(null);

        // Create the Score, which fails.

        restScoreMockMvc.perform(post("/api/scores")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(score)))
                .andExpect(status().isBadRequest());

        List<Score> scores = scoreRepository.findAll();
        assertThat(scores).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllScores() throws Exception {
        // Initialize the database
        scoreRepository.saveAndFlush(score);

        // Get all the scores
        restScoreMockMvc.perform(get("/api/scores?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(score.getId().intValue())))
                .andExpect(jsonPath("$.[*].recordtype").value(hasItem(DEFAULT_RECORDTYPE.toString())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
                .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
                .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())))
                .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.intValue())));
    }

    @Test
    @Transactional
    public void getScore() throws Exception {
        // Initialize the database
        scoreRepository.saveAndFlush(score);

        // Get the score
        restScoreMockMvc.perform(get("/api/scores/{id}", score.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(score.getId().intValue()))
            .andExpect(jsonPath("$.recordtype").value(DEFAULT_RECORDTYPE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(DEFAULT_LASTMODIFIEDDATETIME_STR))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingScore() throws Exception {
        // Get the score
        restScoreMockMvc.perform(get("/api/scores/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateScore() throws Exception {
        // Initialize the database
        scoreRepository.saveAndFlush(score);
        scoreSearchRepository.save(score);
        int databaseSizeBeforeUpdate = scoreRepository.findAll().size();

        // Update the score
        Score updatedScore = new Score();
        updatedScore.setId(score.getId());
        updatedScore.setRecordtype(UPDATED_RECORDTYPE);
        updatedScore.setStatus(UPDATED_STATUS);
        updatedScore.setLastmodifiedby(UPDATED_LASTMODIFIEDBY);
        updatedScore.setLastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME);
        updatedScore.setDomain(UPDATED_DOMAIN);
        updatedScore.setValue(UPDATED_VALUE);

        restScoreMockMvc.perform(put("/api/scores")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedScore)))
                .andExpect(status().isOk());

        // Validate the Score in the database
        List<Score> scores = scoreRepository.findAll();
        assertThat(scores).hasSize(databaseSizeBeforeUpdate);
        Score testScore = scores.get(scores.size() - 1);
        assertThat(testScore.getRecordtype()).isEqualTo(UPDATED_RECORDTYPE);
        assertThat(testScore.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testScore.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testScore.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testScore.getDomain()).isEqualTo(UPDATED_DOMAIN);
        assertThat(testScore.getValue()).isEqualTo(UPDATED_VALUE);

        // Validate the Score in ElasticSearch
        Score scoreEs = scoreSearchRepository.findOne(testScore.getId());
        assertThat(scoreEs).isEqualToComparingFieldByField(testScore);
    }

    @Test
    @Transactional
    public void deleteScore() throws Exception {
        // Initialize the database
        scoreRepository.saveAndFlush(score);
        scoreSearchRepository.save(score);
        int databaseSizeBeforeDelete = scoreRepository.findAll().size();

        // Get the score
        restScoreMockMvc.perform(delete("/api/scores/{id}", score.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean scoreExistsInEs = scoreSearchRepository.exists(score.getId());
        assertThat(scoreExistsInEs).isFalse();

        // Validate the database is empty
        List<Score> scores = scoreRepository.findAll();
        assertThat(scores).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchScore() throws Exception {
        // Initialize the database
        scoreRepository.saveAndFlush(score);
        scoreSearchRepository.save(score);

        // Search the score
        restScoreMockMvc.perform(get("/api/_search/scores?query=id:" + score.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(score.getId().intValue())))
            .andExpect(jsonPath("$.[*].recordtype").value(hasItem(DEFAULT_RECORDTYPE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.intValue())));
    }
}

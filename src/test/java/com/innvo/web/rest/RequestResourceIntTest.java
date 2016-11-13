package com.innvo.web.rest;

import com.innvo.AdapCoreApp;
import com.innvo.domain.Request;
import com.innvo.repository.RequestRepository;
import com.innvo.repository.search.RequestSearchRepository;

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
import java.math.BigDecimal;;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the RequestResource REST controller.
 *
 * @see RequestResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AdapCoreApp.class)
@WebAppConfiguration
@IntegrationTest
public class RequestResourceIntTest {

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

    private static final BigDecimal DEFAULT_AMOUNTREQUESTED = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNTREQUESTED = new BigDecimal(2);

    @Inject
    private RequestRepository requestRepository;

    @Inject
    private RequestSearchRepository requestSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restRequestMockMvc;

    private Request request;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        RequestResource requestResource = new RequestResource();
        ReflectionTestUtils.setField(requestResource, "requestSearchRepository", requestSearchRepository);
        ReflectionTestUtils.setField(requestResource, "requestRepository", requestRepository);
        this.restRequestMockMvc = MockMvcBuilders.standaloneSetup(requestResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        requestSearchRepository.deleteAll();
        request = new Request();
        request.setName(DEFAULT_NAME);
        request.setDescription(DEFAULT_DESCRIPTION);
        request.setStatus(DEFAULT_STATUS);
        request.setLastmodifiedby(DEFAULT_LASTMODIFIEDBY);
        request.setLastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME);
        request.setDomain(DEFAULT_DOMAIN);
        request.setAmountrequested(DEFAULT_AMOUNTREQUESTED);
    }

    @Test
    @Transactional
    public void createRequest() throws Exception {
        int databaseSizeBeforeCreate = requestRepository.findAll().size();

        // Create the Request

        restRequestMockMvc.perform(post("/api/requests")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(request)))
                .andExpect(status().isCreated());

        // Validate the Request in the database
        List<Request> requests = requestRepository.findAll();
        assertThat(requests).hasSize(databaseSizeBeforeCreate + 1);
        Request testRequest = requests.get(requests.size() - 1);
        assertThat(testRequest.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRequest.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRequest.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testRequest.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testRequest.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testRequest.getDomain()).isEqualTo(DEFAULT_DOMAIN);
        assertThat(testRequest.getAmountrequested()).isEqualTo(DEFAULT_AMOUNTREQUESTED);

        // Validate the Request in ElasticSearch
        Request requestEs = requestSearchRepository.findOne(testRequest.getId());
        assertThat(requestEs).isEqualToComparingFieldByField(testRequest);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = requestRepository.findAll().size();
        // set the field null
        request.setName(null);

        // Create the Request, which fails.

        restRequestMockMvc.perform(post("/api/requests")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(request)))
                .andExpect(status().isBadRequest());

        List<Request> requests = requestRepository.findAll();
        assertThat(requests).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = requestRepository.findAll().size();
        // set the field null
        request.setStatus(null);

        // Create the Request, which fails.

        restRequestMockMvc.perform(post("/api/requests")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(request)))
                .andExpect(status().isBadRequest());

        List<Request> requests = requestRepository.findAll();
        assertThat(requests).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = requestRepository.findAll().size();
        // set the field null
        request.setLastmodifiedby(null);

        // Create the Request, which fails.

        restRequestMockMvc.perform(post("/api/requests")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(request)))
                .andExpect(status().isBadRequest());

        List<Request> requests = requestRepository.findAll();
        assertThat(requests).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = requestRepository.findAll().size();
        // set the field null
        request.setLastmodifieddatetime(null);

        // Create the Request, which fails.

        restRequestMockMvc.perform(post("/api/requests")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(request)))
                .andExpect(status().isBadRequest());

        List<Request> requests = requestRepository.findAll();
        assertThat(requests).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = requestRepository.findAll().size();
        // set the field null
        request.setDomain(null);

        // Create the Request, which fails.

        restRequestMockMvc.perform(post("/api/requests")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(request)))
                .andExpect(status().isBadRequest());

        List<Request> requests = requestRepository.findAll();
        assertThat(requests).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllRequests() throws Exception {
        // Initialize the database
        requestRepository.saveAndFlush(request);

        // Get all the requests
        restRequestMockMvc.perform(get("/api/requests?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(request.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
                .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
                .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())))
                .andExpect(jsonPath("$.[*].amountrequested").value(hasItem(DEFAULT_AMOUNTREQUESTED.intValue())));
    }

    @Test
    @Transactional
    public void getRequest() throws Exception {
        // Initialize the database
        requestRepository.saveAndFlush(request);

        // Get the request
        restRequestMockMvc.perform(get("/api/requests/{id}", request.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(request.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(DEFAULT_LASTMODIFIEDDATETIME_STR))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()))
            .andExpect(jsonPath("$.amountrequested").value(DEFAULT_AMOUNTREQUESTED.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingRequest() throws Exception {
        // Get the request
        restRequestMockMvc.perform(get("/api/requests/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRequest() throws Exception {
        // Initialize the database
        requestRepository.saveAndFlush(request);
        requestSearchRepository.save(request);
        int databaseSizeBeforeUpdate = requestRepository.findAll().size();

        // Update the request
        Request updatedRequest = new Request();
        updatedRequest.setId(request.getId());
        updatedRequest.setName(UPDATED_NAME);
        updatedRequest.setDescription(UPDATED_DESCRIPTION);
        updatedRequest.setStatus(UPDATED_STATUS);
        updatedRequest.setLastmodifiedby(UPDATED_LASTMODIFIEDBY);
        updatedRequest.setLastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME);
        updatedRequest.setDomain(UPDATED_DOMAIN);
        updatedRequest.setAmountrequested(UPDATED_AMOUNTREQUESTED);

        restRequestMockMvc.perform(put("/api/requests")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedRequest)))
                .andExpect(status().isOk());

        // Validate the Request in the database
        List<Request> requests = requestRepository.findAll();
        assertThat(requests).hasSize(databaseSizeBeforeUpdate);
        Request testRequest = requests.get(requests.size() - 1);
        assertThat(testRequest.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRequest.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testRequest.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testRequest.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testRequest.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testRequest.getDomain()).isEqualTo(UPDATED_DOMAIN);
        assertThat(testRequest.getAmountrequested()).isEqualTo(UPDATED_AMOUNTREQUESTED);

        // Validate the Request in ElasticSearch
        Request requestEs = requestSearchRepository.findOne(testRequest.getId());
        assertThat(requestEs).isEqualToComparingFieldByField(testRequest);
    }

    @Test
    @Transactional
    public void deleteRequest() throws Exception {
        // Initialize the database
        requestRepository.saveAndFlush(request);
        requestSearchRepository.save(request);
        int databaseSizeBeforeDelete = requestRepository.findAll().size();

        // Get the request
        restRequestMockMvc.perform(delete("/api/requests/{id}", request.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean requestExistsInEs = requestSearchRepository.exists(request.getId());
        assertThat(requestExistsInEs).isFalse();

        // Validate the database is empty
        List<Request> requests = requestRepository.findAll();
        assertThat(requests).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchRequest() throws Exception {
        // Initialize the database
        requestRepository.saveAndFlush(request);
        requestSearchRepository.save(request);

        // Search the request
        restRequestMockMvc.perform(get("/api/_search/requests?query=id:" + request.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(request.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())))
            .andExpect(jsonPath("$.[*].amountrequested").value(hasItem(DEFAULT_AMOUNTREQUESTED.intValue())));
    }
}

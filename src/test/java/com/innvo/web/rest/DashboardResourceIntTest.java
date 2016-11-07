package com.innvo.web.rest;

import com.innvo.AdapCoreApp;
import com.innvo.domain.Dashboard;
import com.innvo.repository.DashboardRepository;
import com.innvo.repository.search.DashboardSearchRepository;

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
 * Test class for the DashboardResource REST controller.
 *
 * @see DashboardResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AdapCoreApp.class)
@WebAppConfiguration
@IntegrationTest
public class DashboardResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));

    private static final String DEFAULT_NAME = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
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
    private DashboardRepository dashboardRepository;

    @Inject
    private DashboardSearchRepository dashboardSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restDashboardMockMvc;

    private Dashboard dashboard;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        DashboardResource dashboardResource = new DashboardResource();
        ReflectionTestUtils.setField(dashboardResource, "dashboardSearchRepository", dashboardSearchRepository);
        ReflectionTestUtils.setField(dashboardResource, "dashboardRepository", dashboardRepository);
        this.restDashboardMockMvc = MockMvcBuilders.standaloneSetup(dashboardResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        dashboardSearchRepository.deleteAll();
        dashboard = new Dashboard();
        dashboard.setName(DEFAULT_NAME);
        dashboard.setStatus(DEFAULT_STATUS);
        dashboard.setLastmodifiedby(DEFAULT_LASTMODIFIEDBY);
        dashboard.setLastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME);
        dashboard.setDomain(DEFAULT_DOMAIN);
    }

    @Test
    @Transactional
    public void createDashboard() throws Exception {
        int databaseSizeBeforeCreate = dashboardRepository.findAll().size();

        // Create the Dashboard

        restDashboardMockMvc.perform(post("/api/dashboards")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dashboard)))
                .andExpect(status().isCreated());

        // Validate the Dashboard in the database
        List<Dashboard> dashboards = dashboardRepository.findAll();
        assertThat(dashboards).hasSize(databaseSizeBeforeCreate + 1);
        Dashboard testDashboard = dashboards.get(dashboards.size() - 1);
        assertThat(testDashboard.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDashboard.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testDashboard.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testDashboard.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testDashboard.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Dashboard in ElasticSearch
        Dashboard dashboardEs = dashboardSearchRepository.findOne(testDashboard.getId());
        assertThat(dashboardEs).isEqualToComparingFieldByField(testDashboard);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = dashboardRepository.findAll().size();
        // set the field null
        dashboard.setName(null);

        // Create the Dashboard, which fails.

        restDashboardMockMvc.perform(post("/api/dashboards")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dashboard)))
                .andExpect(status().isBadRequest());

        List<Dashboard> dashboards = dashboardRepository.findAll();
        assertThat(dashboards).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = dashboardRepository.findAll().size();
        // set the field null
        dashboard.setStatus(null);

        // Create the Dashboard, which fails.

        restDashboardMockMvc.perform(post("/api/dashboards")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dashboard)))
                .andExpect(status().isBadRequest());

        List<Dashboard> dashboards = dashboardRepository.findAll();
        assertThat(dashboards).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = dashboardRepository.findAll().size();
        // set the field null
        dashboard.setLastmodifiedby(null);

        // Create the Dashboard, which fails.

        restDashboardMockMvc.perform(post("/api/dashboards")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dashboard)))
                .andExpect(status().isBadRequest());

        List<Dashboard> dashboards = dashboardRepository.findAll();
        assertThat(dashboards).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = dashboardRepository.findAll().size();
        // set the field null
        dashboard.setLastmodifieddatetime(null);

        // Create the Dashboard, which fails.

        restDashboardMockMvc.perform(post("/api/dashboards")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dashboard)))
                .andExpect(status().isBadRequest());

        List<Dashboard> dashboards = dashboardRepository.findAll();
        assertThat(dashboards).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = dashboardRepository.findAll().size();
        // set the field null
        dashboard.setDomain(null);

        // Create the Dashboard, which fails.

        restDashboardMockMvc.perform(post("/api/dashboards")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dashboard)))
                .andExpect(status().isBadRequest());

        List<Dashboard> dashboards = dashboardRepository.findAll();
        assertThat(dashboards).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllDashboards() throws Exception {
        // Initialize the database
        dashboardRepository.saveAndFlush(dashboard);

        // Get all the dashboards
        restDashboardMockMvc.perform(get("/api/dashboards?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(dashboard.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
                .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
                .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void getDashboard() throws Exception {
        // Initialize the database
        dashboardRepository.saveAndFlush(dashboard);

        // Get the dashboard
        restDashboardMockMvc.perform(get("/api/dashboards/{id}", dashboard.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(dashboard.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(DEFAULT_LASTMODIFIEDDATETIME_STR))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingDashboard() throws Exception {
        // Get the dashboard
        restDashboardMockMvc.perform(get("/api/dashboards/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDashboard() throws Exception {
        // Initialize the database
        dashboardRepository.saveAndFlush(dashboard);
        dashboardSearchRepository.save(dashboard);
        int databaseSizeBeforeUpdate = dashboardRepository.findAll().size();

        // Update the dashboard
        Dashboard updatedDashboard = new Dashboard();
        updatedDashboard.setId(dashboard.getId());
        updatedDashboard.setName(UPDATED_NAME);
        updatedDashboard.setStatus(UPDATED_STATUS);
        updatedDashboard.setLastmodifiedby(UPDATED_LASTMODIFIEDBY);
        updatedDashboard.setLastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME);
        updatedDashboard.setDomain(UPDATED_DOMAIN);

        restDashboardMockMvc.perform(put("/api/dashboards")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedDashboard)))
                .andExpect(status().isOk());

        // Validate the Dashboard in the database
        List<Dashboard> dashboards = dashboardRepository.findAll();
        assertThat(dashboards).hasSize(databaseSizeBeforeUpdate);
        Dashboard testDashboard = dashboards.get(dashboards.size() - 1);
        assertThat(testDashboard.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDashboard.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testDashboard.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testDashboard.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testDashboard.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Dashboard in ElasticSearch
        Dashboard dashboardEs = dashboardSearchRepository.findOne(testDashboard.getId());
        assertThat(dashboardEs).isEqualToComparingFieldByField(testDashboard);
    }

    @Test
    @Transactional
    public void deleteDashboard() throws Exception {
        // Initialize the database
        dashboardRepository.saveAndFlush(dashboard);
        dashboardSearchRepository.save(dashboard);
        int databaseSizeBeforeDelete = dashboardRepository.findAll().size();

        // Get the dashboard
        restDashboardMockMvc.perform(delete("/api/dashboards/{id}", dashboard.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean dashboardExistsInEs = dashboardSearchRepository.exists(dashboard.getId());
        assertThat(dashboardExistsInEs).isFalse();

        // Validate the database is empty
        List<Dashboard> dashboards = dashboardRepository.findAll();
        assertThat(dashboards).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchDashboard() throws Exception {
        // Initialize the database
        dashboardRepository.saveAndFlush(dashboard);
        dashboardSearchRepository.save(dashboard);

        // Search the dashboard
        restDashboardMockMvc.perform(get("/api/_search/dashboards?query=id:" + dashboard.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dashboard.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }
}

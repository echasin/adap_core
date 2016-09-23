package com.innvo.web.rest;

import com.innvo.AdapCoreApp;
import com.innvo.domain.Securitygroup;
import com.innvo.repository.SecuritygroupRepository;
import com.innvo.repository.search.SecuritygroupSearchRepository;

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
 * Test class for the SecuritygroupResource REST controller.
 *
 * @see SecuritygroupResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AdapCoreApp.class)
@WebAppConfiguration
@IntegrationTest
public class SecuritygroupResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));

    private static final String DEFAULT_NAME = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_GROUPID = "AAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_GROUPID = "BBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_VPCID = "AAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_VPCID = "BBBBBBBBBBBBBBBBBBBBBBBBB";
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
    private SecuritygroupRepository securitygroupRepository;

    @Inject
    private SecuritygroupSearchRepository securitygroupSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restSecuritygroupMockMvc;

    private Securitygroup securitygroup;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SecuritygroupResource securitygroupResource = new SecuritygroupResource();
        ReflectionTestUtils.setField(securitygroupResource, "securitygroupSearchRepository", securitygroupSearchRepository);
        ReflectionTestUtils.setField(securitygroupResource, "securitygroupRepository", securitygroupRepository);
        this.restSecuritygroupMockMvc = MockMvcBuilders.standaloneSetup(securitygroupResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        securitygroupSearchRepository.deleteAll();
        securitygroup = new Securitygroup();
        securitygroup.setName(DEFAULT_NAME);
        securitygroup.setDescription(DEFAULT_DESCRIPTION);
        securitygroup.setGroupid(DEFAULT_GROUPID);
        securitygroup.setVpcid(DEFAULT_VPCID);
        securitygroup.setStatus(DEFAULT_STATUS);
        securitygroup.setLastmodifiedby(DEFAULT_LASTMODIFIEDBY);
        securitygroup.setLastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME);
        securitygroup.setDomain(DEFAULT_DOMAIN);
    }

    @Test
    @Transactional
    public void createSecuritygroup() throws Exception {
        int databaseSizeBeforeCreate = securitygroupRepository.findAll().size();

        // Create the Securitygroup

        restSecuritygroupMockMvc.perform(post("/api/securitygroups")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(securitygroup)))
                .andExpect(status().isCreated());

        // Validate the Securitygroup in the database
        List<Securitygroup> securitygroups = securitygroupRepository.findAll();
        assertThat(securitygroups).hasSize(databaseSizeBeforeCreate + 1);
        Securitygroup testSecuritygroup = securitygroups.get(securitygroups.size() - 1);
        assertThat(testSecuritygroup.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSecuritygroup.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testSecuritygroup.getGroupid()).isEqualTo(DEFAULT_GROUPID);
        assertThat(testSecuritygroup.getVpcid()).isEqualTo(DEFAULT_VPCID);
        assertThat(testSecuritygroup.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testSecuritygroup.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testSecuritygroup.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testSecuritygroup.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Securitygroup in ElasticSearch
        Securitygroup securitygroupEs = securitygroupSearchRepository.findOne(testSecuritygroup.getId());
        assertThat(securitygroupEs).isEqualToComparingFieldByField(testSecuritygroup);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = securitygroupRepository.findAll().size();
        // set the field null
        securitygroup.setName(null);

        // Create the Securitygroup, which fails.

        restSecuritygroupMockMvc.perform(post("/api/securitygroups")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(securitygroup)))
                .andExpect(status().isBadRequest());

        List<Securitygroup> securitygroups = securitygroupRepository.findAll();
        assertThat(securitygroups).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = securitygroupRepository.findAll().size();
        // set the field null
        securitygroup.setDescription(null);

        // Create the Securitygroup, which fails.

        restSecuritygroupMockMvc.perform(post("/api/securitygroups")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(securitygroup)))
                .andExpect(status().isBadRequest());

        List<Securitygroup> securitygroups = securitygroupRepository.findAll();
        assertThat(securitygroups).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = securitygroupRepository.findAll().size();
        // set the field null
        securitygroup.setStatus(null);

        // Create the Securitygroup, which fails.

        restSecuritygroupMockMvc.perform(post("/api/securitygroups")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(securitygroup)))
                .andExpect(status().isBadRequest());

        List<Securitygroup> securitygroups = securitygroupRepository.findAll();
        assertThat(securitygroups).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = securitygroupRepository.findAll().size();
        // set the field null
        securitygroup.setLastmodifiedby(null);

        // Create the Securitygroup, which fails.

        restSecuritygroupMockMvc.perform(post("/api/securitygroups")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(securitygroup)))
                .andExpect(status().isBadRequest());

        List<Securitygroup> securitygroups = securitygroupRepository.findAll();
        assertThat(securitygroups).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = securitygroupRepository.findAll().size();
        // set the field null
        securitygroup.setLastmodifieddatetime(null);

        // Create the Securitygroup, which fails.

        restSecuritygroupMockMvc.perform(post("/api/securitygroups")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(securitygroup)))
                .andExpect(status().isBadRequest());

        List<Securitygroup> securitygroups = securitygroupRepository.findAll();
        assertThat(securitygroups).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = securitygroupRepository.findAll().size();
        // set the field null
        securitygroup.setDomain(null);

        // Create the Securitygroup, which fails.

        restSecuritygroupMockMvc.perform(post("/api/securitygroups")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(securitygroup)))
                .andExpect(status().isBadRequest());

        List<Securitygroup> securitygroups = securitygroupRepository.findAll();
        assertThat(securitygroups).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSecuritygroups() throws Exception {
        // Initialize the database
        securitygroupRepository.saveAndFlush(securitygroup);

        // Get all the securitygroups
        restSecuritygroupMockMvc.perform(get("/api/securitygroups?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(securitygroup.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].groupid").value(hasItem(DEFAULT_GROUPID.toString())))
                .andExpect(jsonPath("$.[*].vpcid").value(hasItem(DEFAULT_VPCID.toString())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
                .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
                .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void getSecuritygroup() throws Exception {
        // Initialize the database
        securitygroupRepository.saveAndFlush(securitygroup);

        // Get the securitygroup
        restSecuritygroupMockMvc.perform(get("/api/securitygroups/{id}", securitygroup.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(securitygroup.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.groupid").value(DEFAULT_GROUPID.toString()))
            .andExpect(jsonPath("$.vpcid").value(DEFAULT_VPCID.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(DEFAULT_LASTMODIFIEDDATETIME_STR))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSecuritygroup() throws Exception {
        // Get the securitygroup
        restSecuritygroupMockMvc.perform(get("/api/securitygroups/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSecuritygroup() throws Exception {
        // Initialize the database
        securitygroupRepository.saveAndFlush(securitygroup);
        securitygroupSearchRepository.save(securitygroup);
        int databaseSizeBeforeUpdate = securitygroupRepository.findAll().size();

        // Update the securitygroup
        Securitygroup updatedSecuritygroup = new Securitygroup();
        updatedSecuritygroup.setId(securitygroup.getId());
        updatedSecuritygroup.setName(UPDATED_NAME);
        updatedSecuritygroup.setDescription(UPDATED_DESCRIPTION);
        updatedSecuritygroup.setGroupid(UPDATED_GROUPID);
        updatedSecuritygroup.setVpcid(UPDATED_VPCID);
        updatedSecuritygroup.setStatus(UPDATED_STATUS);
        updatedSecuritygroup.setLastmodifiedby(UPDATED_LASTMODIFIEDBY);
        updatedSecuritygroup.setLastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME);
        updatedSecuritygroup.setDomain(UPDATED_DOMAIN);

        restSecuritygroupMockMvc.perform(put("/api/securitygroups")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedSecuritygroup)))
                .andExpect(status().isOk());

        // Validate the Securitygroup in the database
        List<Securitygroup> securitygroups = securitygroupRepository.findAll();
        assertThat(securitygroups).hasSize(databaseSizeBeforeUpdate);
        Securitygroup testSecuritygroup = securitygroups.get(securitygroups.size() - 1);
        assertThat(testSecuritygroup.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSecuritygroup.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSecuritygroup.getGroupid()).isEqualTo(UPDATED_GROUPID);
        assertThat(testSecuritygroup.getVpcid()).isEqualTo(UPDATED_VPCID);
        assertThat(testSecuritygroup.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testSecuritygroup.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testSecuritygroup.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testSecuritygroup.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Securitygroup in ElasticSearch
        Securitygroup securitygroupEs = securitygroupSearchRepository.findOne(testSecuritygroup.getId());
        assertThat(securitygroupEs).isEqualToComparingFieldByField(testSecuritygroup);
    }

    @Test
    @Transactional
    public void deleteSecuritygroup() throws Exception {
        // Initialize the database
        securitygroupRepository.saveAndFlush(securitygroup);
        securitygroupSearchRepository.save(securitygroup);
        int databaseSizeBeforeDelete = securitygroupRepository.findAll().size();

        // Get the securitygroup
        restSecuritygroupMockMvc.perform(delete("/api/securitygroups/{id}", securitygroup.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean securitygroupExistsInEs = securitygroupSearchRepository.exists(securitygroup.getId());
        assertThat(securitygroupExistsInEs).isFalse();

        // Validate the database is empty
        List<Securitygroup> securitygroups = securitygroupRepository.findAll();
        assertThat(securitygroups).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchSecuritygroup() throws Exception {
        // Initialize the database
        securitygroupRepository.saveAndFlush(securitygroup);
        securitygroupSearchRepository.save(securitygroup);

        // Search the securitygroup
        restSecuritygroupMockMvc.perform(get("/api/_search/securitygroups?query=id:" + securitygroup.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(securitygroup.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].groupid").value(hasItem(DEFAULT_GROUPID.toString())))
            .andExpect(jsonPath("$.[*].vpcid").value(hasItem(DEFAULT_VPCID.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }
}

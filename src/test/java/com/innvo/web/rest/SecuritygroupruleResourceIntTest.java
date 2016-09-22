package com.innvo.web.rest;

import com.innvo.AdapCoreApp;
import com.innvo.domain.Securitygrouprule;
import com.innvo.repository.SecuritygroupruleRepository;
import com.innvo.repository.search.SecuritygroupruleSearchRepository;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.innvo.domain.enumeration.Ruletype;
import com.innvo.domain.enumeration.Protocol;

/**
 * Test class for the SecuritygroupruleResource REST controller.
 *
 * @see SecuritygroupruleResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AdapCoreApp.class)
@WebAppConfiguration
@IntegrationTest
public class SecuritygroupruleResourceIntTest {


    private static final Ruletype DEFAULT_RULETYPE = Ruletype.Inbound;
    private static final Ruletype UPDATED_RULETYPE = Ruletype.Outbound;

    private static final Protocol DEFAULT_PROTOCOL = Protocol.TCP;
    private static final Protocol UPDATED_PROTOCOL = Protocol.SSH;
    private static final String DEFAULT_IPRANGE = "AAAAA";
    private static final String UPDATED_IPRANGE = "BBBBB";

    private static final Integer DEFAULT_FROMPORT = 1;
    private static final Integer UPDATED_FROMPORT = 2;

    private static final Integer DEFAULT_TOPORT = 1;
    private static final Integer UPDATED_TOPORT = 2;

    @Inject
    private SecuritygroupruleRepository securitygroupruleRepository;

    @Inject
    private SecuritygroupruleSearchRepository securitygroupruleSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restSecuritygroupruleMockMvc;

    private Securitygrouprule securitygrouprule;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SecuritygroupruleResource securitygroupruleResource = new SecuritygroupruleResource();
        ReflectionTestUtils.setField(securitygroupruleResource, "securitygroupruleSearchRepository", securitygroupruleSearchRepository);
        ReflectionTestUtils.setField(securitygroupruleResource, "securitygroupruleRepository", securitygroupruleRepository);
        this.restSecuritygroupruleMockMvc = MockMvcBuilders.standaloneSetup(securitygroupruleResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        securitygroupruleSearchRepository.deleteAll();
        securitygrouprule = new Securitygrouprule();
        securitygrouprule.setRuletype(DEFAULT_RULETYPE);
        securitygrouprule.setProtocol(DEFAULT_PROTOCOL);
        securitygrouprule.setIprange(DEFAULT_IPRANGE);
        securitygrouprule.setFromport(DEFAULT_FROMPORT);
        securitygrouprule.setToport(DEFAULT_TOPORT);
    }

    @Test
    @Transactional
    public void createSecuritygrouprule() throws Exception {
        int databaseSizeBeforeCreate = securitygroupruleRepository.findAll().size();

        // Create the Securitygrouprule

        restSecuritygroupruleMockMvc.perform(post("/api/securitygrouprules")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(securitygrouprule)))
                .andExpect(status().isCreated());

        // Validate the Securitygrouprule in the database
        List<Securitygrouprule> securitygrouprules = securitygroupruleRepository.findAll();
        assertThat(securitygrouprules).hasSize(databaseSizeBeforeCreate + 1);
        Securitygrouprule testSecuritygrouprule = securitygrouprules.get(securitygrouprules.size() - 1);
        assertThat(testSecuritygrouprule.getRuletype()).isEqualTo(DEFAULT_RULETYPE);
        assertThat(testSecuritygrouprule.getProtocol()).isEqualTo(DEFAULT_PROTOCOL);
        assertThat(testSecuritygrouprule.getIprange()).isEqualTo(DEFAULT_IPRANGE);
        assertThat(testSecuritygrouprule.getFromport()).isEqualTo(DEFAULT_FROMPORT);
        assertThat(testSecuritygrouprule.getToport()).isEqualTo(DEFAULT_TOPORT);

        // Validate the Securitygrouprule in ElasticSearch
        Securitygrouprule securitygroupruleEs = securitygroupruleSearchRepository.findOne(testSecuritygrouprule.getId());
        assertThat(securitygroupruleEs).isEqualToComparingFieldByField(testSecuritygrouprule);
    }

    @Test
    @Transactional
    public void checkRuletypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = securitygroupruleRepository.findAll().size();
        // set the field null
        securitygrouprule.setRuletype(null);

        // Create the Securitygrouprule, which fails.

        restSecuritygroupruleMockMvc.perform(post("/api/securitygrouprules")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(securitygrouprule)))
                .andExpect(status().isBadRequest());

        List<Securitygrouprule> securitygrouprules = securitygroupruleRepository.findAll();
        assertThat(securitygrouprules).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkProtocolIsRequired() throws Exception {
        int databaseSizeBeforeTest = securitygroupruleRepository.findAll().size();
        // set the field null
        securitygrouprule.setProtocol(null);

        // Create the Securitygrouprule, which fails.

        restSecuritygroupruleMockMvc.perform(post("/api/securitygrouprules")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(securitygrouprule)))
                .andExpect(status().isBadRequest());

        List<Securitygrouprule> securitygrouprules = securitygroupruleRepository.findAll();
        assertThat(securitygrouprules).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSecuritygrouprules() throws Exception {
        // Initialize the database
        securitygroupruleRepository.saveAndFlush(securitygrouprule);

        // Get all the securitygrouprules
        restSecuritygroupruleMockMvc.perform(get("/api/securitygrouprules?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(securitygrouprule.getId().intValue())))
                .andExpect(jsonPath("$.[*].ruletype").value(hasItem(DEFAULT_RULETYPE.toString())))
                .andExpect(jsonPath("$.[*].protocol").value(hasItem(DEFAULT_PROTOCOL.toString())))
                .andExpect(jsonPath("$.[*].iprange").value(hasItem(DEFAULT_IPRANGE.toString())))
                .andExpect(jsonPath("$.[*].fromport").value(hasItem(DEFAULT_FROMPORT)))
                .andExpect(jsonPath("$.[*].toport").value(hasItem(DEFAULT_TOPORT)));
    }

    @Test
    @Transactional
    public void getSecuritygrouprule() throws Exception {
        // Initialize the database
        securitygroupruleRepository.saveAndFlush(securitygrouprule);

        // Get the securitygrouprule
        restSecuritygroupruleMockMvc.perform(get("/api/securitygrouprules/{id}", securitygrouprule.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(securitygrouprule.getId().intValue()))
            .andExpect(jsonPath("$.ruletype").value(DEFAULT_RULETYPE.toString()))
            .andExpect(jsonPath("$.protocol").value(DEFAULT_PROTOCOL.toString()))
            .andExpect(jsonPath("$.iprange").value(DEFAULT_IPRANGE.toString()))
            .andExpect(jsonPath("$.fromport").value(DEFAULT_FROMPORT))
            .andExpect(jsonPath("$.toport").value(DEFAULT_TOPORT));
    }

    @Test
    @Transactional
    public void getNonExistingSecuritygrouprule() throws Exception {
        // Get the securitygrouprule
        restSecuritygroupruleMockMvc.perform(get("/api/securitygrouprules/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSecuritygrouprule() throws Exception {
        // Initialize the database
        securitygroupruleRepository.saveAndFlush(securitygrouprule);
        securitygroupruleSearchRepository.save(securitygrouprule);
        int databaseSizeBeforeUpdate = securitygroupruleRepository.findAll().size();

        // Update the securitygrouprule
        Securitygrouprule updatedSecuritygrouprule = new Securitygrouprule();
        updatedSecuritygrouprule.setId(securitygrouprule.getId());
        updatedSecuritygrouprule.setRuletype(UPDATED_RULETYPE);
        updatedSecuritygrouprule.setProtocol(UPDATED_PROTOCOL);
        updatedSecuritygrouprule.setIprange(UPDATED_IPRANGE);
        updatedSecuritygrouprule.setFromport(UPDATED_FROMPORT);
        updatedSecuritygrouprule.setToport(UPDATED_TOPORT);

        restSecuritygroupruleMockMvc.perform(put("/api/securitygrouprules")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedSecuritygrouprule)))
                .andExpect(status().isOk());

        // Validate the Securitygrouprule in the database
        List<Securitygrouprule> securitygrouprules = securitygroupruleRepository.findAll();
        assertThat(securitygrouprules).hasSize(databaseSizeBeforeUpdate);
        Securitygrouprule testSecuritygrouprule = securitygrouprules.get(securitygrouprules.size() - 1);
        assertThat(testSecuritygrouprule.getRuletype()).isEqualTo(UPDATED_RULETYPE);
        assertThat(testSecuritygrouprule.getProtocol()).isEqualTo(UPDATED_PROTOCOL);
        assertThat(testSecuritygrouprule.getIprange()).isEqualTo(UPDATED_IPRANGE);
        assertThat(testSecuritygrouprule.getFromport()).isEqualTo(UPDATED_FROMPORT);
        assertThat(testSecuritygrouprule.getToport()).isEqualTo(UPDATED_TOPORT);

        // Validate the Securitygrouprule in ElasticSearch
        Securitygrouprule securitygroupruleEs = securitygroupruleSearchRepository.findOne(testSecuritygrouprule.getId());
        assertThat(securitygroupruleEs).isEqualToComparingFieldByField(testSecuritygrouprule);
    }

    @Test
    @Transactional
    public void deleteSecuritygrouprule() throws Exception {
        // Initialize the database
        securitygroupruleRepository.saveAndFlush(securitygrouprule);
        securitygroupruleSearchRepository.save(securitygrouprule);
        int databaseSizeBeforeDelete = securitygroupruleRepository.findAll().size();

        // Get the securitygrouprule
        restSecuritygroupruleMockMvc.perform(delete("/api/securitygrouprules/{id}", securitygrouprule.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean securitygroupruleExistsInEs = securitygroupruleSearchRepository.exists(securitygrouprule.getId());
        assertThat(securitygroupruleExistsInEs).isFalse();

        // Validate the database is empty
        List<Securitygrouprule> securitygrouprules = securitygroupruleRepository.findAll();
        assertThat(securitygrouprules).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchSecuritygrouprule() throws Exception {
        // Initialize the database
        securitygroupruleRepository.saveAndFlush(securitygrouprule);
        securitygroupruleSearchRepository.save(securitygrouprule);

        // Search the securitygrouprule
        restSecuritygroupruleMockMvc.perform(get("/api/_search/securitygrouprules?query=id:" + securitygrouprule.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(securitygrouprule.getId().intValue())))
            .andExpect(jsonPath("$.[*].ruletype").value(hasItem(DEFAULT_RULETYPE.toString())))
            .andExpect(jsonPath("$.[*].protocol").value(hasItem(DEFAULT_PROTOCOL.toString())))
            .andExpect(jsonPath("$.[*].iprange").value(hasItem(DEFAULT_IPRANGE.toString())))
            .andExpect(jsonPath("$.[*].fromport").value(hasItem(DEFAULT_FROMPORT)))
            .andExpect(jsonPath("$.[*].toport").value(hasItem(DEFAULT_TOPORT)));
    }
}

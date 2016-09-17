package com.innvo.web.rest;

import com.innvo.AdapCoreApp;
import com.innvo.domain.Recordtype;
import com.innvo.repository.RecordtypeRepository;
import com.innvo.repository.search.RecordtypeSearchRepository;

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

import com.innvo.domain.enumeration.Objecttype;

/**
 * Test class for the RecordtypeResource REST controller.
 *
 * @see RecordtypeResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AdapCoreApp.class)
@WebAppConfiguration
@IntegrationTest
public class RecordtypeResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));


    private static final Objecttype DEFAULT_OBJECTTYPE = Objecttype.Asset;
    private static final Objecttype UPDATED_OBJECTTYPE = Objecttype.Organization;
    private static final String DEFAULT_NAME = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
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
    private RecordtypeRepository recordtypeRepository;

    @Inject
    private RecordtypeSearchRepository recordtypeSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restRecordtypeMockMvc;

    private Recordtype recordtype;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        RecordtypeResource recordtypeResource = new RecordtypeResource();
        ReflectionTestUtils.setField(recordtypeResource, "recordtypeSearchRepository", recordtypeSearchRepository);
        ReflectionTestUtils.setField(recordtypeResource, "recordtypeRepository", recordtypeRepository);
        this.restRecordtypeMockMvc = MockMvcBuilders.standaloneSetup(recordtypeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        recordtypeSearchRepository.deleteAll();
        recordtype = new Recordtype();
        recordtype.setObjecttype(DEFAULT_OBJECTTYPE);
        recordtype.setName(DEFAULT_NAME);
        recordtype.setDescription(DEFAULT_DESCRIPTION);
        recordtype.setStatus(DEFAULT_STATUS);
        recordtype.setLastmodifiedby(DEFAULT_LASTMODIFIEDBY);
        recordtype.setLastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME);
        recordtype.setDomain(DEFAULT_DOMAIN);
    }

    @Test
    @Transactional
    public void createRecordtype() throws Exception {
        int databaseSizeBeforeCreate = recordtypeRepository.findAll().size();

        // Create the Recordtype

        restRecordtypeMockMvc.perform(post("/api/recordtypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(recordtype)))
                .andExpect(status().isCreated());

        // Validate the Recordtype in the database
        List<Recordtype> recordtypes = recordtypeRepository.findAll();
        assertThat(recordtypes).hasSize(databaseSizeBeforeCreate + 1);
        Recordtype testRecordtype = recordtypes.get(recordtypes.size() - 1);
        assertThat(testRecordtype.getObjecttype()).isEqualTo(DEFAULT_OBJECTTYPE);
        assertThat(testRecordtype.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRecordtype.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRecordtype.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testRecordtype.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testRecordtype.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testRecordtype.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Recordtype in ElasticSearch
        Recordtype recordtypeEs = recordtypeSearchRepository.findOne(testRecordtype.getId());
        assertThat(recordtypeEs).isEqualToComparingFieldByField(testRecordtype);
    }

    @Test
    @Transactional
    public void checkObjecttypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = recordtypeRepository.findAll().size();
        // set the field null
        recordtype.setObjecttype(null);

        // Create the Recordtype, which fails.

        restRecordtypeMockMvc.perform(post("/api/recordtypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(recordtype)))
                .andExpect(status().isBadRequest());

        List<Recordtype> recordtypes = recordtypeRepository.findAll();
        assertThat(recordtypes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = recordtypeRepository.findAll().size();
        // set the field null
        recordtype.setName(null);

        // Create the Recordtype, which fails.

        restRecordtypeMockMvc.perform(post("/api/recordtypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(recordtype)))
                .andExpect(status().isBadRequest());

        List<Recordtype> recordtypes = recordtypeRepository.findAll();
        assertThat(recordtypes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = recordtypeRepository.findAll().size();
        // set the field null
        recordtype.setStatus(null);

        // Create the Recordtype, which fails.

        restRecordtypeMockMvc.perform(post("/api/recordtypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(recordtype)))
                .andExpect(status().isBadRequest());

        List<Recordtype> recordtypes = recordtypeRepository.findAll();
        assertThat(recordtypes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = recordtypeRepository.findAll().size();
        // set the field null
        recordtype.setLastmodifiedby(null);

        // Create the Recordtype, which fails.

        restRecordtypeMockMvc.perform(post("/api/recordtypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(recordtype)))
                .andExpect(status().isBadRequest());

        List<Recordtype> recordtypes = recordtypeRepository.findAll();
        assertThat(recordtypes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = recordtypeRepository.findAll().size();
        // set the field null
        recordtype.setLastmodifieddatetime(null);

        // Create the Recordtype, which fails.

        restRecordtypeMockMvc.perform(post("/api/recordtypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(recordtype)))
                .andExpect(status().isBadRequest());

        List<Recordtype> recordtypes = recordtypeRepository.findAll();
        assertThat(recordtypes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = recordtypeRepository.findAll().size();
        // set the field null
        recordtype.setDomain(null);

        // Create the Recordtype, which fails.

        restRecordtypeMockMvc.perform(post("/api/recordtypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(recordtype)))
                .andExpect(status().isBadRequest());

        List<Recordtype> recordtypes = recordtypeRepository.findAll();
        assertThat(recordtypes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllRecordtypes() throws Exception {
        // Initialize the database
        recordtypeRepository.saveAndFlush(recordtype);

        // Get all the recordtypes
        restRecordtypeMockMvc.perform(get("/api/recordtypes?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(recordtype.getId().intValue())))
                .andExpect(jsonPath("$.[*].objecttype").value(hasItem(DEFAULT_OBJECTTYPE.toString())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
                .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
                .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void getRecordtype() throws Exception {
        // Initialize the database
        recordtypeRepository.saveAndFlush(recordtype);

        // Get the recordtype
        restRecordtypeMockMvc.perform(get("/api/recordtypes/{id}", recordtype.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(recordtype.getId().intValue()))
            .andExpect(jsonPath("$.objecttype").value(DEFAULT_OBJECTTYPE.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(DEFAULT_LASTMODIFIEDDATETIME_STR))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingRecordtype() throws Exception {
        // Get the recordtype
        restRecordtypeMockMvc.perform(get("/api/recordtypes/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRecordtype() throws Exception {
        // Initialize the database
        recordtypeRepository.saveAndFlush(recordtype);
        recordtypeSearchRepository.save(recordtype);
        int databaseSizeBeforeUpdate = recordtypeRepository.findAll().size();

        // Update the recordtype
        Recordtype updatedRecordtype = new Recordtype();
        updatedRecordtype.setId(recordtype.getId());
        updatedRecordtype.setObjecttype(UPDATED_OBJECTTYPE);
        updatedRecordtype.setName(UPDATED_NAME);
        updatedRecordtype.setDescription(UPDATED_DESCRIPTION);
        updatedRecordtype.setStatus(UPDATED_STATUS);
        updatedRecordtype.setLastmodifiedby(UPDATED_LASTMODIFIEDBY);
        updatedRecordtype.setLastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME);
        updatedRecordtype.setDomain(UPDATED_DOMAIN);

        restRecordtypeMockMvc.perform(put("/api/recordtypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedRecordtype)))
                .andExpect(status().isOk());

        // Validate the Recordtype in the database
        List<Recordtype> recordtypes = recordtypeRepository.findAll();
        assertThat(recordtypes).hasSize(databaseSizeBeforeUpdate);
        Recordtype testRecordtype = recordtypes.get(recordtypes.size() - 1);
        assertThat(testRecordtype.getObjecttype()).isEqualTo(UPDATED_OBJECTTYPE);
        assertThat(testRecordtype.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRecordtype.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testRecordtype.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testRecordtype.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testRecordtype.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testRecordtype.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Recordtype in ElasticSearch
        Recordtype recordtypeEs = recordtypeSearchRepository.findOne(testRecordtype.getId());
        assertThat(recordtypeEs).isEqualToComparingFieldByField(testRecordtype);
    }

    @Test
    @Transactional
    public void deleteRecordtype() throws Exception {
        // Initialize the database
        recordtypeRepository.saveAndFlush(recordtype);
        recordtypeSearchRepository.save(recordtype);
        int databaseSizeBeforeDelete = recordtypeRepository.findAll().size();

        // Get the recordtype
        restRecordtypeMockMvc.perform(delete("/api/recordtypes/{id}", recordtype.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean recordtypeExistsInEs = recordtypeSearchRepository.exists(recordtype.getId());
        assertThat(recordtypeExistsInEs).isFalse();

        // Validate the database is empty
        List<Recordtype> recordtypes = recordtypeRepository.findAll();
        assertThat(recordtypes).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchRecordtype() throws Exception {
        // Initialize the database
        recordtypeRepository.saveAndFlush(recordtype);
        recordtypeSearchRepository.save(recordtype);

        // Search the recordtype
        restRecordtypeMockMvc.perform(get("/api/_search/recordtypes?query=id:" + recordtype.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recordtype.getId().intValue())))
            .andExpect(jsonPath("$.[*].objecttype").value(hasItem(DEFAULT_OBJECTTYPE.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }
}

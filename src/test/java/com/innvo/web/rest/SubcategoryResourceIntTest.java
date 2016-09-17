package com.innvo.web.rest;

import com.innvo.AdapCoreApp;
import com.innvo.domain.Subcategory;
import com.innvo.repository.SubcategoryRepository;
import com.innvo.repository.search.SubcategorySearchRepository;

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
 * Test class for the SubcategoryResource REST controller.
 *
 * @see SubcategoryResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AdapCoreApp.class)
@WebAppConfiguration
@IntegrationTest
public class SubcategoryResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));

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
    private SubcategoryRepository subcategoryRepository;

    @Inject
    private SubcategorySearchRepository subcategorySearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restSubcategoryMockMvc;

    private Subcategory subcategory;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SubcategoryResource subcategoryResource = new SubcategoryResource();
        ReflectionTestUtils.setField(subcategoryResource, "subcategorySearchRepository", subcategorySearchRepository);
        ReflectionTestUtils.setField(subcategoryResource, "subcategoryRepository", subcategoryRepository);
        this.restSubcategoryMockMvc = MockMvcBuilders.standaloneSetup(subcategoryResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        subcategorySearchRepository.deleteAll();
        subcategory = new Subcategory();
        subcategory.setName(DEFAULT_NAME);
        subcategory.setDescription(DEFAULT_DESCRIPTION);
        subcategory.setStatus(DEFAULT_STATUS);
        subcategory.setLastmodifiedby(DEFAULT_LASTMODIFIEDBY);
        subcategory.setLastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME);
        subcategory.setDomain(DEFAULT_DOMAIN);
    }

    @Test
    @Transactional
    public void createSubcategory() throws Exception {
        int databaseSizeBeforeCreate = subcategoryRepository.findAll().size();

        // Create the Subcategory

        restSubcategoryMockMvc.perform(post("/api/subcategories")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(subcategory)))
                .andExpect(status().isCreated());

        // Validate the Subcategory in the database
        List<Subcategory> subcategories = subcategoryRepository.findAll();
        assertThat(subcategories).hasSize(databaseSizeBeforeCreate + 1);
        Subcategory testSubcategory = subcategories.get(subcategories.size() - 1);
        assertThat(testSubcategory.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSubcategory.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testSubcategory.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testSubcategory.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testSubcategory.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testSubcategory.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Subcategory in ElasticSearch
        Subcategory subcategoryEs = subcategorySearchRepository.findOne(testSubcategory.getId());
        assertThat(subcategoryEs).isEqualToComparingFieldByField(testSubcategory);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = subcategoryRepository.findAll().size();
        // set the field null
        subcategory.setName(null);

        // Create the Subcategory, which fails.

        restSubcategoryMockMvc.perform(post("/api/subcategories")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(subcategory)))
                .andExpect(status().isBadRequest());

        List<Subcategory> subcategories = subcategoryRepository.findAll();
        assertThat(subcategories).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = subcategoryRepository.findAll().size();
        // set the field null
        subcategory.setStatus(null);

        // Create the Subcategory, which fails.

        restSubcategoryMockMvc.perform(post("/api/subcategories")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(subcategory)))
                .andExpect(status().isBadRequest());

        List<Subcategory> subcategories = subcategoryRepository.findAll();
        assertThat(subcategories).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = subcategoryRepository.findAll().size();
        // set the field null
        subcategory.setLastmodifiedby(null);

        // Create the Subcategory, which fails.

        restSubcategoryMockMvc.perform(post("/api/subcategories")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(subcategory)))
                .andExpect(status().isBadRequest());

        List<Subcategory> subcategories = subcategoryRepository.findAll();
        assertThat(subcategories).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = subcategoryRepository.findAll().size();
        // set the field null
        subcategory.setLastmodifieddatetime(null);

        // Create the Subcategory, which fails.

        restSubcategoryMockMvc.perform(post("/api/subcategories")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(subcategory)))
                .andExpect(status().isBadRequest());

        List<Subcategory> subcategories = subcategoryRepository.findAll();
        assertThat(subcategories).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = subcategoryRepository.findAll().size();
        // set the field null
        subcategory.setDomain(null);

        // Create the Subcategory, which fails.

        restSubcategoryMockMvc.perform(post("/api/subcategories")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(subcategory)))
                .andExpect(status().isBadRequest());

        List<Subcategory> subcategories = subcategoryRepository.findAll();
        assertThat(subcategories).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSubcategories() throws Exception {
        // Initialize the database
        subcategoryRepository.saveAndFlush(subcategory);

        // Get all the subcategories
        restSubcategoryMockMvc.perform(get("/api/subcategories?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(subcategory.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
                .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
                .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void getSubcategory() throws Exception {
        // Initialize the database
        subcategoryRepository.saveAndFlush(subcategory);

        // Get the subcategory
        restSubcategoryMockMvc.perform(get("/api/subcategories/{id}", subcategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(subcategory.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(DEFAULT_LASTMODIFIEDDATETIME_STR))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSubcategory() throws Exception {
        // Get the subcategory
        restSubcategoryMockMvc.perform(get("/api/subcategories/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSubcategory() throws Exception {
        // Initialize the database
        subcategoryRepository.saveAndFlush(subcategory);
        subcategorySearchRepository.save(subcategory);
        int databaseSizeBeforeUpdate = subcategoryRepository.findAll().size();

        // Update the subcategory
        Subcategory updatedSubcategory = new Subcategory();
        updatedSubcategory.setId(subcategory.getId());
        updatedSubcategory.setName(UPDATED_NAME);
        updatedSubcategory.setDescription(UPDATED_DESCRIPTION);
        updatedSubcategory.setStatus(UPDATED_STATUS);
        updatedSubcategory.setLastmodifiedby(UPDATED_LASTMODIFIEDBY);
        updatedSubcategory.setLastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME);
        updatedSubcategory.setDomain(UPDATED_DOMAIN);

        restSubcategoryMockMvc.perform(put("/api/subcategories")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedSubcategory)))
                .andExpect(status().isOk());

        // Validate the Subcategory in the database
        List<Subcategory> subcategories = subcategoryRepository.findAll();
        assertThat(subcategories).hasSize(databaseSizeBeforeUpdate);
        Subcategory testSubcategory = subcategories.get(subcategories.size() - 1);
        assertThat(testSubcategory.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSubcategory.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSubcategory.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testSubcategory.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testSubcategory.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testSubcategory.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Subcategory in ElasticSearch
        Subcategory subcategoryEs = subcategorySearchRepository.findOne(testSubcategory.getId());
        assertThat(subcategoryEs).isEqualToComparingFieldByField(testSubcategory);
    }

    @Test
    @Transactional
    public void deleteSubcategory() throws Exception {
        // Initialize the database
        subcategoryRepository.saveAndFlush(subcategory);
        subcategorySearchRepository.save(subcategory);
        int databaseSizeBeforeDelete = subcategoryRepository.findAll().size();

        // Get the subcategory
        restSubcategoryMockMvc.perform(delete("/api/subcategories/{id}", subcategory.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean subcategoryExistsInEs = subcategorySearchRepository.exists(subcategory.getId());
        assertThat(subcategoryExistsInEs).isFalse();

        // Validate the database is empty
        List<Subcategory> subcategories = subcategoryRepository.findAll();
        assertThat(subcategories).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchSubcategory() throws Exception {
        // Initialize the database
        subcategoryRepository.saveAndFlush(subcategory);
        subcategorySearchRepository.save(subcategory);

        // Search the subcategory
        restSubcategoryMockMvc.perform(get("/api/_search/subcategories?query=id:" + subcategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(subcategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }
}

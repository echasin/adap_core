package com.innvo.web.rest;

import com.innvo.AdapCoreApp;
import com.innvo.domain.Key;
import com.innvo.repository.KeyRepository;
import com.innvo.repository.search.KeySearchRepository;

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
 * Test class for the KeyResource REST controller.
 *
 * @see KeyResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AdapCoreApp.class)
@WebAppConfiguration
@IntegrationTest
public class KeyResourceIntTest {

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
    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    @Inject
    private KeyRepository keyRepository;

    @Inject
    private KeySearchRepository keySearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restKeyMockMvc;

    private Key key;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        KeyResource keyResource = new KeyResource();
        ReflectionTestUtils.setField(keyResource, "keySearchRepository", keySearchRepository);
        ReflectionTestUtils.setField(keyResource, "keyRepository", keyRepository);
        this.restKeyMockMvc = MockMvcBuilders.standaloneSetup(keyResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        keySearchRepository.deleteAll();
        key = new Key();
        key.setName(DEFAULT_NAME);
        key.setStatus(DEFAULT_STATUS);
        key.setLastmodifiedby(DEFAULT_LASTMODIFIEDBY);
        key.setLastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME);
        key.setDomain(DEFAULT_DOMAIN);
        key.setDescription(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createKey() throws Exception {
        int databaseSizeBeforeCreate = keyRepository.findAll().size();

        // Create the Key

        restKeyMockMvc.perform(post("/api/keys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(key)))
                .andExpect(status().isCreated());

        // Validate the Key in the database
        List<Key> keys = keyRepository.findAll();
        assertThat(keys).hasSize(databaseSizeBeforeCreate + 1);
        Key testKey = keys.get(keys.size() - 1);
        assertThat(testKey.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testKey.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testKey.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testKey.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testKey.getDomain()).isEqualTo(DEFAULT_DOMAIN);
        assertThat(testKey.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the Key in ElasticSearch
        Key keyEs = keySearchRepository.findOne(testKey.getId());
        assertThat(keyEs).isEqualToComparingFieldByField(testKey);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = keyRepository.findAll().size();
        // set the field null
        key.setName(null);

        // Create the Key, which fails.

        restKeyMockMvc.perform(post("/api/keys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(key)))
                .andExpect(status().isBadRequest());

        List<Key> keys = keyRepository.findAll();
        assertThat(keys).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = keyRepository.findAll().size();
        // set the field null
        key.setStatus(null);

        // Create the Key, which fails.

        restKeyMockMvc.perform(post("/api/keys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(key)))
                .andExpect(status().isBadRequest());

        List<Key> keys = keyRepository.findAll();
        assertThat(keys).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = keyRepository.findAll().size();
        // set the field null
        key.setLastmodifiedby(null);

        // Create the Key, which fails.

        restKeyMockMvc.perform(post("/api/keys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(key)))
                .andExpect(status().isBadRequest());

        List<Key> keys = keyRepository.findAll();
        assertThat(keys).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = keyRepository.findAll().size();
        // set the field null
        key.setLastmodifieddatetime(null);

        // Create the Key, which fails.

        restKeyMockMvc.perform(post("/api/keys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(key)))
                .andExpect(status().isBadRequest());

        List<Key> keys = keyRepository.findAll();
        assertThat(keys).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = keyRepository.findAll().size();
        // set the field null
        key.setDomain(null);

        // Create the Key, which fails.

        restKeyMockMvc.perform(post("/api/keys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(key)))
                .andExpect(status().isBadRequest());

        List<Key> keys = keyRepository.findAll();
        assertThat(keys).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllKeys() throws Exception {
        // Initialize the database
        keyRepository.saveAndFlush(key);

        // Get all the keys
        restKeyMockMvc.perform(get("/api/keys?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(key.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
                .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
                .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getKey() throws Exception {
        // Initialize the database
        keyRepository.saveAndFlush(key);

        // Get the key
        restKeyMockMvc.perform(get("/api/keys/{id}", key.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(key.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(DEFAULT_LASTMODIFIEDDATETIME_STR))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingKey() throws Exception {
        // Get the key
        restKeyMockMvc.perform(get("/api/keys/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateKey() throws Exception {
        // Initialize the database
        keyRepository.saveAndFlush(key);
        keySearchRepository.save(key);
        int databaseSizeBeforeUpdate = keyRepository.findAll().size();

        // Update the key
        Key updatedKey = new Key();
        updatedKey.setId(key.getId());
        updatedKey.setName(UPDATED_NAME);
        updatedKey.setStatus(UPDATED_STATUS);
        updatedKey.setLastmodifiedby(UPDATED_LASTMODIFIEDBY);
        updatedKey.setLastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME);
        updatedKey.setDomain(UPDATED_DOMAIN);
        updatedKey.setDescription(UPDATED_DESCRIPTION);

        restKeyMockMvc.perform(put("/api/keys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedKey)))
                .andExpect(status().isOk());

        // Validate the Key in the database
        List<Key> keys = keyRepository.findAll();
        assertThat(keys).hasSize(databaseSizeBeforeUpdate);
        Key testKey = keys.get(keys.size() - 1);
        assertThat(testKey.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testKey.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testKey.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testKey.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testKey.getDomain()).isEqualTo(UPDATED_DOMAIN);
        assertThat(testKey.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the Key in ElasticSearch
        Key keyEs = keySearchRepository.findOne(testKey.getId());
        assertThat(keyEs).isEqualToComparingFieldByField(testKey);
    }

    @Test
    @Transactional
    public void deleteKey() throws Exception {
        // Initialize the database
        keyRepository.saveAndFlush(key);
        keySearchRepository.save(key);
        int databaseSizeBeforeDelete = keyRepository.findAll().size();

        // Get the key
        restKeyMockMvc.perform(delete("/api/keys/{id}", key.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean keyExistsInEs = keySearchRepository.exists(key.getId());
        assertThat(keyExistsInEs).isFalse();

        // Validate the database is empty
        List<Key> keys = keyRepository.findAll();
        assertThat(keys).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchKey() throws Exception {
        // Initialize the database
        keyRepository.saveAndFlush(key);
        keySearchRepository.save(key);

        // Search the key
        restKeyMockMvc.perform(get("/api/_search/keys?query=id:" + key.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(key.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }
}

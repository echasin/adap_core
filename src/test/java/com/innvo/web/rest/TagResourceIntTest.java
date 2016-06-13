package com.innvo.web.rest;

import com.innvo.AdapCoreApp;
import com.innvo.domain.Tag;
import com.innvo.repository.TagRepository;
import com.innvo.repository.search.TagSearchRepository;

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
 * Test class for the TagResource REST controller.
 *
 * @see TagResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AdapCoreApp.class)
@WebAppConfiguration
@IntegrationTest
public class TagResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));

    private static final String DEFAULT_NAME = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_RECORDTYPE = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_RECORDTYPE = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_CATEGORY = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_CATEGORY = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_SUBCATEGORY = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_SUBCATEGORY = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_TYPE = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
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
    private TagRepository tagRepository;

    @Inject
    private TagSearchRepository tagSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restTagMockMvc;

    private Tag tag;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TagResource tagResource = new TagResource();
        ReflectionTestUtils.setField(tagResource, "tagSearchRepository", tagSearchRepository);
        ReflectionTestUtils.setField(tagResource, "tagRepository", tagRepository);
        this.restTagMockMvc = MockMvcBuilders.standaloneSetup(tagResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        tagSearchRepository.deleteAll();
        tag = new Tag();
        tag.setName(DEFAULT_NAME);
        tag.setRecordtype(DEFAULT_RECORDTYPE);
        tag.setCategory(DEFAULT_CATEGORY);
        tag.setSubcategory(DEFAULT_SUBCATEGORY);
        tag.setType(DEFAULT_TYPE);
        tag.setStatus(DEFAULT_STATUS);
        tag.setLastmodifiedby(DEFAULT_LASTMODIFIEDBY);
        tag.setLastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME);
        tag.setDomain(DEFAULT_DOMAIN);
    }

    @Test
    @Transactional
    public void createTag() throws Exception {
        int databaseSizeBeforeCreate = tagRepository.findAll().size();

        // Create the Tag

        restTagMockMvc.perform(post("/api/tags")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tag)))
                .andExpect(status().isCreated());

        // Validate the Tag in the database
        List<Tag> tags = tagRepository.findAll();
        assertThat(tags).hasSize(databaseSizeBeforeCreate + 1);
        Tag testTag = tags.get(tags.size() - 1);
        assertThat(testTag.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTag.getRecordtype()).isEqualTo(DEFAULT_RECORDTYPE);
        assertThat(testTag.getCategory()).isEqualTo(DEFAULT_CATEGORY);
        assertThat(testTag.getSubcategory()).isEqualTo(DEFAULT_SUBCATEGORY);
        assertThat(testTag.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testTag.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testTag.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testTag.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testTag.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Tag in ElasticSearch
        Tag tagEs = tagSearchRepository.findOne(testTag.getId());
        assertThat(tagEs).isEqualToComparingFieldByField(testTag);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = tagRepository.findAll().size();
        // set the field null
        tag.setName(null);

        // Create the Tag, which fails.

        restTagMockMvc.perform(post("/api/tags")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tag)))
                .andExpect(status().isBadRequest());

        List<Tag> tags = tagRepository.findAll();
        assertThat(tags).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkRecordtypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = tagRepository.findAll().size();
        // set the field null
        tag.setRecordtype(null);

        // Create the Tag, which fails.

        restTagMockMvc.perform(post("/api/tags")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tag)))
                .andExpect(status().isBadRequest());

        List<Tag> tags = tagRepository.findAll();
        assertThat(tags).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = tagRepository.findAll().size();
        // set the field null
        tag.setStatus(null);

        // Create the Tag, which fails.

        restTagMockMvc.perform(post("/api/tags")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tag)))
                .andExpect(status().isBadRequest());

        List<Tag> tags = tagRepository.findAll();
        assertThat(tags).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = tagRepository.findAll().size();
        // set the field null
        tag.setLastmodifiedby(null);

        // Create the Tag, which fails.

        restTagMockMvc.perform(post("/api/tags")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tag)))
                .andExpect(status().isBadRequest());

        List<Tag> tags = tagRepository.findAll();
        assertThat(tags).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = tagRepository.findAll().size();
        // set the field null
        tag.setLastmodifieddatetime(null);

        // Create the Tag, which fails.

        restTagMockMvc.perform(post("/api/tags")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tag)))
                .andExpect(status().isBadRequest());

        List<Tag> tags = tagRepository.findAll();
        assertThat(tags).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = tagRepository.findAll().size();
        // set the field null
        tag.setDomain(null);

        // Create the Tag, which fails.

        restTagMockMvc.perform(post("/api/tags")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tag)))
                .andExpect(status().isBadRequest());

        List<Tag> tags = tagRepository.findAll();
        assertThat(tags).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTags() throws Exception {
        // Initialize the database
        tagRepository.saveAndFlush(tag);

        // Get all the tags
        restTagMockMvc.perform(get("/api/tags?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(tag.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].recordtype").value(hasItem(DEFAULT_RECORDTYPE.toString())))
                .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
                .andExpect(jsonPath("$.[*].subcategory").value(hasItem(DEFAULT_SUBCATEGORY.toString())))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
                .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
                .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void getTag() throws Exception {
        // Initialize the database
        tagRepository.saveAndFlush(tag);

        // Get the tag
        restTagMockMvc.perform(get("/api/tags/{id}", tag.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(tag.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.recordtype").value(DEFAULT_RECORDTYPE.toString()))
            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY.toString()))
            .andExpect(jsonPath("$.subcategory").value(DEFAULT_SUBCATEGORY.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(DEFAULT_LASTMODIFIEDDATETIME_STR))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTag() throws Exception {
        // Get the tag
        restTagMockMvc.perform(get("/api/tags/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTag() throws Exception {
        // Initialize the database
        tagRepository.saveAndFlush(tag);
        tagSearchRepository.save(tag);
        int databaseSizeBeforeUpdate = tagRepository.findAll().size();

        // Update the tag
        Tag updatedTag = new Tag();
        updatedTag.setId(tag.getId());
        updatedTag.setName(UPDATED_NAME);
        updatedTag.setRecordtype(UPDATED_RECORDTYPE);
        updatedTag.setCategory(UPDATED_CATEGORY);
        updatedTag.setSubcategory(UPDATED_SUBCATEGORY);
        updatedTag.setType(UPDATED_TYPE);
        updatedTag.setStatus(UPDATED_STATUS);
        updatedTag.setLastmodifiedby(UPDATED_LASTMODIFIEDBY);
        updatedTag.setLastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME);
        updatedTag.setDomain(UPDATED_DOMAIN);

        restTagMockMvc.perform(put("/api/tags")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedTag)))
                .andExpect(status().isOk());

        // Validate the Tag in the database
        List<Tag> tags = tagRepository.findAll();
        assertThat(tags).hasSize(databaseSizeBeforeUpdate);
        Tag testTag = tags.get(tags.size() - 1);
        assertThat(testTag.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTag.getRecordtype()).isEqualTo(UPDATED_RECORDTYPE);
        assertThat(testTag.getCategory()).isEqualTo(UPDATED_CATEGORY);
        assertThat(testTag.getSubcategory()).isEqualTo(UPDATED_SUBCATEGORY);
        assertThat(testTag.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testTag.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testTag.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testTag.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testTag.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Tag in ElasticSearch
        Tag tagEs = tagSearchRepository.findOne(testTag.getId());
        assertThat(tagEs).isEqualToComparingFieldByField(testTag);
    }

    @Test
    @Transactional
    public void deleteTag() throws Exception {
        // Initialize the database
        tagRepository.saveAndFlush(tag);
        tagSearchRepository.save(tag);
        int databaseSizeBeforeDelete = tagRepository.findAll().size();

        // Get the tag
        restTagMockMvc.perform(delete("/api/tags/{id}", tag.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean tagExistsInEs = tagSearchRepository.exists(tag.getId());
        assertThat(tagExistsInEs).isFalse();

        // Validate the database is empty
        List<Tag> tags = tagRepository.findAll();
        assertThat(tags).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchTag() throws Exception {
        // Initialize the database
        tagRepository.saveAndFlush(tag);
        tagSearchRepository.save(tag);

        // Search the tag
        restTagMockMvc.perform(get("/api/_search/tags?query=id:" + tag.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tag.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].recordtype").value(hasItem(DEFAULT_RECORDTYPE.toString())))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].subcategory").value(hasItem(DEFAULT_SUBCATEGORY.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }
}

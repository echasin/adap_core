package com.innvo.web.rest;

import com.innvo.AdapCoreApp;
import com.innvo.domain.Activitymbr;
import com.innvo.repository.ActivitymbrRepository;
import com.innvo.repository.search.ActivitymbrSearchRepository;

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


/**
 * Test class for the ActivitymbrResource REST controller.
 *
 * @see ActivitymbrResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AdapCoreApp.class)
@WebAppConfiguration
@IntegrationTest
public class ActivitymbrResourceIntTest {


    @Inject
    private ActivitymbrRepository activitymbrRepository;

    @Inject
    private ActivitymbrSearchRepository activitymbrSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restActivitymbrMockMvc;

    private Activitymbr activitymbr;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ActivitymbrResource activitymbrResource = new ActivitymbrResource();
        ReflectionTestUtils.setField(activitymbrResource, "activitymbrSearchRepository", activitymbrSearchRepository);
        ReflectionTestUtils.setField(activitymbrResource, "activitymbrRepository", activitymbrRepository);
        this.restActivitymbrMockMvc = MockMvcBuilders.standaloneSetup(activitymbrResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        activitymbrSearchRepository.deleteAll();
        activitymbr = new Activitymbr();
    }

    @Test
    @Transactional
    public void createActivitymbr() throws Exception {
        int databaseSizeBeforeCreate = activitymbrRepository.findAll().size();

        // Create the Activitymbr

        restActivitymbrMockMvc.perform(post("/api/activitymbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(activitymbr)))
                .andExpect(status().isCreated());

        // Validate the Activitymbr in the database
        List<Activitymbr> activitymbrs = activitymbrRepository.findAll();
        assertThat(activitymbrs).hasSize(databaseSizeBeforeCreate + 1);
        Activitymbr testActivitymbr = activitymbrs.get(activitymbrs.size() - 1);

        // Validate the Activitymbr in ElasticSearch
        Activitymbr activitymbrEs = activitymbrSearchRepository.findOne(testActivitymbr.getId());
        assertThat(activitymbrEs).isEqualToComparingFieldByField(testActivitymbr);
    }

    @Test
    @Transactional
    public void getAllActivitymbrs() throws Exception {
        // Initialize the database
        activitymbrRepository.saveAndFlush(activitymbr);

        // Get all the activitymbrs
        restActivitymbrMockMvc.perform(get("/api/activitymbrs?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(activitymbr.getId().intValue())));
    }

    @Test
    @Transactional
    public void getActivitymbr() throws Exception {
        // Initialize the database
        activitymbrRepository.saveAndFlush(activitymbr);

        // Get the activitymbr
        restActivitymbrMockMvc.perform(get("/api/activitymbrs/{id}", activitymbr.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(activitymbr.getId().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingActivitymbr() throws Exception {
        // Get the activitymbr
        restActivitymbrMockMvc.perform(get("/api/activitymbrs/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateActivitymbr() throws Exception {
        // Initialize the database
        activitymbrRepository.saveAndFlush(activitymbr);
        activitymbrSearchRepository.save(activitymbr);
        int databaseSizeBeforeUpdate = activitymbrRepository.findAll().size();

        // Update the activitymbr
        Activitymbr updatedActivitymbr = new Activitymbr();
        updatedActivitymbr.setId(activitymbr.getId());

        restActivitymbrMockMvc.perform(put("/api/activitymbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedActivitymbr)))
                .andExpect(status().isOk());

        // Validate the Activitymbr in the database
        List<Activitymbr> activitymbrs = activitymbrRepository.findAll();
        assertThat(activitymbrs).hasSize(databaseSizeBeforeUpdate);
        Activitymbr testActivitymbr = activitymbrs.get(activitymbrs.size() - 1);

        // Validate the Activitymbr in ElasticSearch
        Activitymbr activitymbrEs = activitymbrSearchRepository.findOne(testActivitymbr.getId());
        assertThat(activitymbrEs).isEqualToComparingFieldByField(testActivitymbr);
    }

    @Test
    @Transactional
    public void deleteActivitymbr() throws Exception {
        // Initialize the database
        activitymbrRepository.saveAndFlush(activitymbr);
        activitymbrSearchRepository.save(activitymbr);
        int databaseSizeBeforeDelete = activitymbrRepository.findAll().size();

        // Get the activitymbr
        restActivitymbrMockMvc.perform(delete("/api/activitymbrs/{id}", activitymbr.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean activitymbrExistsInEs = activitymbrSearchRepository.exists(activitymbr.getId());
        assertThat(activitymbrExistsInEs).isFalse();

        // Validate the database is empty
        List<Activitymbr> activitymbrs = activitymbrRepository.findAll();
        assertThat(activitymbrs).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchActivitymbr() throws Exception {
        // Initialize the database
        activitymbrRepository.saveAndFlush(activitymbr);
        activitymbrSearchRepository.save(activitymbr);

        // Search the activitymbr
        restActivitymbrMockMvc.perform(get("/api/_search/activitymbrs?query=id:" + activitymbr.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(activitymbr.getId().intValue())));
    }
}

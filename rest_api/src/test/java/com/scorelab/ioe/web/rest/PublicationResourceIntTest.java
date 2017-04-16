package com.scorelab.ioe.web.rest;

import com.scorelab.ioe.IoeApp;
import com.scorelab.ioe.domain.Publication;
import com.scorelab.ioe.repository.PublicationRepository;

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
 * Test class for the PublicationResource REST controller.
 *
 * @see PublicationResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IoeApp.class)
@WebAppConfiguration
@IntegrationTest
public class PublicationResourceIntTest {


    private static final Long DEFAULT_PUBLICATION_ID = 1L;
    private static final Long UPDATED_PUBLICATION_ID = 2L;

    @Inject
    private PublicationRepository publicationRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restPublicationMockMvc;

    private Publication publication;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PublicationResource publicationResource = new PublicationResource();
        ReflectionTestUtils.setField(publicationResource, "publicationRepository", publicationRepository);
        this.restPublicationMockMvc = MockMvcBuilders.standaloneSetup(publicationResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        publication = new Publication();
        publication.setPublicationId(DEFAULT_PUBLICATION_ID);
    }

    @Test
    @Transactional
    public void createPublication() throws Exception {
        int databaseSizeBeforeCreate = publicationRepository.findAll().size();

        // Create the Publication

        restPublicationMockMvc.perform(post("/api/publications")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(publication)))
                .andExpect(status().isCreated());

        // Validate the Publication in the database
        List<Publication> publications = publicationRepository.findAll();
        assertThat(publications).hasSize(databaseSizeBeforeCreate + 1);
        Publication testPublication = publications.get(publications.size() - 1);
        assertThat(testPublication.getPublicationId()).isEqualTo(DEFAULT_PUBLICATION_ID);
    }

    @Test
    @Transactional
    public void getAllPublications() throws Exception {
        // Initialize the database
        publicationRepository.saveAndFlush(publication);

        // Get all the publications
        restPublicationMockMvc.perform(get("/api/publications?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(publication.getId().intValue())))
                .andExpect(jsonPath("$.[*].publicationId").value(hasItem(DEFAULT_PUBLICATION_ID.intValue())));
    }

    @Test
    @Transactional
    public void getPublication() throws Exception {
        // Initialize the database
        publicationRepository.saveAndFlush(publication);

        // Get the publication
        restPublicationMockMvc.perform(get("/api/publications/{id}", publication.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(publication.getId().intValue()))
            .andExpect(jsonPath("$.publicationId").value(DEFAULT_PUBLICATION_ID.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingPublication() throws Exception {
        // Get the publication
        restPublicationMockMvc.perform(get("/api/publications/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePublication() throws Exception {
        // Initialize the database
        publicationRepository.saveAndFlush(publication);
        int databaseSizeBeforeUpdate = publicationRepository.findAll().size();

        // Update the publication
        Publication updatedPublication = new Publication();
        updatedPublication.setId(publication.getId());
        updatedPublication.setPublicationId(UPDATED_PUBLICATION_ID);

        restPublicationMockMvc.perform(put("/api/publications")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedPublication)))
                .andExpect(status().isOk());

        // Validate the Publication in the database
        List<Publication> publications = publicationRepository.findAll();
        assertThat(publications).hasSize(databaseSizeBeforeUpdate);
        Publication testPublication = publications.get(publications.size() - 1);
        assertThat(testPublication.getPublicationId()).isEqualTo(UPDATED_PUBLICATION_ID);
    }

    @Test
    @Transactional
    public void deletePublication() throws Exception {
        // Initialize the database
        publicationRepository.saveAndFlush(publication);
        int databaseSizeBeforeDelete = publicationRepository.findAll().size();

        // Get the publication
        restPublicationMockMvc.perform(delete("/api/publications/{id}", publication.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Publication> publications = publicationRepository.findAll();
        assertThat(publications).hasSize(databaseSizeBeforeDelete - 1);
    }
}

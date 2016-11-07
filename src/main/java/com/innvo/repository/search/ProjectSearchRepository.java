package com.innvo.repository.search;

import com.innvo.domain.Project;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Project entity.
 */
public interface ProjectSearchRepository extends ElasticsearchRepository<Project, Long> {
}

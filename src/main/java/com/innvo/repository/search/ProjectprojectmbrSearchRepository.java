package com.innvo.repository.search;

import com.innvo.domain.Projectprojectmbr;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Projectprojectmbr entity.
 */
public interface ProjectprojectmbrSearchRepository extends ElasticsearchRepository<Projectprojectmbr, Long> {
}

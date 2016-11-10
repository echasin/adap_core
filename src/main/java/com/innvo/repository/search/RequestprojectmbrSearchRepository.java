package com.innvo.repository.search;

import com.innvo.domain.Requestprojectmbr;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Requestprojectmbr entity.
 */
public interface RequestprojectmbrSearchRepository extends ElasticsearchRepository<Requestprojectmbr, Long> {
}

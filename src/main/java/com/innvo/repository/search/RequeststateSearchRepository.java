package com.innvo.repository.search;

import com.innvo.domain.Requeststate;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Requeststate entity.
 */
public interface RequeststateSearchRepository extends ElasticsearchRepository<Requeststate, Long> {
}

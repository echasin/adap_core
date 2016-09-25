package com.innvo.repository.search;

import com.innvo.domain.Key;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Key entity.
 */
public interface KeySearchRepository extends ElasticsearchRepository<Key, Long> {
}

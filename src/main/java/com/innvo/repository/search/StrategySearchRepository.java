package com.innvo.repository.search;

import com.innvo.domain.Strategy;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Strategy entity.
 */
public interface StrategySearchRepository extends ElasticsearchRepository<Strategy, Long> {
}

package com.innvo.repository.search;

import com.innvo.domain.Strategymbr;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Strategymbr entity.
 */
public interface StrategymbrSearchRepository extends ElasticsearchRepository<Strategymbr, Long> {
}

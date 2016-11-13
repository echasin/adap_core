package com.innvo.repository.search;

import com.innvo.domain.Fiscalyear;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Fiscalyear entity.
 */
public interface FiscalyearSearchRepository extends ElasticsearchRepository<Fiscalyear, Long> {
}

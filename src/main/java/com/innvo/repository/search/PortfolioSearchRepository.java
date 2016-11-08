package com.innvo.repository.search;

import com.innvo.domain.Portfolio;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Portfolio entity.
 */
public interface PortfolioSearchRepository extends ElasticsearchRepository<Portfolio, Long> {
}

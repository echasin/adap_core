package com.innvo.repository.search;

import com.innvo.domain.Portfolioprojectmbr;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Portfolioprojectmbr entity.
 */
public interface PortfolioprojectmbrSearchRepository extends ElasticsearchRepository<Portfolioprojectmbr, Long> {
}

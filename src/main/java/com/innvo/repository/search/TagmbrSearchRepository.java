package com.innvo.repository.search;

import com.innvo.domain.Tagmbr;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Tagmbr entity.
 */
public interface TagmbrSearchRepository extends ElasticsearchRepository<Tagmbr, Long> {
}

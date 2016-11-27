package com.innvo.repository.search;

import com.innvo.domain.Activitymbr;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Activitymbr entity.
 */
public interface ActivitymbrSearchRepository extends ElasticsearchRepository<Activitymbr, Long> {
}

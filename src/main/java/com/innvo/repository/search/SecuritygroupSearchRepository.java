package com.innvo.repository.search;

import com.innvo.domain.Securitygroup;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Securitygroup entity.
 */
public interface SecuritygroupSearchRepository extends ElasticsearchRepository<Securitygroup, Long> {
}

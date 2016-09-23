package com.innvo.repository.search;

import com.innvo.domain.Securitygrouprule;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Securitygrouprule entity.
 */
public interface SecuritygroupruleSearchRepository extends ElasticsearchRepository<Securitygrouprule, Long> {
}

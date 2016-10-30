package com.innvo.repository.search;

import com.innvo.domain.Dashboard;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Dashboard entity.
 */
public interface DashboardSearchRepository extends ElasticsearchRepository<Dashboard, Long> {
}

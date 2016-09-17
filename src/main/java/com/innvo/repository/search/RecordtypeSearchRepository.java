package com.innvo.repository.search;

import com.innvo.domain.Recordtype;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Recordtype entity.
 */
public interface RecordtypeSearchRepository extends ElasticsearchRepository<Recordtype, Long> {
}

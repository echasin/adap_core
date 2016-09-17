package com.innvo.repository.search;

import com.innvo.domain.Subcategory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Subcategory entity.
 */
public interface SubcategorySearchRepository extends ElasticsearchRepository<Subcategory, Long> {
}

package com.innvo.repository;

import com.innvo.domain.Asset;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Asset entity.
 */
@SuppressWarnings("unused")
public interface AssetRepository extends JpaRepository<Asset,Long> {

    @Query("select distinct asset from Asset asset left join fetch asset.categories left join fetch asset.subcategories")
    List<Asset> findAllWithEagerRelationships();

    @Query("select asset from Asset asset left join fetch asset.categories left join fetch asset.subcategories where asset.id =:id")
    Asset findOneWithEagerRelationships(@Param("id") Long id);

}

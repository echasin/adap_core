package com.innvo.repository;

import com.innvo.domain.Asset;
import com.innvo.domain.Location;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

/**
 * Spring Data JPA repository for the Asset entity.
 */
@SuppressWarnings("unused")
public interface AssetRepository extends JpaRepository<Asset,Long> {
	
	@Query("SELECT d FROM Location d WHERE d.asset IN :result")
    public Set<Location> findByAssetId(@Param("result") Asset result);

}

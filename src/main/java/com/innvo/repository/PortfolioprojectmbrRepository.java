package com.innvo.repository;

import com.innvo.domain.Portfolioprojectmbr;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Portfolioprojectmbr entity.
 */
@SuppressWarnings("unused")
public interface PortfolioprojectmbrRepository extends JpaRepository<Portfolioprojectmbr,Long> {

	
	int countByPortfoliolhsId(long id);
	
	Page<Portfolioprojectmbr> findByPortfoliolhsIdAndProjectrhsRecordtypeName(long id,String name,Pageable pageable);
}

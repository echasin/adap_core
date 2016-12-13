package com.innvo.repository;

import com.innvo.domain.Projectprojectmbr;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Projectprojectmbr entity.
 */
@SuppressWarnings("unused")
public interface ProjectprojectmbrRepository extends JpaRepository<Projectprojectmbr,Long> {
	
	Page<Projectprojectmbr> findByProjectrhsIdOrProjectlhsId(long rId,long lId,Pageable pageable);
}

package com.innvo.repository;

import com.innvo.domain.Activitymbr;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Activitymbr entity.
 */
@SuppressWarnings("unused")
public interface ActivitymbrRepository extends JpaRepository<Activitymbr,Long> {
	
	List<Activitymbr> findByProjectId(long id);

}

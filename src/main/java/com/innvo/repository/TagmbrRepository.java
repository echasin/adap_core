package com.innvo.repository;

import com.innvo.domain.Tagmbr;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Tagmbr entity.
 */
@SuppressWarnings("unused")
public interface TagmbrRepository extends JpaRepository<Tagmbr,Long> {

}

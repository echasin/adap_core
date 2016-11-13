package com.innvo.repository;

import com.innvo.domain.Fiscalyear;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Fiscalyear entity.
 */
@SuppressWarnings("unused")
public interface FiscalyearRepository extends JpaRepository<Fiscalyear,Long> {

}

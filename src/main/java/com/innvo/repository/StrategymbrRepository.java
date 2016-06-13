package com.innvo.repository;

import com.innvo.domain.Strategymbr;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Strategymbr entity.
 */
@SuppressWarnings("unused")
public interface StrategymbrRepository extends JpaRepository<Strategymbr,Long> {

}

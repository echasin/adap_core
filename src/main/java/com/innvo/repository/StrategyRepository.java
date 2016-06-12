package com.innvo.repository;

import com.innvo.domain.Strategy;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Strategy entity.
 */
@SuppressWarnings("unused")
public interface StrategyRepository extends JpaRepository<Strategy,Long> {

}

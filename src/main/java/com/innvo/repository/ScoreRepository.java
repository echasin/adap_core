package com.innvo.repository;

import com.innvo.domain.Score;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Score entity.
 */
@SuppressWarnings("unused")
public interface ScoreRepository extends JpaRepository<Score,Long> {

}

package com.innvo.repository;

import com.innvo.domain.Key;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Key entity.
 */
@SuppressWarnings("unused")
public interface KeyRepository extends JpaRepository<Key,Long> {

}

package com.innvo.repository;

import com.innvo.domain.Requeststate;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Requeststate entity.
 */
@SuppressWarnings("unused")
public interface RequeststateRepository extends JpaRepository<Requeststate,Long> {

}

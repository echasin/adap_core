package com.innvo.repository;

import com.innvo.domain.Portfolioprojectmbr;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Portfolioprojectmbr entity.
 */
@SuppressWarnings("unused")
public interface PortfolioprojectmbrRepository extends JpaRepository<Portfolioprojectmbr,Long> {

}

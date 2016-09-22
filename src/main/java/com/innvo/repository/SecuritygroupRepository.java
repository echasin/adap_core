package com.innvo.repository;

import com.innvo.domain.Securitygroup;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Securitygroup entity.
 */
@SuppressWarnings("unused")
public interface SecuritygroupRepository extends JpaRepository<Securitygroup,Long> {

}

package com.innvo.repository;

import com.innvo.domain.Securitygrouprule;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Securitygrouprule entity.
 */
@SuppressWarnings("unused")
public interface SecuritygroupruleRepository extends JpaRepository<Securitygrouprule,Long> {

}

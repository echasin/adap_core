package com.innvo.repository;

import com.innvo.domain.Organizationorganizationmbr;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Organizationorganizationmbr entity.
 */
@SuppressWarnings("unused")
public interface OrganizationorganizationmbrRepository extends JpaRepository<Organizationorganizationmbr,Long> {

    @Query("select distinct organizationorganizationmbr from Organizationorganizationmbr organizationorganizationmbr left join fetch organizationorganizationmbr.categories")
    List<Organizationorganizationmbr> findAllWithEagerRelationships();

    @Query("select organizationorganizationmbr from Organizationorganizationmbr organizationorganizationmbr left join fetch organizationorganizationmbr.categories where organizationorganizationmbr.id =:id")
    Organizationorganizationmbr findOneWithEagerRelationships(@Param("id") Long id);

}

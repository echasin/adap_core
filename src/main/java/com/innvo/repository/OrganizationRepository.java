package com.innvo.repository;

import com.innvo.domain.Organization;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Organization entity.
 */
@SuppressWarnings("unused")
public interface OrganizationRepository extends JpaRepository<Organization,Long> {

    @Query("select distinct organization from Organization organization left join fetch organization.categories left join fetch organization.subcategories")
    List<Organization> findAllWithEagerRelationships();

    @Query("select organization from Organization organization left join fetch organization.categories left join fetch organization.subcategories where organization.id =:id")
    Organization findOneWithEagerRelationships(@Param("id") Long id);

}

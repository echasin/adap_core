package com.innvo.repository;

import com.innvo.domain.Projectprojectmbr;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Projectprojectmbr entity.
 */
@SuppressWarnings("unused")
public interface ProjectprojectmbrRepository extends JpaRepository<Projectprojectmbr,Long> {

    @Query("select distinct projectprojectmbr from Projectprojectmbr projectprojectmbr left join fetch projectprojectmbr.categories")
    List<Projectprojectmbr> findAllWithEagerRelationships();

    @Query("select projectprojectmbr from Projectprojectmbr projectprojectmbr left join fetch projectprojectmbr.categories where projectprojectmbr.id =:id")
    Projectprojectmbr findOneWithEagerRelationships(@Param("id") Long id);

}

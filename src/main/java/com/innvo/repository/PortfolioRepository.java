package com.innvo.repository;

import com.innvo.domain.Portfolio;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Portfolio entity.
 */
@SuppressWarnings("unused")
public interface PortfolioRepository extends JpaRepository<Portfolio,Long> {

    @Query("select distinct portfolio from Portfolio portfolio left join fetch portfolio.categories left join fetch portfolio.subcategories")
    List<Portfolio> findAllWithEagerRelationships();

    @Query("select portfolio from Portfolio portfolio left join fetch portfolio.categories left join fetch portfolio.subcategories where portfolio.id =:id")
    Portfolio findOneWithEagerRelationships(@Param("id") Long id);

}

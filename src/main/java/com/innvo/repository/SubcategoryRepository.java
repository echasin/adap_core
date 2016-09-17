package com.innvo.repository;

import com.innvo.domain.Subcategory;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Subcategory entity.
 */
@SuppressWarnings("unused")
public interface SubcategoryRepository extends JpaRepository<Subcategory,Long> {

}

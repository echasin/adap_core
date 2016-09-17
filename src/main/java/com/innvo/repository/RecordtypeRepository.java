package com.innvo.repository;

import com.innvo.domain.Recordtype;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Recordtype entity.
 */
@SuppressWarnings("unused")
public interface RecordtypeRepository extends JpaRepository<Recordtype,Long> {

}

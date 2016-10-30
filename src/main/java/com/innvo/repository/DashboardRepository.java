package com.innvo.repository;

import com.innvo.domain.Dashboard;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Dashboard entity.
 */
@SuppressWarnings("unused")
public interface DashboardRepository extends JpaRepository<Dashboard,Long> {

}

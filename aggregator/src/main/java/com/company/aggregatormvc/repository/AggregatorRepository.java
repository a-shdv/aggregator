package com.company.aggregatormvc.repository;

import com.company.aggregatormvc.model.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AggregatorRepository extends JpaRepository<Vacancy, Long> {
}
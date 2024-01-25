package com.company.aggregator.repository;

import com.company.aggregator.model.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AggregatorRepository extends JpaRepository<Vacancy, Long> {
    Vacancy findBySource(String source);
}
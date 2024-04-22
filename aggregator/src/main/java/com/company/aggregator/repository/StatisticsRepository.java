package com.company.aggregator.repository;

import com.company.aggregator.entity.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatisticsRepository extends JpaRepository<Statistics, Long> {
    Optional<Statistics> findByUsername(String username);
}

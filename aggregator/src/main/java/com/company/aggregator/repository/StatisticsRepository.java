package com.company.aggregator.repository;

import com.company.aggregator.entity.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatisticsRepository extends JpaRepository<Statistics, Long> {
    Statistics findByUsername(String username);
}

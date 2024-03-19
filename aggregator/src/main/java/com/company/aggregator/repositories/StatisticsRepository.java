package com.company.aggregator.repositories;

import com.company.aggregator.models.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatisticsRepository extends JpaRepository<Statistics, Long> {
}

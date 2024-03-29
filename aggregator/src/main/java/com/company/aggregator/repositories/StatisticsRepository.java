package com.company.aggregator.repositories;

import com.company.aggregator.models.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatisticsRepository extends JpaRepository<Statistics, Long> {
    Optional<Statistics> findStatisticsByUsername(String username);
}

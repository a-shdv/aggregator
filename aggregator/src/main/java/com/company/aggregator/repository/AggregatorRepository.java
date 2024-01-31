package com.company.aggregator.repository;

import com.company.aggregator.model.User;
import com.company.aggregator.model.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AggregatorRepository extends JpaRepository<Vacancy, Long> {
    Vacancy findBySource(String source);

    List<Vacancy> findByUser(User user);
}
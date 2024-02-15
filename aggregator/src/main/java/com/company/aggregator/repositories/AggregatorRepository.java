package com.company.aggregator.repositories;

import com.company.aggregator.models.User;
import com.company.aggregator.models.Vacancy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AggregatorRepository extends JpaRepository<Vacancy, Long> {
    Vacancy findBySource(String source);

    Page<Vacancy> findByUser(User user, PageRequest pageRequest);

    List<Vacancy> findByUser(User user);
}
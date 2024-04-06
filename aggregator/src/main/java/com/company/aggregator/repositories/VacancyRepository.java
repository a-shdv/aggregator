package com.company.aggregator.repositories;

import com.company.aggregator.models.User;
import com.company.aggregator.models.Vacancy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
    Optional<Vacancy> findBySource(String source);

    Page<Vacancy> findByUser(User user, PageRequest pageRequest);

    List<Vacancy> findByUser(User user);
}
package com.company.aggregator.repository;

import com.company.aggregator.entity.User;
import com.company.aggregator.entity.Vacancy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
    Optional<Vacancy> findVacancyBySource(String source);

    Optional<Page<Vacancy>> findVacancyByUser(User user, PageRequest pageRequest);

    Optional<List<Vacancy>> findVacanciesByUser(User user);
}
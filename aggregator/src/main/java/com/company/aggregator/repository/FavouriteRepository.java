package com.company.aggregator.repository;

import com.company.aggregator.model.Favourite;
import com.company.aggregator.model.User;
import com.company.aggregator.model.Vacancy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavouriteRepository extends JpaRepository<Favourite, Long> {
    Favourite findBySource(String source);

    List<Favourite> findByUser(User user);

    Page<Favourite> findByUser(User user, PageRequest pageRequest);

}

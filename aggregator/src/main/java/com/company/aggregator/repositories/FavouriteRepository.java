package com.company.aggregator.repositories;

import com.company.aggregator.models.Favourite;
import com.company.aggregator.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavouriteRepository extends JpaRepository<Favourite, Long> {
    Favourite findBySource(String source);

    List<Favourite> findByUser(User user);

    Page<Favourite> findByUser(User user, PageRequest pageRequest);

}

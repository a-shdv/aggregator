package com.company.aggregator.repository;

import com.company.aggregator.entity.Favourite;
import com.company.aggregator.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavouriteRepository extends JpaRepository<Favourite, Long> {
    Favourite findBySource(String source);

    List<Favourite> findListByUser(User user);

    Page<Favourite> findListByUser(User user, PageRequest pageRequest);

    Favourite findByUserAndSource(User user, String source);
}

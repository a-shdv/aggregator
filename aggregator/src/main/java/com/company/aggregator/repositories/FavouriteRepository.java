package com.company.aggregator.repositories;

import com.company.aggregator.models.Favourite;
import com.company.aggregator.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavouriteRepository extends JpaRepository<Favourite, Long> {
    Optional<List<Favourite>> findFavouritesByUser(User user);

    Optional<Page<Favourite>> findFavouritesByUser(User user, PageRequest pageRequest);

    Optional<Favourite> findByUserAndSource(User user, String source);
}

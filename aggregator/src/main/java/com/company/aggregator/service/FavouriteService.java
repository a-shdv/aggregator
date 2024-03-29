package com.company.aggregator.service;

import com.company.aggregator.exception.favourite.FavouriteAlreadyExistsException;
import com.company.aggregator.exception.favourite.FavouriteNotFoundException;
import com.company.aggregator.exception.favourite.FavouritesIsEmptyException;
import com.company.aggregator.entity.Favourite;
import com.company.aggregator.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface FavouriteService {
    Page<Favourite> findFavourites(User user, PageRequest pageRequest);

    List<Favourite> findFavouritesByUser(User user) throws FavouritesIsEmptyException;

    void deleteFromFavourites(User user, Long id) throws FavouriteNotFoundException;

    void deleteAllFavourites(User user);

    void addToFavourites(User user, Favourite favourite) throws FavouriteAlreadyExistsException;
}

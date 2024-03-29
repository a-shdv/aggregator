package com.company.aggregator.services;

import com.company.aggregator.exceptions.favourite.FavouriteAlreadyExistsException;
import com.company.aggregator.exceptions.favourite.FavouriteNotFoundException;
import com.company.aggregator.exceptions.favourite.FavouritesIsEmptyException;
import com.company.aggregator.models.Favourite;
import com.company.aggregator.models.User;
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

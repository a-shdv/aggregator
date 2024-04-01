package com.company.aggregator.services;

import com.company.aggregator.exceptions.FavouriteAlreadyExistsException;
import com.company.aggregator.exceptions.FavouriteNotFoundException;
import com.company.aggregator.exceptions.FavouritesIsEmptyException;
import com.company.aggregator.models.Favourite;
import com.company.aggregator.models.User;
import com.company.aggregator.repositories.FavouriteRepository;
import com.company.aggregator.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavouriteService {
    private final FavouriteRepository favouriteRepository;
    private final UserRepository userRepository;

    @Transactional
    public Page<Favourite> findFavourites(User user, PageRequest pageRequest) {
        return favouriteRepository.findListByUser(user, pageRequest);
    }

    @Transactional
    public void addToFavourites(User user, Favourite favourite) throws FavouriteAlreadyExistsException {
        if (favouriteRepository.findByUserAndSource(user, favourite.getSource()) != null) {
            throw new FavouriteAlreadyExistsException("Вакансия уже существует в избранном " + favourite.getSource());
        }
        favourite.setUser(user);
        favouriteRepository.save(favourite);
    }

    @Transactional
    public void deleteFavourites(User user) {
        List<Favourite> favourites = favouriteRepository.findListByUser(user);
        favourites.clear();
        user.setFavourites(favourites);
        userRepository.save(user);
    }

    @Transactional
    public void deleteFromFavourites(User user, Long id) throws FavouriteNotFoundException {
        Optional<Favourite> favourite = favouriteRepository.findById(id);
        if (favourite.isEmpty()) {
            throw new FavouriteNotFoundException("Вакансия не найдена!");
        }
        List<Favourite> favourites = favouriteRepository.findListByUser(user);
        favourites.remove(favourite.get());
        user.setFavourites(favourites);
        userRepository.save(user);
        favouriteRepository.deleteById(id);
    }

    @Transactional
    public List<Favourite> findByUser(User user) throws FavouritesIsEmptyException {
        List<Favourite> favourites = favouriteRepository.findListByUser(user);
        if (favourites.isEmpty()) {
            throw new FavouritesIsEmptyException("Список избранных вакансий пуст!");
        }
        return favourites;
    }
}

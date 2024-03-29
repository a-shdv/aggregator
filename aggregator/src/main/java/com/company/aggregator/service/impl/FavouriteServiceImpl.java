package com.company.aggregator.service.impl;

import com.company.aggregator.exception.favourite.FavouriteAlreadyExistsException;
import com.company.aggregator.exception.favourite.FavouriteNotFoundException;
import com.company.aggregator.exception.favourite.FavouritesIsEmptyException;
import com.company.aggregator.entity.Favourite;
import com.company.aggregator.entity.User;
import com.company.aggregator.repository.FavouriteRepository;
import com.company.aggregator.repository.UserRepository;
import com.company.aggregator.service.FavouriteService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FavouriteServiceImpl implements FavouriteService {
    FavouriteRepository favouriteRepository;
    UserRepository userRepository;

    @Override
    @Transactional
    public Page<Favourite> findFavourites(User user, PageRequest pageRequest) {
        return favouriteRepository.findFavouritesByUser(user, pageRequest).get();
    }

    @Override
    @Transactional
    public List<Favourite> findFavouritesByUser(User user) throws FavouritesIsEmptyException {
        List<Favourite> favourites = favouriteRepository.findFavouritesByUser(user).get();
        if (favourites.isEmpty()) {
            throw new FavouritesIsEmptyException("Список избранных вакансий пуст!");
        }
        return favourites;
    }

    @Override
    @Transactional
    public void deleteAllFavourites(User user) {
        List<Favourite> favourites = favouriteRepository.findFavouritesByUser(user).get();
        favourites.clear();
        user.setFavourites(favourites);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void addToFavourites(User user, Favourite favourite) throws FavouriteAlreadyExistsException {
        if (favouriteRepository.findByUserAndSource(user, favourite.getSource()).isPresent()) {
            throw new FavouriteAlreadyExistsException("Вакансия уже существует в избранном " + favourite.getSource());
        }
        favourite.setUser(user);
        favouriteRepository.save(favourite);
    }

    @Override
    @Transactional
    public void deleteFromFavourites(User user, Long id) throws FavouriteNotFoundException {
        Optional<Favourite> favourite = favouriteRepository.findById(id);
        if (favourite.isEmpty()) {
            throw new FavouriteNotFoundException("Вакансия не найдена!");
        }
        List<Favourite> favourites = favouriteRepository.findFavouritesByUser(user).get();
        favourites.remove(favourite.get());
        user.setFavourites(favourites);
        userRepository.save(user);
        favouriteRepository.deleteById(id);
    }
}

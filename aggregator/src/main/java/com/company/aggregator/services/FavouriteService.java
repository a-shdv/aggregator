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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavouriteService {
    private final FavouriteRepository favouriteRepository;
    private final UserRepository userRepository;

    @Async("asyncExecutor")
    @Transactional
    public CompletableFuture<Page<Favourite>> findFavouritesAsync(User user, PageRequest pageRequest) {
        return CompletableFuture.completedFuture(favouriteRepository.findListByUser(user, pageRequest));
    }

    @Async("asyncExecutor")
    @Transactional
    public void addToFavouritesAsync(User user, Favourite favourite) throws FavouriteAlreadyExistsException {
        if (favouriteRepository.findBySource(favourite.getSource()) != null) {
            throw new FavouriteAlreadyExistsException("Вакансия уже существует в избранном " + favourite.getSource());
        }
        favourite.setUser(user);
        favouriteRepository.save(favourite);
    }

    @Async("asyncExecutor")
    @Transactional
    public void deleteFavouritesAsync(User user) {
        List<Favourite> favourites = favouriteRepository.findListByUser(user);
        favourites.clear();
        user.setFavourites(favourites);
        userRepository.save(user);
    }

    @Async("asyncExecutor")
    @Transactional
    public void deleteFromFavouritesAsync(User user, Long id) throws FavouriteNotFoundException {
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

    @Async("asyncExecutor")
    @Transactional
    public CompletableFuture<Favourite> findBySourceAsync(String source) {
        return CompletableFuture.completedFuture(favouriteRepository.findBySource(source));
    }

    @Async("asyncExecutor")
    @Transactional
    public CompletableFuture<List<Favourite>> findByUser(User user) throws FavouritesIsEmptyException {
        CompletableFuture<List<Favourite>> favourites = CompletableFuture.completedFuture(favouriteRepository.findListByUser(user));
        if (favourites.join().isEmpty()) {
            throw new FavouritesIsEmptyException("Список избранных вакансий пуст!");
        }
        return favourites;
    }
}

package com.company.aggregator.services;

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
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavouriteService {
    private final FavouriteRepository favouriteRepository;
    private final UserRepository userRepository;

    @Async
    @Transactional
    public CompletableFuture<Page<Favourite>> findFavouritesAsync(User user, PageRequest pageRequest) {
        return CompletableFuture.completedFuture(favouriteRepository.findByUser(user, pageRequest));
    }

    @Async
    @Transactional
    public void addToFavouritesAsync(User user, Favourite favourite) {
        favourite.setUser(user);
        favouriteRepository.save(favourite);
    }

    @Async
    @Transactional
    public void deleteFavourites(User user) {
        List<Favourite> favourites = favouriteRepository.findByUser(user);
        favourites.clear();
        user.setFavourites(favourites);
        userRepository.save(user);
    }

    @Async
    @Transactional
    public CompletableFuture<Favourite> findBySourceAsync(String source) {
        return CompletableFuture.completedFuture(favouriteRepository.findBySource(source));
    }

    @Async
    @Transactional
    public CompletableFuture<List<Favourite>> findByUser(User user) throws FavouritesIsEmptyException {
        CompletableFuture<List<Favourite>> favourites = CompletableFuture.completedFuture(favouriteRepository.findByUser(user));
        if (favourites.join().isEmpty()) {
            throw new FavouritesIsEmptyException("Список избранных вакансий пуст!");
        }
        return favourites;
    }
}

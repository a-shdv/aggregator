package com.company.aggregator.service;

import com.company.aggregator.model.Favourite;
import com.company.aggregator.model.User;
import com.company.aggregator.repository.FavouriteRepository;
import com.company.aggregator.repository.UserRepository;
import com.company.aggregator.utils.Role;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final FavouriteRepository favouriteRepository;
    private final PasswordEncoder passwordEncoder;

    @Async
    @Transactional
    public void saveUserAsync(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singleton(Role.USER));
        userRepository.save(user);
    }

    @Async
    @Transactional
    public CompletableFuture<User> findUserByUsernameAsync(String username) {
        return CompletableFuture.completedFuture((User) loadUserByUsername(username));
    }

    @Transactional
    public User findUserByUsername(String username) {
        return (User) loadUserByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(username);
    }
}

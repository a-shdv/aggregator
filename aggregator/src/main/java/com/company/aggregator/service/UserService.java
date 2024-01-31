package com.company.aggregator.service;

import com.company.aggregator.model.User;
import com.company.aggregator.repository.UserRepository;
import com.company.aggregator.utils.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CompletableFuture<Void> saveUserAsync(User user) {
        return CompletableFuture.runAsync(() -> {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(Collections.singleton(Role.USER));
            userRepository.save(user);
        });
    }

    public User findUserByUsername(String username) {
        return (User) loadUserByUsername(username);
    }

    public CompletableFuture<User> findUserByUsernameAsync(String username) {
        return CompletableFuture.supplyAsync(() -> (User) loadUserByUsername(username));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(username);
    }
}

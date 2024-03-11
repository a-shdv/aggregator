package com.company.aggregator.services;

import com.company.aggregator.dtos.ChangePasswordDto;
import com.company.aggregator.dtos.ChangeUsernameDto;
import com.company.aggregator.exceptions.UserAlreadyExistsException;
import com.company.aggregator.models.User;
import com.company.aggregator.repositories.FavouriteRepository;
import com.company.aggregator.repositories.UserRepository;
import com.company.aggregator.enums.Role;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
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

    @Async
    @Transactional
    public void changeUsername(User user, ChangeUsernameDto changeUsernameDto) throws UserAlreadyExistsException {
        String updatedUsername = changeUsernameDto.getUsername();
        if (loadUserByUsername(updatedUsername) != null) {
            throw new UserAlreadyExistsException("User with username " + changeUsernameDto.getUsername() + " already exists!");
        }
        user.setUsername(updatedUsername);
        userRepository.save(user);
    }

    @Async
    @Transactional
    public void changePassword(User user, ChangePasswordDto changePasswordDto) {
        if (passwordEncoder.matches(changePasswordDto.oldPassword(), user.getPassword())) {

        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(username);
    }
}

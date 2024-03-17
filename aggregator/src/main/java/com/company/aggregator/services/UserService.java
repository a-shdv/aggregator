package com.company.aggregator.services;

import com.company.aggregator.dtos.ChangePasswordDto;
import com.company.aggregator.dtos.UserLockStatusDto;
import com.company.aggregator.enums.Role;
import com.company.aggregator.models.User;
import com.company.aggregator.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${constants.admin.email}")
    private String adminEmail;
    @Value("${constants.admin.login}")
    private String adminUsername;
    @Value("${constants.admin.password}")
    private String adminPassword;

    @PostConstruct
    private void saveAdmin() {
        if (loadUserByUsername(adminUsername) == null) {
            User admin = User.builder()
                    .email(adminEmail)
                    .username(adminUsername)
                    .password(passwordEncoder.encode(adminPassword))
                    .roles(Collections.singleton(Role.ADMIN))
                    .isAccountNonLocked(true)
                    .build();
            log.info("admin: { email: {}, username: {}, password: {} }", adminEmail, adminUsername, adminPassword);
            userRepository.save(admin);
        }
    }

    @Async("asyncExecutor")
    @Transactional
    public CompletableFuture<User> saveUserAsync(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singleton(Role.USER));
        user.setAccountNonLocked(true);
        return CompletableFuture.completedFuture(userRepository.save(user));
    }

    @Async("asyncExecutor")
    @Transactional
    public CompletableFuture<List<User>> findUsersAsync(PageRequest pageRequest) {
        return CompletableFuture.completedFuture(userRepository
                .findAll(pageRequest)
                .stream()
                .filter(el -> el.getRoles().stream().findFirst().get().getAuthority().equals("USER")).collect(Collectors.toList()));
    }

    @Async("asyncExecutor")
    @Transactional
    public CompletableFuture<User> findUserByUsernameAsync(String username) {
        return CompletableFuture.completedFuture((User) loadUserByUsername(username));
    }

    @Transactional
    public User findUserByUsername(String username) {
        return (User) loadUserByUsername(username);
    }

//    @Async
//    @Transactional
//    public void changeUsername(User user, ChangeUsernameDto changeUsernameDto) {
//        String updatedUsername = changeUsernameDto.getUsername();
//        user.setUsername(updatedUsername);
//        userRepository.save(user);
//    }

    @Async("asyncExecutor")
    @Transactional
    public CompletableFuture<User> changePassword(User user, ChangePasswordDto changePasswordDto) {
        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        return CompletableFuture.completedFuture(userRepository.save(user));
    }

    @Async("asyncExecutor")
    @Transactional
    public CompletableFuture<User> block(UserLockStatusDto userLockStatusDto) {
        User user = (User) loadUserByUsername(userLockStatusDto.getUsername());
        user.setAccountNonLocked(false);
        return CompletableFuture.completedFuture(userRepository.save(user));
    }

    @Async("asyncExecutor")
    @Transactional
    public CompletableFuture<User> unblock(UserLockStatusDto userLockStatusDto) {
        User user = (User) loadUserByUsername(userLockStatusDto.getUsername());
        user.setAccountNonLocked(true);
        return CompletableFuture.completedFuture(userRepository.save(user));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(username);
    }
}

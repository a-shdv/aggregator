package com.company.aggregator.services;

import com.company.aggregator.dtos.ChangePasswordDto;
import com.company.aggregator.dtos.UserLockStatusDto;
import com.company.aggregator.enums.Role;
import com.company.aggregator.models.Image;
import com.company.aggregator.models.User;
import com.company.aggregator.repositories.ImageStorageRepository;
import com.company.aggregator.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final ImageStorageRepository imageStorageRepository;
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

    @Transactional
    public void signUpUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singleton(Role.USER));
        user.setAccountNonLocked(true);
        userRepository.save(user);
    }

    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Transactional
    public List<User> findUsers(PageRequest pageRequest) {
        return userRepository
                .findAll(pageRequest)
                .stream()
                .filter(el -> el.getRoles().stream().findFirst().get().getAuthority().equals("USER")).collect(Collectors.toList());
    }

    @Transactional
    public User findUserByUsername(String username) {
        return (User) loadUserByUsername(username);
    }

    @Transactional
    public void changePassword(User user, ChangePasswordDto changePasswordDto) {
        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void block(UserLockStatusDto userLockStatusDto) {
        User user = (User) loadUserByUsername(userLockStatusDto.getUsername());
        user.setAccountNonLocked(false);
        userRepository.save(user);
    }

    @Transactional
    public void unblock(UserLockStatusDto userLockStatusDto) {
        User user = (User) loadUserByUsername(userLockStatusDto.getUsername());
        user.setAccountNonLocked(true);
        userRepository.save(user);
    }

    @Transactional
    public void uploadAvatar(User user, Image avatar) {
        avatar.setUser(user);
        user.setAvatar(avatar);
        userRepository.save(user);
        imageStorageRepository.save(avatar);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(username);
    }
}

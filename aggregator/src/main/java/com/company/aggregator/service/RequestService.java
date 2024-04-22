package com.company.aggregator.service;

import com.company.aggregator.dto.RequestDto;
import com.company.aggregator.entity.Request;
import com.company.aggregator.entity.User;
import com.company.aggregator.repository.RequestRepository;
import com.company.aggregator.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    @Transactional
    public void save(RequestDto requestDto) {
        Optional<User> user = userRepository.findByUsername(requestDto.username());
        if (user.isPresent()) {
            Optional<Request> requestFromDb = requestRepository.findByUser(user.get());
            requestFromDb.ifPresent(requestRepository::delete);
            Request request = Request.builder()
                    .title(requestDto.title())
                    .salary(requestDto.salary())
                    .onlyWithSalary(requestDto.onlyWithSalary())
                    .experience(requestDto.experience())
                    .cityId(requestDto.cityId())
                    .isRemoteAvailable(requestDto.isRemoteAvailable())
                    .numOfRequests(requestDto.numOfRequests())
                    .build();
            request.setUser(user.get());
            user.get().setRequest(request);
            requestRepository.save(request);
            userRepository.save(user.get());
        }
    }

    public void save(Request request) {
        requestRepository.save(request);
    }

    @Transactional
    public Request findByUser(User user) {
        return requestRepository.findByUser(user).get();
    }
}

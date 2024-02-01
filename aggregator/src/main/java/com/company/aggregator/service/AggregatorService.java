package com.company.aggregator.service;

import com.company.aggregator.model.User;
import com.company.aggregator.model.Vacancy;
import com.company.aggregator.rabbitmq.dto.ReceiveMessageDto;
import com.company.aggregator.repository.AggregatorRepository;
import com.company.aggregator.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AggregatorService {
    private final AggregatorRepository aggregatorRepository;
    private final UserRepository userRepository;

    @Transactional
    public CompletableFuture<Void> saveMessage(ReceiveMessageDto receiveMessageDto) {
        return CompletableFuture.runAsync(() -> aggregatorRepository.save(ReceiveMessageDto.toVacancy(receiveMessageDto)));
    }

    @Transactional
    public CompletableFuture<Void> saveMessageListAsync(List<ReceiveMessageDto> receiveMessageDtoList, User user) {
        return CompletableFuture.runAsync(() -> {
            List<Vacancy> vacancies = ReceiveMessageDto.toVacancyList(receiveMessageDtoList);
            vacancies.forEach(vacancy -> vacancy.setUser(user));
            aggregatorRepository.saveAll(vacancies);
        });
    }

    @Transactional
    public CompletableFuture<List<Vacancy>> findVacanciesAsync() {
        return CompletableFuture.supplyAsync(aggregatorRepository::findAll);
    }

    @Transactional
    public CompletableFuture<Page<Vacancy>> findVacanciesAsync(User user, PageRequest pageRequest) {
        return CompletableFuture.supplyAsync(() -> aggregatorRepository.findByUser(user, pageRequest));
    }

    @Transactional
    public CompletableFuture<Void> deleteVacanciesByUserAsync(User user) {
        return CompletableFuture.runAsync(() -> {
            List<Vacancy> vacancies = aggregatorRepository.findByUser(user);
            vacancies.clear();
            user.setVacancies(vacancies);
            userRepository.save(user);
        });
    }

    public Vacancy findBySource(String source) {
        return aggregatorRepository.findBySource(source);
    }
}

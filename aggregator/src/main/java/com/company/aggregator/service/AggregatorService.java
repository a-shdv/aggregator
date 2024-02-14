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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AggregatorService {
    private final AggregatorRepository aggregatorRepository;
    private final UserRepository userRepository;

    @Async
    @Transactional
    public void saveMessage(ReceiveMessageDto receiveMessageDto) {
        aggregatorRepository.save(ReceiveMessageDto.toVacancy(receiveMessageDto));
    }


    @Async
    @Transactional
    public void saveMessageListAsync(List<ReceiveMessageDto> receiveMessageDtoList, User user) {
        List<Vacancy> vacancies = ReceiveMessageDto.toVacancyList(receiveMessageDtoList);
        vacancies.forEach(vacancy -> vacancy.setUser(user));
        aggregatorRepository.saveAll(vacancies);
    }


    @Async
    @Transactional
    public CompletableFuture<List<Vacancy>> findVacanciesAsync() {
        return CompletableFuture.completedFuture(aggregatorRepository.findAll());
    }


    @Async
    @Transactional
    public CompletableFuture<Page<Vacancy>> findVacanciesAsync(User user, PageRequest pageRequest) {
        return CompletableFuture.completedFuture(aggregatorRepository.findByUser(user, pageRequest));
    }

    @Async
    @Transactional
    public void deleteVacanciesByUserAsync(User user) {
        List<Vacancy> vacancies = aggregatorRepository.findByUser(user);
        vacancies.clear();
        user.setVacancies(vacancies);
        userRepository.save(user);
    }

    public Vacancy findBySource(String source) {
        return aggregatorRepository.findBySource(source);
    }
}

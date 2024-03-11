package com.company.aggregator.services;

import com.company.aggregator.models.User;
import com.company.aggregator.models.Vacancy;
import com.company.aggregator.rabbitmq.dtos.ReceiveMessageDto;
import com.company.aggregator.repositories.AggregatorRepository;
import com.company.aggregator.repositories.UserRepository;
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

    @Async("jobExecutor")
    @Transactional
    public void saveMessage(ReceiveMessageDto receiveMessageDto) {
        aggregatorRepository.save(ReceiveMessageDto.toVacancy(receiveMessageDto));
    }


    @Async("jobExecutor")
    @Transactional
    public void saveMessageListAsync(List<ReceiveMessageDto> receiveMessageDtoList, User user) {
        List<Vacancy> vacancies = ReceiveMessageDto.toVacancyList(receiveMessageDtoList);
        vacancies.forEach(vacancy -> vacancy.setUser(user));
        aggregatorRepository.saveAll(vacancies);
    }


    @Async("jobExecutor")
    @Transactional
    public CompletableFuture<List<Vacancy>> findVacanciesAsync() {
        return CompletableFuture.completedFuture(aggregatorRepository.findAll());
    }


    @Async("jobExecutor")
    @Transactional
    public CompletableFuture<Page<Vacancy>> findVacanciesAsync(User user, PageRequest pageRequest) {
        return CompletableFuture.completedFuture(aggregatorRepository.findByUser(user, pageRequest));
    }

    @Async("jobExecutor")
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

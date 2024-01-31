package com.company.aggregator.service;

import com.company.aggregator.model.User;
import com.company.aggregator.model.Vacancy;
import com.company.aggregator.rabbitmq.dto.ReceiveMessageDto;
import com.company.aggregator.repository.AggregatorRepository;
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

    @Transactional
    public CompletableFuture<Void> saveMessage(ReceiveMessageDto receiveMessageDto) {
        return CompletableFuture.runAsync(() -> aggregatorRepository.save(ReceiveMessageDto.toVacancy(receiveMessageDto)));
    }

    @Transactional
    public CompletableFuture<Void> saveMessageListAsync(List<ReceiveMessageDto> receiveMessageDtoList) {
        return CompletableFuture.runAsync(() -> {
            List<Vacancy> vacancies = ReceiveMessageDto.toVacancyList(receiveMessageDtoList);
            aggregatorRepository.saveAll(vacancies);
        });
    }

    @Transactional
    public CompletableFuture<List<Vacancy>> findVacanciesAsync() {
        return CompletableFuture.supplyAsync(aggregatorRepository::findAll);
    }

    @Transactional
    public CompletableFuture<Page<Vacancy>> findVacanciesAsync(PageRequest pageRequest) {
        return CompletableFuture.supplyAsync(() -> aggregatorRepository.findAll(pageRequest));
    }

    @Transactional
    public CompletableFuture<Void> deleteVacanciesAsync() {
        return CompletableFuture.runAsync(aggregatorRepository::deleteAll);
    }

    public Vacancy findBySource(String source) {
        return aggregatorRepository.findBySource(source);
    }
}

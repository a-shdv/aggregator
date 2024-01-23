package com.company.aggregator.service;

import com.company.aggregator.model.Vacancy;
import com.company.aggregator.rabbitmq.dto.ReceiveMessageDto;
import com.company.aggregator.repository.AggregatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class AggregatorService {
    private final AggregatorRepository aggregatorRepository;

    @Autowired
    public AggregatorService(AggregatorRepository aggregatorRepository) {
        this.aggregatorRepository = aggregatorRepository;
    }

    public CompletableFuture<Void> save(ReceiveMessageDto receiveMessageDto) {
        return CompletableFuture.runAsync(() -> aggregatorRepository.save(ReceiveMessageDto.toVacancy(receiveMessageDto)));
    }

    public CompletableFuture<List<Vacancy>> findAll() {
        return CompletableFuture.supplyAsync(aggregatorRepository::findAll);
    }

    public CompletableFuture<Page<Vacancy>> findAll(PageRequest pageRequest) {
        return CompletableFuture.supplyAsync(() -> aggregatorRepository.findAll(pageRequest));
    }
}

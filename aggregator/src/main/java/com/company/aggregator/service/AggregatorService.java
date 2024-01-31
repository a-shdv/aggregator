package com.company.aggregator.service;

import com.company.aggregator.model.Vacancy;
import com.company.aggregator.rabbitmq.dto.ReceiveMessageDto;
import com.company.aggregator.repository.AggregatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class AggregatorService {
    private final AggregatorRepository aggregatorRepository;

    @Autowired
    public AggregatorService(AggregatorRepository aggregatorRepository) {
        this.aggregatorRepository = aggregatorRepository;
    }

    @Transactional
    public CompletableFuture<Void> saveMessage(ReceiveMessageDto receiveMessageDto) {
        return CompletableFuture.runAsync(() -> aggregatorRepository.save(ReceiveMessageDto.toVacancy(receiveMessageDto)));
    }

    @Transactional
    public CompletableFuture<Void> saveMessageList(List<ReceiveMessageDto> receiveMessageDtoList) {
        return CompletableFuture.runAsync(() -> aggregatorRepository.saveAll(ReceiveMessageDto.toVacancyList(receiveMessageDtoList)));
    }

    @Transactional
    public CompletableFuture<List<Vacancy>> findVacancies() {
        return CompletableFuture.supplyAsync(aggregatorRepository::findAll);
    }

    @Transactional
    public CompletableFuture<Page<Vacancy>> findVacancies(PageRequest pageRequest) {
        return CompletableFuture.supplyAsync(() -> aggregatorRepository.findAll(pageRequest));
    }

    @Transactional
    public CompletableFuture<Void> deleteVacancies() {
        return CompletableFuture.runAsync(aggregatorRepository::deleteAll);
    }

    public Vacancy findBySource(String source) {
        return aggregatorRepository.findBySource(source);
    }
}

package com.company.aggregator.services;

import com.company.aggregator.models.Statistics;
import com.company.aggregator.rabbitmq.dtos.statistics.ReceiveMessageDto;
import com.company.aggregator.repositories.StatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final StatisticsRepository statisticsRepository;

    public CompletableFuture<Statistics> saveStatisticsAsync(com.company.aggregator.rabbitmq.dtos.statistics.ReceiveMessageDto message) {
        return CompletableFuture.completedFuture(statisticsRepository.save(ReceiveMessageDto.toStatistics(message)));
    }

    public CompletableFuture<Void> statistics() {

        return CompletableFuture.completedFuture(null);
    }
}

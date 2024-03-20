package com.company.aggregator.services;

import com.company.aggregator.models.Statistics;
import com.company.aggregator.models.User;
import com.company.aggregator.rabbitmq.dtos.statistics.ReceiveMessageDto;
import com.company.aggregator.repositories.StatisticsRepository;
import com.company.aggregator.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final StatisticsRepository statisticsRepository;
    private final UserRepository userRepository;

    @Async
    @Transactional
    public CompletableFuture<Statistics> saveStatisticsAsync(User user, com.company.aggregator.rabbitmq.dtos.statistics.ReceiveMessageDto message) {
        return CompletableFuture.completedFuture(statisticsRepository.save(ReceiveMessageDto.toStatistics(message)));
    }

    @Async
    @Transactional
    public CompletableFuture<User> deleteStatisticsByUserAsync(User user) {
        Statistics statistics = statisticsRepository.findStatisticsByUsername(user.getUsername());
        statisticsRepository.delete(statistics);
        user.setStatistics(null);
        return CompletableFuture.completedFuture(userRepository.save(user));
    }

    public CompletableFuture<Void> statistics() {

        return CompletableFuture.completedFuture(null);
    }
}

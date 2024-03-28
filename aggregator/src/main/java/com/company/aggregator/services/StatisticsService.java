package com.company.aggregator.services;

import com.company.aggregator.dtos.StatisticsDto;
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

    @Transactional
    public void deleteStatisticsAsync(User user) {
        Statistics statistics = statisticsRepository.findStatisticsByUsername(user.getUsername());
        if (statistics != null) {
            user.setStatistics(null);
            userRepository.save(user);
            statisticsRepository.delete(statistics);
        }
    }

    @Transactional
    public void saveStatisticsAsync(User user, com.company.aggregator.rabbitmq.dtos.statistics.ReceiveMessageDto message) {
        Statistics statistics = ReceiveMessageDto.toStatistics(message);
        statistics.setUser(user);
        user.setStatistics(statistics);
        statisticsRepository.save(statistics);
        userRepository.save(user);
    }

    @Transactional
    public Statistics findStatisticsByUsernameAsync(String username) {
        return statisticsRepository.findStatisticsByUsername(username);
    }

    @Transactional
    public void deleteStatisticsAsync(StatisticsDto statisticsDto) {
        User user = userRepository.findUserByUsername(statisticsDto.getUsername());
        if (user.getStatistics() != null) {
            Statistics statistics = statisticsRepository.findStatisticsByUsername(user.getUsername());
            statisticsRepository.delete(statistics);
            user.setStatistics(null);
            userRepository.save(user);
        }
    }
}

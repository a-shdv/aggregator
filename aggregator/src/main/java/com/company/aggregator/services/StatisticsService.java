package com.company.aggregator.services;

import com.company.aggregator.dtos.StatisticsDto;
import com.company.aggregator.models.Statistics;
import com.company.aggregator.models.User;
import com.company.aggregator.rabbitmq.dtos.statistics.ReceiveMessageDto;
import com.company.aggregator.repositories.StatisticsRepository;
import com.company.aggregator.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final StatisticsRepository statisticsRepository;
    private final UserRepository userRepository;

    @Transactional
    public void deleteStatistics(User user) {
        Statistics statistics = statisticsRepository.findStatisticsByUsername(user.getUsername());
        if (statistics != null) {
            user.setStatistics(null);
            userRepository.save(user);
            statisticsRepository.delete(statistics);
        }
    }

    @Transactional
    public void saveStatistics(User user, com.company.aggregator.rabbitmq.dtos.statistics.ReceiveMessageDto message) {
        Statistics statistics = ReceiveMessageDto.toStatistics(message);
        statistics.setUser(user);
        user.setStatistics(statistics);
        statisticsRepository.save(statistics);
        userRepository.save(user);
    }

    @Transactional
    public Statistics findStatisticsByUsername(String username) {
        return statisticsRepository.findStatisticsByUsername(username);
    }

    @Transactional
    public void deleteStatistics(StatisticsDto statisticsDto) {
        User user = userRepository.findUserByUsername(statisticsDto.getUsername());
        if (user.getStatistics() != null) {
            Statistics statistics = statisticsRepository.findStatisticsByUsername(user.getUsername());
            statisticsRepository.delete(statistics);
            user.setStatistics(null);
            userRepository.save(user);
        }
    }
}

package com.company.aggregator.service;

import com.company.aggregator.dto.StatisticsDto;
import com.company.aggregator.entity.Statistics;
import com.company.aggregator.entity.User;
import com.company.aggregator.rabbitmq.dto.statistics.ReceiveMessageDto;
import com.company.aggregator.repository.StatisticsRepository;
import com.company.aggregator.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final StatisticsRepository statisticsRepository;
    private final UserRepository userRepository;

    @Transactional
    public void deleteStatistics(User user) {
        Optional<Statistics> statistics = statisticsRepository.findByUsername(user.getUsername());
        if (statistics.isPresent()) {
            user.setStatistics(null);
            userRepository.save(user);
            statisticsRepository.delete(statistics.get());
        }
    }

    @Transactional
    public void saveStatistics(User user, com.company.aggregator.rabbitmq.dto.statistics.ReceiveMessageDto message) {
        Statistics statistics = ReceiveMessageDto.toStatistics(message);
        statistics.setUser(user);
        user.setStatistics(statistics);
        statisticsRepository.save(statistics);
        userRepository.save(user);
    }

    @Transactional
    public Statistics findStatisticsByUsername(String username) {
        return statisticsRepository.findByUsername(username).get();
    }

    @Transactional
    public void deleteStatistics(StatisticsDto statisticsDto) {
        User user = userRepository.findByUsername(statisticsDto.getUsername()).get();
        if (user.getStatistics() != null) {
            Statistics statistics = statisticsRepository.findByUsername(user.getUsername()).get();
            statisticsRepository.delete(statistics);
            user.setStatistics(null);
            userRepository.save(user);
        }
    }
}

package com.company.aggregator.service.impl;

import com.company.aggregator.dto.StatisticsDto;
import com.company.aggregator.exception.statistics.StatisticsNotFoundException;
import com.company.aggregator.entity.Statistics;
import com.company.aggregator.entity.User;
import com.company.aggregator.rabbitmq.dto.statistics.ReceiveMessageDto;
import com.company.aggregator.repository.StatisticsRepository;
import com.company.aggregator.repository.UserRepository;
import com.company.aggregator.service.StatisticsService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticsServiceImpl implements StatisticsService {
    StatisticsRepository statisticsRepository;
    UserRepository userRepository;

    @Override
    @Transactional
    public void saveStatistics(User user, com.company.aggregator.rabbitmq.dto.statistics.ReceiveMessageDto message) {
        Statistics statistics = ReceiveMessageDto.toStatistics(message);
        statistics.setUser(user);
        user.setStatistics(statistics);
        statisticsRepository.save(statistics);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteStatistics(StatisticsDto statisticsDto) {
        Optional<User> user = userRepository.findUserByUsername(statisticsDto.getUsername());
        if (user.get().getStatistics() != null) {
            Optional<Statistics> statistics = statisticsRepository.findStatisticsByUsername(user.get().getUsername());
            statisticsRepository.delete(statistics.get());
            user.get().setStatistics(null);
            userRepository.save(user.get());
        }
    }

    @Override
    @Transactional
    public Statistics findStatisticsByUsername(String username) throws StatisticsNotFoundException {
        return Optional.ofNullable(statisticsRepository
                .findStatisticsByUsername(username)
                .orElseThrow(() -> new StatisticsNotFoundException("Statistics has not been created yet!"))).get();
    }

    @Override
    @Transactional
    public void deleteStatisticsByUser(User user) {
        Optional<Statistics> statistics = statisticsRepository.findStatisticsByUsername(user.getUsername());
        if (statistics.isPresent()) {
            user.setStatistics(null);
            userRepository.save(user);
            statisticsRepository.delete(statistics.get());
        }
    }
}

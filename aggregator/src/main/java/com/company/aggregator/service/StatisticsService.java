package com.company.aggregator.service;

import com.company.aggregator.dto.StatisticsDto;
import com.company.aggregator.exception.statistics.StatisticsNotFoundException;
import com.company.aggregator.entity.Statistics;
import com.company.aggregator.entity.User;

public interface StatisticsService {
    void saveStatistics(User user, com.company.aggregator.rabbitmq.dto.statistics.ReceiveMessageDto message);

    void deleteStatistics(StatisticsDto statisticsDto);

    Statistics findStatisticsByUsername(String username) throws StatisticsNotFoundException;

    void deleteStatisticsByUser(User user);
}

package com.company.aggregator.services;

import com.company.aggregator.dtos.StatisticsDto;
import com.company.aggregator.exceptions.statistics.StatisticsNotFoundException;
import com.company.aggregator.models.Statistics;
import com.company.aggregator.models.User;

public interface StatisticsService {
    void saveStatistics(User user, com.company.aggregator.rabbitmq.dtos.statistics.ReceiveMessageDto message);

    void deleteStatistics(StatisticsDto statisticsDto);

    Statistics findStatisticsByUsername(String username) throws StatisticsNotFoundException;

    void deleteStatisticsByUser(User user);
}

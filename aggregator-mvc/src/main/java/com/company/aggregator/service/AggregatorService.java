package com.company.aggregator.service;

import com.company.aggregator.model.Vacancy;
import com.company.aggregator.rabbitmq.dto.ReceiveMessageDto;
import com.company.aggregator.repository.AggregatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class AggregatorService {
    private final AggregatorRepository aggregatorRepository;

    @Autowired
    public AggregatorService(AggregatorRepository aggregatorRepository) {
        this.aggregatorRepository = aggregatorRepository;
    }

    public void save(ReceiveMessageDto receiveMessageDto) {
        aggregatorRepository.save(ReceiveMessageDto.toVacancy(receiveMessageDto));
    }

    public Page<Vacancy> findAll(PageRequest pageRequest) {
        return aggregatorRepository.findAll(pageRequest);
    }
}

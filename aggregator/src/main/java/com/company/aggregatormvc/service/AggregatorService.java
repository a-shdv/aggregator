package com.company.aggregatormvc.service;

import com.company.aggregatormvc.model.Vacancy;
import com.company.aggregatormvc.rabbitmq.dto.ReceiveMessageDto;
import com.company.aggregatormvc.repository.AggregatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Vacancy> findAll() {
        return aggregatorRepository.findAll();
    }

    public Page<Vacancy> findAll(PageRequest pageRequest) {
        return aggregatorRepository.findAll(pageRequest);
    }
}

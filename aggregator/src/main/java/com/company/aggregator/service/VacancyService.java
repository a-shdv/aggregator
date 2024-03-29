package com.company.aggregator.service;

import com.company.aggregator.exception.vacancy.VacancyNotFoundException;
import com.company.aggregator.entity.User;
import com.company.aggregator.entity.Vacancy;
import com.company.aggregator.rabbitmq.dto.vacancy.ReceiveMessageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface VacancyService {
    Page<Vacancy> findVacancies(User user, PageRequest pageRequest);

    Vacancy findById(Long id) throws VacancyNotFoundException;
    Vacancy findVacancyBySource(String source) throws VacancyNotFoundException;
    void saveVacancies(List<ReceiveMessageDto> receiveMessageDtoList, User user);

    void deleteVacanciesByUser(User user);
}

package com.company.aggregator.services;

import com.company.aggregator.exceptions.vacancy.VacancyNotFoundException;
import com.company.aggregator.models.User;
import com.company.aggregator.models.Vacancy;
import com.company.aggregator.rabbitmq.dtos.vacancies.ReceiveMessageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface VacancyService {
    Page<Vacancy> findVacancies(User user, PageRequest pageRequest);

    Vacancy findById(Long id) throws VacancyNotFoundException;

    void saveVacancies(List<ReceiveMessageDto> receiveMessageDtoList, User user);

    void deleteVacanciesByUser(User user);
}

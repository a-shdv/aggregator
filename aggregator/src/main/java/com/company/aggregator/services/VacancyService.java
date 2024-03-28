package com.company.aggregator.services;

import com.company.aggregator.exceptions.VacancyNotFoundException;
import com.company.aggregator.models.User;
import com.company.aggregator.models.Vacancy;
import com.company.aggregator.rabbitmq.dtos.vacancies.ReceiveMessageDto;
import com.company.aggregator.repositories.UserRepository;
import com.company.aggregator.repositories.VacancyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final UserRepository userRepository;

    @Transactional
    public List<Vacancy> saveMessageList(List<ReceiveMessageDto> receiveMessageDtoList, User user) {
        List<Vacancy> vacancies = ReceiveMessageDto.toVacancyList(receiveMessageDtoList);
        vacancies.forEach(vacancy -> vacancy.setUser(user));
        return vacancyRepository.saveAll(vacancies);
    }

    @Transactional
    public Page<Vacancy> findVacancies(User user, PageRequest pageRequest) {
        return vacancyRepository.findByUser(user, pageRequest);
    }

    @Transactional
    public User deleteVacanciesByUser(User user) {
        List<Vacancy> vacancies = vacancyRepository.findByUser(user);
        vacancies.clear();
        user.setVacancies(vacancies);
        return userRepository.save(user);
    }

    public Vacancy findById(Long id) throws VacancyNotFoundException {
        Optional<Vacancy> vacancy = vacancyRepository.findById(id);
        if (vacancy.isEmpty()) {
            throw new VacancyNotFoundException("Вакансия не найдена!");
        }
        return vacancyRepository.findById(id).get();
    }
}

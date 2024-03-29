package com.company.aggregator.service.impl;

import com.company.aggregator.entity.User;
import com.company.aggregator.entity.Vacancy;
import com.company.aggregator.exception.vacancy.VacancyNotFoundException;
import com.company.aggregator.rabbitmq.dto.vacancy.ReceiveMessageDto;
import com.company.aggregator.repository.UserRepository;
import com.company.aggregator.repository.VacancyRepository;
import com.company.aggregator.service.VacancyService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VacancyServiceImpl implements VacancyService {
    VacancyRepository vacancyRepository;
    UserRepository userRepository;

    @Override
    @Transactional
    public Page<Vacancy> findVacancies(User user, PageRequest pageRequest) {
        return vacancyRepository.findVacancyByUser(user, pageRequest).get();
    }

    @Override
    @Transactional
    public Vacancy findById(Long id) throws VacancyNotFoundException {
        Optional<Vacancy> vacancy = vacancyRepository.findById(id);
        if (vacancy.isEmpty()) {
            throw new VacancyNotFoundException("Вакансия не найдена!");
        }
        return vacancyRepository.findById(id).get();
    }

    @Override
    public Vacancy findVacancyBySource(String source) throws VacancyNotFoundException {
        return vacancyRepository.findVacancyBySource(source).orElseThrow(() -> new VacancyNotFoundException("Vacancy %s has not been found".formatted(source)));
    }

    @Override
    @Transactional
    public void saveVacancies(List<ReceiveMessageDto> receiveMessageDtoList, User user) {
        List<Vacancy> vacancies = ReceiveMessageDto.toVacancyList(receiveMessageDtoList);
        vacancies.forEach(vacancy -> vacancy.setUser(user));
        vacancyRepository.saveAll(vacancies);
    }

    @Override
    @Transactional
    public void deleteVacanciesByUser(User user) {
        List<Vacancy> vacancies = vacancyRepository.findVacanciesByUser(user).get();
        vacancies.clear();
        user.setVacancies(vacancies);
        userRepository.save(user);
    }
}

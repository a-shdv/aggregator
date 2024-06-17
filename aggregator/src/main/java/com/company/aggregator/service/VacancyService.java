package com.company.aggregator.service;

import com.company.aggregator.entity.User;
import com.company.aggregator.entity.Vacancy;
import com.company.aggregator.exception.VacancyNotFoundException;
import com.company.aggregator.rabbitmq.dto.vacancies.ReceiveMessageDto;
import com.company.aggregator.repository.UserRepository;
import com.company.aggregator.repository.VacancyRepository;
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
    public void saveMessageList(List<ReceiveMessageDto> receiveMessageDtoList, User user) {
        List<Vacancy> vacancies = ReceiveMessageDto.toVacancyList(receiveMessageDtoList);
        vacancies.forEach(vacancy -> vacancy.setUser(user));
        vacancyRepository.saveAll(vacancies);
    }

    @Transactional
    public List<Vacancy> findAll() {
        return vacancyRepository.findAll();
    }

    @Transactional
    public Vacancy saveTest(Vacancy vacancy){
        return vacancyRepository.save(vacancy);
    }

    @Transactional
    public Vacancy findBySource(String source) {
        return vacancyRepository.findBySource(source).orElse(null);
    }

    @Transactional
    public Page<Vacancy> findVacancies(User user, PageRequest pageRequest) {
        Optional<Page<Vacancy>> vacancies = vacancyRepository.findByUser(user, pageRequest);
        return vacancies.orElse(null);

    }

    @Transactional
    public void deleteVacanciesByUser(User user) {
        List<Vacancy> vacancies = vacancyRepository.findByUser(user).get();
        vacancies.clear();
        user.setVacancies(vacancies);
        userRepository.save(user);
    }

    public Vacancy findById(Long id) throws VacancyNotFoundException {
        return vacancyRepository.findById(id).orElseThrow(() -> new VacancyNotFoundException("Вакансия не найдена!"));
    }
}

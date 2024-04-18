package com.company.aggregator.repository;

import com.company.aggregator.entity.Request;
import com.company.aggregator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    Request findByUser(User user);
}

package com.adacorp.task_managementV2.repository;

import com.adacorp.task_managementV2.model.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StateRepository extends JpaRepository<State, Long>, QuerydslPredicateExecutor<State> {
    Optional<State> findByCode(String code) ;
    Optional<State> findByDescription(String description) ;
}

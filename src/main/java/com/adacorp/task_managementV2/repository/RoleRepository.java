package com.adacorp.task_managementV2.repository;

import com.adacorp.task_managementV2.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByCode (String code) ;
    Optional<Role> findBylibelle(String libelle) ;
}

package com.adacorp.task_managementV2.repository.handleRepository;

import com.adacorp.task_managementV2.model.handleModel.UserLoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;

/**
 * FDE_77400 : VERIFICATION DU DOUBLONS DE EMAIL
 * LORS DE LA CONNEXION & PRISE EN COMPTE DES EMAILS ACTIFS PENDANT AUTHENTICATION.
 */
@Transactional
public interface UserLoginAttemptRepository extends JpaRepository<UserLoginAttempt, Long>,
        QuerydslPredicateExecutor<UserLoginAttempt> {

    /*
     * EVO 66850 - protection anti-bruteforce
     * Get the last login attempt for one user
     */
    @Query("SELECT a FROM UserLoginAttempt a WHERE a.ipAddress = (?1)")
    public UserLoginAttempt findByIpAddress(String ipAddress);
}

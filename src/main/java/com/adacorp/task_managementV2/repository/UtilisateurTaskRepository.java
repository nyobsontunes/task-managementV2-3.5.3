package com.adacorp.task_managementV2.repository;

import com.adacorp.task_managementV2.model.Task;
import com.adacorp.task_managementV2.model.Utilisateur;
import com.adacorp.task_managementV2.model.UtilisateurTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;

public interface UtilisateurTaskRepository extends JpaRepository<UtilisateurTask, Long>, QuerydslPredicateExecutor<UtilisateurTask> {


    Optional<UtilisateurTask> findByTask_Id(Long id) ;
    Optional<UtilisateurTask> findByTask(Task task) ;
    List<UtilisateurTask> findAllByUtilisateur(Utilisateur utilisateur) ;
    List<UtilisateurTask> findAllByUtilisateur_Id(Long id) ;
    List<UtilisateurTask> findAllByUtilisateurOrderByTaskIdDesc (Utilisateur utilisateur) ;
    List<UtilisateurTask> findByUtilisateur_EmailAndTask_State_CodeOrderByAssignmentDateDesc(String email, String code) ;
}

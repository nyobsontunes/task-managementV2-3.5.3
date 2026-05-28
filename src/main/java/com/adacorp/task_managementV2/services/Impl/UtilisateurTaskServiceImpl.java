package com.adacorp.task_managementV2.services.Impl;

import com.adacorp.task_managementV2.model.Task;
import com.adacorp.task_managementV2.model.Utilisateur;
import com.adacorp.task_managementV2.model.UtilisateurTask;
import com.adacorp.task_managementV2.repository.UtilisateurTaskRepository;
import com.adacorp.task_managementV2.services.UtilisateurTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UtilisateurTaskServiceImpl implements UtilisateurTaskService {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilisateurTaskServiceImpl.class);

    private final UtilisateurTaskRepository utilisateurTaskRepository ;

    @Autowired
    public UtilisateurTaskServiceImpl(UtilisateurTaskRepository utilisateurTaskRepository) {
        this.utilisateurTaskRepository = utilisateurTaskRepository;
    }

    @Override
    public void save(UtilisateurTask utilisateurTask) {
        this.utilisateurTaskRepository.save(utilisateurTask);
    }

    @Override
    public void update(UtilisateurTask utilisateurTask) {
        this.utilisateurTaskRepository.save(utilisateurTask);
    }

    @Override
    public void delete(Long id) {
        this.utilisateurTaskRepository.deleteById(id);
    }

    @Override
    public void delete(UtilisateurTask utilisateurTask) {
        this.utilisateurTaskRepository.delete(utilisateurTask);
    }

    @Override
    public Optional<UtilisateurTask> findOne(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<UtilisateurTask> findByTask(Task task) {
        // return Optional.empty();
        return this.utilisateurTaskRepository.findByTask(task);
    }

    @Override
    public List<UtilisateurTask> findByUtilisateur(Utilisateur utilisateur) {
        // return List.of();
        return this.utilisateurTaskRepository.findAllByUtilisateur(utilisateur);
    }

    @Override
    public List<UtilisateurTask> findAll() {
        // return List.of();
        return this.utilisateurTaskRepository.findAll();
    }
    @Override
    public List<UtilisateurTask> findByUtilisateur_EmailAndTask_State_CodeOrderByAssignmentDateDesc(String email, String code) {
        return utilisateurTaskRepository.findByUtilisateur_EmailAndTask_State_CodeOrderByAssignmentDateDesc(email, code);
    }

    public static String generateCode() {
        // Récupère la date et l’heure actuelles
        LocalDateTime now = LocalDateTime.now();
        // Formatte en "ddHHmm" (jour, heure, minute)
        String base = now.format(DateTimeFormatter.ofPattern("ddHHmm"));
        // Convertit en entier puis en base 36 (pour obtenir des lettres et chiffres)
        String datePart = Integer.toString(Integer.parseInt(base), 36).toUpperCase();
        // Ajoute un petit aléatoire
        String randomPart = Integer.toString(new Random().nextInt(36 * 36), 36).toUpperCase();
        // Combine, puis tronque à 5 caractères max
        String code = (datePart + randomPart).replaceAll("[^A-Z0-9]", "");
        return code.length() > 5 ? code.substring(0, 5) : String.format("%-5s", code).replace(' ', 'X');
    }
}

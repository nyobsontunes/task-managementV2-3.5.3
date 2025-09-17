package com.adacorp.task_managementV2.batch;

import com.adacorp.task_managementV2.batch.batchConfig.BatchCleanupProperties;
import com.adacorp.task_managementV2.services.UtilisateurService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import java.util.Calendar;
import java.util.Date;

/**
 * FDE_77421 : Classe du Batch de Désactivation des comptes
 * Non Connecté depuis Au moins 90 Jours.
 */
@Component
public class AccountCleanupBatch implements CommandLineRunner {

    private final UtilisateurService utilisateurService;
    private final BatchCleanupProperties properties;

    /**
     * Logger de la classe AccountCleanupBatch
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountCleanupBatch.class);

    @Autowired
    public AccountCleanupBatch(UtilisateurService utilisateurService, BatchCleanupProperties properties) {
        this.utilisateurService = utilisateurService;
        this.properties = properties;
    }

    // @Scheduled(cron = "#{@batchCleanupProperties.cron}")
    @Transactional
    public void desActivateInactiveAccounts() {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -properties.getInactiveDays()); // reculer de 90 jours
        Date date2IlYA90Jours = cal.getTime();

        int updated2 = utilisateurService.desActivateInactiveAccountsWithLimitDate(date2IlYA90Jours);
        LOGGER.info("Batch SQL 1D : terminé : " + updated2 + " comptes désactivés.");

        this.utilisateurService.desActivateInactiveAccounts(properties.getInactiveDays());
        LOGGER.info("Batch SQL 2D : terminé.");

        int updated3 = utilisateurService.reActivateInactiveAccountsWithLimitDate(date2IlYA90Jours);
        LOGGER.info("Batch SQL 1A : terminé : " + updated3 + " comptes désactivés.");

        this.utilisateurService.reActivateInactiveAccounts(properties.getInactiveDays());
        LOGGER.info("Batch SQL 2A : terminé.");

        LOGGER.info("➡ Batch exécuté !");
    }

    // Lancer le batch chaque jour à l’heure définie
    @Scheduled(cron = "${saga.batch.cleanup.cron}")
    public void runBatch() {
        LOGGER.info("➡ Batch planifié !");
        desActivateInactiveAccounts() ;
    }

    // Lancer le batch en exécution périodique (fixedRate)
    @Scheduled(fixedRate = 60000)
    @Scheduled(fixedRateString  = "${saga.batch.cleanup.fixedRate}")
    public void runAtStartup() {
        LOGGER.info("➡ Batch exécuté toutes les X ms (fixedRate) !");
        runBatch();
    }

    // Lancement du batch au démarrage
    @PostConstruct
    public void runAtStartupPostConstruct() {
        LOGGER.info("➡ Batch lancé au démarrage !!!");
        runBatch();
    }

    @Override
    public void run(String... args) {
        runBatch();
    }
}

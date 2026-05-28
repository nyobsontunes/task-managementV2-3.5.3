package com.adacorp.task_managementV2.security;

import com.adacorp.task_managementV2.batch.AccountCleanupBatch;
import com.adacorp.task_managementV2.model.Utilisateur;
import com.adacorp.task_managementV2.repository.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    /**
     * Logger de la classe CustomUserDetailsService
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UtilisateurRepository utilisateurRepository;

    @Autowired
    public CustomUserDetailsService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        /*
         * FDE_77400 : VERIFICATION DU DOUBLONS DE EMAIL
         * LORS DE LA CONNEXION & PRISE EN COMPTE DES EMAILS ACTIFS PENDANT AUTHENTICATION.
         */
        if (username == null) {
            LOGGER.error("[SAGA] - L'adresse email suivante {}  est vide dans le controller... Veillez contacter l'administrateur", username);
            throw new UsernameNotFoundException("[TaskManagement] - L'adresse email suivante "+username+" est vide dans le controller... Veillez contacter l'administrateur") ;
        }

        // Utilisateur utilisateur2 = utilisateurRepository.findByEmail(username).orElse( null );
        // On affecte le Contenu de la Méthode à L'objet Utilisateur Crée Pour la Session (AppUser utilisateur).
        Utilisateur utilisateur =
                utilisateurRepository.findByEmail(username)
                                    .orElseThrow( () -> new UsernameNotFoundException("Could not find utilisateur") );

        Date dateDerniereConnexion = new Date() ;
        // Outils de Formatage de la Date en format Database
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        String dateHeureConnexionString = sdf.format(dateDerniereConnexion);
        Date dateHeureConnexionDate ;
        try {
            dateHeureConnexionDate = sdf.parse(dateHeureConnexionString) ;
        } catch (ParseException e) {
            throw new RuntimeException("[TaskManagement] Exception : "+e.getMessage());
        }

        /*
            Explanation of Pattern:
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
                    yyyy → 4-digit year
                    MM → 2-digit month
                    dd → 2-digit day
                    HH → 2-digit hour in 24-hour format (use hh for 12-hour)
                    mm → 2-digit minute
                    ss → 2-digit seconds
                    SSS → 3-digit milliseconds (not mmm)
        */

        // on met à jour dans la BD l'info de Dernière Connexion Utilisateur
        utilisateur.setDateDerniereConnexion(dateHeureConnexionDate);
        utilisateurRepository.save(utilisateur);

        LOGGER.info(
                "Appelle de la Classe CustomUserDetailsService implements UserDetailsService " +
                " Chargement de la méthode public UserDetails loadUserByUsername  Email = {} " ,username
        );

        LOGGER.info( " [TaskManagement] utilisateur.fistName & utilisateur.lastName = {} {} ", utilisateur.getFirstName() ,utilisateur.getLastName());
        LOGGER.info( " [TaskManagement] utilisateur.telephone = {} " ,utilisateur.getTelephone());
        LOGGER.info( " [TaskManagement] utilisateur.email = {} " ,utilisateur.getEmail());
        LOGGER.info( " [TaskManagement] utilisateur.password = {} ", utilisateur.getPassword());
        LOGGER.info( " [TaskManagement] utilisateur.dateDerniereConnexionString = {} ", dateHeureConnexionString);
        LOGGER.info( " [TaskManagement] utilisateur.dateDerniereConnexionDateEntity = {} ", utilisateur.getDateDerniereConnexion());
        LOGGER.info( " [TaskManagement] utilisateur.dateDerniereConnexionDateObject = {} ", dateDerniereConnexion);

        // On injecte toutes informations de l'Utilisateur Authentifié dans la classe (CustomUserDetails).
        LOGGER.info( " [TaskManagement] FIN ET ENVOIE DE L'OBJET Utilisateur à la Classe CustomUserDetails" );

        // (CustomerUserDetails implements UserDetails)
        return new CustomerUserDetails(utilisateur);
    }
}

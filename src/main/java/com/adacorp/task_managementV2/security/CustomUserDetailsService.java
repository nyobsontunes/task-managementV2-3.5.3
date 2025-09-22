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

        // Utilisateur utilisateur2 = utilisateurRepository.findByEmail(username).orElse( null );
        // On affecte le Contenu de la Méthode à L'objet Utilisateur Crée Pour la Session (AppUser utilisateur).
        Utilisateur utilisateur =
                utilisateurRepository.findByEmail(username)
                                    .orElseThrow( () -> new UsernameNotFoundException("Could not find utilisateur") );

        LOGGER.info(
                "Appelle de la Classe CustomUserDetailsService implements UserDetailsService " +
                " Chargement de la méthode public UserDetails loadUserByUsername  Email = {} " ,username
        );

        LOGGER.info( " utilisateur.fistName & utilisateur.lastName = {} {} ", utilisateur.getFirstName() ,utilisateur.getLastName());
        LOGGER.info( " utilisateur.telephone = {} " ,utilisateur.getTelephone());
        LOGGER.info( " utilisateur.email = {} " ,utilisateur.getEmail());
        LOGGER.info( " utilisateur.password = {} ", utilisateur.getPassword());

        // On injecte toutes informations de l'Utilisateur Authentifié dans la classe (CustomUserDetails).
        LOGGER.info( " FIN ET ENVOIE DE L'OBJET Utilisateur à la Classe CustomUserDetails" );

        // (CustomerUserDetails implements UserDetails)
        return new CustomerUserDetails(utilisateur);
    }
}

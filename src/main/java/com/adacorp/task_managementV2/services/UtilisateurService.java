package com.adacorp.task_managementV2.services;

import com.adacorp.task_managementV2.model.Utilisateur;
import org.springframework.data.repository.query.Param;
import org.springframework.validation.BindingResult;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface UtilisateurService {

    void save(Utilisateur utilisateur) ;

    void update (Utilisateur utilisateur);

    //la surcharge de méthode (Overloading : même Type de Retour, même nom, Argument différents)
    void delete(Long id);

    //la surcharge de méthode (Overloading : même Type de Retour, même nom, Argument différents)
    void delete(Utilisateur utilisateur);

    Optional<Utilisateur> findOneByFirstName (String firstName);

    List<Utilisateur> findAllByFirstName (String firstName);

    Optional<Utilisateur> findOne (Long id);

    List<Utilisateur> findAll();

    Optional<Utilisateur> findByEmail(String email);

    boolean existsByEmail(String email);

    /**
     * FDE_77421 : Service du Batch de Désactivation des Comptes
     * param limitDays
     */
    void desActivateInactiveAccounts(int limitDays);

    /**
     * FDE_77421 : Service du Batch de Désactivation des Comptes
     * param limitDate
     */
    int desActivateInactiveAccountsWithLimitDate(Date limitDate);

    /**
     * FDE_77421 : Service du Batch de Réactivation des Comptes
     * param limitDays
     */
    void reActivateInactiveAccounts(int limitDays);

    /**
     * FDE_77421 : Service du Batch de Réactivation des Comptes
     * param limitDate
     */
    int reActivateInactiveAccountsWithLimitDate(Date limitDate);

    /**
     * permet de verifier si on peut updater utilisateur dans la base
     *
     * @param utilisateur     : utilisateur a updater
     * @param utilisateurBase : utilisateur existant en base
     * @return true si on peut, false sinon
     */
    boolean peutEnregistrerUtilisateur(Utilisateur utilisateur, Utilisateur utilisateurBase,
                                       final BindingResult bindingResult);



    /**
     * Active ou désactive un utilisateur
     *
     * @param id l'identifiant de l'utilsateur à activer ou désactiver
     */
    void activer(long id);

    /**
     * Force à "actif" tous les utilisateurs identifiés
     *
     * @param ids les identifiants des utilisateurs à activer
     */
    void activer(long[] ids);

    /**
     * Force à "inactif" tous les utilisateurs identifiés
     *
     * @param ids les identifiants des utilisateurs à désactiver
     */
    void desactiver(long[] ids);

    /**
     * Retrouve un contact par son id avec un fetch special pour la compagnie
     *
     * @param id du contact
     * @return contact associé à l'id
     */
    Utilisateur findByIdFetch(final long id);
}

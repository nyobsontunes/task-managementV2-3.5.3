package com.adacorp.task_managementV2.services.Impl;

import com.adacorp.task_managementV2.model.Utilisateur;
import com.adacorp.task_managementV2.repository.UtilisateurRepository;
import com.adacorp.task_managementV2.services.UtilisateurService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.security.InvalidParameterException;
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.*;

@Service
@Transactional
@PropertySource("classpath:mail.properties")
@PropertySource("classpath:application.properties")
public class UtilisateurServiceImpl implements UtilisateurService {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilisateurServiceImpl.class);

    /**
     * Clé du message flash des Utilisateurs Externes à DGAC
     */
    public static final String CLE_MESSAGE_UTILISATEUR_EXTERNE = "saga.administration.utilisateurs.echec.actiondemasseArgument";

    /**
     * Clé du message flash d'appartenance à des comptes Utilisateurs Actifs
     */
    public static final String CLE_MESSAGE_UTILISATEUR_ACTIF = "saga.administration.utilisateurs.erreur.emailActif";

    /**
     * Clé du message flash d'appartenance à des comptes Utilisateurs Actifs
     */
    public static final String CLE_MESSAGE_UTILISATEUR_ACTIVATION_EMAIL_MULTIPLE = "saga.administration.utilisateurs.erreur.emailActivationEnMasse";
    /**
     * Taille du mot de passe
     */
    public static final int PASSWORD_LENGTH = 10;

    private final UtilisateurRepository utilisateurRepository ;

    @Autowired
    protected MessageSource messageSource;

    @Autowired
    public UtilisateurServiceImpl(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository ;
    }

    @Override
    public void save(Utilisateur utilisateur) {
        this.utilisateurRepository.save(utilisateur);
    }

    @Override
    public void update(Utilisateur utilisateur) {
        this.utilisateurRepository.save(utilisateur);
    }

    @Override
    public void delete(Long id) {
        this.utilisateurRepository.deleteById(id);
    }

    @Override
    public void delete(Utilisateur utilisateur) {
        this.utilisateurRepository.delete(utilisateur);
    }

    @Override
    public Optional<Utilisateur> findOneByFirstName(String firstName) {
        // return Optional.empty();
        return this.utilisateurRepository.findOneByFirstNameLikeNativeQuery(firstName);
    }

    @Override
    public List<Utilisateur> findAllByFirstName(String firstName) {
        // return List.of();
        return this.utilisateurRepository.findAllByFirstNameLikeNativeQuery(firstName);
    }

    @Override
    public Optional<Utilisateur> findOne(Long id) {
        // return Optional.empty();
        return this.utilisateurRepository.findById(id);
    }

    @Override
    public List<Utilisateur> findAll() {
        // return List.of();
        return this.utilisateurRepository.findAll();
    }

    @Override
    public Optional<Utilisateur> findByEmail(String email) {
        // return Optional.empty();
        return this.utilisateurRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return this.utilisateurRepository.existsByEmail(email);
    }

    /**
     * FDE_77421 : Service du Batch de Désactivation des Comptes
     * param limitDays
     */
    @Override
    public void desActivateInactiveAccounts(int limitDays) {
        utilisateurRepository.desActivateInactiveAccounts(limitDays);
    }

    /**
     * FDE_77421 : Service du Batch de Désactivation des Comptes
     * param limitDate
     */
    @Override
    public int desActivateInactiveAccountsWithLimitDate(Date limitDate) {
        return utilisateurRepository.desActivateInactiveAccountsWithLimitDate(limitDate);
    }

    /**
     * FDE_77421 : Service du Batch de Réactivation des Comptes
     * param limitDays
     */
    @Override
    public void reActivateInactiveAccounts(int limitDays) {
        utilisateurRepository.reActivateInactiveAccounts(limitDays);
    }

    /**
     * FDE_77421 : Service du Batch de Réactivation des Comptes
     * param limitDate
     */
    @Override
    public int reActivateInactiveAccountsWithLimitDate(Date limitDate) {
        return utilisateurRepository.desActivateInactiveAccountsWithLimitDate(limitDate);
    }

    @Override
    public boolean peutEnregistrerUtilisateur(final Utilisateur utilisateur,
                                              final Utilisateur utilisateurBase, final BindingResult bindingResult) {
        if (utilisateur != null && utilisateurBase != null) {

            final UtilisateurCourrant utilisateurAModifier = new UtilisateurCourrant(utilisateurBase,
                    utilisateurBase.getLibelleRolesPermission());

            if (utilisateurAModifier.isChargeInstructionOuResponsableBureau()) {
                final boolean misAjourBureau = miseAjourBureau(utilisateur, utilisateurBase,
                        utilisateurAModifier);
                if (!misAjourBureau) {
                    bindingResult.rejectValue("bureau",
                            "saga.administration.utilisateurs.brueau.invalide");
                    return false;
                }
            }

            if (utilisateurAModifier.isFrontOfficeUtilisateur()) {
                final boolean miseAjour = miseAjourUtilisateur(utilisateur, utilisateurBase,
                        utilisateurAModifier, bindingResult);
                if (!miseAjour) {
                    return false;
                }

                final boolean adresseEnregistee = enregistrerAdresse(utilisateur, utilisateurBase,
                        utilisateurAModifier);
                if (!adresseEnregistee) {
                    bindingResult.rejectValue(ADRESSE,
                            "saga.administration.utilisateurs.adresse.invalide");
                    return false;
                }

                final boolean compagnieEnregistee = enregistrerCompagnie(utilisateur,
                        utilisateurBase, utilisateurAModifier);
                if (!compagnieEnregistee) {
                    bindingResult.rejectValue("compagnie",
                            "saga.administration.utilisateurs.compagnie.invalide");
                    return false;
                }

                /*
                  FDE_77400 : 2- Lorsqu'on sauvegarde un compte (création ou modification), vérifier qu'aucun compte actif ne possède déjà la même adresse mail.
                  Sinon, mettre un message d'erreur et empêcher la sauvegarde.
                 */
                final int nbreUtilisateurActif = utilisateur.isAccountNonLocked() ? countUtilisateurByEmailAndActifAndTypeAndLdap(utilisateurBase.getEmail(), true, TypeUtilisateur.UTILISATEUR, utilisateurBase.isLdap()) : 0 ;
                if (nbreUtilisateurActif == 1) {
                    LOGGER.error("[TaskManagement] - L'adresse mail est {} déjà associée à un unique compte actif.", utilisateurBase.getEmail());
                    bindingResult.rejectValue("actif", "saga.utilisateur.demandeIdentifiant.erreur.emailActif");
                    return false;
                }
                else
                if (nbreUtilisateurActif > 1) {
                    LOGGER.error("[TaskManagement] -  L'adresse mail est {} déjà associée à plus d'un compte actif : Merci de désactiver les autres comptes & ne laisser qu'un seul actif.", utilisateurBase.getEmail());
                    bindingResult.rejectValue("actif","saga.administration.utilisateurs.erreur.emailActif.multiple.invalide");
                    return false;
                } else {
                    utilisateurBase.setAccountNonLocked(utilisateur.isAccountNonLocked());
                }
            }

            if (utilisateur.getBureau() != null) {
                final boolean miseAjourZoneGeo = miseAjourZoneGeographique(utilisateur,
                        utilisateurBase);
                if (!miseAjourZoneGeo) {
                    bindingResult.rejectValue("zoneGeographiques",
                            "saga.administration.utilisateurs.zoneGeographique.invalide");
                    return false;
                }
            }

            return true;

        }
        return false;
    }

    @Override
    public void activer(final long id) {
        final Utilisateur utilisateur = this.findByIdFetch(id);
        if (utilisateur != null) {
            // FDE_77400 : 3- Lors de la réactivation d'un compte, vérifier qu'aucun autre compte actif ne possède déjà la même adresse mail.
            final int nbreUtilisateurActif = !utilisateur.isAccountNonLocked() ? countUtilisateurByEmailAndActifAndTypeAndLdap(utilisateur.getEmail(), true, TypeUtilisateur.UTILISATEUR, utilisateur.isLdap()) : 0 ;
            if (nbreUtilisateurActif == 1) {
                String msgSource = messageSource.getMessage("saga.administration.utilisateurs.erreur.emailActif", new Object[]{utilisateur.getEmail()}, Locale.getDefault() );
                LOGGER.error("[TaskManagement] - L'adresse mail <{}> est déjà associée à un compte actif Unique.", utilisateur.getEmail());
                LOGGER.error("[TaskManagement] - {}", msgSource);
                throw new InvalidParameterException(msgSource.replace("{0}",utilisateur.getEmail()));
            }
            else
            if (nbreUtilisateurActif > 1) {
                LOGGER.error("[TaskManagement] -  L'adresse mail est {} déjà associée à plus d'un compte actif : Merci de désactiver les autres comptes & ne laisser qu'un seul actif.", utilisateur.getEmail());
                throw new InvalidParameterException("[TaskManagement] -  " +
                        " L'adresse mail <"+ utilisateur.getEmail() +"> est associée à plus d'un compte actif : " +
                        " Merci de désactiver les autres comptes & ne laisser qu'un seul actif.");
            } else {
                utilisateur.setAccountNonLocked(!utilisateur.isAccountNonLocked());
                this.update(utilisateur);
            }
        } else {
            throw new InvalidParameterException(MessageFormat.format(
                    "L'utilisateur {0} n'existe pas", id));
        }

    }

    @Override
    public void activer(final long[] ids) {
        boolean peutActiver = true;
        List<String> emailList = new ArrayList<>();
        for (final long id : ids) {
            // Info Utilisateur de la Boucle.
            final Optional<Utilisateur> utilisateur = this.utilisateurRepository.findById(id);
            // Chargement de la liste Pour le contrôle des doublons d'email.
            utilisateur.ifPresent(value -> emailList.add(value.getEmail()));

            if (utilisateur.isPresent() && utilisateur.get().isAccountNonLocked()) {
                peutActiver = false;
            }

            // FDE_77400 : 3- Lors de la réactivation d'un compte, vérifier qu'aucun autre compte actif ne possède déjà la même adresse mail.
            final int nbreUtilisateurActif = (utilisateur.isPresent() && utilisateur.get().isAccountNonLocked()) ? countUtilisateurByEmailAndActifAndTypeAndLdap(utilisateur.get().getEmail(), true, TypeUtilisateur.UTILISATEUR, utilisateur.isLdap()) : 0 ;
            if (nbreUtilisateurActif >= 1) {
                // String msg = msgAdminErreurEmailActif.replace("{0}", utilisateur.get().getEmail());
                String msgSource = messageSource.getMessage("saga.administration.utilisateurs.erreur.emailActif", new Object[]{utilisateur.get().getEmail()}, Locale.getDefault() );
                LOGGER.error("[TaskManagement] - L'adresse mail <{}> est déjà associée à un compte actif.", utilisateur.get().getEmail());
                LOGGER.error("[TaskManagement] - {}", msgSource);
                throw new InvalidParameterException(msgSource.replace("{0}",utilisateur.get().getEmail()));
            }
        }

        int compteur;
        for (final long id : ids) {
            final Optional<Utilisateur> utilisateur = this.utilisateurRepository.findById(id);
            compteur = Math.toIntExact(emailList.stream().filter(s -> s.equalsIgnoreCase(utilisateur.get().getEmail())).count());
            if (utilisateur.isPresent() && compteur > 1 ) {
                String msgSource = messageSource.getMessage(CLE_MESSAGE_UTILISATEUR_ACTIVATION_EMAIL_MULTIPLE, new Object[]{utilisateur.get().getEmail()}, Locale.getDefault() );
                LOGGER.error("[TaskManagement] - L'adresse mail <{}> ne peut être associée qu'à un seul compte actif.", utilisateur.get().getEmail());
                throw new InvalidParameterException(msgSource.replace("{0}",utilisateur.get().getEmail()));
            }
        }

        if (peutActiver) {
            utilisateurRepository.activer(ids);
        } else {
            throw new InvalidParameterException("Un des utilisateurs n'est pas un utilisateur externe");
        }
    }

    @Override
    public void desactiver(final long[] ids) {
        boolean peutDesactiver = true;
        for (final long id : ids) {
            final Optional<Utilisateur> utilisateur = utilisateurRepository.findById(id);

            if (utilisateur.isPresent() && !utilisateur.get().isAccountNonLocked()) {
                peutDesactiver = false;
                String msgSource = messageSource.getMessage(CLE_MESSAGE_UTILISATEUR_EXTERNE, new Object[]{utilisateur.get().getEmail()}, Locale.getDefault() );
                LOGGER.error("[TaskManagement] - Un des utilisateurs n'est pas un utilisateur inactif : <{}>", utilisateur.get().getEmail());
                throw new InvalidParameterException(msgSource.replace("{0}",utilisateur.get().getEmail()));
            }

            if (utilisateur.isEmpty()) {
                peutDesactiver = false;
            }
        }
        if (peutDesactiver) {
            utilisateurRepository.desactiver(ids);
        } else {
            throw new InvalidParameterException("Un des utilisateurs n'est pas un utilisateur externe");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Utilisateur findByIdFetch(final long id) {
        return utilisateurRepository.findByIdFetch(id);
    }

    /**
     * Génére un mot de passe aléatoire
     *
     * @return password aléatoire
     */
    private static String generateRandomPassword() {
        // Pick from some letters that won't be easily mistaken for each
        // other. So, for example, omit o O and 0, 1 l and L.
        final String letters = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ123456789+@";

        final Random random = new SecureRandom();

        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            final int index = (random.nextInt() * letters.length());
            sb.append(letters.charAt(index));
            // sb.append(letters.substring(index, index + 1));
        }
        return sb.toString();
    }

}

package com.adacorp.task_managementV2.model;

import fr.gouv.developpementdurable.dgac.saga.commun.enumerations.EtatImportCsv;
import fr.gouv.developpementdurable.dgac.saga.metier.entite.referentiels.Compagnie;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * Entite de la table "import_csv"
 *
 * @author cmouliets
 */
@Entity
@Getter
@Setter
@Table(name = "import_csv")
public class ImportCsv implements Serializable {
    private static final long serialVersionUID = -749016377626990872L;

    /*
    ##################################################################
     *
     * Attributs
     *
    ##################################################################
     */


    /**
     * Id qui permet l'unicité de ImportCsv
     */
    @Id
    @GenericGenerator(name = "generator", strategy = "increment")
    @GeneratedValue(generator = "generator", strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    /**
     * Etat
     */
    @Column(name = "etat", nullable = false)
    @Enumerated(EnumType.STRING)
    private EtatImportCsv etat;

    /**
     * Compagnie
     */
    @ManyToOne
    @JoinColumn(name = "id_compagnie", referencedColumnName = "id")
    private Compagnie compagnie;

    /**
     * Compagnier de Courtier
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_courtier", referencedColumnName = "id")
    private CompagnieCourtier compagnieCourtier;

    /**
     * Saison IATA
     */
    @ManyToOne
    @JoinColumn(name = "id_saison_iata", referencedColumnName = "id")
    private SaisonIata saisonIata;

    /**
     * Objet de la demande
     */
    @Size(max = 100)
    @Column(name = "objet_demande", nullable = true, length = 100)
    private String objetDemande;

    /**
     * Utilisateur
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utilisateur", referencedColumnName = "id")
    private Utilisateur utilisateur;

    /**
     * Date de depot
     */
    @Column(name = "date_depot", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private final Date dateDepot = new Date();

    /**
     * Nom du fichier
     */
    @Column(name = "nom_fichier", nullable = false)
    private String nomFichier;

    /**
     * Fichier de log
     */
    @Column(name = "fichier_log")
    private String fichierLog;

    /**
     * numero de la demande
     */
    @Column(name = "numero_demande")
    private String numeroDemande;

}

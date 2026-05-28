package com.adacorp.task_managementV2.model;


import com.adacorp.task_managementV2.auditConfig.AbstractEntity;
import com.adacorp.task_managementV2.constantes.TypeUtilisateur;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.processing.Pattern;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Table(name="utilisateur")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur extends AbstractEntity implements Serializable {

    @Column(name = "firstName", nullable = false)
    private String firstName ;

    @Column(name = "lastName", nullable = false)
    private String lastName ;

    @Column(name = "sexe", nullable = false)
    private char sexe ;

    @Column(name = "telephone", nullable = false)
    private String telephone ;

    /**
     * Email de l'utilisateur avec la contrainte suivante : le mail doit
     * respecter l'expression reguliere :
     * ^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]{2,}[.][a-zA-Z]{2,4}$
     */
    /* @NotEmpty
    @Pattern(regexp = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]{2,}[.][a-zA-Z]{2,4}$",
            message = "format email invalide")
    @Size(max = 350)
    */
    @Column(name = "email", unique = false, nullable = false, length = 350)
    private String email ;

    @Column(name = "password", nullable = false)
    private String password ;

    @Transient
    private String confirmPassword ;

    @Column(name = "is_account_non_locked", nullable = false)
    private boolean isAccountNonLocked ;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role ;

    // Une Habilitation peut être affecté à 0.* une liste de HabilitationRole
    @OneToMany(mappedBy = "utilisateur")
    private List<UtilisateurTask> utilisateurTaskList = new ArrayList<>();

    /**
     * FDE_77421 : Date de Dernière Connexion du compte utilisateur
     * param dateDerniereConnexion
     * the dateDerniereConnexion to set
     */
    @Getter
    @Setter
    @Column(name = "date_derniere_connexion", unique = false, updatable = true, nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateDerniereConnexion;

    /**
     * Type d'utilisateur
     */
    @Column(name = "type_utilisateur", nullable = true)
    @Enumerated(EnumType.STRING)
    private TypeUtilisateur typeUtilisateur;
}

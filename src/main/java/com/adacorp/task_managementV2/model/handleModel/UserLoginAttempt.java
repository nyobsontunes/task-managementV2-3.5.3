package com.adacorp.task_managementV2.model.handleModel;

import jakarta.persistence.*;
import lombok.Getter;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * FDE_77400 : VERIFICATION DU DOUBLONS DE EMAIL
 * LORS DE LA CONNEXION & PRISE EN COMPTE DES EMAILS ACTIFS PENDANT AUTHENTICATION.
 */
@Getter
@Entity
@Table(name = "utilisateur_login_attempts")
@SequenceGenerator(name = "utilisateur_login_attempts_seq", sequenceName = "utilisateur_login_attempts_seq", allocationSize=1)
public class UserLoginAttempt implements Serializable {

    /**
     * Variable serialVersionUID pour la serialisation
     */
    private static final long serialVersionUID = 4346344855802266597L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "utilisateur_login_attempts_generator")
    @SequenceGenerator(name = "utilisateur_login_attempts_generator", sequenceName = "utilisateur_login_attempts_seq", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;
    @Column(name = "attempt_date")
    private Timestamp attemptDate;
    @Column(name="attempt_counter")
    private int counter;
    @Column(name="is_disabled")
    private boolean isDisabled;
    @Column(name="attempt_ip")
    private String ipAddress;


    public void setIpAddress(String ipAddress) {
        this. ipAddress = ipAddress;
    }

    public void setId(Long id) {this.id = id; }

    public void setAttemptDate(Timestamp attemptDate) { this.attemptDate = attemptDate; }

    public void setCounter(int counter) { this.counter = counter; }

    public void setDisabled(boolean disabled) {
        isDisabled = disabled;
    }
}

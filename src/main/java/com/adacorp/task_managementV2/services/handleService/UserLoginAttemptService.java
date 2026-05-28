package com.adacorp.task_managementV2.services.handleService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * FDE_77400 : VERIFICATION DU DOUBLONS DE EMAIL
 * LORS DE LA CONNEXION & PRISE EN COMPTE DES EMAILS ACTIFS PENDANT AUTHENTICATION.
 */
public interface UserLoginAttemptService {
    /** method to register every login failure attempt for a given username. */
    void registerLoginFailure(final String ipAddress);

    /** method to reset the counter for successful login. (delete the record). */
    void resetCounter(final String ipAddress);

    /**
     * method to inform if user has the possibility to attempt logging in.
     * the method also resets the counter if a specified time has elapsed.
     */
    boolean isIPAddressBlocked(String ipAddress) ;

    /** method to retrieve the IP address from which the request has been made. */
    String getClientIP(HttpServletRequest request);

}

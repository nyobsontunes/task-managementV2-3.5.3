package com.adacorp.task_managementV2.services.handleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * FDE_77400 : VERIFICATION DU DOUBLONS DE EMAIL
 * LORS DE LA CONNEXION & PRISE EN COMPTE DES EMAILS ACTIFS PENDANT AUTHENTICATION.
 */
@Component
public class CustomSuccessAuthenticationService extends SavedRequestAwareAuthenticationSuccessHandler {


    @Autowired
    UserLoginAttemptService userLoginAttemptService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {

        // Logging successful authentication
        logger.info("Authentication succeeded for userName: {" + authentication.getName() + "}");

        // Perform custom logic on successful login and reset the counter
        String clientIP = userLoginAttemptService.getClientIP(request);
        userLoginAttemptService.resetCounter(clientIP);

        logger.info("CustomSuccessAuthenticationService : Login attempt counter reset for IP: {"+ clientIP + "}");
        super.onAuthenticationSuccess(request, response, authentication);
    }


}

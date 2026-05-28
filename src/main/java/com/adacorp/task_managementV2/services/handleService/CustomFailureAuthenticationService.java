package com.adacorp.task_managementV2.services.handleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * FDE_77400 : VERIFICATION DU DOUBLONS DE EMAIL
 * LORS DE LA CONNEXION & PRISE EN COMPTE DES EMAILS ACTIFS PENDANT AUTHENTICATION.
 */
@Component
public class CustomFailureAuthenticationService extends SimpleUrlAuthenticationFailureHandler {
    @Autowired
    UserLoginAttemptService userLoginAttemptService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        // FDE_77400 : Prise en compte du Gestionnaire d'exception venant du loadByUserName()
        request.getSession().setAttribute("SPRING_SECURITY_LAST_EXCEPTION", exception.getMessage());

        String ipAddress = userLoginAttemptService.getClientIP(request);

        if (Objects.isNull(ipAddress)) {
            setDefaultFailureUrl("/index-login?error=0");
        }

        if (!userLoginAttemptService.isIPAddressBlocked(ipAddress)) {
            userLoginAttemptService.registerLoginFailure(ipAddress);
        }

        if (userLoginAttemptService.isIPAddressBlocked(ipAddress)) {
            logger.info(String.format("L'utilisateur avec l'adresse IP %s est bloqué", ipAddress));
            setDefaultFailureUrl("/index-login?error=1");
        } else {
            setDefaultFailureUrl("/index-login?error=0");
        }
        super.onAuthenticationFailure(request, response, exception);
    }

}
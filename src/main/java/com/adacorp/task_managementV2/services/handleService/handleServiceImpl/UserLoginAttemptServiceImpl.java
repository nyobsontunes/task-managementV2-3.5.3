package com.adacorp.task_managementV2.services.handleService.handleServiceImpl;

import com.adacorp.task_managementV2.services.handleService.UserLoginAttemptService;
import com.adacorp.task_managementV2.model.handleModel.UserLoginAttempt;
import com.adacorp.task_managementV2.repository.handleRepository.UserLoginAttemptRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * FDE_77400 : VERIFICATION DU DOUBLONS DE EMAIL
 * LORS DE LA CONNEXION & PRISE EN COMPTE DES EMAILS ACTIFS PENDANT AUTHENTICATION.
 */
@Service("bruteForceProtectionService")
@PropertySource("classpath:security.properties")
public class UserLoginAttemptServiceImpl implements UserLoginAttemptService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserLoginAttemptServiceImpl.class);

    private static final String IP_LOCAL = "127.0.0.1";
    private static final String X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String X_REAL_IP = "X-Real-IP";

    @Value("${security.minute.blocking}")
    private int minuteBlocking;

    @Value("${security.numberofattempts}")
    private int numberOfAttempts;

    @Autowired
    UserLoginAttemptRepository userLoginAttemptRepository;

    @Override
    public void registerLoginFailure(String ipAddress) {
        UserLoginAttempt userAttempt = userLoginAttemptRepository.findByIpAddress(ipAddress);
        if (userAttempt == null) {
            userAttempt = new UserLoginAttempt();
            userAttempt.setCounter(1);
            userAttempt.setAttemptDate(Timestamp.valueOf(LocalDateTime.now()));
            userAttempt.setDisabled(false);
            userAttempt.setIpAddress(ipAddress);
        } else {
            int failedCounter = userAttempt.getCounter();
            if (numberOfAttempts == failedCounter+1) {
                userAttempt.setDisabled(true);
            }
            userAttempt.setCounter(failedCounter + 1);
            userAttempt.setAttemptDate(Timestamp.valueOf(LocalDateTime.now()));
        }

        userLoginAttemptRepository.save(userAttempt);
    }

    @Override
    public void resetCounter(String ipAddress) {
        if (Objects.nonNull(ipAddress)) {
            UserLoginAttempt userAttempt = userLoginAttemptRepository.findByIpAddress(ipAddress);
            if (userAttempt != null) {
                userLoginAttemptRepository.delete(userAttempt);
                LOGGER.info("L'utilisateur avec l'adresse IP {} est maintenant débloqué", ipAddress);
            }
        }
    }

    @Override
    public boolean isIPAddressBlocked(String ipAddress){
        if (Objects.nonNull(ipAddress)) {
            UserLoginAttempt userAttempt = userLoginAttemptRepository.findByIpAddress(ipAddress);
            if (userAttempt != null && userAttempt.isDisabled()) {
                LocalDateTime lastLoginTime = userAttempt.getAttemptDate().toLocalDateTime();
                LocalDateTime lastLoginTimePlusDelta = lastLoginTime.plusMinutes(minuteBlocking);
                if (lastLoginTimePlusDelta.isAfter(LocalDateTime.now())) {
                    return true;
                }
                resetCounter(ipAddress);
            }
        }
        return false;
    }

    @Override
    public String getClientIP(HttpServletRequest request) {
        LOGGER.debug("Adresse IP présente dans le header X-Forwarded-For {}", request.getHeader(X_FORWARDED_FOR));
        LOGGER.debug("Adresse IP présente dans le header X-Real-IP {}", request.getHeader(X_REAL_IP));
        LOGGER.debug("Adresse IP présente dans le getRemoteAddr {}", request.getRemoteAddr());
        String ip = request.getHeader(X_FORWARDED_FOR);
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            ip = ip.split(",")[0].trim();
            if (!IP_LOCAL.equals(ip)) {
                LOGGER.info("Utilisation de l'adresse IP présente dans le header X-Forwarded-For {}", request.getHeader(X_FORWARDED_FOR));
                return ip;
            }
        }

        ip = request.getHeader(X_REAL_IP);
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip) && !IP_LOCAL.equals(ip)) {
            LOGGER.info("Utilisation de l'adresse IP présente dans le header X-Real-IP {}", request.getHeader(X_REAL_IP));
            return ip;
        }

        ip = request.getRemoteAddr();
        if (!IP_LOCAL.equals(ip)) {
            LOGGER.info("Utilisation de l'adresse IP présente dans le getRemoteAddr {}", request.getRemoteAddr());
            return ip;
        }
        return null;
    }

}
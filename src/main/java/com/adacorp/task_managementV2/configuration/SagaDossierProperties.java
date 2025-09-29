package com.adacorp.task_managementV2.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Setter
@Configuration
@PropertySource(value = {"classpath:document.properties"})
public class SagaDossierProperties {
    @Value("${saga.dossier.temporaire}")
    private String dossierTemporaire;

    @Value("${saga.dossier.demande}")
    private String dossierDemande;

    @Value("${saga.dossier.ssim}")
    private String dossierSsim;

    @Value("${saga.dossier.csv}")
    private String dossierCsv;

    @Value("${saga.dossier.template}")
    private String dossierTemplate;

    @Value("${saga.dossier.compagnie}")
    private String dossierCompagnie;
}

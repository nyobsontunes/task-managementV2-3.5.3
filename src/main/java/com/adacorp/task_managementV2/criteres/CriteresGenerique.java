/**
 *
 */
package com.adacorp.task_managementV2.criteres;

import com.querydsl.core.types.Predicate;

/**
 * Interface générique pour tous les critères de recherche de l'application SAGA
 *
 * @author hmens
 *
 */
public interface CriteresGenerique {

    /**
     * Construit le prédicat pour querydsl à partir des critères remplis dans
     * l'objet
     *
     * @return le prédicat pour la recherche
     */
    public Predicate buildPredicate();

}

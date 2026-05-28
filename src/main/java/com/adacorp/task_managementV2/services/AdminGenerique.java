/**
 *
 */
package com.adacorp.task_managementV2.services;

import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Interface régissant les méthodes à implémenter dans les controleurs
 * d'administration.
 *
 * @author hmens
 *
 * @param <T>
 *            L'entité éditée par l'interface d'administration
 * @param <K>
 *            Le critère de recherche associé
 */
public interface AdminGenerique<T, K> {
    /**
     * Page d'accueil de l'administration des compagnies
     *
     * @param model
     *            la map des attributs passés à thymeleaf
     * @param t
     *            un élément vide pour le formulaire de création
     * @return l'uri de la page
     */
    @RequestMapping
    String accueil(@RequestParam(value = "fromsidebar", required = false) final String fromsidebar,
            final Model model, final T t, final HttpServletRequest request);

    /**
     * Création ou mise à jour de compagnies
     *
     * @param t
     *            un élément vide pour le formulaire de création
     * @param bindingResult
     *            le résultat de la validation
     * @param model
     *            le model à retourner au template
     * @return le template à afficher
     */
    @RequestMapping(method = RequestMethod.POST, params = { "enregistrer" })
    String enregistrer(@RequestParam(value = "fromsidebar", required = false) final String fromsidebar,
                       @Validated final T t, final BindingResult bindingResult, final Model model,
                       final RedirectAttributes redirectAttributes, final Locale locale, final HttpServletRequest request);

    /**
     * Reçoit la requête de recherche, la persiste en session si les variables
     * sont valides puis redirige vers l'affichage
     *
     * @param element
     *            un élément vide pour le formulaire de création
     * @param criteres
     *            les critères de recherche
     * @param result
     *            stocke les résultats de validation des critères de recherche
     * @return un redirect vers l'affichage si tout s'est bien passé, sinon
     *         retourne le template avec les erreurs
     */
    @RequestMapping(method = RequestMethod.POST, params = { "recherche" })
    String rechercher(@RequestParam(value = "fromsidebar", required = false) final String fromsidebar,
            final T element, @Validated final K criteres, final BindingResult result, final Model model,
            final HttpServletRequest request);

    /**
     * Réinitialise les critères de recherche en session
     *
     * @return redirection vers l'accueil
     */
    @RequestMapping(method = RequestMethod.POST, params = { "resetRecherche" })
    String resetRecherche();

    /**
     * Active ou désactive une compagnie
     *
     * @param id
     *            l'identifiant de la compagnie à activer / désactiver
     * @param redirectAttributes
     *            les attributs de redirection utilisés pour stocker le message
     *            flash de succès
     * @param locale
     *            la locale courante
     * @return redirection vers l'accueil
     */
    @RequestMapping(value = "/{id}/activer")
    String activer(@PathVariable final long id, RedirectAttributes redirectAttributes, final Locale locale);

    /**
     * Désactive tous les éléments sélectionnés
     *
     * @param ids
     *            les identifiants des éléments à désactiver
     * @param redirectAttributes
     *            les attributs de redirection utilisés pour stocker le message
     *            flash de succès
     * @param locale
     *            la locale courante
     * @return redirection vers l'accueil
     */
    @RequestMapping(params = { "desactivationDeMasse" })
    String desactivationDeMasse(@RequestParam(value = "selectedElements") final long[] ids,
            final RedirectAttributes redirectAttributes, final Locale locale);

    /**
     * Active tous les éléments sélectionnés
     *
     * @param ids
     *            les identifiants des éléments à activer
     * @param redirectAttributes
     *            les attributs de redirection utilisés pour stocker le message
     *            flash de succès
     * @param locale
     *            la locale courante
     * @return redirection vers l'accueil
     */
    @RequestMapping(params = { "activationDeMasse" })
    String activationDeMasse(@RequestParam(value = "selectedElements") final long[] ids,
            final RedirectAttributes redirectAttributes, final Locale locale);

    /**
     * Change le tri courant et le sauve en session
     *
     * @param pageable
     *            l'objet de pagination récupéré des paramètres
     * @return redirection vers l'accueil
     */
    @RequestMapping(params = { "sort" })
    String trier(final Pageable pageable);

    /**
     * Change le nombre de lignes affiché et le sauve en session
     *
     * @param pageable
     *            l'objet de pagination récupéré des paramètres
     * @return redirection vers l'accueil
     */
    @RequestMapping(params = { "size" })
    String changerLignesParPages(final Pageable pageable);

    /**
     * Change la page courante et sauve en session
     *
     * @param pageable
     *            l'objet de pagination récupéré des paramètres
     * @return redirection vers l'accueil
     */
    @RequestMapping(params = { "page" })
    String changerDePage(final Pageable pageable);
}

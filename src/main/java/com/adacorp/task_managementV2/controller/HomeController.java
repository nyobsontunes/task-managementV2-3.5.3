package com.adacorp.task_managementV2.controller;

import com.adacorp.task_managementV2.constantes.RoleConstante;
import com.adacorp.task_managementV2.constantes.StateConstante;
import com.adacorp.task_managementV2.model.HostName;
import com.adacorp.task_managementV2.model.Task;
import com.adacorp.task_managementV2.model.Utilisateur;
import com.adacorp.task_managementV2.model.UtilisateurTask;
import com.adacorp.task_managementV2.security.CustomUserDetailsService;
import com.adacorp.task_managementV2.security.CustomerUserDetails;
import com.adacorp.task_managementV2.services.StateService;
import com.adacorp.task_managementV2.services.TaskService;
import com.adacorp.task_managementV2.services.UtilisateurService;
import com.adacorp.task_managementV2.services.UtilisateurTaskService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    private UtilisateurService userService ;
    private StateService stateService ;
    private TaskService taskService ;
    private UtilisateurTaskService userTaskService ;
    private Task task ;
    private UtilisateurTask userTask ;
    private static final  String INDEX_LOGIN = "index-login" ;
    private static final  String INDEX_FORGOT_PASSWPORD = "index-forgot-password" ;
    private static final String SUCCESS = "success" ;
    private static final String ERROR = "error" ;
    private static final String USER = "user" ;
    private static final String MSG = "msg" ;
    @Autowired
    public HomeController(UtilisateurService userService, StateService stateService, TaskService taskService, UtilisateurTaskService userTaskService) {
        this.userService = userService;
        this.stateService = stateService;
        this.taskService = taskService;
        this.userTaskService = userTaskService;
    }

    /** @ RequestMapping(value = "/", method = RequestMethod.GET) **/
    @GetMapping(value = "/")
    public String indexApp(Model model){
        model.addAttribute(SUCCESS,"Task Management Application !!!") ;
        // **************** Ceci est hors Spring-security ***********************
        // return "redirect:/home-list-users";
        // **************** Ceci est hors Spring-security ***********************
        return INDEX_LOGIN ;
    }

    /** @ RequestMapping(value = "/index-login", method = RequestMethod.GET) **/
    @GetMapping(value = "/index-login")
    public String indexLogin(Model model, @RequestParam final Optional<String> error, HttpServletRequest request){
        model.addAttribute(SUCCESS,"Task Management Application !!!") ;
        // **************** Ceci est hors Spring-security ***********************
        // return "redirect:/home-list-users";
        // **************** Ceci est hors Spring-security ***********************

        // FDE_77400 : Gestion des Erreurs & des Exceptions Java Thymeleaf.
        if (error.isPresent()) {
            if (error.get().equals("1")) {
                model.addAttribute("isUserBlocked", true);
            } else {
                model.addAttribute("estConnecte", false);
            }
        } else {
            // FDE_77400 : Objet de récupération du lot d'exception venant du CustomFailureAuthenticationService.
            Object errorException = request.getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
            if (errorException != null) {
                // Renvoie du Message de l'exception vers la vue pour orienter l'Utilisateur vers l'Administrateur
                model.addAttribute("errorMsg", errorException);
                request.getSession().removeAttribute("SPRING_SECURITY_LAST_EXCEPTION");
            }
        }
        return INDEX_LOGIN ;
    }

    /** @ RequestMapping(value = "/dashboard", method = RequestMethod.GET) **/
    @GetMapping("/dashboard")
    public String dashboard(Principal p, HttpServletRequest request, Model model) {

        /* Utilisateur de la session de Connexion */
        Utilisateur connectedUserDB = userService.findByEmail( p.getName() ).get() ;

        // Gestion de l'Authentification en Mode ADMIN
        if (connectedUserDB.getRole().getCode().equals(RoleConstante.ROLE_ADMIN)) {
            model.addAttribute("newState", this.taskService.countByStateCode(StateConstante.NEW) ) ;
            model.addAttribute("todoState", this.taskService.countByStateCode(StateConstante.TODO) ) ;
            model.addAttribute("doneState", this.taskService.countByStateCode(StateConstante.DONE) ) ;
            model.addAttribute("allState", this.taskService.count() ) ;

            List<Task> taskNewList = this.taskService.findAllStateCode(StateConstante.NEW) ;
            List<Task> taskTodoList = this.taskService.findAllStateCode(StateConstante.TODO) ;
            List<Task> taskDoneList = this.taskService.findAllStateCode(StateConstante.DONE) ;

            model.addAttribute("taskNewList", taskNewList ) ;
            model.addAttribute("taskTodoList", taskTodoList ) ;
            model.addAttribute("taskDoneList", taskDoneList ) ;
        }

        // Gestion de l'Authentification en Mode !ADMIN
        if (!connectedUserDB.getRole().getCode().equals(RoleConstante.ROLE_ADMIN)) {

            // Méthode Reference - Pour la liste des tâches en NEW de l'Utilisateur connecté
            List<UtilisateurTask> taskNewList = this.userTaskService.findByUtilisateur_EmailAndTask_State_CodeOrderByAssignmentDateDesc(connectedUserDB.getEmail(), StateConstante.NEW) ;
            // Méthode Reference - Pour la liste des tâches en TO.DO de l'Utilisateur connecté
            List<UtilisateurTask> taskTodoList = this.userTaskService.findByUtilisateur_EmailAndTask_State_CodeOrderByAssignmentDateDesc(connectedUserDB.getEmail(), StateConstante.TODO) ;
            // Méthode Reference - Pour la liste des tâches en DONE de l'Utilisateur connecté
            List<UtilisateurTask> taskDoneList = this.userTaskService.findByUtilisateur_EmailAndTask_State_CodeOrderByAssignmentDateDesc(connectedUserDB.getEmail(), StateConstante.DONE) ;


            model.addAttribute("newState", taskNewList.size() ) ;
            model.addAttribute("todoState", taskTodoList.size() ) ;
            model.addAttribute("doneState", taskDoneList.size() ) ;
            model.addAttribute("allState", taskNewList.size()+taskTodoList.size()+taskDoneList.size() ) ;


            model.addAttribute("taskNewList", taskNewList ) ;
            model.addAttribute("taskTodoList", taskTodoList ) ;
            model.addAttribute("taskDoneList", taskDoneList ) ;
        }

        model.addAttribute("Titre", "commun.label.dashboard") ;

        HostName H = new HostName();

        //Affecter au Poste client la valeur de la méthode getPosteName
        String posteClient =   H.getPosteClient(request);
        //Modification de la valeur du poste client après affectation
        H.setHostname(posteClient);

        // Session Active juste pour 24H
        request.getSession().setMaxInactiveInterval(3600*24);

        // On stocke l'objet session dans le String ${session.connectedUserDB} ${session.connectedUserDB.email}
        request.getSession().setAttribute( "userPrincipal", p ) ;
        request.getSession().setAttribute( "connectedUserDB", connectedUserDB ) ;
        request.getSession().setAttribute( "connectedUserHostName", posteClient ) ;

        return "views/welcome-dashboard";
    }

    /** @ RequestMapping(value = "/index-logout", method = RequestMethod.GET) **/
    @GetMapping(value = "/index-logout")
    public String logOut(HttpServletRequest request, Model model){
        model.addAttribute(SUCCESS,"Task Management Application - Logout User !!!") ;
        // On détruit la session
        request.getSession().invalidate();
        return INDEX_LOGIN ;
    }

    /**
     * FDE_77400 : La page d'erreur 403
     * @return le nom du template de la page 403
     */
    @GetMapping("/403")
    public String error403() {
        return "error/403";
    }

    /**
     * FDE_77400 : La page d'erreur 404
     * @return le nom du template de la page 404
     */
    @GetMapping("/404")
    public String error404(final Model model, final HttpSession session,
                           final HttpServletRequest request) {
        addAuthUserSession(model ,session);
        return "error/404";

    }

    /**
     * FDE_77400 : La page d'erreur 500
     * @return le nom du template de la page 500
     */
    @GetMapping("/500")
    public String error500(final Model model, final HttpSession session, final HttpServletRequest request) {
        addAuthUserSession(model ,session);
        return "error/500";
    }

    /**
     * FDE_77400 : La page d'erreur global
     * @return le nom du template de la page error
     */
    @GetMapping("/error")
    public String handleGenericError(HttpServletRequest request, Model model, final HttpSession session) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == 404) {
            return "redirect:/404";
        }
        else if (statusCode == 500) {
            return "redirect:/500";
        }
        else if (statusCode == 403) {
            return "redirect:/403";
        }
        addAuthUserSession(model ,session);
        return "error/error";
    }

    /**
     * FDE_77400 : Méthode de récupération de User Session.
     * return Un Objet Utilisateur de Session
     */
    private void addAuthUserSession(Model model ,HttpSession session) {
        final SecurityContextImpl securitySession = (SecurityContextImpl) session.getAttribute("SPRING_SECURITY_CONTEXT");
        if (securitySession != null) {
            final Authentication auth = securitySession.getAuthentication();
            if (auth != null) {
                final CustomerUserDetails utilisateurCourrant = (CustomerUserDetails) auth.getPrincipal();
                model.addAttribute("utilisateurCourrant", utilisateurCourrant);
            }
        }
    }

    /**
     * Fonction pour : ignorer les champs Date vides dans le formulaires HTML
     * true → autorise les valeurs null si le champ est vide.
     * Le format "yyyy-MM-dd hh:mm:ss.SSS" doit correspondre à celui de ton <input type="date">.
     * */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS"), true));
    }
}

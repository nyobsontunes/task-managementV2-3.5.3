package com.adacorp.task_managementV2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping(value = "/")
    public String getSbAdminIndex(Model model){
        model.addAttribute("bonjour","BONJOUR LE MONDE");
        return "index" ;
    }
}

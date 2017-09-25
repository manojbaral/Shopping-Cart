package com.manoj.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Manoj Baral on 9/24/2017.
 */

@Controller
public class HomeController {

    @RequestMapping("/")
    public String home(){
        return "redirect:/stripe/";
    }
}

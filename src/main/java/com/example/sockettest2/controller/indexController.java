package com.example.sockettest2.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class indexController {

    @GetMapping("/test")
    public String test(){
        return "index";
    }
}

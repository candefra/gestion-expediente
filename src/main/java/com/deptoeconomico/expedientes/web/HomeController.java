package com.deptoeconomico.expedientes.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/principal")
    public String home() {
    	return "principal/principal";
    }
}
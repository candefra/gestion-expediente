package com.deptoeconomico.expedientes.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.deptoeconomico.expedientes.model.DestinatarioFrecuente;
import com.deptoeconomico.expedientes.service.DestinatarioFrecuenteService;

@Controller
@RequestMapping("/destinatarios")
public class DestinatarioFrecuenteController {

    private final DestinatarioFrecuenteService service;

    public DestinatarioFrecuenteController(DestinatarioFrecuenteService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("destinatarios", service.listarTodos());
        model.addAttribute("destinatario", new DestinatarioFrecuente());
        return "destinatarios/lista";
    }

    @PostMapping
    public String guardar(@ModelAttribute DestinatarioFrecuente destinatario) {
        service.guardar(destinatario);
        return "redirect:/destinatarios";
    }
}

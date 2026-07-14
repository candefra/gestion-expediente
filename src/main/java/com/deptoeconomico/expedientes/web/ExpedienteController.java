package com.deptoeconomico.expedientes.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.deptoeconomico.expedientes.model.Expediente;
import com.deptoeconomico.expedientes.service.EmpleadoService;
import com.deptoeconomico.expedientes.service.ExpedienteService;

@Controller
@RequestMapping("/expedientes")
public class ExpedienteController {

    private final ExpedienteService expedienteService;
    private final EmpleadoService empleadoService;

    public ExpedienteController(ExpedienteService expedienteService,
                                 EmpleadoService empleadoService) {
        this.expedienteService = expedienteService;
        this.empleadoService = empleadoService;
    }

    /**
     * Lista de expedientes. Si viene "empleadoId" por parametro,
     * filtra solo los de ese empleado (para ver "lo que tiene cada uno").
     */
    
    @GetMapping
    public String listar(@RequestParam(required = false) Long empleadoId, Model model) {
        List<Expediente> expedientes = (empleadoId != null)
                ? expedienteService.listarPorEmpleado(empleadoId)
                : expedienteService.listarTodos();

        model.addAttribute("expedientes", expedientes);
        model.addAttribute("empleados", empleadoService.listarTodos());
        model.addAttribute("empleadoIdSeleccionado", empleadoId);
        return "expedientes/lista";
    }

    /** Asigna (o reasigna) un expediente a un empleado. */
    @PostMapping("/{numeroTramite}/asignar")
    public String asignar(@PathVariable String numeroTramite, @RequestParam Long empleadoId) {
        expedienteService.asignarEmpleado(numeroTramite, empleadoId);
        return "redirect:/expedientes";
    }
    
    @GetMapping("/buscar")
    public String buscarExpediente() {
        return "expedientes/busqueda";
    }
    
    @PostMapping("/buscar")
    public String buscar(@RequestParam String numeroTramite, Model model) {

        try {
            Expediente expediente = expedienteService.buscarPorNumero(numeroTramite);
            model.addAttribute("expediente", expediente);
            return "expedientes/menu";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "expedientes/busqueda";
        }
    }
}

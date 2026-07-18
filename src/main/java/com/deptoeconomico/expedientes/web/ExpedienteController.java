package com.deptoeconomico.expedientes.web;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.deptoeconomico.expedientes.model.CondicionExpediente;
import com.deptoeconomico.expedientes.model.Empleado;
import com.deptoeconomico.expedientes.model.Expediente;
import com.deptoeconomico.expedientes.model.TipoExpediente;
import com.deptoeconomico.expedientes.service.EmpleadoService;
import com.deptoeconomico.expedientes.service.ExpedienteService;
import com.deptoeconomico.expedientes.service.NotaService;

@Controller
@RequestMapping("/expedientes")
public class ExpedienteController {

    private final ExpedienteService expedienteService;
    private final EmpleadoService empleadoService;  
    private final NotaService notaService;
    
    public ExpedienteController(
            ExpedienteService expedienteService,
            EmpleadoService empleadoService,
            NotaService notaService) {

        this.expedienteService = expedienteService;
        this.empleadoService = empleadoService;
        this.notaService = notaService;
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
        Map<String, Boolean> tieneNotas = new HashMap<>();

        for (Expediente exp : expedientes) {
            tieneNotas.put(
                exp.getNumeroTramite(),
                notaService.tieneNotas(exp.getNumeroTramite())
            );
        }

        model.addAttribute("tieneNotas", tieneNotas);
        
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
    
    @GetMapping("/nuevo")
    public String nuevo(Model model){

        model.addAttribute("empleados", empleadoService.listarTodos());
        model.addAttribute("tipos", TipoExpediente.values());
        model.addAttribute("fechaHoy", LocalDate.now());

        return "expedientes/nuevo";
    }
    
    @PostMapping
    public String guardar(
            @RequestParam String numeroTramite,
            @RequestParam(required = false) String numeroUnico,
            @RequestParam TipoExpediente tipo,
            @RequestParam(required = false) String caratula,
            @RequestParam(required = false) String iniciador,
            @RequestParam(required = false) String origen,
            @RequestParam LocalDate fechaIngreso,
            @RequestParam(required = false) Long empleadoId){

        Expediente expediente = new Expediente();

        expediente.setNumeroTramite(numeroTramite);
        expediente.setNumeroUnico(numeroUnico);
        expediente.setTipo(tipo);
        expediente.setCaratula(caratula);
        expediente.setIniciador(iniciador);
        expediente.setOrigen(origen);
        expediente.setFechaIngreso(fechaIngreso);

        expediente.setCondicion(CondicionExpediente.NUEVO);

        if(empleadoId != null){
            Empleado empleado = empleadoService.buscarPorId(empleadoId);
            expediente.setEmpleadoAsignado(empleado);
        }

        expedienteService.guardar(expediente);

        return "redirect:/expedientes";
    }
}

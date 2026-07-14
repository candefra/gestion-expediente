package com.deptoeconomico.expedientes.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.deptoeconomico.expedientes.model.DetalleRemito;
import com.deptoeconomico.expedientes.model.Expediente;
import com.deptoeconomico.expedientes.model.Remito;
import com.deptoeconomico.expedientes.model.TipoExpediente;
import com.deptoeconomico.expedientes.service.RemitoService;

@Controller
@RequestMapping("/remitos")
public class RemitoController {

    /** Cantidad de filas vacias con las que arranca el formulario nuevo. */
    private static final int FILAS_INICIALES = 3;

    private final RemitoService remitoService;

    public RemitoController(RemitoService remitoService) {
        this.remitoService = remitoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("remitos", remitoService.listarTodos());
        return "remitos/lista";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        RemitoFormulario formulario = new RemitoFormulario();
        for (int i = 0; i < FILAS_INICIALES; i++) {
            formulario.getDetalles().add(new DetalleFormulario());
        }
        model.addAttribute("remitoFormulario", formulario);
        model.addAttribute("tipos", TipoExpediente.values());
        return "remitos/nuevo";
    }

    @PostMapping
    public String guardar(@ModelAttribute("remitoFormulario") RemitoFormulario formulario) {
        Remito remito = new Remito();
        remito.setNumeroRemito(formulario.getNumeroRemito());
        remito.setFechaRemito(formulario.getFechaRemito());
        remito.setRemitente(formulario.getRemitente());
        remito.setDestino(formulario.getDestino());

        List<DetalleRemito> detalles = new ArrayList<>();
        for (DetalleFormulario fila : formulario.getDetalles()) {
            // las filas que quedaron vacias (sobrantes) se ignoran
            if (fila.getNumeroTramite() == null || fila.getNumeroTramite().isBlank()) {
                continue;
            }

            Expediente expediente = new Expediente();
            expediente.setNumeroTramite(fila.getNumeroTramite());
            expediente.setNumeroUnico(fila.getNumeroUnico());
            expediente.setTipo(fila.getTipo());
            expediente.setCaratula(fila.getCaratula());
            expediente.setIniciador(fila.getIniciador());
            expediente.setOrigen(fila.getOrigen());

            DetalleRemito detalle = new DetalleRemito();
            detalle.setExpediente(expediente);
            detalle.setFojas(fila.getFojas());
            detalles.add(detalle);
        }

        remitoService.registrarRemito(remito, detalles);
        return "redirect:/remitos";
    }
}

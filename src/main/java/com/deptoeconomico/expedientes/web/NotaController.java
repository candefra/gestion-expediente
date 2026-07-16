package com.deptoeconomico.expedientes.web;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.deptoeconomico.expedientes.model.Empleado;
import com.deptoeconomico.expedientes.model.EstadoDocumento;
import com.deptoeconomico.expedientes.model.EstadoNota;
import com.deptoeconomico.expedientes.model.Expediente;
import com.deptoeconomico.expedientes.model.Nota;
import com.deptoeconomico.expedientes.model.TipoNota;
import com.deptoeconomico.expedientes.service.DestinatarioFrecuenteService;
import com.deptoeconomico.expedientes.service.EmpleadoService;
import com.deptoeconomico.expedientes.service.ExpedienteService;
import com.deptoeconomico.expedientes.service.NotaService;

@Controller
@RequestMapping("/notas")
public class NotaController {

    private final NotaService notaService;
    private final ExpedienteService expedienteService;
    private final EmpleadoService empleadoService;
    private final DestinatarioFrecuenteService destinatarioFrecuenteService;

    public NotaController(NotaService notaService,
                           ExpedienteService expedienteService,
                           EmpleadoService empleadoService,
                           DestinatarioFrecuenteService destinatarioFrecuenteService) {
        this.notaService = notaService;
        this.expedienteService = expedienteService;
        this.empleadoService = empleadoService;
        this.destinatarioFrecuenteService = destinatarioFrecuenteService;
    }
    
    @GetMapping("/expediente/{numeroTramite}")
    public String verNotas(@PathVariable String numeroTramite, Model model) {

        Expediente expediente = expedienteService.buscarPorNumero(numeroTramite);

        model.addAttribute("expediente", expediente);
        model.addAttribute("notas", notaService.listarPorExpediente(numeroTramite));

        return "notas/historial";
    }
    
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> verPdf(@PathVariable Long id) throws IOException {

        Nota nota = notaService.buscarPorId(id);

        byte[] archivo = notaService.generarPdf(nota);

        String nombreArchivo =
                nota.getTipoTexto().toLowerCase()
                + "-"
                + nota.getNumero()
                + "-"
                + nota.getExpediente().getNumeroTramite()
                + ".pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline()
                                .filename(nombreArchivo)
                                .build()
                                .toString())
                .body(archivo);
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("notas", notaService.listarTodas());
        model.addAttribute("estados", EstadoNota.values());
        return "notas/lista";
    }

    @GetMapping("/nueva")
    public String formularioNueva(@RequestParam(required = false) String numeroTramite, Model model) {
        model.addAttribute("expedientes", expedienteService.listarTodos());
        model.addAttribute("empleados", empleadoService.listarTodos());
        model.addAttribute("tipos", TipoNota.values());
        model.addAttribute("destinatariosFrecuentes", destinatarioFrecuenteService.listarTodos());
        model.addAttribute("numeroTramiteSeleccionado", numeroTramite);
        model.addAttribute("fechaHoy", LocalDate.now());
        return "notas/nueva";
    }

    @PostMapping
    public Object generar(
            @RequestParam(required = false) String numeroTramite,
            @RequestParam(required = false) Long empleadoId,
            @RequestParam(required = false) TipoNota tipo,
            @RequestParam String accion,
            @RequestParam(required = false) String tipoOtro,
            @RequestParam(required = false) String cargo,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String nombreDestinatario,
            @RequestParam(required = false) String cuerpo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha)
            throws IOException {

    	Expediente expediente = null;
    	if (numeroTramite != null && !numeroTramite.isBlank()) {
    	    expediente = expedienteService.buscarPorNumero(numeroTramite);
    	}

    	Empleado empleado = null;
    	if (empleadoId != null) {
    	    empleado = empleadoService.buscarPorId(empleadoId);
    	}

        Nota nota = new Nota();
        nota.setExpediente(expediente);
        nota.setEmpleado(empleado);
        nota.setTipo(tipo);
        nota.setTipoOtro(tipoOtro);
        nota.setCargo(cargo);
        nota.setArea(area);
        nota.setNombreDestinatario(nombreDestinatario);
        nota.setCuerpo(cuerpo);
        nota.setFecha(fecha);

        // -------- GUARDAR ----------
        if ("guardar".equals(accion)) {

            nota.setEstadoDocumento(EstadoDocumento.BORRADOR);

            notaService.guardar(nota);

            return "redirect:/notas";
        }

        // -------- FINALIZAR ----------
        if (expediente == null
                || empleado == null
                || tipo == null
                || cuerpo == null
                || cuerpo.isBlank()
                || fecha == null) {

            throw new IllegalArgumentException(
                    "Para finalizar la nota debe completar todos los campos obligatorios.");
        }
        nota.setEstadoDocumento(EstadoDocumento.FINALIZADO);

        Nota notaGuardada = notaService.guardar(nota);

        expedienteService.finalizarExpediente(numeroTramite);

        byte[] archivo = notaService.generarPdf(notaGuardada);

        String nombreArchivo =
                notaGuardada.getTipoTexto().toLowerCase()
                + "-"
                + notaGuardada.getNumero()
                + "-"
                + expediente.getNumeroTramite()
                + ".pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(nombreArchivo)
                                .build()
                                .toString())
                .body(archivo);
    }
}
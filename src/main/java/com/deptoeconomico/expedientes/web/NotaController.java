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
    public ResponseEntity<byte[]> generar(@RequestParam String numeroTramite,
                                           @RequestParam Long empleadoId,
                                           @RequestParam TipoNota tipo,
                                           @RequestParam(required = false) String tipoOtro,
                                           @RequestParam(required = false) String cargo,
                                           @RequestParam(required = false) String area,
                                           @RequestParam(required = false) String nombreDestinatario,
                                           @RequestParam String cuerpo,
                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha)
            throws IOException {

        Expediente expediente = expedienteService.buscarPorNumero(numeroTramite);
        Empleado empleado = empleadoService.buscarPorId(empleadoId);

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

        Nota notaGuardada = notaService.guardar(nota);
        byte[] archivo = notaService.generarPdf(notaGuardada);

        String nombreArchivo = notaGuardada.getTipoTexto().toLowerCase() + "-" + notaGuardada.getNumero()
                + "-" + expediente.getNumeroTramite() + ".pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(nombreArchivo).build().toString())
                .body(archivo);
    }

    @PostMapping("/{id}/estado")
    public String actualizarEstado(@PathVariable Long id, @RequestParam EstadoNota estado) {
        notaService.actualizarEstado(id, estado);
        return "redirect:/notas";
    }
}

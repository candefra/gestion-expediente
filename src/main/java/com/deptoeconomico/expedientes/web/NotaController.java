package com.deptoeconomico.expedientes.web;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
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
import com.deptoeconomico.expedientes.model.Expediente;
import com.deptoeconomico.expedientes.model.Nota;
import com.deptoeconomico.expedientes.model.TipoNota;
import com.deptoeconomico.expedientes.service.DestinatarioFrecuenteService;
import com.deptoeconomico.expedientes.service.EmpleadoService;
import com.deptoeconomico.expedientes.service.NotaService;
import com.deptoeconomico.expedientes.model.EstadoNota;
import com.deptoeconomico.expedientes.service.ExpedienteService;

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
                                .build().toString())
                .body(archivo);
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("notas", notaService.listarTodas());
        model.addAttribute("estados", EstadoNota.values());
        return "notas/lista";
    }

    @GetMapping("/nueva")
    public String formularioNueva(@RequestParam(required = false) String numeroTramite,
                                  @RequestParam(required = false) Long id,
                                  Model model) {
        model.addAttribute("expedientes", expedienteService.listarTodos());
        model.addAttribute("empleados", empleadoService.listarTodos());
        model.addAttribute("tipos", TipoNota.values());
        model.addAttribute("destinatariosFrecuentes", destinatarioFrecuenteService.listarTodos());
        model.addAttribute("fechaHoy", LocalDate.now());

        if (id != null) {
            Nota nota = notaService.buscarPorId(id);
            model.addAttribute("nota", nota);
            model.addAttribute("numeroTramiteSeleccionado",
                    nota.getExpediente() != null ? nota.getExpediente().getNumeroTramite() : null);
        } else if (numeroTramite != null) {
            model.addAttribute("numeroTramiteSeleccionado", numeroTramite);
            Optional<Nota> borrador = notaService.buscarBorrador(numeroTramite);
            if (borrador.isPresent()) {
                model.addAttribute("nota", borrador.get());
            } else {
                Nota nota = new Nota();
                Expediente expediente = expedienteService.buscarPorNumero(numeroTramite);
                if (expediente != null && expediente.getEmpleadoAsignado() != null) {
                    nota.setEmpleado(expediente.getEmpleadoAsignado());
                }
                model.addAttribute("nota", nota);
            }
        } else {
            model.addAttribute("nota", new Nota());
        }
        return "notas/nueva";
    }

    // ✅ CORRECCIÓN 124/135 — Parámetro notaId para retornar()
    @GetMapping("/retornar")
    public String retornar(@RequestParam(required = false) String accion,
                           @RequestParam Long notaId,
                           @RequestParam(required = false) String retorno) {

        if ("finalizar".equals(accion)) {
            return "redirect:/expedientes";
        }

        if (notaService.existe(notaId)) {
            return "notas/nueva?id=" + notaId;
        }

        return "redirect:" + (retorno != null ? retorno : "/expedientes");
    }

    @PostMapping
    public Object generar(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String numeroTramite,
            @RequestParam(required = false) Long empleadoId,
            @RequestParam(required = false) TipoNota tipo,
            @RequestParam String accion,
            @RequestParam(required = false) String tipoOtro,
            @RequestParam(required = false) String cargo,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String nombreDestinatario,
            @RequestParam(required = false) String cuerpo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            Model model) throws IOException {

        Expediente expediente = null;
        if (numeroTramite != null && !numeroTramite.isBlank()) {
            expediente = expedienteService.buscarPorNumero(numeroTramite);
        }

        Empleado empleado = null;
        if (empleadoId != null) {
            empleado = empleadoService.buscarPorId(empleadoId);
        }

        Nota nota;
        if (id != null) {
            nota = notaService.buscarPorId(id);
        } else {
            nota = new Nota();
        }

        // --- Asignaciones ---
        nota.setExpediente(expediente);
        nota.setEmpleado(empleado);
        nota.setTipo(tipo);
        nota.setTipoOtro(tipoOtro);
        nota.setCargo(cargo);
        nota.setArea(area);
        nota.setNombreDestinatario(nombreDestinatario);
        nota.setCuerpo(cuerpo);
        nota.setFecha(fecha);

        // ✅ CORRECCIÓN LÍNEA ~124: GUARDAR (notaService.existe con notaId)
        if ("guardar".equals(accion)) {
            Nota notaGuardada = notaService.guardar(nota);
            model.addAttribute("expedientes", expedienteService.listarTodos());
            model.addAttribute("empleados", empleadoService.listarTodos());
            model.addAttribute("tipos", TipoNota.values());
            model.addAttribute("destinatariosFrecuentes", destinatarioFrecuenteService.listarTodos());
            model.addAttribute("numeroTramiteSeleccionado", numeroTramite);
            model.addAttribute("fechaHoy", LocalDate.now());
            model.addAttribute("nota", notaGuardada);
            return "redirect:/notas/nueva?id=" + notaGuardada.getId();
        }

        // ✅ CORRECCIÓN LÍNEA ~128: FINALIZAR (StringBuilder inicializado, expediente no null)
        if ("finalizar".equals(accion)) {
            if (expediente == null) {
                throw new IllegalArgumentException("Expediente no seleccionado.");
            }

            StringBuilder faltantes = new StringBuilder();
            if (empleado == null) faltantes.append("Generada por, ");
            if (tipo == null) faltantes.append("Tipo, ");
            if (cargo == null || cargo.isBlank()) faltantes.append("Cargo, ");
            if (area == null || area.isBlank()) faltantes.append("Área, ");
            if (nombreDestinatario == null || nombreDestinatario.isBlank()) faltantes.append("Nombre del destinatario, ");
            if (cuerpo == null || cuerpo.isBlank()) faltantes.append("Cuerpo, ");
            if (fecha == null) faltantes.append("Fecha, ");

            if (faltantes.length() > 0) {
                String lista = faltantes.substring(0, faltantes.length() - 2);

                // Volvemos a armar el formulario con lo que el usuario ya cargó
                model.addAttribute("expedientes", expedienteService.listarTodos());
                model.addAttribute("empleados", empleadoService.listarTodos());
                model.addAttribute("tipos", TipoNota.values());
                model.addAttribute("destinatariosFrecuentes", destinatarioFrecuenteService.listarTodos());
                model.addAttribute("numeroTramiteSeleccionado", numeroTramite);
                model.addAttribute("fechaHoy", LocalDate.now());
                model.addAttribute("nota", nota);
                model.addAttribute("error", "Para finalizar la nota debe completar: " + lista + ".");
                return "notas/nueva";
            }

            Nota notaGuardada = notaService.finalizar(nota);
            expedienteService.finalizarExpediente(expediente.getNumeroTramite(), empleado);

            return "redirect:/expedientes?notaFinalizada=" + notaGuardada.getId();
        }

        throw new IllegalArgumentException("Acción no válida.");
    }
}
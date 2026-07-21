package com.deptoeconomico.expedientes.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deptoeconomico.expedientes.model.CondicionExpediente;
import com.deptoeconomico.expedientes.model.Empleado;
import com.deptoeconomico.expedientes.model.EstadoExpediente;
import com.deptoeconomico.expedientes.model.Expediente;
import com.deptoeconomico.expedientes.repository.EmpleadoRepository;
import com.deptoeconomico.expedientes.repository.ExpedienteRepository;

@Service
public class ExpedienteService {

    private final ExpedienteRepository expedienteRepository;
    private final EmpleadoRepository empleadoRepository;

    public ExpedienteService(ExpedienteRepository expedienteRepository,
                              EmpleadoRepository empleadoRepository) {
        this.expedienteRepository = expedienteRepository;
        this.empleadoRepository = empleadoRepository;
    }

    public List<Expediente> listarTodos() {
        return expedienteRepository.findAll();
    }

    /** Expedientes que tiene actualmente un empleado puntual. */
    public List<Expediente> listarPorEmpleado(Long empleadoId) {
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("No existe el empleado con id " + empleadoId));
        return expedienteRepository.findByEmpleadoAsignado(empleado);
    }

    /** Expedientes que llegaron pero todavia no se le asignaron a nadie. */
    public List<Expediente> listarSinAsignar() {
        return expedienteRepository.findByEmpleadoAsignadoIsNull();
    }

    public Expediente buscarPorNumero(String numeroTramite) {
        return expedienteRepository.findById(numeroTramite)
                .orElseThrow(() ->
                    new IllegalArgumentException("No existe el expediente " + numeroTramite));
    }

    /**
     * Asigna (o reasigna) un expediente a un empleado.
     * Como solo guardamos el estado actual, esto simplemente pisa
     * el empleado anterior con el nuevo.
     */
    @Transactional
    public Expediente asignarEmpleado(String numeroTramite, Long empleadoId) {
        Expediente expediente = expedienteRepository.findById(numeroTramite)
                .orElseThrow(() -> new IllegalArgumentException("No existe el expediente " + numeroTramite));
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("No existe el empleado con id " + empleadoId));
        if (expediente.getEstado() == EstadoExpediente.FINALIZADO) {
            throw new IllegalStateException("No se puede reasignar un expediente finalizado.");
        }

        expediente.setEmpleadoAsignado(empleado);
        return expedienteRepository.save(expediente);
    }
    
   
    @Transactional
    public void finalizarExpediente(String numeroTramite, Empleado empleadoQueResponde) {
        Expediente expediente = buscarPorNumero(numeroTramite);
        expediente.setEstado(EstadoExpediente.FINALIZADO);
        if (empleadoQueResponde != null) {
            expediente.setEmpleadoAsignado(empleadoQueResponde);
        }
        expedienteRepository.save(expediente);
    }
    
    @Transactional
    public Expediente guardar(Expediente expediente) {
        // ← VALIDACIÓN: no permitir duplicados
        if (expediente.getNumeroTramite() != null && !expediente.getNumeroTramite().isBlank()) {
            boolean existe = expedienteRepository.existsByNumeroTramite(expediente.getNumeroTramite());
            if (existe) {
                throw new IllegalArgumentException(
                    "Ya existe un expediente con el número de trámite: " + expediente.getNumeroTramite()
                );
            }
        }
        return expedienteRepository.save(expediente);
    }
    
    public List<Expediente> buscarConFiltros(String search, Long empleadoId) {
        List<Expediente> resultado;

        if (search != null && !search.isBlank()) {
            resultado = expedienteRepository
                .findByNumeroTramiteContainingIgnoreCaseOrCaratulaContainingIgnoreCaseOrderByFechaIngresoDesc(
                    search, search);
        } else if (empleadoId != null) {
            Empleado empleado = empleadoRepository.findById(empleadoId)
                    .orElseThrow(() -> new IllegalArgumentException("No existe el empleado con id " + empleadoId));
            resultado = expedienteRepository.findByEmpleadoAsignadoOrderByFechaIngresoDesc(empleado);
        } else {
            resultado = expedienteRepository.findAllByOrderByFechaIngresoDesc();
        }

        return resultado;
    }
    
}

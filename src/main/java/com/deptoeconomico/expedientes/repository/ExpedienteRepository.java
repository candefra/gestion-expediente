package com.deptoeconomico.expedientes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deptoeconomico.expedientes.model.Empleado;
import com.deptoeconomico.expedientes.model.EstadoExpediente;
import com.deptoeconomico.expedientes.model.Expediente;
import com.deptoeconomico.expedientes.model.TipoExpediente;

public interface ExpedienteRepository extends JpaRepository<Expediente, String> {

    /** Todos los expedientes que tiene actualmente un empleado. */
    List<Expediente> findByEmpleadoAsignado(Empleado empleado);

    /** Todos los expedientes de un tipo (cambio de titularidad, DDJJ, etc). */
    List<Expediente> findByTipo(TipoExpediente tipo);

    /** Expedientes que todavia no fueron asignados a nadie. */
    List<Expediente> findByEmpleadoAsignadoIsNull();
    
    boolean existsByNumeroTramite(String numeroTramite); // ← AGREGAR ESTO

    List<Expediente> findByEstado(EstadoExpediente estado);
    }



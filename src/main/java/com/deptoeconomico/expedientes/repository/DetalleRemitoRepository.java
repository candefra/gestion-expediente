package com.deptoeconomico.expedientes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deptoeconomico.expedientes.model.DetalleRemito;
import com.deptoeconomico.expedientes.model.Expediente;

public interface DetalleRemitoRepository extends JpaRepository<DetalleRemito, Long> {

    /** Historial de remitos por los que paso un expediente. */
    List<DetalleRemito> findByExpediente(Expediente expediente);
}

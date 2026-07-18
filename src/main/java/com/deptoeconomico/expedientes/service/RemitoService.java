package com.deptoeconomico.expedientes.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deptoeconomico.expedientes.model.CondicionExpediente;
import com.deptoeconomico.expedientes.model.DetalleRemito;
import com.deptoeconomico.expedientes.model.Expediente;
import com.deptoeconomico.expedientes.model.Remito;
import com.deptoeconomico.expedientes.repository.ExpedienteRepository;
import com.deptoeconomico.expedientes.repository.RemitoRepository;

@Service
public class RemitoService {

    private final RemitoRepository remitoRepository;
    private final ExpedienteRepository expedienteRepository;

    public RemitoService(RemitoRepository remitoRepository,
                          ExpedienteRepository expedienteRepository) {
        this.remitoRepository = remitoRepository;
        this.expedienteRepository = expedienteRepository;
    }

    public List<Remito> listarTodos() {
        return remitoRepository.findAll();
    }

    /**
     * Registra un remito nuevo junto con los expedientes que trae.
     *
     * Para cada detalle: si el numero de tramite ya existe en la base,
     * lo trata como REINGRESADO (reusa el expediente existente).
     * Si no existe, lo crea como NUEVO con los datos que vienen
     * cargados en detalle.getExpediente().
     *
     * @param remito   remito a crear (numero, fecha, remitente, destino)
     * @param detalles lista de detalles, cada uno con su Expediente
     *                 (con los datos cargados) y la cantidad de fojas
     */
    @Transactional
    public Remito registrarRemito(Remito remito, List<DetalleRemito> detalles) {
        for (DetalleRemito detalle : detalles) {
            Expediente expedienteCargado = detalle.getExpediente();
            String numeroTramite = expedienteCargado.getNumeroTramite();

            Expediente expediente = expedienteRepository.findById(numeroTramite)
                    .map(existente -> {
                        existente.setCondicion(CondicionExpediente.REINGRESADO);
                        existente.setFechaIngreso(remito.getFechaRemito());
                        return existente;
                    })
                    .orElseGet(() -> {
                        expedienteCargado.setCondicion(CondicionExpediente.NUEVO);
                        expedienteCargado.setFechaIngreso(remito.getFechaRemito());
                        return expedienteCargado;
                    });

            if (expediente.getTipo() == null) {
                throw new IllegalArgumentException(
                    "Todos los expedientes del remito deben tener un tipo de trámite."
                );
            }
            expedienteRepository.save(expediente);
            
            detalle.setExpediente(expediente);
            detalle.setRemito(remito);
            remito.getDetalles().add(detalle);
        }

        return remitoRepository.save(remito);
    }
}

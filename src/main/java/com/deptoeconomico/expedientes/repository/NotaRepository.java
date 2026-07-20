package com.deptoeconomico.expedientes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.deptoeconomico.expedientes.model.EstadoDocumento;
import com.deptoeconomico.expedientes.model.Expediente;
import com.deptoeconomico.expedientes.model.Nota;
import com.deptoeconomico.expedientes.model.TipoNota;

public interface NotaRepository extends JpaRepository<Nota, Long> {

    /** Todas las notas generadas para un expediente puntual. */
    List<Nota> findByExpediente(Expediente expediente);

    /** Cuenta cuantas notas de un tipo ya tiene un expediente, para calcular el proximo numero. */
    long countByExpedienteAndTipo(Expediente expediente, TipoNota tipo);
    
    /** Notas de un expediente + tipo en el mismo año */
    @Query("SELECT n FROM Nota n WHERE " +
    	       "n.expediente.numeroTramite = :numeroTramite " +
    	       "AND n.tipo = :tipo " +
    	       "AND EXTRACT(YEAR FROM n.fecha) = :anio")
    List<Nota> findByExpedienteAndTipoAndFechaYear(
    	        @Param("numeroTramite") String numeroTramite,
    	        @Param("tipo") TipoNota tipo,
    	        @Param("anio") int anio);
    
    List<Nota> findByExpedienteNumeroTramiteOrderByFechaDesc(String numeroTramite);
    
    boolean existsByExpedienteNumeroTramite(String numeroTramite);
    
    Optional<Nota> findFirstByExpedienteNumeroTramiteAndEstadoDocumento(String numeroTramite, EstadoDocumento estadoDocumento);
    
      
}

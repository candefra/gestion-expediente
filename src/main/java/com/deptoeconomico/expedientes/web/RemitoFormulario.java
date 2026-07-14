package com.deptoeconomico.expedientes.web;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Objeto que junta los datos del remito y la lista de expedientes
 * que trae, para poder cargarlos todos juntos en una sola pantalla.
 */
public class RemitoFormulario {

    private String numeroRemito;
    private LocalDate fechaRemito;
    private String remitente;
    private String destino;
    private List<DetalleFormulario> detalles = new ArrayList<>();

    public String getNumeroRemito() {
        return numeroRemito;
    }

    public void setNumeroRemito(String numeroRemito) {
        this.numeroRemito = numeroRemito;
    }

    public LocalDate getFechaRemito() {
        return fechaRemito;
    }

    public void setFechaRemito(LocalDate fechaRemito) {
        this.fechaRemito = fechaRemito;
    }

    public String getRemitente() {
        return remitente;
    }

    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public List<DetalleFormulario> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleFormulario> detalles) {
        this.detalles = detalles;
    }
}

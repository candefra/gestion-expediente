package com.deptoeconomico.expedientes.model;

/**
 * Resultado de una nota una vez que sale del departamento y vuelve
 * una respuesta. PENDIENTE es el estado inicial, antes de saber que
 * paso con ella.
 */
public enum EstadoNota {
    PENDIENTE("Pendiente"),
    RESUELTO("Resuelto"),
    OBSERVADO("Observado");

    private final String etiqueta;

    EstadoNota(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}

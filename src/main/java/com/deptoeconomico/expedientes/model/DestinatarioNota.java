package com.deptoeconomico.expedientes.model;

/**
 * A quien va dirigida la nota. Si no encaja en ninguna, se usa OTRO
 * y se completa el campo de texto libre "destinatarioOtro" de la Nota.
 */
public enum DestinatarioNota {
    JEFE_RT("Jefe RT"),
    DIRECCION_IMPUESTO("Dirección de Impuesto"),
    CONTRIBUYENTE("Contribuyente"),
    DIRECTOR("Director"),
    JEFE_DEPTO("Jefe de Departamento"),
    OTRO("Otro");

    private final String etiqueta;

    DestinatarioNota(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}

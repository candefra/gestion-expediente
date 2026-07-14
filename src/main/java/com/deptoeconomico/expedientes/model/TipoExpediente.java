package com.deptoeconomico.expedientes.model;

/**
 * Tipos de tramite que puede tener un expediente.
 * Si aparece un tramite que no encaja en ninguno, se usa OTROS.
 */
public enum TipoExpediente {
    CAMBIO_TITULARIDAD("Cambio de titularidad"),
    CAMBIO_DOMICILIO("Cambio de domicilio"),
    DDJJ("Declaración jurada"),
    RECTIFICACION_SUPERFICIE("Rectificación de superficie"),
    RECTIFICACION_AVALUO("Rectificación de avalúo"),
    OTROS("Otros");

    private final String etiqueta;

    TipoExpediente(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}

package com.deptoeconomico.expedientes.model;

public enum TipoNota {
    NOTA("Nota"),
    PROVIDENCIA("Providencia"),
    OTRO("Otro");

    private final String etiqueta;

    TipoNota(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}

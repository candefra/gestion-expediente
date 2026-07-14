package com.deptoeconomico.expedientes.model;

/**
 * Tipo de documento que se genera. La numeracion correlativa (ver
 * Nota.numero) se cuenta por separado para cada combinacion de
 * expediente + tipo: la Nota 1 y la Nota 2 de un expediente son
 * independientes de la Providencia 1 de ese mismo expediente.
 */
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

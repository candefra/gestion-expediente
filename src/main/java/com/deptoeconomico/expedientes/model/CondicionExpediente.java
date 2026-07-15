package com.deptoeconomico.expedientes.model;

/**
 * Indica si el expediente entra por primera vez al depto o si ya habia
 * estado antes y vuelve a ingresar.
 */
public enum CondicionExpediente {
    NUEVO("Nuevo"),
    REINGRESADO("Reingresado"),
	FINALIZADO("Finalizado");
	

    private final String etiqueta;

    CondicionExpediente(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}

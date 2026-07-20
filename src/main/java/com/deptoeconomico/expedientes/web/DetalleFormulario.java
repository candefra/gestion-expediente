package com.deptoeconomico.expedientes.web;

import com.deptoeconomico.expedientes.model.TipoExpediente;

/**
 * Representa UNA fila del formulario de carga de remito
 * (un expediente con su cantidad de fojas).
 * No es una entidad de base de datos, es solo para recibir el HTML.
 */
public class DetalleFormulario {

    private String numeroTramite;
    private String numeroUnico;
    private TipoExpediente tipo;
    private String caratula;
    private String iniciador;
    private String origen;
    private Integer fojas;
    private Long empleado;

    public Long getEmpleado() {
        return empleado;
    }
    public void setEmpleado(Long empleado) {
        this.empleado = empleado;
    }
   

    public String getNumeroTramite() {
        return numeroTramite;
    }

    public void setNumeroTramite(String numeroTramite) {
        this.numeroTramite = numeroTramite;
    }

    public String getNumeroUnico() {
        return numeroUnico;
    }

    public void setNumeroUnico(String numeroUnico) {
        this.numeroUnico = numeroUnico;
    }

    public TipoExpediente getTipo() {
        return tipo;
    }

    public void setTipo(TipoExpediente tipo) {
        this.tipo = tipo;
    }

    public String getCaratula() {
        return caratula;
    }

    public void setCaratula(String caratula) {
        this.caratula = caratula;
    }

    public String getIniciador() {
        return iniciador;
    }

    public void setIniciador(String iniciador) {
        this.iniciador = iniciador;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public Integer getFojas() {
        return fojas;
    }

    public void setFojas(Integer fojas) {
        this.fojas = fojas;
    }


}

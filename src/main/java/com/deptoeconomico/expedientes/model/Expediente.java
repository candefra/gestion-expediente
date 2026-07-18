package com.deptoeconomico.expedientes.model;

import java.time.LocalDate;


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Expediente {

    /**
     * El numero de tramite/ATER (ej: 1228-4899-2026) identifica siempre
     * al mismo expediente, por eso lo usamos como clave primaria en vez
     * de generar un id aparte.
     */
    @Id
    @NotBlank(message = "El numero de tramite es obligatorio")
    private String numeroTramite;

    /** Algunos expedientes ademas tienen un numero unico interno. Es opcional. */
    private String numeroUnico;

    @NotNull(message = "El tipo de tramite es obligatorio")
    @Enumerated(EnumType.STRING)
    private TipoExpediente tipo;

    private String caratula;

    private String iniciador;

    private String origen;
    
       
    @NotNull(message = "La fecha de ingreso es obligatoria")
    private LocalDate fechaIngreso;

    @NotNull(message = "La condicion es obligatoria")
    @Enumerated(EnumType.STRING)
    private CondicionExpediente condicion;
   
    @NotNull
    @Enumerated(EnumType.STRING)
    private EstadoExpediente estado = EstadoExpediente.EN_TRAMITE;
   
    /**
     * Quien tiene el expediente ahora mismo. Se va actualizando cada vez
     * que se reasigna; no guardamos el historial de reasignaciones internas.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Empleado empleadoAsignado;

    public Expediente() {
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

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public CondicionExpediente getCondicion() {
        return condicion;
    }

    public void setCondicion(CondicionExpediente condicion) {
        this.condicion = condicion;
    }

    public Empleado getEmpleadoAsignado() {
        return empleadoAsignado;
    }

    public void setEmpleadoAsignado(Empleado empleadoAsignado) {
        this.empleadoAsignado = empleadoAsignado;
    }
    public EstadoExpediente getEstado() {
        return estado;
    }

    public void setEstado(EstadoExpediente estado) {
        this.estado = estado;
    }
}

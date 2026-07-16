package com.deptoeconomico.expedientes.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

/**
 * Nota o providencia generada a partir de un expediente. El numero
 * es correlativo por expediente + tipo (la Nota 1 y Nota 2 de un
 * expediente son independientes de la Providencia 1 de ese mismo
 * expediente) — lo calcula NotaService al guardar.
 */
@Entity
public class Nota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TipoNota tipo;

    /** Solo se completa si tipo == OTRO. */
    private String tipoOtro;

    /** Correlativo dentro del mismo expediente + tipo. Lo calcula el service. */
    private Integer numero;

    @NotNull
    private LocalDate fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private Expediente expediente;

    @ManyToOne(fetch = FetchType.LAZY)
    private Empleado empleado;

    /** Cargo del destinatario (ej: "Jefe RT"). Se completa a mano o autocompletando desde el catálogo. */
    private String cargo;

    /** Área del destinatario (ej: "Dirección de Impuesto"). Opcional. */
    private String area;

    /** Nombre del destinatario (ej: "Dr. Fulano Pérez"). Opcional. */
    private String nombreDestinatario;

    /** El texto que escribe el empleado con el contenido de la nota. */
    @Column(length = 4000)
    private String cuerpo;

    @NotNull
    @Enumerated(EnumType.STRING)
    private EstadoNota estado = EstadoNota.PENDIENTE;

    @NotNull
    @Enumerated(EnumType.STRING)
    private EstadoDocumento estadoDocumento = EstadoDocumento.BORRADOR;

    public Nota() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoNota getTipo() {
        return tipo;
    }

    public void setTipo(TipoNota tipo) {
        this.tipo = tipo;
    }

    public String getTipoOtro() {
        return tipoOtro;
    }

    public void setTipoOtro(String tipoOtro) {
        this.tipoOtro = tipoOtro;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Expediente getExpediente() {
        return expediente;
    }

    public void setExpediente(Expediente expediente) {
        this.expediente = expediente;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getNombreDestinatario() {
        return nombreDestinatario;
    }

    public void setNombreDestinatario(String nombreDestinatario) {
        this.nombreDestinatario = nombreDestinatario;
    }

    public String getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(String cuerpo) {
        this.cuerpo = cuerpo;
    }

    public EstadoNota getEstado() {
        return estado;
    }

    public void setEstado(EstadoNota estado) {
        this.estado = estado;
    }

    /** Etiqueta final del tipo, resolviendo el caso "Otro". */
    public String getTipoTexto() {
        if (tipo == TipoNota.OTRO) {
            return tipoOtro;
        }
        return tipo != null ? tipo.getEtiqueta() : "";
    }

    /** Título tal como va a aparecer arriba del documento, ej: "NOTA N° 2/ATER". */
    public String getTituloDocumento() {
        return getTipoTexto().toUpperCase() + " N° " + (numero != null ? numero : "") + "/DC-ATER";
    }
    
    public EstadoDocumento getEstadoDocumento() {
        return estadoDocumento;
    }

    public void setEstadoDocumento(EstadoDocumento estadoDocumento) {
        this.estadoDocumento = estadoDocumento;
    }
}

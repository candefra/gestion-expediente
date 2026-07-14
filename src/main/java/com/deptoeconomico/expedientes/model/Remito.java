package com.deptoeconomico.expedientes.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Remito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El numero de remito es obligatorio")
    private String numeroRemito;

    @NotNull(message = "La fecha del remito es obligatoria")
    private LocalDate fechaRemito;

    /** De donde viene el remito, ej: "Mesa de Entradas Catastro" */
    private String remitente;

    /** A donde va dirigido, ej: "Dpto. Económico" */
    private String destino;

    @OneToMany(mappedBy = "remito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleRemito> detalles = new ArrayList<>();

    public Remito() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public List<DetalleRemito> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleRemito> detalles) {
        this.detalles = detalles;
    }
}

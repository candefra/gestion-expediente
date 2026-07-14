package com.deptoeconomico.expedientes.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

/**
 * Destinatarios que se repiten seguido (jefes de otras areas, RT, etc.)
 * para no tener que tipearlos cada vez. Los contribuyentes, que varian
 * en cada nota, no hace falta cargarlos aca: se completan a mano en el
 * formulario de la nota.
 */
@Entity
public class DestinatarioFrecuente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El cargo es obligatorio")
    private String cargo;

    private String area;

    private String nombre;

    public DestinatarioFrecuente() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}

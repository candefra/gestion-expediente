package com.deptoeconomico.expedientes.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deptoeconomico.expedientes.model.Empleado;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
}

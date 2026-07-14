package com.deptoeconomico.expedientes.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deptoeconomico.expedientes.model.Remito;

public interface RemitoRepository extends JpaRepository<Remito, Long> {
}

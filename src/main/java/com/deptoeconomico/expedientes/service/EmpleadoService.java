package com.deptoeconomico.expedientes.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.deptoeconomico.expedientes.model.Empleado;
import com.deptoeconomico.expedientes.repository.EmpleadoRepository;

@Service
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;

    public EmpleadoService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    public List<Empleado> listarTodos() {
        return empleadoRepository.findAll();
    }

    public Empleado guardar(Empleado empleado) {
        return empleadoRepository.save(empleado);
    }

    public Empleado buscarPorId(Long id) {
        return empleadoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe el empleado con id " + id));
    }
}

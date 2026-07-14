package com.deptoeconomico.expedientes.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.deptoeconomico.expedientes.model.DestinatarioFrecuente;
import com.deptoeconomico.expedientes.repository.DestinatarioFrecuenteRepository;

@Service
public class DestinatarioFrecuenteService {

    private final DestinatarioFrecuenteRepository repository;

    public DestinatarioFrecuenteService(DestinatarioFrecuenteRepository repository) {
        this.repository = repository;
    }

    public List<DestinatarioFrecuente> listarTodos() {
        return repository.findAll();
    }

    public DestinatarioFrecuente guardar(DestinatarioFrecuente destinatario) {
        return repository.save(destinatario);
    }
}

package com.binasjc.spring_server_binasjc.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.binasjc.spring_server_binasjc.model.ReservaBicicleta;
import com.binasjc.spring_server_binasjc.repositories.ReservaBicicletaRepository;

@Service
public class ReservaBicicletaService {

    @Autowired
    private ReservaBicicletaRepository repository;

    public ReservaBicicleta save(ReservaBicicleta reserva) {
        return repository.save(reserva);
    }

    public List<ReservaBicicleta> getAllReservasBicicletas() {
        return (List<ReservaBicicleta>) repository.findAll();
    }

    public void delete(ReservaBicicleta reserva) {
        repository.delete(reserva);
    }

    public List<ReservaBicicleta> findByIdUsuarioAndEstado(int idUsuario, int estado) {
        return (List<ReservaBicicleta>) repository.findByIdUsuarioAndEstado(idUsuario, estado);
    }

}

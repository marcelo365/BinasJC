package com.binasjc.spring_server_binasjc.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.binasjc.spring_server_binasjc.model.Bicicleta;
import com.binasjc.spring_server_binasjc.model.Estacao;
import com.binasjc.spring_server_binasjc.repositories.BicicletaRepository;

@Service
public class BicicletaService {

    @Autowired
    private BicicletaRepository repository;

    public Bicicleta save(Bicicleta bicicleta) {
        return repository.save(bicicleta);
    }

    public List<Bicicleta> getAllBicicletas() {
        return (List<Bicicleta>) repository.findAll();
    }

    public void delete(Bicicleta bicicleta) {
        repository.delete(bicicleta);
    }

    public Bicicleta findByID(int id) {
        return repository.findById(id).orElse(null); // Retorna null se não for encontrado
    }

    public List<Bicicleta> findByEstacao(Estacao estacao) {
        return repository.findByEstacao(estacao);
    }

    public List<Bicicleta> findByNome(String nome) {
        return repository.findByNome(nome);
    }

    public List<Bicicleta> getBicicletasDisponiveisEstacao(Estacao estacao) {
        return repository.findByDisponibilidadeAndEstacao(1, estacao);
    }

}

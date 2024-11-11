package com.binasjc.spring_server_binasjc.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.binasjc.spring_server_binasjc.model.Estacao;
import com.binasjc.spring_server_binasjc.repositories.EstacaoRepository;

@Service
public class EstacaoService {

    @Autowired
    private EstacaoRepository repository;

    public Estacao save(Estacao estacao) {
        return repository.save(estacao);
    }

    public List<Estacao> getAllEstacoes() {
        return (List<Estacao>) repository.findAll();
    }

    public void delete(Estacao estacao) {
        repository.delete(estacao);
    }

    public Estacao findByID(int id) {
        return repository.findById(id).orElse(null); // Retorna null se não for encontrado
    }

    public List<Estacao> findByNome(String nome) {
        return repository.findByNome(nome); // Retorna null se não for encontrado
    }

}

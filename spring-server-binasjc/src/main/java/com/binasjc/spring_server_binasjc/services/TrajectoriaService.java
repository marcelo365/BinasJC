package com.binasjc.spring_server_binasjc.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.binasjc.spring_server_binasjc.model.Trajectoria;
import com.binasjc.spring_server_binasjc.repositories.TrajectoriaRepository;

@Service
public class TrajectoriaService {

    @Autowired
    private TrajectoriaRepository repository;

    public Trajectoria save(Trajectoria trajectoria) {
        return repository.save(trajectoria);
    }

    public List<Trajectoria> findByIdUsuario(int idUsuario) {
        return repository.findByIdUsuario(idUsuario);
    }

}

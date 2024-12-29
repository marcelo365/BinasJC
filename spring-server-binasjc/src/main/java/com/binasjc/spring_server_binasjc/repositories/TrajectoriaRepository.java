package com.binasjc.spring_server_binasjc.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.binasjc.spring_server_binasjc.model.Trajectoria;

@Repository
public interface TrajectoriaRepository extends CrudRepository< Trajectoria , Integer> {
    List<Trajectoria> findByIdUsuario(int idUsuario);   
}

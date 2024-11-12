package com.binasjc.spring_server_binasjc.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class ReservaBicicleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // Identificador único para cada reserva

    @ManyToOne
    @JoinColumn(name = "bicicleta_id", nullable = false)
    private Bicicleta bicicleta; // Relacionamento com a entidade Bicicleta

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Utilizador usuario; // Relacionamento com a entidade Usuario/Utilizador

    private int estado; //0 - se a reserva foi passada ou seja faz parte do histórico , 1 - se a reserva é recente e o usuario pretende usar a bicicleta ou está a usar a bicicleta 

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bicicleta getBicicleta() {
        return bicicleta;
    }

    public void setBicicleta(Bicicleta bicicleta) {
        this.bicicleta = bicicleta;
    }

    public Utilizador getUsuario() {
        return usuario;
    }

    public void setUsuario(Utilizador usuario) {
        this.usuario = usuario;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }


}

package com.binasjc.spring_server_binasjc.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Utilizador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nomecompleto;
    private String username;
    private String senha;
    private float pontos;

    public Utilizador() {

    }

    public Utilizador(String nomecompleto, String username, String senha) {
        this.nomecompleto = nomecompleto;
        this.username = username;
        this.senha = senha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomecompleto() {
        return nomecompleto;
    }

    public void setNomecompleto(String nomecompleto) {
        this.nomecompleto = nomecompleto;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public float getPontos() {
        return pontos;
    }

    public void setPontos(float pontos) {
        this.pontos = pontos;
    }

    @Override
    public String toString() {
        return "Utilizador [id=" + id + ", nomecompleto=" + nomecompleto + ", username=" + username + ", senha=" + senha
                + "]";
    }

}

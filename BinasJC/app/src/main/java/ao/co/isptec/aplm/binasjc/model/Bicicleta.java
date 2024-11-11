package ao.co.isptec.aplm.binasjc.model;

import java.io.Serializable;

public class Bicicleta implements Serializable {

    private int id;
    private String nome;
    private int disponibilidade; // 0 - não disponivel , 1 disponivel
    private Estacao estacao;

    public Bicicleta(String nome, Estacao estacao) {
        this.nome = nome;
        this.estacao = estacao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getDisponibilidade() {
        return disponibilidade;
    }

    public void setDisponibilidade(int disponibilidade) {
        this.disponibilidade = disponibilidade;
    }

    public Estacao getEstacao() {
        return estacao;
    }

    public void setEstacao(Estacao estacao) {
        this.estacao = estacao;
    }

}

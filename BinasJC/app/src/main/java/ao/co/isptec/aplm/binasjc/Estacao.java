package ao.co.isptec.aplm.binasjc;

import java.io.Serializable;

public class Estacao implements Serializable {

    private String nome;
    private int bicicletasDisponiveis;
    private int localizacao;


    public Estacao(String nome, int bicicletasDisponiveis, int localizacao) {
        this.bicicletasDisponiveis = bicicletasDisponiveis;
        this.nome = nome;
        this.localizacao = localizacao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getBicicletasDisponiveis() {
        return bicicletasDisponiveis;
    }

    public void setBicicletasDisponiveis(int bicicletasDisponiveis) {
        this.bicicletasDisponiveis = bicicletasDisponiveis;
    }

    public int getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(int localizacao) {
        this.localizacao = localizacao;
    }
}

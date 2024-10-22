package ao.co.isptec.aplm.binasjc;

import java.io.Serializable;

public class Bicicleta implements Serializable {

    private String nome;
    private String estacaoPertencente;


    public Bicicleta(String nome , String estacao){
        this.nome = nome;
        this.estacaoPertencente = estacao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEstacaoPertencente() {
        return estacaoPertencente;
    }

    public void setEstacaoPertencente(String estacaoPertencente) {
        this.estacaoPertencente = estacaoPertencente;
    }
}

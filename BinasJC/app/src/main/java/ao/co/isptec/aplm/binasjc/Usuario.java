package ao.co.isptec.aplm.binasjc;

import java.io.Serializable;

public class Usuario implements Serializable {

    public String nome;

    public Usuario(String nome){
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}

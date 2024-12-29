package ao.co.isptec.aplm.binasjc.model;

import java.io.Serializable;

public class Utilizador implements Serializable {

    private int id;
    private String nomecompleto;
    private String username;
    private String senha;
    private double pontos;
    private int idEstacaoReservaBicicleta;

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

    public double getPontos() {
        return pontos;
    }

    public void setPontos(double pontos) {
        this.pontos = pontos;
    }

    public int getIdEstacaoReservaBicicleta() {
        return idEstacaoReservaBicicleta;
    }

    public void setIdEstacaoReservaBicicleta(int idEstacaoReservaBicicleta) {
        this.idEstacaoReservaBicicleta = idEstacaoReservaBicicleta;
    }

    @Override
    public String toString() {
        return "Utilizador [id=" + id + ", nomecompleto=" + nomecompleto + ", username=" + username + ", senha=" + senha
                + "]";
    }
}

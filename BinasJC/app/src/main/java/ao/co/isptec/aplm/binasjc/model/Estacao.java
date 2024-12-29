package ao.co.isptec.aplm.binasjc.model;

import java.io.Serializable;

public class Estacao implements Serializable {

    private int id;
    private String nome;
    private double latitude;
    private double longitude;
    private int bicicletasDisponiveis;

    public Estacao() {

    }

    public Estacao(String nome) {
        this.nome = nome;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getBicicletasDisponiveis() {
        return bicicletasDisponiveis;
    }

    public void setBicicletasDisponiveis(int bicicletasDisponiveis) {
        this.bicicletasDisponiveis = bicicletasDisponiveis;
    }

    @Override
    public String toString() {
        return "Estacao{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}

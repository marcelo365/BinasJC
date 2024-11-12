package ao.co.isptec.aplm.binasjc.model;

import java.io.Serializable;

public class ReservaBicicleta implements Serializable {


    private int id; // Identificador único para cada reserva
    private Bicicleta bicicleta; // Relacionamento com a entidade Bicicleta
    private Utilizador usuario; // Relacionamento com a entidade Usuario/Utilizador
    private int estado; //0 - se a reserva foi passada ou seja faz parte do histórico , 1 - se a reserva é recente e o usuario pretende usar a bicicleta ou está a usar a bicicleta

    public ReservaBicicleta (Bicicleta bicicleta , Utilizador usuario){
        this.bicicleta = bicicleta;
        this.usuario = usuario;
        estado = 1;
    }

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

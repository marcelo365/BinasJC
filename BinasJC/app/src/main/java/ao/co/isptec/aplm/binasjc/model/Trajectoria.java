package ao.co.isptec.aplm.binasjc.model;

import java.io.Serializable;

public class Trajectoria implements Serializable {

    private int id;
    private int idTrajectoria;
    private int idUsuario;
    private double latitude;
    private double longitude;

    public Trajectoria() {

    }

    public Trajectoria(int idTrajectoria, double latitude, double longitude, int idUsuario) {
        this.idTrajectoria = idTrajectoria;
        this.latitude = latitude;
        this.longitude = longitude;
        this.idUsuario = idUsuario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdTrajectoria() {
        return idTrajectoria;
    }

    public void setIdTrajectoria(int idTrajectoria) {
        this.idTrajectoria = idTrajectoria;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}

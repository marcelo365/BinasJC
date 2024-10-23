package ao.co.isptec.aplm.binasjc;

import java.io.Serializable;

public class Trajectoria implements Serializable {

    private String data;
    private float distanciaPercorrida;

    public Trajectoria(String data , float distancia){
        this.data = data;
        this.distanciaPercorrida = distancia;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public float getDistanciaPercorrida() {
        return distanciaPercorrida;
    }

    public void setDistanciaPercorrida(float distanciaPercorrida) {
        this.distanciaPercorrida = distanciaPercorrida;
    }
}

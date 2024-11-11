package ao.co.isptec.aplm.binasjc.model;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ao.co.isptec.aplm.binasjc.MainActivity;
import ao.co.isptec.aplm.binasjc.retrofit.BicicletaApi;
import ao.co.isptec.aplm.binasjc.retrofit.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Estacao implements Serializable {

    private int id;
    private String nome;

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

    public void getBicicletasDisponiveis(final BicicletaCallback callback) {

        RetrofitService retrofitService = new RetrofitService();
        BicicletaApi bicicletaApi = retrofitService.getRetrofit().create(BicicletaApi.class);

        bicicletaApi.getBicicletasDisponiveisEstacao(this).enqueue(new Callback<List<Bicicleta>>() {
            @Override
            public void onResponse(Call<List<Bicicleta>> call, Response<List<Bicicleta>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    int size = response.body().size();
                    callback.onBicicletasDisponiveis(size);
                }else{
                    Log.e("Erro", "Resposta não bem-sucedida");
                }
            }

            @Override
            public void onFailure(Call<List<Bicicleta>> call, Throwable throwable) {
                Log.d("Erro de rede ao pegar bicicletas disponveis de uma estação", "Falha na requisição: " + throwable.getMessage(), throwable);
            }
        });

    }

    @Override
    public String toString() {
        return "Estacao [id=" + id + ", nome=" + nome + "]";
    }


    public interface BicicletaCallback {
        void onBicicletasDisponiveis(int count);
    }

}

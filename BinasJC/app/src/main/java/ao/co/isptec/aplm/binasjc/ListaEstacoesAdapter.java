package ao.co.isptec.aplm.binasjc;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ao.co.isptec.aplm.binasjc.model.Bicicleta;
import ao.co.isptec.aplm.binasjc.model.Estacao;
import ao.co.isptec.aplm.binasjc.retrofit.BicicletaApi;
import ao.co.isptec.aplm.binasjc.retrofit.EstacaoApi;
import ao.co.isptec.aplm.binasjc.retrofit.RetrofitService;
import ao.co.isptec.aplm.binasjc.retrofit.UtilizadorApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListaEstacoesAdapter extends ArrayAdapter<Estacao> {

    private RetrofitService retrofitService;
    private BicicletaApi bicicletaApi;


    public ListaEstacoesAdapter(@NonNull Context context, ArrayList<Estacao> listaEstacoes) {
        super(context, R.layout.item_lista_estacoes, listaEstacoes);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Estacao estacao = getItem(position);
        retrofitService = new RetrofitService();
        bicicletaApi = retrofitService.getRetrofit().create(BicicletaApi.class);

        Log.e("Estacao", estacao.toString());

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_lista_estacoes, parent, false);
        }

        TextView nomeEstacao = convertView.findViewById(R.id.nomeCiclista);
        TextView bicicletasDisponiveis = convertView.findViewById(R.id.estacaoBicicleta);
        TextView distancia = convertView.findViewById(R.id.distancia);

        nomeEstacao.setText(estacao.getNome().toString());

        bicicletaApi.getBicicletasDisponiveisEstacao(estacao.getId()).enqueue(new Callback<List<Bicicleta>>() {
            @Override
            public void onResponse(Call<List<Bicicleta>> call, Response<List<Bicicleta>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    int size = response.body().size();
                    bicicletasDisponiveis.setText(String.valueOf(size));
                } else {
                    Log.e("Erro", "Resposta não bem-sucedida, código: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Bicicleta>> call, Throwable throwable) {
                Log.d("Erro de rede ao pegar bicicletas disponveis de uma estação", "Falha na requisição: " + throwable.getMessage(), throwable);
            }
        });

        distancia.setText("677 km");

        return convertView;
    }

}

package ao.co.isptec.aplm.binasjc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import ao.co.isptec.aplm.binasjc.model.Estacao;

public class ListaEstacoesAdapter extends ArrayAdapter<Estacao> {


    public ListaEstacoesAdapter(@NonNull Context context, ArrayList<Estacao> listaEstacoes) {
        super(context, R.layout.item_lista_estacoes, listaEstacoes);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Estacao estacao = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_lista_estacoes, parent, false);
        }

        TextView nomeEstacao = convertView.findViewById(R.id.nomeCiclista);
        TextView bicicletasDisponiveis = convertView.findViewById(R.id.estacaoBicicleta);
        TextView distancia = convertView.findViewById(R.id.distancia);

        nomeEstacao.setText(estacao.getNome().toString());

        estacao.getBicicletasDisponiveis(new Estacao.BicicletaCallback() {
            @Override
            public void onBicicletasDisponiveis(int count) {
                bicicletasDisponiveis.setText(String.valueOf(count));
            }
        });

        distancia.setText("677 km");

        return convertView;
    }

}

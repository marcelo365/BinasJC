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
        bicicletasDisponiveis.setText( String.valueOf(estacao.getBicicletasDisponiveis())  );
        distancia.setText( String.valueOf(estacao.getLocalizacao()) );

        return convertView;
    }
}

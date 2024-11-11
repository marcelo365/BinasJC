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

import ao.co.isptec.aplm.binasjc.model.Bicicleta;

public class ListaBicicletasAdapter extends ArrayAdapter<Bicicleta> {

    public ListaBicicletasAdapter(@NonNull Context context, ArrayList<Bicicleta> listaBicicletas) {
        super(context, R.layout.item_lista_bicicletas, listaBicicletas);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Bicicleta bicicleta = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_lista_bicicletas, parent, false);
        }

        TextView nomeBicicleta = convertView.findViewById(R.id.nomeCiclista);
        TextView nomeEstacaoPertencente = convertView.findViewById(R.id.estacaoBicicleta);


        nomeBicicleta.setText(bicicleta.getNome().toString());
        nomeEstacaoPertencente.setText( bicicleta.getEstacao().getNome()  );

        return convertView;
    }
}

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

import ao.co.isptec.aplm.binasjc.model.Trajectoria;

public class ListaTrajectoriasAdapter extends ArrayAdapter<String> {
    public ListaTrajectoriasAdapter(@NonNull Context context, ArrayList<String> listaTrajectoriasDistancias) {
        super(context, R.layout.item_historico_trajectorias, listaTrajectoriasDistancias);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String trajectoriaDistancia = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_historico_trajectorias, parent, false);
        }

        TextView numeroTrajecto = convertView.findViewById(R.id.numeroTrajecto);
        TextView distanciaPercorrida = convertView.findViewById(R.id.distanciaPercorrida);


        numeroTrajecto.setText("Trajecto " + (position+1) );
        distanciaPercorrida.setText(trajectoriaDistancia + " m");

        return convertView;
    }
}

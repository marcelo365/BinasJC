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

public class ListaTrajectoriasAdapter extends ArrayAdapter<Trajectoria> {
    public ListaTrajectoriasAdapter(@NonNull Context context, ArrayList<Trajectoria> listaTrajectorias) {
        super(context, R.layout.item_historico_trajectorias, listaTrajectorias);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Trajectoria trajectoria = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_historico_trajectorias, parent, false);
        }

        TextView dataTrajectoria = convertView.findViewById(R.id.dataTrajectoria);
        TextView distanciaPercorrida = convertView.findViewById(R.id.distanciaPercorrida);


        dataTrajectoria.setText(trajectoria.getData().toString());
        distanciaPercorrida.setText(String.valueOf(trajectoria.getDistanciaPercorrida()));

        return convertView;
    }
}

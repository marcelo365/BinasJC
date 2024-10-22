package ao.co.isptec.aplm.binasjc;

import android.content.Context;
import android.os.CpuUsageInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ListaCiclistasAdapter extends ArrayAdapter<Usuario> {

    public ListaCiclistasAdapter(@NonNull Context context, ArrayList<Usuario> listaCiclistas) {
        super(context, R.layout.item_lista_ciclistas, listaCiclistas);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Usuario ciclista = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_lista_ciclistas, parent, false);
        }

        TextView nomeCiclista = convertView.findViewById(R.id.nomeCiclista);


        nomeCiclista.setText(ciclista.getNome().toString());

        return convertView;
    }
}
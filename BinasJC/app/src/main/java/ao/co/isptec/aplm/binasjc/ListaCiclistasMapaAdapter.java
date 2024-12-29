package ao.co.isptec.aplm.binasjc;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import ao.co.isptec.aplm.binasjc.model.Utilizador;
import ao.co.isptec.aplm.binasjc.retrofit.RetrofitService;
import ao.co.isptec.aplm.binasjc.retrofit.UtilizadorApi;
import ao.co.isptec.aplm.binasjc.shared.SharedPreferencesUtil;
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListaCiclistasMapaAdapter extends ArrayAdapter<Utilizador> {

    private Utilizador utilizadorActual;
    private RetrofitService retrofitService;
    private UtilizadorApi utilizadorApi;

    public ListaCiclistasMapaAdapter(@NonNull Context context, ArrayList<Utilizador> listaCiclistas) {
        super(context, R.layout.item_lista_ciclistas_mapa, listaCiclistas);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Utilizador ciclista = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_lista_ciclistas_mapa, parent, false);
        }

        TextView nomeCiclista = convertView.findViewById(R.id.nomeCiclista);

        nomeCiclista.setText(ciclista.getUsername().toString());

        return convertView;
    }



}

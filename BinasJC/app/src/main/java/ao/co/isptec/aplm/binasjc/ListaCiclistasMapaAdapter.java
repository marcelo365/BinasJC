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
        utilizadorActual = SharedPreferencesUtil.getUtilizador(context);
        retrofitService = new RetrofitService();
        utilizadorApi = retrofitService.getRetrofit().create(UtilizadorApi.class);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Utilizador ciclista = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_lista_ciclistas_mapa, parent, false);
        }

        TextView nomeCiclista = convertView.findViewById(R.id.nomeCiclista);
        ImageView enviarPontosBtn = convertView.findViewById(R.id.enviarPontosBtn);

        enviarPontosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarPontos(v, ciclista);
            }
        });

        nomeCiclista.setText(ciclista.getUsername().toString());

        return convertView;
    }


    public void enviarPontos(View view, Utilizador usuarioVisualizar) {
        EditText input = new EditText(getContext());
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL); // Permite números e ponto/virgula

        // Criação do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Envio de Pontos"); // Título da caixa de diálogo
        builder.setMessage("Digite a quantidade de pontos que pretende enviar");
        builder.setView(input); // Define o EditText como o conteúdo da caixa de diálogo

        // Adiciona o botão "OK"
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Pega o texto inserido no EditText e exibe com um Toast
                String inputText = input.getText().toString();

                if (!inputText.isEmpty()) {

                    if (utilizadorActual.getPontos() < Float.valueOf(inputText)) {
                        Toast.makeText(getContext(), "Não foi possível enviar a quantidade de pontos desejada , pontos insuficientes", Toast.LENGTH_LONG).show();
                    } else if (Float.valueOf(inputText) < 0) {
                        Toast.makeText(getContext(), "Não foi possível enviar a quantidade de pontos desejada , não existe quantidade negativa", Toast.LENGTH_LONG).show();
                    } else {

                        utilizadorActual.setPontos(utilizadorActual.getPontos() - Float.valueOf(inputText));
                        SharedPreferencesUtil.saveUtilizador(getContext(), utilizadorActual);
                        usuarioVisualizar.setPontos(usuarioVisualizar.getPontos() + Float.valueOf(inputText));

                        utilizadorApi.save(utilizadorActual).enqueue(new Callback<Utilizador>() {
                            @Override
                            public void onResponse(Call<Utilizador> call, Response<Utilizador> response) {

                                if (response.isSuccessful() && response.body() != null) {
                                    Toast.makeText(getContext(), "Pontos enviados com sucesso", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Erro ao atualizar pontos usuario de origem " + response.code(), Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onFailure(Call<Utilizador> call, Throwable throwable) {
                                Toast.makeText(getContext(), "Erro de rede ao atualizar pontos usuario de origem", Toast.LENGTH_SHORT).show();
                            }
                        });

                        utilizadorApi.save(usuarioVisualizar).enqueue(new Callback<Utilizador>() {
                            @Override
                            public void onResponse(Call<Utilizador> call, Response<Utilizador> response) {

                                if (response.isSuccessful() && response.body() != null) {
                                    // Toast.makeText(VisualizarCiclista.this, "Pontos enviados com sucesso" + response.code(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Erro ao atualizar pontos usuario de origem " + response.code(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Utilizador> call, Throwable throwable) {
                                Toast.makeText(getContext(), "Erro de rede ao atualizar pontos usuario de origem", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }

            }
        });

        // Adiciona o botão "Cancelar"
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Fecha o diálogo se "Cancelar" for clicado
            }
        });

        // Exibe a caixa de diálogo
        builder.show();
    }

}

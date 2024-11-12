package ao.co.isptec.aplm.binasjc.shared;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ao.co.isptec.aplm.binasjc.VisualizarBicicleta;
import ao.co.isptec.aplm.binasjc.model.Bicicleta;
import ao.co.isptec.aplm.binasjc.model.ReservaBicicleta;
import ao.co.isptec.aplm.binasjc.model.Utilizador;
import ao.co.isptec.aplm.binasjc.retrofit.ReservaBicicletaApi;
import ao.co.isptec.aplm.binasjc.retrofit.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SharedPreferencesUtil {

    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String KEY_UTILIZADOR = "usuario";
    private static final String KEY_BICICLETA_RESERVADA = "bicicleta_reservada";

    private static final RetrofitService retrofitService = new RetrofitService();
    private static final ReservaBicicletaApi reservaBicicletaApi = retrofitService.getRetrofit().create(ReservaBicicletaApi.class);

    // Função para salvar o objeto Usuario
    public static void saveUtilizador(Context context, Utilizador utilizador) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Converter o objeto Utilizador para uma String JSON
        Gson gson = new Gson();
        String utilizadorJson = gson.toJson(utilizador);

        // Salvar a String JSON no SharedPreferences
        editor.putString(KEY_UTILIZADOR, utilizadorJson);
        editor.apply();
    }

    // Função para recuperar o objeto Utilizador
    public static Utilizador getUtilizador(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Recuperar a String JSON do SharedPreferences
        String utilizadorJson = sharedPreferences.getString(KEY_UTILIZADOR, null);

        if (utilizadorJson != null) {
            // Converter a String JSON de volta para o objeto Utilizador
            Gson gson = new Gson();
            return gson.fromJson(utilizadorJson, Utilizador.class);
        }

        return null; // Retorna null caso não encontre o usuário
    }

    public static void verificarBicicletaReservada(Context context, final ReservaBicicletaCallback callback) {

        reservaBicicletaApi.getReservaBicicletaByUsuarioAndEstado(getUtilizador(context).getId(), 1).enqueue(new Callback<List<ReservaBicicleta>>() {
            @Override
            public void onResponse(Call<List<ReservaBicicleta>> call, Response<List<ReservaBicicleta>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    if (!response.body().isEmpty()) {
                        callback.onReturn(response.body().get(0));
                    } else {
                        callback.onReturn(null);
                    }

                } else {
                    Toast.makeText(context, "Erro ao verificar bicicleta reservada" + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ReservaBicicleta>> call, Throwable throwable) {
                Toast.makeText(context, "Erro de rede ao verificar bicicleta reservada", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // Definição do callback
    public interface ReservaBicicletaCallback {
        public void onReturn(ReservaBicicleta reservaBicicleta);
    }

}

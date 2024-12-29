package ao.co.isptec.aplm.binasjc.shared;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.logging.Level;
import java.util.logging.Logger;

import ao.co.isptec.aplm.binasjc.TelaLogin;
import ao.co.isptec.aplm.binasjc.TelaMapa;
import ao.co.isptec.aplm.binasjc.model.Utilizador;
import ao.co.isptec.aplm.binasjc.retrofit.RetrofitService;
import ao.co.isptec.aplm.binasjc.retrofit.UtilizadorApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SharedPreferencesUtil {

    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String KEY_UTILIZADOR = "usuario";
    private static int idUsuario = -1;


    // Função para salvar o objeto Usuario
    public static void saveUtilizador(Context context, Utilizador utilizador) {
        if (utilizador != null) {
            idUsuario = utilizador.getId();
        }

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
    public static void getUtilizador(Context context, UtilizadorCallback callback) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Recuperar a String JSON do SharedPreferences
        String utilizadorJson = sharedPreferences.getString(KEY_UTILIZADOR, null);

        if (utilizadorJson != null) {
            // Converter a String JSON de volta para o objeto Utilizador
            Gson gson = new Gson();

            //

            RetrofitService retrofitService = new RetrofitService();
            ;
            UtilizadorApi utilizadorApi = retrofitService.getRetrofit().create(UtilizadorApi.class);

            utilizadorApi.getUtilizadorByUsername(gson.fromJson(utilizadorJson, Utilizador.class).getUsername()).enqueue(new Callback<Utilizador>() {
                @Override
                public void onResponse(Call<Utilizador> call, Response<Utilizador> response) {

                    if (response.isSuccessful() && (response.body() != null)) {
                        callback.onSuccess(response.body());
                    }
                }

                @Override
                public void onFailure(Call<Utilizador> call, Throwable throwable) {
                    Toast.makeText(context.getApplicationContext(), "Erro de rede verificar usuario", Toast.LENGTH_SHORT).show();
                }
            });

            //

        }

    }

    public interface UtilizadorCallback {
        void onSuccess(Utilizador utilizador);
    }

    public static int getIdUsuario() {
        return idUsuario;
    }
}

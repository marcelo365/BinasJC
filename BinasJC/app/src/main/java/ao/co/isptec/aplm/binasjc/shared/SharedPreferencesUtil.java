package ao.co.isptec.aplm.binasjc.shared;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import ao.co.isptec.aplm.binasjc.model.Utilizador;

public class SharedPreferencesUtil {

    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String KEY_UTILIZADOR = "usuario";

    // Função para salvar o objeto Usuario
    public static void saveUtilizador(Context context, Utilizador utilizador) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Converter o objeto Utilizador para uma String JSON
        Gson gson = new Gson();
        String utilizadorJson = gson.toJson(utilizador);

        // Salvar a String JSON no SharedPreferences
        editor.putString(KEY_UTILIZADOR , utilizadorJson);
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

}

package ao.co.isptec.aplm.binasjc;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.logging.Level;
import java.util.logging.Logger;

import ao.co.isptec.aplm.binasjc.model.Utilizador;
import ao.co.isptec.aplm.binasjc.retrofit.RetrofitService;
import ao.co.isptec.aplm.binasjc.retrofit.UtilizadorApi;
import ao.co.isptec.aplm.binasjc.shared.SharedPreferencesUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TelaLogin extends AppCompatActivity {

    private EditText senha;
    private EditText userName;
    private RetrofitService retrofitService;
    private UtilizadorApi utilizadorApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        senha = findViewById(R.id.senhaLogin);
        userName = findViewById(R.id.userNameLogin);
        retrofitService = new RetrofitService();
        utilizadorApi = retrofitService.getRetrofit().create(UtilizadorApi.class);

        TextView linkRegistrar = findViewById(R.id.linkRegistar);
        linkRegistrar.setText(Html.fromHtml("<u>Registe-se</u>"));

    }

    public void irRegistrar(View view) {
        Intent intent = new Intent(this, TelaRegisto.class);
        startActivity(intent);
    }


    public void irUsuario(View view) {
        Intent intent = new Intent(this, TelaInicialCiclista.class);
        startActivity(intent);
    }

    public void fazerLogin(View view) {
        String userNameLogin = userName.getText().toString();
        String senhaLogin = senha.getText().toString();

        if (userNameLogin.isEmpty()) {
            Toast.makeText(this, "Campo UserName vazio", Toast.LENGTH_SHORT).show();
            return;
        }

        if (senhaLogin.isEmpty()) {
            Toast.makeText(this, "Campo senha vazio", Toast.LENGTH_SHORT).show();
            return;
        }

        utilizadorApi.getUtilizadorByUsernameAndSenha(userNameLogin, senhaLogin).enqueue(new Callback<Utilizador>() {
            @Override
            public void onResponse(Call<Utilizador> call, Response<Utilizador> response) {


                if (response.isSuccessful() && (response.body() != null)) {
                    SharedPreferencesUtil.saveUtilizador(getApplicationContext() , response.body());
                    Intent intent = new Intent(TelaLogin.this, TelaInicialCiclista.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(TelaLogin.this, "Usuário não encontrado , porfavor verifique a senha ou username", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Utilizador> call, Throwable throwable) {
                Toast.makeText(TelaLogin.this, "Erro de rede verificar usuario", Toast.LENGTH_SHORT).show();
                Logger.getLogger(TelaLogin.class.getName()).log(Level.SEVERE, "Error ocurred", throwable);
            }
        });
    }

}
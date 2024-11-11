package ao.co.isptec.aplm.binasjc;

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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TelaRegisto extends AppCompatActivity {

    private EditText nomeCompleto;
    private EditText userName;
    private EditText senha;
    private EditText confirmarSenha;

    private RetrofitService retrofitService;
    private UtilizadorApi utilizadorApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_registo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nomeCompleto = findViewById(R.id.nomeCompletoRegistar);
        userName = findViewById(R.id.userNameRegistar);
        senha = findViewById(R.id.senhaRegistar);
        confirmarSenha = findViewById(R.id.confirmarSenhaRegistar);
        retrofitService = new RetrofitService();
        utilizadorApi = retrofitService.getRetrofit().create(UtilizadorApi.class);

        TextView linkLogin = findViewById(R.id.linkLogin);
        linkLogin.setText(Html.fromHtml("<u>Login</u>"));

    }

    public void irLogin(View view) {
        finish();
    }

    public void registrarUtilizador(View view) {

        String nome = nomeCompleto.getText().toString();
        String username = userName.getText().toString();
        String senhaUsuario = senha.getText().toString();
        String confirmarSenhaUsuario = confirmarSenha.getText().toString();

        if (nome.isEmpty()) {
            Toast.makeText(this, "Campo Nome Completo vazio", Toast.LENGTH_SHORT).show();
            return;
        }

        if (username.isEmpty()) {
            Toast.makeText(this, "Campo UserName vazio", Toast.LENGTH_SHORT).show();
            return;
        }

        if (senhaUsuario.isEmpty()) {
            Toast.makeText(this, "Campo Senha vazio", Toast.LENGTH_SHORT).show();
            return;
        }

        if (confirmarSenhaUsuario.isEmpty()) {
            Toast.makeText(this, "Senha não confirmada", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!senhaUsuario.equals(confirmarSenhaUsuario)) {
            Toast.makeText(this, "Senha confirmada incorrecta", Toast.LENGTH_SHORT).show();
            return;
        }

        verificarUsuarioExistente(username, new VerificacaoUsuarioCallback() {
            @Override
            public void onResultado(boolean usuarioExistente) {

                if (usuarioExistente) {
                    Toast.makeText(TelaRegisto.this, "Username já existe , digite outro", Toast.LENGTH_SHORT).show();
                } else {

                    //regista o usuário
                    utilizadorApi.save(new Utilizador(nome, username, senhaUsuario)).enqueue(new Callback<Utilizador>() {

                        @Override
                        public void onResponse(Call<Utilizador> call, Response<Utilizador> response) {

                            if (response.isSuccessful()) {
                                Utilizador utilizador = response.body();
                                nomeCompleto.setText("");
                                userName.setText("");
                                senha.setText("");
                                confirmarSenha.setText("");
                                Toast.makeText(TelaRegisto.this, "Usuário salvo com sucesso ", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(TelaRegisto.this, "Erro ao Salvar Usuário", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<Utilizador> call, Throwable throwable) {
                            Toast.makeText(TelaRegisto.this, "Erro de rede", Toast.LENGTH_SHORT).show();
                            Logger.getLogger(TelaRegisto.class.getName()).log(Level.SEVERE, "Error ocurred", throwable);
                        }
                    });

                }
            }
        });

    }

    private void verificarUsuarioExistente(String username, VerificacaoUsuarioCallback callback) {

        utilizadorApi.getUtilizadorByUsername(username).enqueue(new Callback<Utilizador>() {
            @Override
            public void onResponse(Call<Utilizador> call, Response<Utilizador> response) {

                boolean usuarioExistente = false;

                if (response.isSuccessful() && (response.body() != null)) {
                    usuarioExistente = true;
                }

                callback.onResultado(usuarioExistente);

            }

            @Override
            public void onFailure(Call<Utilizador> call, Throwable throwable) {
                Toast.makeText(TelaRegisto.this, "Erro de rede verificar usuario", Toast.LENGTH_SHORT).show();
                Logger.getLogger(TelaRegisto.class.getName()).log(Level.SEVERE, "Error ocurred", throwable);
                callback.onResultado(false);
            }
        });
    }

    public interface VerificacaoUsuarioCallback {
        void onResultado(boolean usuarioExistente);
    }


}
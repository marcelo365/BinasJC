package ao.co.isptec.aplm.binasjc;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
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

public class VisualizarCiclista extends AppCompatActivity {

    private TextView nomeUsuario;
    private Utilizador utilizadorActual;
    private TextView nomeCiclista;
    private Utilizador usuarioVisualizar;
    private RetrofitService retrofitService;
    private UtilizadorApi utilizadorApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_visualizar_ciclista);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        retrofitService = new RetrofitService();
        utilizadorApi = retrofitService.getRetrofit().create(UtilizadorApi.class);
        usuarioVisualizar = (Utilizador) getIntent().getSerializableExtra("ciclista");
        nomeUsuario = findViewById(R.id.nomeUsuarioVisualizarCiclista);
        nomeCiclista = findViewById(R.id.nomeCiclista);
        nomeCiclista.setText(usuarioVisualizar.getNomecompleto());

        utilizadorActual = SharedPreferencesUtil.getUtilizador(getApplicationContext());
        if (utilizadorActual != null) {
            nomeUsuario.setText(utilizadorActual.getUsername());
        }

    }

    public void voltar(View view) {
        finish();
    }

    public void enviarPontos(View view) {
        EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL); // Permite números e ponto/virgula

        // Criação do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                        Toast.makeText(VisualizarCiclista.this, "Não foi possível enviar a quantidade de pontos desejada , pontos insuficientes", Toast.LENGTH_LONG).show();
                    }else if( Float.valueOf(inputText) < 0){
                        Toast.makeText(VisualizarCiclista.this, "Não foi possível enviar a quantidade de pontos desejada , não existe quantidade negativa", Toast.LENGTH_LONG).show();
                    }else {

                        utilizadorActual.setPontos(utilizadorActual.getPontos() - Float.valueOf(inputText));
                        SharedPreferencesUtil.saveUtilizador(getApplicationContext(), utilizadorActual);
                        usuarioVisualizar.setPontos(usuarioVisualizar.getPontos() + Float.valueOf(inputText));

                        utilizadorApi.save(utilizadorActual).enqueue(new Callback<Utilizador>() {
                            @Override
                            public void onResponse(Call<Utilizador> call, Response<Utilizador> response) {

                                if (response.isSuccessful() && response.body() != null) {
                                    Toast.makeText(VisualizarCiclista.this, "Pontos enviados com sucesso", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(VisualizarCiclista.this, "Erro ao atualizar pontos usuario de origem " + response.code(), Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onFailure(Call<Utilizador> call, Throwable throwable) {
                                Toast.makeText(VisualizarCiclista.this, "Erro de rede ao atualizar pontos usuario de origem", Toast.LENGTH_SHORT).show();
                                Logger.getLogger(VisualizarCiclista.class.getName()).log(Level.SEVERE, "Error ocurred", throwable);
                            }
                        });

                        utilizadorApi.save(usuarioVisualizar).enqueue(new Callback<Utilizador>() {
                            @Override
                            public void onResponse(Call<Utilizador> call, Response<Utilizador> response) {

                                if (response.isSuccessful() && response.body() != null) {
                                    // Toast.makeText(VisualizarCiclista.this, "Pontos enviados com sucesso" + response.code(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(VisualizarCiclista.this, "Erro ao atualizar pontos usuario de origem " + response.code(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Utilizador> call, Throwable throwable) {
                                Toast.makeText(VisualizarCiclista.this, "Erro de rede ao atualizar pontos usuario de origem", Toast.LENGTH_SHORT).show();
                                Logger.getLogger(VisualizarCiclista.class.getName()).log(Level.SEVERE, "Error ocurred", throwable);
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
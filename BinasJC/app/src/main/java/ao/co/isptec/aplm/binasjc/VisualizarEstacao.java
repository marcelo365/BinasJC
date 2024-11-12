package ao.co.isptec.aplm.binasjc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ao.co.isptec.aplm.binasjc.model.Bicicleta;
import ao.co.isptec.aplm.binasjc.model.Estacao;
import ao.co.isptec.aplm.binasjc.model.Utilizador;
import ao.co.isptec.aplm.binasjc.retrofit.BicicletaApi;
import ao.co.isptec.aplm.binasjc.retrofit.RetrofitService;
import ao.co.isptec.aplm.binasjc.retrofit.UtilizadorApi;
import ao.co.isptec.aplm.binasjc.shared.SharedPreferencesUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisualizarEstacao extends AppCompatActivity {


    private ArrayList<Bicicleta> listaBicicletas;
    private ListView listaObjectos;
    private TextView nomeEstacao;
    private Estacao estacaoVisualizar;
    private RetrofitService retrofitService;
    private BicicletaApi bicicletaApi;
    private TextView nomeUsuario;
    private Utilizador utilizadorActual;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_visualizar_estacao);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        estacaoVisualizar = (Estacao) getIntent().getSerializableExtra("estacao");
        nomeUsuario = findViewById(R.id.nomeUsuarioVisualizarEstacao);
        listaObjectos = findViewById(R.id.listaTrajectos);
        nomeEstacao = findViewById(R.id.nomeEstacaoVisualizarEstacao);
        nomeEstacao.setText(estacaoVisualizar.getNome().toString());
        retrofitService = new RetrofitService();
        bicicletaApi = retrofitService.getRetrofit().create(BicicletaApi.class);

        utilizadorActual = SharedPreferencesUtil.getUtilizador(getApplicationContext());
        if (utilizadorActual != null) {
            nomeUsuario.setText(utilizadorActual.getUsername());
        }

        apresentarBicicletasEstacao();

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        apresentarBicicletasEstacao();
                    }
                }
        );

    }

    public void voltar(View view) {
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent); // Define o resultado como OK
        finish();
    }

    public void apresentarBicicletasEstacao() {
        bicicletaApi.getBicicletaByEstacao(estacaoVisualizar.getId()).enqueue(new Callback<List<Bicicleta>>() {
            @Override
            public void onResponse(Call<List<Bicicleta>> call, Response<List<Bicicleta>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    listaBicicletas = new ArrayList<>(response.body());
                    ListaBicicletasAdapter adapter = new ListaBicicletasAdapter(VisualizarEstacao.this, listaBicicletas);
                    listaObjectos.setAdapter(adapter);


                    listaObjectos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(VisualizarEstacao.this, VisualizarBicicleta.class);
                            intent.putExtra("bicicleta", listaBicicletas.get(position));
                            activityResultLauncher.launch(intent);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Bicicleta>> call, Throwable throwable) {
                Toast.makeText(VisualizarEstacao.this, "Erro de rede listar bicicletas visualizar estacao", Toast.LENGTH_SHORT).show();
                Logger.getLogger(VisualizarEstacao.class.getName()).log(Level.SEVERE, "Error ocurred", throwable);
            }
        });
    }

}
package ao.co.isptec.aplm.binasjc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
import ao.co.isptec.aplm.binasjc.retrofit.RetrofitService;
import ao.co.isptec.aplm.binasjc.retrofit.UtilizadorApi;
import ao.co.isptec.aplm.binasjc.shared.SharedPreferencesUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisualizarCiclistasMapa extends AppCompatActivity {

    private ListView listaObjectosCiclistas;
    private ArrayList<Utilizador> listaCiclistas;

    private RetrofitService retrofitService;
    private UtilizadorApi utilizadorApi;

    private TextView nomeUsuario;
    private Utilizador utilizadorActual;
    private EditText searchInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_visualizar_ciclistas_mapa);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        retrofitService = new RetrofitService();
        utilizadorApi = retrofitService.getRetrofit().create(UtilizadorApi.class);
        listaObjectosCiclistas = findViewById(R.id.listaObjectosCiclistas);
        searchInput = findViewById(R.id.searchInputVisualizarCiclistaMapa);
        nomeUsuario = findViewById(R.id.nomeUsuarioVisualizarCiclistaMapa);

        utilizadorActual = SharedPreferencesUtil.getUtilizador(getApplicationContext());
        if (utilizadorActual != null) {
            nomeUsuario.setText(utilizadorActual.getUsername());
        }


        utilizadorApi.getAllUtilizadores().enqueue(new Callback<List<Utilizador>>() {
            @Override
            public void onResponse(Call<List<Utilizador>> call, Response<List<Utilizador>> response) {


                if (response.isSuccessful() && response.body() != null) {

                    listaCiclistas = new ArrayList<>(response.body());
                    ListaCiclistasMapaAdapter adapter = new ListaCiclistasMapaAdapter(VisualizarCiclistasMapa.this, listaCiclistas);
                    listaObjectosCiclistas.setAdapter(adapter);
                }

            }

            @Override
            public void onFailure(Call<List<Utilizador>> call, Throwable throwable) {
                Toast.makeText(VisualizarCiclistasMapa.this, "Erro de rede listar Ciclistas Visualizar mapa", Toast.LENGTH_SHORT).show();
                Logger.getLogger(VisualizarCiclistasMapa.class.getName()).log(Level.SEVERE, "Error ocurred", throwable);
            }
        });

    }

    public void irVisualizarMapa(View view) {
        finish();
    }

    public void irTelaMensagens(View view) {
        Intent intent = new Intent(this, TelaMensagens.class);
        startActivity(intent);
    }

    public void efectuarPesquisa(View view) {

        String conteudo = searchInput.getText().toString();

        if (!conteudo.isEmpty()) {


            utilizadorApi.getUtilizadorByUsername(conteudo).enqueue(new Callback<Utilizador>() {
                @Override
                public void onResponse(Call<Utilizador> call, Response<Utilizador> response) {

                    if (response.isSuccessful() && response.body() != null) {
                        listaCiclistas = new ArrayList<>();
                        listaCiclistas.add(response.body());
                        ListaCiclistasMapaAdapter adapter = new ListaCiclistasMapaAdapter(VisualizarCiclistasMapa.this, listaCiclistas);
                        listaObjectosCiclistas.setAdapter(adapter);

                        listaObjectosCiclistas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //Intent intent = new Intent(VisualizarCiclistasMapa.this, VisualizarCiclista.class);
                                //intent.putExtra("ciclista", listaCiclistas.get(position));
                                //startActivity(intent);
                            }
                        });

                    } else {
                        Toast.makeText(VisualizarCiclistasMapa.this, "Nenhum ciclista encontrado com esse username", Toast.LENGTH_SHORT).show();
                    }


                }

                @Override
                public void onFailure(Call<Utilizador> call, Throwable throwable) {
                    Toast.makeText(VisualizarCiclistasMapa.this, "Erro de rede ao pesquisar ciclista", Toast.LENGTH_SHORT).show();
                    Logger.getLogger(VisualizarCiclistasMapa.class.getName()).log(Level.SEVERE, "Error ocurred", throwable);
                }
            });

            searchInput.setText("");

        } else {
            Toast.makeText(this, "Porfavor insira o username do ciclista que deseja", Toast.LENGTH_SHORT).show();
        }
    }


}
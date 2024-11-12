package ao.co.isptec.aplm.binasjc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
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
import ao.co.isptec.aplm.binasjc.retrofit.EstacaoApi;
import ao.co.isptec.aplm.binasjc.retrofit.RetrofitService;
import ao.co.isptec.aplm.binasjc.retrofit.UtilizadorApi;
import ao.co.isptec.aplm.binasjc.shared.SharedPreferencesUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TelaInicialCiclista extends AppCompatActivity {

    private AppCompatButton btnEstacoes;
    private AppCompatButton btnCiclistas;
    private AppCompatButton btnBicicletas;
    private EditText searchInput;
    private TextView nomeUsuario;
    private Utilizador utilizadorActual;


    private ArrayList<Estacao> listaEstacoes;
    private ArrayList<Bicicleta> listaBicicletas;
    private ArrayList<Utilizador> listaCiclistas;
    private ListView listaObjectos;


    private RetrofitService retrofitService;
    private UtilizadorApi utilizadorApi;
    private EstacaoApi estacaoApi;
    private BicicletaApi bicicletaApi;
    private int tipoPesquisa; // 0 - pesquisa de estações , 1 - pesquisa de bicicletas , 2 - pesquisa de ciclistas
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ActivityResultLauncher<Intent> activityResultLauncherEstacoes;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_inicial_ciclista);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView iconeConta = findViewById(R.id.iconeConta);
        iconeConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaInicialCiclista.this, MinhaConta.class);
                startActivity(intent);
            }
        });


        retrofitService = new RetrofitService();
        utilizadorApi = retrofitService.getRetrofit().create(UtilizadorApi.class);
        estacaoApi = retrofitService.getRetrofit().create(EstacaoApi.class);
        bicicletaApi = retrofitService.getRetrofit().create(BicicletaApi.class);

        listaObjectos = findViewById(R.id.listaObjectos);
        btnBicicletas = findViewById(R.id.btnBicicletas);
        btnCiclistas = findViewById(R.id.btnCiclistas);
        btnEstacoes = findViewById(R.id.btnEstacoes);
        searchInput = findViewById(R.id.searchInput);
        nomeUsuario = findViewById(R.id.nomeUsuario);

        utilizadorActual = SharedPreferencesUtil.getUtilizador(getApplicationContext());
        if (utilizadorActual != null) {
            nomeUsuario.setText(utilizadorActual.getUsername());
        }

        irEstacoes(null);
        tipoPesquisa = 0;

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        irBicicletas(null);
                    }
                }
        );

        activityResultLauncherEstacoes = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        irEstacoes(null);
                    }
                }
        );
    }

    public void irEstacoes(View view) {


        estacaoApi.getAllEstacaoes().enqueue(new Callback<List<Estacao>>() {
            @Override
            public void onResponse(Call<List<Estacao>> call, Response<List<Estacao>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    listaEstacoes = new ArrayList<>(response.body());
                    ListaEstacoesAdapter adapter = new ListaEstacoesAdapter(TelaInicialCiclista.this, listaEstacoes);
                    listaObjectos.setAdapter(adapter);

                    listaObjectos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(TelaInicialCiclista.this, VisualizarEstacao.class);
                            intent.putExtra("estacao", listaEstacoes.get(position));
                            activityResultLauncherEstacoes.launch(intent);
                        }
                    });

                }

            }

            @Override
            public void onFailure(Call<List<Estacao>> call, Throwable throwable) {
                Toast.makeText(TelaInicialCiclista.this, "Erro de rede listar Usuários", Toast.LENGTH_SHORT).show();
                Logger.getLogger(TelaInicialCiclista.class.getName()).log(Level.SEVERE, "Error ocurred", throwable);
            }
        });

        btnEstacoes.setBackgroundTintList(getResources().getColorStateList(R.color.azulFundoBotaoClicado, getTheme()));
        btnCiclistas.setBackgroundTintList(null);
        btnBicicletas.setBackgroundTintList(null);
        searchInput.setHint(R.string.p_estacao);
        tipoPesquisa = 0;
        searchInput.setText("");

    }


    public void irBicicletas(View view) {


        bicicletaApi.getAllBicicletas().enqueue(new Callback<List<Bicicleta>>() {
            @Override
            public void onResponse(Call<List<Bicicleta>> call, Response<List<Bicicleta>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    listaBicicletas = new ArrayList<>(response.body());
                    ListaBicicletasAdapter adapter = new ListaBicicletasAdapter(TelaInicialCiclista.this, listaBicicletas);
                    listaObjectos.setAdapter(adapter);

                    listaObjectos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(TelaInicialCiclista.this, VisualizarBicicleta.class);
                            intent.putExtra("bicicleta", listaBicicletas.get(position));
                            //startActivity(intent);
                            activityResultLauncher.launch(intent);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Bicicleta>> call, Throwable throwable) {
                Toast.makeText(TelaInicialCiclista.this, "Erro de rede listar Bicicletas", Toast.LENGTH_SHORT).show();
                Logger.getLogger(TelaInicialCiclista.class.getName()).log(Level.SEVERE, "Error ocurred", throwable);
            }
        });

        btnBicicletas.setBackgroundTintList(getResources().getColorStateList(R.color.azulFundoBotaoClicado, getTheme()));
        btnCiclistas.setBackgroundTintList(null);
        btnEstacoes.setBackgroundTintList(null);
        searchInput.setHint(R.string.p_bicicleta);
        tipoPesquisa = 1;
        searchInput.setText("");

    }

    public void irCiclistas(View view) {


        utilizadorApi.getAllUtilizadores().enqueue(new Callback<List<Utilizador>>() {
            @Override
            public void onResponse(Call<List<Utilizador>> call, Response<List<Utilizador>> response) {


                if (response.isSuccessful() && response.body() != null) {


                    listaCiclistas = new ArrayList<>(response.body());
                    ListaCiclistasAdapter adapter = new ListaCiclistasAdapter(TelaInicialCiclista.this, listaCiclistas);
                    listaObjectos.setAdapter(adapter);


                    listaObjectos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(TelaInicialCiclista.this, VisualizarCiclista.class);
                            intent.putExtra("ciclista", listaCiclistas.get(position));
                            startActivity(intent);
                        }
                    });
                }

            }

            @Override
            public void onFailure(Call<List<Utilizador>> call, Throwable throwable) {
                Toast.makeText(TelaInicialCiclista.this, "Erro de rede listar Ciclistas", Toast.LENGTH_SHORT).show();
                Logger.getLogger(TelaInicialCiclista.class.getName()).log(Level.SEVERE, "Error ocurred", throwable);
            }
        });

        btnCiclistas.setBackgroundTintList(getResources().getColorStateList(R.color.azulFundoBotaoClicado, getTheme()));
        btnBicicletas.setBackgroundTintList(null);
        btnEstacoes.setBackgroundTintList(null);
        searchInput.setHint(R.string.p_ciclista);
        tipoPesquisa = 2;
        searchInput.setText("");
    }


    public void irVisualizarMapa(View view) {
        Intent intent = new Intent(this, TelaMapa.class);
        startActivity(intent);
    }

    public void efectuarPesquisa(View view) {

        String conteudo = searchInput.getText().toString();

        if (!conteudo.isEmpty()) {

            switch (tipoPesquisa) {
                case 0:

                    estacaoApi.getEstacaoByNome(conteudo).enqueue(new Callback<List<Estacao>>() {
                        @Override
                        public void onResponse(Call<List<Estacao>> call, Response<List<Estacao>> response) {

                            if (response.isSuccessful() && response.body() != null) {

                                if (!response.body().isEmpty()) {
                                    listaEstacoes = new ArrayList<>(response.body());
                                    ListaEstacoesAdapter adapter = new ListaEstacoesAdapter(TelaInicialCiclista.this, listaEstacoes);
                                    listaObjectos.setAdapter(adapter);

                                    listaObjectos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            Intent intent = new Intent(TelaInicialCiclista.this, VisualizarEstacao.class);
                                            intent.putExtra("estacao", listaEstacoes.get(position));
                                            startActivity(intent);
                                        }
                                    });
                                } else {
                                    Toast.makeText(TelaInicialCiclista.this, "Nenhuma estação encontrada com esse nome", Toast.LENGTH_SHORT).show();
                                }


                            } else {
                                Toast.makeText(TelaInicialCiclista.this, "Nenhuma estação encontrada com esse nome", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<List<Estacao>> call, Throwable throwable) {
                            Toast.makeText(TelaInicialCiclista.this, "Erro de rede ao pesquisar estacao", Toast.LENGTH_SHORT).show();
                            Logger.getLogger(TelaInicialCiclista.class.getName()).log(Level.SEVERE, "Error ocurred", throwable);
                        }
                    });

                    break;


                case 1:

                    bicicletaApi.getBicicletaByNome(conteudo).enqueue(new Callback<List<Bicicleta>>() {
                        @Override
                        public void onResponse(Call<List<Bicicleta>> call, Response<List<Bicicleta>> response) {

                            if (response.isSuccessful() && response.body() != null) {

                                if (!response.body().isEmpty()) {
                                    listaBicicletas = new ArrayList<>(response.body());
                                    ListaBicicletasAdapter adapter = new ListaBicicletasAdapter(TelaInicialCiclista.this, listaBicicletas);
                                    listaObjectos.setAdapter(adapter);

                                    listaObjectos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            Intent intent = new Intent(TelaInicialCiclista.this, VisualizarBicicleta.class);
                                            intent.putExtra("bicicleta", listaBicicletas.get(position));
                                            startActivity(intent);
                                        }
                                    });
                                } else {
                                    Toast.makeText(TelaInicialCiclista.this, "Nenhuma bicicleta encontrada com esse nome", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }

                        @Override
                        public void onFailure(Call<List<Bicicleta>> call, Throwable throwable) {
                            Toast.makeText(TelaInicialCiclista.this, "Erro de rede ao pesquisar bicicleta", Toast.LENGTH_SHORT).show();
                            Logger.getLogger(TelaInicialCiclista.class.getName()).log(Level.SEVERE, "Error ocurred", throwable);
                        }
                    });

                    break;

                case 2:

                    utilizadorApi.getUtilizadorByUsername(conteudo).enqueue(new Callback<Utilizador>() {
                        @Override
                        public void onResponse(Call<Utilizador> call, Response<Utilizador> response) {

                            if (response.isSuccessful() && response.body() != null) {
                                listaCiclistas = new ArrayList<>();
                                listaCiclistas.add(response.body());
                                ListaCiclistasAdapter adapter = new ListaCiclistasAdapter(TelaInicialCiclista.this, listaCiclistas);
                                listaObjectos.setAdapter(adapter);
                            } else {
                                Toast.makeText(TelaInicialCiclista.this, "Nenhum ciclista encontrado com esse username", Toast.LENGTH_SHORT).show();
                            }

                            listaObjectos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent = new Intent(TelaInicialCiclista.this, VisualizarCiclista.class);
                                    intent.putExtra("ciclista", listaCiclistas.get(position));
                                    startActivity(intent);
                                }
                            });

                        }

                        @Override
                        public void onFailure(Call<Utilizador> call, Throwable throwable) {
                            Toast.makeText(TelaInicialCiclista.this, "Erro de rede ao pesquisar ciclista", Toast.LENGTH_SHORT).show();
                            Logger.getLogger(TelaInicialCiclista.class.getName()).log(Level.SEVERE, "Error ocurred", throwable);
                        }
                    });

                    break;
            }

            searchInput.setText("");

        } else {
            Toast.makeText(this, "Porfavor insira o que quiser pesquisar", Toast.LENGTH_SHORT).show();
        }

    }


}
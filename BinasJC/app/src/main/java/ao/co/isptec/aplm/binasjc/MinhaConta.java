package ao.co.isptec.aplm.binasjc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import ao.co.isptec.aplm.binasjc.model.Trajectoria;
import ao.co.isptec.aplm.binasjc.model.Utilizador;
import ao.co.isptec.aplm.binasjc.retrofit.EstacaoApi;
import ao.co.isptec.aplm.binasjc.retrofit.RetrofitService;
import ao.co.isptec.aplm.binasjc.retrofit.TrajectoriaApi;
import ao.co.isptec.aplm.binasjc.retrofit.UtilizadorApi;
import ao.co.isptec.aplm.binasjc.shared.SharedPreferencesUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MinhaConta extends AppCompatActivity {

    private ListView listaObjectosTrajectos;
    private ArrayList<Trajectoria> listaTrajectorias;
    private ArrayList<String> listaTrajectoriasDistancias;
    private RetrofitService retrofitService;
    private TrajectoriaApi trajectoriaApi;

    private TextView nomeUsuario;
    private TextView nomeUsuarioCompleto;
    private TextView pontos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_minha_conta);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        nomeUsuario = findViewById(R.id.nomeUsuarioMinhaConta);
        nomeUsuarioCompleto = findViewById(R.id.nomeCompletoMapa);
        pontos = findViewById(R.id.pontosAcumulados);

        retrofitService = new RetrofitService();
        trajectoriaApi = retrofitService.getRetrofit().create(TrajectoriaApi.class);


        SharedPreferencesUtil.getUtilizador(MinhaConta.this, new SharedPreferencesUtil.UtilizadorCallback() {
            @Override
            public void onSuccess(Utilizador utilizador) {
                nomeUsuario.setText(utilizador.getUsername());
                nomeUsuarioCompleto.setText(utilizador.getNomecompleto());
                pontos.setText(String.valueOf(utilizador.getPontos()));


                listaObjectosTrajectos = findViewById(R.id.listaTrajectos);

                trajectoriaApi.getTrajectoriaByIdUsuario(utilizador.getId()).enqueue(new Callback<List<Trajectoria>>() {
                    @Override
                    public void onResponse(Call<List<Trajectoria>> call, Response<List<Trajectoria>> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            listaTrajectorias = new ArrayList<>(response.body());
                            listaTrajectoriasDistancias = new ArrayList<>(calcularDistanciasDasTrajectorias(listaTrajectorias));
                            ListaTrajectoriasAdapter adapter = new ListaTrajectoriasAdapter(MinhaConta.this, listaTrajectoriasDistancias);
                            listaObjectosTrajectos.setAdapter(adapter);

                            listaObjectosTrajectos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent = new Intent(MinhaConta.this, VisualizarTrajectoria.class);
                                    intent.putExtra("trajectoria", position);
                                    startActivity(intent);
                                }
                            });

                        }

                    }

                    @Override
                    public void onFailure(Call<List<Trajectoria>> call, Throwable throwable) {
                        Toast.makeText(MinhaConta.this, "Erro de rede trajectorias", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        //Guardando o contexto da Actividade Actual e o TextView para a visualização de mensagens recebidas
        ActivityContextProvider.setCurrentActivity(MinhaConta.this);
        ActivityContextProvider.setOutputMsg(null);
        //
    }


    public void voltar(View view) {
        finish();
    }

    public void fazerLogout(View view) {
        SharedPreferencesUtil.saveUtilizador(getApplicationContext(), null);
        Intent intent = new Intent(this, TelaLogin.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish(); // Fecha a atividade
    }

    private ArrayList<String> calcularDistanciasDasTrajectorias(ArrayList<Trajectoria> listaTrajectorias) {
        ArrayList<String> distancias = new ArrayList<>();
        ArrayList<Trajectoria> trajectoriaActual = new ArrayList<>();

        if (!listaTrajectorias.isEmpty()) {
            int idTrajectoria = listaTrajectorias.get(0).getIdTrajectoria();

            for (Trajectoria trajectoria : listaTrajectorias) {
                if (idTrajectoria == trajectoria.getIdTrajectoria()) {
                    trajectoriaActual.add(trajectoria);
                } else {
                    idTrajectoria = trajectoria.getIdTrajectoria();
                    distancias.add(String.valueOf(calcularDistanciaPercorrida(trajectoriaActual)));
                    trajectoriaActual = new ArrayList<>();
                    trajectoriaActual.add(trajectoria);
                }
            }

            if (!trajectoriaActual.isEmpty()) {
                distancias.add(String.valueOf(calcularDistanciaPercorrida(trajectoriaActual)));
            }
        }

        return distancias;
    }

    private double calcularDistancia(LatLng ponto1, LatLng ponto2) {
        double lat1 = Math.toRadians(ponto1.latitude);
        double lon1 = Math.toRadians(ponto1.longitude);
        double lat2 = Math.toRadians(ponto2.latitude);
        double lon2 = Math.toRadians(ponto2.longitude);

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return 6371000 * c; // Distância em metros
    }

    private double calcularDistanciaPercorrida(ArrayList<Trajectoria> trajectoria) {
        double distanciaTotal = 0.0;

        for (int j = 1; j < trajectoria.size(); j++) {
            LatLng pontoAnterior = new LatLng(trajectoria.get(j - 1).getLatitude(), trajectoria.get(j - 1).getLongitude());
            LatLng pontoAtual = new LatLng(trajectoria.get(j).getLatitude(), trajectoria.get(j).getLongitude());

            // Calcule a distância entre os pontos consecutivos
            distanciaTotal += calcularDistancia(pontoAnterior, pontoAtual);
        }

        return Math.round(distanciaTotal * 1000.0) / 1000.0; // Distância total em metros
    }

}
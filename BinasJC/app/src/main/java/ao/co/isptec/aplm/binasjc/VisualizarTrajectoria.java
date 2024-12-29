package ao.co.isptec.aplm.binasjc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import ao.co.isptec.aplm.binasjc.model.Trajectoria;
import ao.co.isptec.aplm.binasjc.model.Utilizador;
import ao.co.isptec.aplm.binasjc.retrofit.RetrofitService;
import ao.co.isptec.aplm.binasjc.retrofit.TrajectoriaApi;
import ao.co.isptec.aplm.binasjc.shared.SharedPreferencesUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisualizarTrajectoria extends AppCompatActivity implements OnMapReadyCallback {

    private SupportMapFragment mapFragment;
    private TextView nomeUsuario;
    private ArrayList<LatLng> trajectoriaApresentar;
    private GoogleMap mMap;
    private RetrofitService retrofitService;
    private TrajectoriaApi trajectoriaApi;
    private int posicao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_visualizar_trajectoria);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        posicao = intent.getIntExtra("trajectoria", -1);

        TextView trajectoNumero = findViewById(R.id.trajectoNumero);
        trajectoNumero.setText("Trajecto " + (posicao + 1));

        retrofitService = new RetrofitService();
        trajectoriaApi = retrofitService.getRetrofit().create(TrajectoriaApi.class);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        ImageView voltar = findViewById(R.id.voltar);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        nomeUsuario = findViewById(R.id.nomeUsuarioVisualizarTrajectoria);



    }

    @Override
    protected void onStart() {
        super.onStart();
        //Guardando o contexto da Actividade Actual e o TextView para a visualização de mensagens recebidas
        ActivityContextProvider.setCurrentActivity(VisualizarTrajectoria.this);
        ActivityContextProvider.setOutputMsg(null);
        //
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        //desenhar a trajetoria no mapa

        SharedPreferencesUtil.getUtilizador(VisualizarTrajectoria.this, new SharedPreferencesUtil.UtilizadorCallback() {
            @Override
            public void onSuccess(Utilizador utilizador) {
                nomeUsuario.setText(utilizador.getUsername());

                //inicializar
                trajectoriaApresentar = new ArrayList<>();

                trajectoriaApi.getTrajectoriaByIdUsuario(utilizador.getId()).enqueue(new Callback<List<Trajectoria>>() {
                    @Override
                    public void onResponse(Call<List<Trajectoria>> call, Response<List<Trajectoria>> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            ArrayList<ArrayList<Trajectoria>> trajectoriasOrganizadas = new ArrayList<>(organizarTrajectorias(response.body()));
                            int j = 0;

                            for (ArrayList<Trajectoria> trajectoria : trajectoriasOrganizadas) {
                                if (j == posicao) {

                                    for (Trajectoria t : trajectoria) {
                                        trajectoriaApresentar.add(new LatLng(t.getLatitude(), t.getLongitude()));
                                    }

                                    TextView distanciaPercorrida = findViewById(R.id.distanciaPercorrida);
                                    distanciaPercorrida.setText("" + calcularDistanciaPercorrida(trajectoria) + " m");

                                    desenharTrajecto();

                                    Marker inicio = mMap.addMarker(new MarkerOptions().position(trajectoriaApresentar.get(0)).title("partida"));
                                    Marker fim = mMap.addMarker(new MarkerOptions().position(trajectoriaApresentar.get(trajectoriaApresentar.size() - 1)).title("destino"));
                                    desenharTrajecto();
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(trajectoriaApresentar.get(0), 15));
                                    break;
                                }
                                j++;
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Trajectoria>> call, Throwable throwable) {
                        Toast.makeText(VisualizarTrajectoria.this, "Erro de rede trajectorias", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

    }

    private void desenharTrajecto() {
            PolylineOptions polylineOptions = new PolylineOptions()
                    .clickable(false)
                    .color(R.color.azulBebe)
                    .width(6)
                    .addAll(trajectoriaApresentar);
            Polyline polyline = mMap.addPolyline(polylineOptions);
    }

    private ArrayList<ArrayList<Trajectoria>> organizarTrajectorias(List<Trajectoria> listaTrajectorias) {
        ArrayList<ArrayList<Trajectoria>> trajectoriasOrganizadas = new ArrayList<>();
        ArrayList<Trajectoria> trajectoriaActual = new ArrayList<>();

        if (!listaTrajectorias.isEmpty()) {
            int idTrajectoria = listaTrajectorias.get(0).getIdTrajectoria();

            for (Trajectoria trajectoria : listaTrajectorias) {
                if (idTrajectoria == trajectoria.getIdTrajectoria()) {
                    trajectoriaActual.add(trajectoria);
                } else {
                    idTrajectoria = trajectoria.getIdTrajectoria();
                    trajectoriasOrganizadas.add(trajectoriaActual);
                    trajectoriaActual = new ArrayList<>();
                    trajectoriaActual.add(trajectoria);
                }
            }

            if (!trajectoriaActual.isEmpty()) {
                trajectoriasOrganizadas.add(trajectoriaActual);
            }
        }

        return trajectoriasOrganizadas;
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


}
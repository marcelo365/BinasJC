package ao.co.isptec.aplm.binasjc;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Messenger;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ao.co.isptec.aplm.binasjc.model.Estacao;
import ao.co.isptec.aplm.binasjc.model.Trajectoria;
import ao.co.isptec.aplm.binasjc.model.Utilizador;
import ao.co.isptec.aplm.binasjc.retrofit.EstacaoApi;
import ao.co.isptec.aplm.binasjc.retrofit.RetrofitService;
import ao.co.isptec.aplm.binasjc.retrofit.TrajectoriaApi;
import ao.co.isptec.aplm.binasjc.retrofit.UtilizadorApi;
import ao.co.isptec.aplm.binasjc.shared.SharedPreferencesUtil;
import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TelaMapa extends AppCompatActivity implements OnMapReadyCallback, SimWifiP2pManager.PeerListListener {

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView nomeUsuario;
    private Button iniTraj;
    private ArrayList<Estacao> listaEstacoes;
    private RetrofitService retrofitService;
    private EstacaoApi estacaoApi;
    private UtilizadorApi utilizadorApi;
    private TrajectoriaApi trajectoriaApi;
    private ArrayList<LatLng> trajectoria;
    private ArrayList<LatLng> pontosRecolhidosTrajectoria = new ArrayList<>();
    private Handler handler;
    private Runnable runnable;

    private PolylineOptions polylineOptions = new PolylineOptions();
    private Marker marcadorVerde;

    // Raio da Terra em metros
    private static final double Raio = 6371000;
    int i = 0;
    double dist;
    TextView pontos;

    //variável para saber se o mapa tem um evento de clique associado a ele
    private boolean isMapClickEnabled = true; // Variável de controle

    //variáveis para wifi-direct e broadcastReceiver
    private SimWifiP2pBroadcastReceiver mReceiver;
    private WifiService mWifiService;
    private boolean mBoundWifiService = false;
    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            WifiService.LocalBinder binder = (WifiService.LocalBinder) service;
            mWifiService = binder.getService();
            mBoundWifiService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBoundWifiService = false;
            mWifiService = null;
        }
    };

    private boolean verificarEstacaoPartidaDestino = false; //variável de controle para o onPeersAvailable , false para quando for para verificar a estacao de partida e true a de destino
    private boolean verificarSeDepositouBicicleta = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_mapa);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // register broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
        mReceiver = new SimWifiP2pBroadcastReceiver(TelaMapa.this);
        ContextCompat.registerReceiver(this, mReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
        //


        Intent intent = new Intent(this, WifiService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        polylineOptions = polylineOptions
                .clickable(false)
                .color(ContextCompat.getColor(this, R.color.azulBebe))
                .width(6);

        ImageView iconeConta = findViewById(R.id.iconeConta);
        iconeConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaMapa.this, MinhaConta.class);
                startActivity(intent);
            }
        });

        iniTraj = (Button) findViewById(R.id.iniTraj);
        pontos = findViewById(R.id.pontosAcumulados);

        iniTraj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarTrajecto();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-8.906299, 13.308273), 15));
                Log.i("teste", trajectoria.toString());
            }
        });

        iniTraj.setEnabled(false);

        retrofitService = new RetrofitService();
        utilizadorApi = retrofitService.getRetrofit().create(UtilizadorApi.class);
        estacaoApi = retrofitService.getRetrofit().create(EstacaoApi.class);
        trajectoriaApi = retrofitService.getRetrofit().create(TrajectoriaApi.class);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
        nomeUsuario = findViewById(R.id.nomeUsuarioTelaMapa);


        SharedPreferencesUtil.getUtilizador(TelaMapa.this, new SharedPreferencesUtil.UtilizadorCallback() {
            @Override
            public void onSuccess(Utilizador utilizador) {
                nomeUsuario.setText(utilizador.getUsername());


            }
        });


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //------------------------------------------------------------------------------------------
        //INICIALIZAR O ARRAY COM AS TRAJETORIAS NO FICHEIRO
        trajectoria = new ArrayList<>();

        try {
            InputStream inputStream = getAssets().open("coordenadas.txt");
            Log.i("Try", "Input" + inputStream.toString());
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {

                String[] parts = line.split(",");
                if (parts.length == 2) {
                    double latitude = Double.parseDouble(parts[0].trim());
                    double longitude = Double.parseDouble(parts[1].trim());
                    trajectoria.add(new LatLng(latitude, longitude));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //------------------------------------------------------------------------------------------


        estacaoApi.getAllEstacaoes().enqueue(new Callback<List<Estacao>>() {
            @Override
            public void onResponse(Call<List<Estacao>> call, Response<List<Estacao>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    listaEstacoes = new ArrayList<>(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Estacao>> call, Throwable throwable) {
                Toast.makeText(TelaMapa.this, "Erro de rede listar Estações", Toast.LENGTH_SHORT).show();
                Logger.getLogger(TelaMapa.class.getName()).log(Level.SEVERE, "Error ocurred", throwable);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        ////-------------------
        atualizarEstacoesPinos();
        ///----------------------------

        //Guardando o contexto da Actividade Actual e o TextView para a visualização de mensagens recebidas
        ActivityContextProvider.setCurrentActivity(TelaMapa.this);
        ActivityContextProvider.setOutputMsg(null);
        //
    }

    @Override
    protected void onPause() {
        super.onPause();
        ////-------------------
        atualizarEstacoesPinos();
        ///----------------------------
        if (mReceiver != null) {
            try {
                unregisterReceiver(mReceiver);
            } catch (IllegalArgumentException e) {
                // O receptor não estava registrado, você pode ignorar isso ou lidar com o erro de outra maneira
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {

        boolean existeBicicletaPerto = false;
        for (SimWifiP2pDevice dispositivo : peers.getDeviceList()) {
            if (dispositivo.deviceName.contains("bicicleta")) {
                existeBicicletaPerto = true;
            }
        }

        if (!verificarEstacaoPartidaDestino) {
            if (existeBicicletaPerto) {
                iniTraj.setEnabled(true);
            }
        } else {
            if (!existeBicicletaPerto) {

                SharedPreferencesUtil.getUtilizador(TelaMapa.this, new SharedPreferencesUtil.UtilizadorCallback() {
                    @Override
                    public void onSuccess(Utilizador utilizador) {
                        Utilizador utilizadorActualizado = utilizador;
                        utilizadorActualizado.setIdEstacaoReservaBicicleta(0);
                        utilizadorActualizado.setPontos(utilizador.getPontos() + Double.parseDouble(pontos.getText().toString()) );

                        utilizadorApi.save(utilizadorActualizado).enqueue(new Callback<Utilizador>() {
                            @Override
                            public void onResponse(Call<Utilizador> call, Response<Utilizador> response) {

                                if (response.isSuccessful() && response.body() != null) {
                                    Toast.makeText(TelaMapa.this, "Usuário depositou a bicicleta com sucesso", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Utilizador> call, Throwable throwable) {
                                Log.d("Erro", "Erro de rede ao depositar bicicleta");
                            }
                        });

                        Estacao estacaoFinal = procurarEstacao(2);
                        estacaoFinal.setBicicletasDisponiveis(estacaoFinal.getBicicletasDisponiveis() + 1);

                        estacaoApi.save(estacaoFinal).enqueue(new Callback<Estacao>() {
                            @Override
                            public void onResponse(Call<Estacao> call, Response<Estacao> response) {

                                if (response.isSuccessful() && response.body() != null) {

                                }
                            }

                            @Override
                            public void onFailure(Call<Estacao> call, Throwable throwable) {
                                Toast.makeText(TelaMapa.this, "Erro de rede atualizar estação após depósito", Toast.LENGTH_SHORT).show();
                                Logger.getLogger(TelaMapa.class.getName()).log(Level.SEVERE, "Error ocurred", throwable);
                            }
                        });

                        verificarSeDepositouBicicleta = true;

                    }
                });

                guardarPontosRecolhidosBD();

            } else {
                mWifiService.getManager().requestPeers(mWifiService.getmChannel(), TelaMapa.this);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        marcadorVerde = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(-8.906299, 13.308273)) // posição inicial
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (isMapClickEnabled) { // Verifica se o listener está habilitado
                    // Move o marcador para a posição clicada
                    verificarEstacaoPartidaDestino = false;
                    marcadorVerde.setPosition(latLng);
                    if (verificarSeEncontraDentroDoRaioEstacao(1)) {
                        Toast.makeText(TelaMapa.this, "Dentro do raio de 100 metros da estação de partida", Toast.LENGTH_SHORT).show();

                        SharedPreferencesUtil.getUtilizador(TelaMapa.this, new SharedPreferencesUtil.UtilizadorCallback() {
                            @Override
                            public void onSuccess(Utilizador utilizador) {
                                if (utilizador.getIdEstacaoReservaBicicleta() == 1) {
                                    mWifiService.getManager().requestPeers(mWifiService.getmChannel(), TelaMapa.this); //pedir a lista de dispositivos mais próximos
                                }
                            }
                        });
                    }


                }
            }
        });


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-8.906299, 13.308273), 15));


        // Verificando se a permissão de localização foi concedida
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Se a permissão não for concedida, solicitar permissão
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            // Se já tiver permissão, ativar a localização no mapa
            getCurrentLocation();
        }

        if (listaEstacoes != null)
            for (Estacao estacao : listaEstacoes) {
                LatLng Location = new LatLng(estacao.getLatitude(), estacao.getLongitude());
                Marker marker = mMap.addMarker(new MarkerOptions().position(Location).title(estacao.getNome()));
                marker.setTag(estacao);
            }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {

                if (marker.equals(marcadorVerde)) {
                    return false;
                }

                SharedPreferencesUtil.getUtilizador(TelaMapa.this, new SharedPreferencesUtil.UtilizadorCallback() {
                    @Override
                    public void onSuccess(Utilizador utilizador) {

                        Estacao estacao = (Estacao) marker.getTag();
                        AlertDialog.Builder builderInformacaoEstacao = new AlertDialog.Builder(TelaMapa.this).setTitle("Estação: " + estacao.getNome())  // Título do Dialog
                                .setMessage("Bicicletas Disponíveis : " + estacao.getBicicletasDisponiveis())  // Mensagem do Dialog
                                .setPositiveButton("OK", null);  // Botão de confirmação

                        Button btnReservar = new Button(TelaMapa.this);
                        btnReservar.setText("Reservar Bicicleta");
                        btnReservar.setBackgroundColor(getResources().getColor(R.color.azulFundoBotao));
                        btnReservar.setTextColor(getResources().getColor(R.color.white));

                        Button btnCancelarReserva = new Button(TelaMapa.this);
                        btnCancelarReserva.setText("Cancelar reserva de bicicleta");
                        btnCancelarReserva.setBackgroundColor(getResources().getColor(R.color.azulFundoBotaoClicado));
                        btnCancelarReserva.setTextColor(getResources().getColor(R.color.white));


                        if (utilizador.getIdEstacaoReservaBicicleta() == 0) {
                            btnReservar.setEnabled(true);
                            btnReservar.setTextColor(getResources().getColor(R.color.white));
                            btnCancelarReserva.setEnabled(false);
                            btnCancelarReserva.setTextColor(getResources().getColor(R.color.cinza));

                        } else if (utilizador.getIdEstacaoReservaBicicleta() == estacao.getId()) {
                            btnReservar.setEnabled(false);
                            btnReservar.setTextColor(getResources().getColor(R.color.cinza));
                            btnCancelarReserva.setEnabled(true);
                            btnCancelarReserva.setTextColor(getResources().getColor(R.color.white));
                        } else {
                            btnReservar.setEnabled(false);
                            btnReservar.setTextColor(getResources().getColor(R.color.cinza));
                            btnCancelarReserva.setEnabled(false);
                            btnCancelarReserva.setTextColor(getResources().getColor(R.color.cinza));
                        }

                        LinearLayout layout = new LinearLayout(TelaMapa.this);
                        layout.setOrientation(LinearLayout.VERTICAL);  // Colocar os botões em coluna
                        layout.setPadding(32, 16, 32, 16);  // Adicionar algum padding para os botões não ficarem grudados nas bordas


                        // Adicionar os botões ao layout
                        layout.addView(btnReservar);
                        layout.addView(btnCancelarReserva);

                        builderInformacaoEstacao.setView(layout);
                        // Criar o AlertDialog a partir do Builder
                        AlertDialog dialog = builderInformacaoEstacao.create();

                        btnReservar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (estacao.getBicicletasDisponiveis() > 0) {

                                    Utilizador utilizadorActual = utilizador;
                                    utilizadorActual.setIdEstacaoReservaBicicleta(estacao.getId());
                                    SharedPreferencesUtil.saveUtilizador(TelaMapa.this, utilizadorActual);

                                    utilizadorApi.save(utilizadorActual).enqueue(new Callback<Utilizador>() {
                                        @Override
                                        public void onResponse(Call<Utilizador> call, Response<Utilizador> response) {

                                            if (response.isSuccessful() && response.body() != null) {
                                                Toast.makeText(TelaMapa.this, "Bicicleta reservada com sucesso", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.d("Erro", "Erro ao reservar bicicleta");
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Utilizador> call, Throwable throwable) {
                                            Log.d("Erro", "Erro de rede ao reservar bicicleta");
                                        }
                                    });

                                    estacao.setBicicletasDisponiveis(estacao.getBicicletasDisponiveis() - 1);
                                    estacaoApi.save(estacao).enqueue(new Callback<Estacao>() {
                                        @Override
                                        public void onResponse(Call<Estacao> call, Response<Estacao> response) {

                                            if (response.isSuccessful() && response.body() != null) {

                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Estacao> call, Throwable throwable) {
                                            Toast.makeText(TelaMapa.this, "Erro de rede atualizar estação", Toast.LENGTH_SHORT).show();
                                            Logger.getLogger(TelaMapa.class.getName()).log(Level.SEVERE, "Error ocurred", throwable);
                                        }
                                    });

                                } else {
                                    Toast.makeText(TelaMapa.this, "Esta estação não possui bicicletas disponíveis para reserva", Toast.LENGTH_SHORT).show();
                                }

                                dialog.dismiss();


                            }
                        });


                        btnCancelarReserva.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                Utilizador utilizadorActual = utilizador;
                                utilizadorActual.setIdEstacaoReservaBicicleta(0);
                                SharedPreferencesUtil.saveUtilizador(TelaMapa.this, utilizadorActual);

                                utilizadorApi.save(utilizadorActual).enqueue(new Callback<Utilizador>() {
                                    @Override
                                    public void onResponse(Call<Utilizador> call, Response<Utilizador> response) {

                                        if (response.isSuccessful() && response.body() != null) {
                                            Toast.makeText(TelaMapa.this, "Reserva de bicicleta cancelada", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.d("Erro", "Erro ao cancelar reserva de bicicleta");
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Utilizador> call, Throwable throwable) {
                                        Log.d("Erro", "Erro de rede ao cancelar reserva de bicicleta");
                                    }
                                });

                                estacao.setBicicletasDisponiveis(estacao.getBicicletasDisponiveis() + 1);
                                estacaoApi.save(estacao).enqueue(new Callback<Estacao>() {
                                    @Override
                                    public void onResponse(Call<Estacao> call, Response<Estacao> response) {

                                        if (response.isSuccessful() && response.body() != null) {

                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Estacao> call, Throwable throwable) {
                                        Toast.makeText(TelaMapa.this, "Erro de rede ao atualizar estação", Toast.LENGTH_SHORT).show();
                                        Logger.getLogger(TelaMapa.class.getName()).log(Level.SEVERE, "Error ocurred", throwable);
                                    }
                                });


                                dialog.dismiss();
                            }
                        });


                        dialog.show();
                        marker.showInfoWindow();
                    }
                });

                return true;
            }
        });

    }

    // Método para obter a localização atual do usuário
    private void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
        fusedLocationClient.getCurrentLocation(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener(this, new com.google.android.gms.tasks.OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    // Obtendo a latitude e longitude
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    // Adicionando o marcador (pino) na localização atual
                    mMap.addMarker(new MarkerOptions().position(currentLocation).title("Você está aqui"));

                    // Movendo a câmera para a localização atual
                    //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            // Verificando se a permissão foi concedida
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão concedida, ativar localização no mapa
                getCurrentLocation();
                Toast.makeText(this, "Acesso à localização concedido", Toast.LENGTH_SHORT).show();
            } else {
                // Se a permissão não for concedida, pode mostrar uma mensagem para o usuário
                // Por exemplo, mostrar um Toast ou um alerta
                Toast.makeText(this, "Acesso à localização não concedido", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void irVisualizarCiclistas(View view) {
        Intent intent = new Intent(this, VisualizarCiclistasMapa.class);
        startActivity(intent);
    }


    private void atualizarEstacoesPinos() {

        if (mMap != null && listaEstacoes != null) {

            estacaoApi.getAllEstacaoes().enqueue(new Callback<List<Estacao>>() {
                @Override
                public void onResponse(Call<List<Estacao>> call, Response<List<Estacao>> response) {

                    if (response.isSuccessful() && response.body() != null) {

                        listaEstacoes = new ArrayList<>(response.body());
                    }
                }

                @Override
                public void onFailure(Call<List<Estacao>> call, Throwable throwable) {
                    Toast.makeText(TelaMapa.this, "Erro de rede listar Estações", Toast.LENGTH_SHORT).show();
                    Logger.getLogger(TelaMapa.class.getName()).log(Level.SEVERE, "Error ocurred", throwable);
                }
            });


            mMap.clear(); // Remove todos os marcadores antigos
            for (Estacao estacao : listaEstacoes) {
                LatLng location = new LatLng(estacao.getLatitude(), estacao.getLongitude());
                Marker marker = mMap.addMarker(new MarkerOptions().position(location).title(estacao.getNome()));
                marker.setTag(estacao);
            }

            marcadorVerde = mMap.addMarker(new MarkerOptions()
                    .position(marcadorVerde.getPosition()) // posição inicial
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

            mMap.addPolyline(polylineOptions);

            getCurrentLocation();
        }
    }


    public void iniciarTrajecto() {
        verificarEstacaoPartidaDestino = true;
        isMapClickEnabled = false;
        iniTraj.setEnabled(false);
        i = 0;
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (i < trajectoria.size()) {
                    pontosRecolhidosTrajectoria.add(trajectoria.get(i));
                    desenharTrajecto(trajectoria.get(i));
                    dist = calcularDistanciaPercorrida();
                    pontos.setText("" + Math.round(calcularDistanciaPercorrida() * 1000.0) / 1000.0);

                    if (i == trajectoria.size() - 1) {
                        if (verificarSeEncontraDentroDoRaioEstacao(2)) {
                            Toast.makeText(TelaMapa.this, "Dentro do raio de 100 metros da estação de destino", Toast.LENGTH_SHORT).show();
                            mWifiService.getManager().requestPeers(mWifiService.getmChannel(), TelaMapa.this); //pedir a lista de dispositivos mais próximos
                        }
                    }


                    i++;
                    // Reagenda a execução após 3 segundos
                    handler.postDelayed(this, 3000);
                }
            }
        };

        handler.postDelayed(runnable, 1);
    }


    private void desenharTrajecto(LatLng pos) {
        polylineOptions.add(pos);
        marcadorVerde.setPosition(pos);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15));
        mMap.addPolyline(polylineOptions);
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

        return Raio * c; // Distância em metros
    }

    private double calcularDistanciaPercorrida() {
        double distanciaTotal = 0.0;

        for (int j = 1; j <= i; j++) {
            LatLng pontoAnterior = trajectoria.get(j - 1);
            LatLng pontoAtual = trajectoria.get(j);

            // Calcule a distância entre os pontos consecutivos
            distanciaTotal += calcularDistancia(pontoAnterior, pontoAtual);
        }

        return distanciaTotal; // Distância total em metros
    }

    private void moverPino(LatLng posicao) {
        if (mMap != null) {
            if (marcadorVerde != null) {
                marcadorVerde.setPosition(posicao);
            }
        }
    }

    private boolean verificarSeEncontraDentroDoRaioEstacao(int idEstacao) {

        if (listaEstacoes != null) {
            for (Estacao estacao : listaEstacoes) {
                if (estacao.getId() == idEstacao) {
                    double distancia = calcularDistancia(marcadorVerde.getPosition(), new LatLng(estacao.getLatitude(), estacao.getLongitude()));
                    return (distancia <= 100) ? true : false;
                }
            }
        }
        return false;
    }

    private Estacao procurarEstacao(int idEstacao) {
        if (listaEstacoes != null) {
            for (Estacao estacao : listaEstacoes) {
                if (estacao.getId() == idEstacao) {
                    return estacao;
                }
            }
        }
        return null;
    }

    private void guardarPontosRecolhidosBD() {
        if (pontosRecolhidosTrajectoria != null) {


            trajectoriaApi.getTrajectoriaByIdUsuario(SharedPreferencesUtil.getIdUsuario()).enqueue(new Callback<List<Trajectoria>>() {
                @Override
                public void onResponse(Call<List<Trajectoria>> call, Response<List<Trajectoria>> response) {

                    if (response.isSuccessful() && response.body() != null) {

                        int id = 0;
                        for (Trajectoria trajectoria : response.body()) {
                            if (trajectoria.getIdTrajectoria() > id) {
                                id = trajectoria.getIdTrajectoria();
                            }
                        }
                        id = id + 1;
                        salvarPontoSequencial(0, SharedPreferencesUtil.getIdUsuario(), id);
                    }

                }

                @Override
                public void onFailure(Call<List<Trajectoria>> call, Throwable throwable) {
                    Toast.makeText(TelaMapa.this, "Erro de rede trajectorias", Toast.LENGTH_SHORT).show();
                }
            });


        }
    }


    private void salvarPontoSequencial(int index, int idUsuario, int idTrajectoriaSerAdicionada) {
        if (index < pontosRecolhidosTrajectoria.size()) {
            LatLng posicao = pontosRecolhidosTrajectoria.get(index);

            Trajectoria trajectoria = new Trajectoria(idTrajectoriaSerAdicionada, posicao.latitude, posicao.longitude, idUsuario);

            trajectoriaApi.save(trajectoria).enqueue(new Callback<Trajectoria>() {
                @Override
                public void onResponse(Call<Trajectoria> call, Response<Trajectoria> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        //Toast.makeText(TelaMapa.this, "Coordenada guardada: " + index, Toast.LENGTH_SHORT).show();
                    }
                    // Chama o próximo ponto
                    salvarPontoSequencial(index + 1, idUsuario, idTrajectoriaSerAdicionada);
                }

                @Override
                public void onFailure(Call<Trajectoria> call, Throwable throwable) {
                    Toast.makeText(TelaMapa.this, "Erro de rede salvar trajectoria", Toast.LENGTH_SHORT).show();
                    // Mesmo em caso de erro, continua para o próximo ponto
                    //salvarPontoSequencial(index + 1, utilizador);
                }
            });
        }
    }


}
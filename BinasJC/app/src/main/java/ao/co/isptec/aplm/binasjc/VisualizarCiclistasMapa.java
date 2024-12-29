package ao.co.isptec.aplm.binasjc;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ao.co.isptec.aplm.binasjc.model.Utilizador;
import ao.co.isptec.aplm.binasjc.retrofit.RetrofitService;
import ao.co.isptec.aplm.binasjc.retrofit.UtilizadorApi;
import ao.co.isptec.aplm.binasjc.shared.SharedPreferencesUtil;
import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisualizarCiclistasMapa extends AppCompatActivity implements SimWifiP2pManager.PeerListListener, SimWifiP2pManager.GroupInfoListener {

    private ListView listaObjectosCiclistas;
    private ArrayList<Utilizador> listaCiclistas;

    private RetrofitService retrofitService;
    private UtilizadorApi utilizadorApi;

    private TextView nomeUsuario;
    private ImageView iconeConta;

    //
    public static final String TAG = "AppWifi";
    private SimWifiP2pBroadcastReceiver mReceiver;

    //
    private EditText inputMsg; // campo para digitar mensagem
    private TextView outputMsg; //text view para visualizar as mensagens
    private boolean enviarMsgOuPontos; //true - enviar mensagem , false - enviar pontos
    private String nomeDispositivoActual;

    //
    private SimWifiP2pSocketServer mSrvSocket = null;
    //Um socket é criado em uma aplicação para permitir a comunicação de rede.
    //No lado do servidor, o socket é chamado de server socket e fica aguardando as conexões de clientes.
    //Conexão entre Cliente e Servidor:
    //
    //O servidor fica "escutando" uma porta usando o server socket.
    //Quando um cliente tenta se conectar ao servidor, o servidor aceita a solicitação com o metodo accept(). Esse metodo retorna um socket de comunicação, que representa a conexão estabelecida entre o cliente e o servidor.
    private SimWifiP2pSocket mCliSocket = null;
    //


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

        ImageView iconeConta = findViewById(R.id.iconeConta);
        iconeConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VisualizarCiclistasMapa.this, MinhaConta.class);
                startActivity(intent);
            }
        });


        retrofitService = new RetrofitService();
        utilizadorApi = retrofitService.getRetrofit().create(UtilizadorApi.class);
        listaObjectosCiclistas = findViewById(R.id.listaObjectosCiclistas);
        nomeUsuario = findViewById(R.id.nomeUsuarioVisualizarCiclistaMapa);

        SharedPreferencesUtil.getUtilizador(VisualizarCiclistasMapa.this, new SharedPreferencesUtil.UtilizadorCallback() {
            @Override
            public void onSuccess(Utilizador utilizador) {
                nomeUsuario.setText(utilizador.getUsername());
            }
        });


        // register broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
        mReceiver = new SimWifiP2pBroadcastReceiver(VisualizarCiclistasMapa.this);
        ContextCompat.registerReceiver(this, mReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
        //


        Intent intent = new Intent(this, WifiService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        guiUpdateInitState();
        findViewById(R.id.enviarMensagemBtn).setOnClickListener(listenerSendButton);
        findViewById(R.id.desconectarBtn).setOnClickListener(listenerDisconnectButton);
        findViewById(R.id.enviarPontosBtn).setOnClickListener(listenerSendPointsButton);

    }

    @Override
    protected void onStart() {
        super.onStart();
        //Guardando o contexto da Actividade Actual e o TextView para a visualização de mensagens recebidas
        ActivityContextProvider.setCurrentActivity(VisualizarCiclistasMapa.this);
        ActivityContextProvider.setOutputMsg(findViewById(R.id.outputMsg));
        //
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mReceiver != null) {
            try {
                unregisterReceiver(mReceiver);
            } catch (IllegalArgumentException e) {
                // O receptor não estava registrado, você pode ignorar isso ou lidar com o erro de outra maneira
                e.printStackTrace();
            }
        }
    }


    public void irVisualizarMapa(View view) {
        listenerDisconnectButton.onClick(findViewById(R.id.desconectarBtn));
        finish();
    }

    public void procurarDispositivosProximos(View view) {
        mWifiService.getManager().requestGroupInfo(mWifiService.getmChannel(), VisualizarCiclistasMapa.this); //pedir a lista de dispositivos mais próximos
    }

    private void guiUpdateInitState() {

        listaObjectosCiclistas.setVisibility(View.VISIBLE);

        inputMsg = (EditText) findViewById(R.id.inputMsg);
        inputMsg.setVisibility(View.GONE);

        outputMsg = (TextView) findViewById(R.id.outputMsg);
        outputMsg.setVisibility(View.GONE);
        outputMsg.setText("");

        findViewById(R.id.enviarPontosBtn).setEnabled(false);
        findViewById(R.id.procurarDispositivosBtn).setEnabled(true);
        findViewById(R.id.enviarMensagemBtn).setEnabled(false);
        findViewById(R.id.desconectarBtn).setEnabled(false);
        findViewById(R.id.inputMsg).setEnabled(false);
        findViewById(R.id.iconeConta).setEnabled(true);
    }


    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {

    }

    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList simWifiP2pDeviceList, SimWifiP2pInfo simWifiP2pInfo) {

        nomeDispositivoActual = simWifiP2pInfo.getDeviceName();

        listaCiclistas = new ArrayList<>();
        // compile list of devices in range
        for (String deviceName : simWifiP2pInfo.getDevicesInNetwork()) {
            SimWifiP2pDevice device = simWifiP2pDeviceList.getByName(deviceName);
            String devstr = "" + device.deviceName + " (" + device.getVirtIp() + ")\n";
            if (!device.getVirtIp().equals("0.0.0.0")) {
                listaCiclistas.add(new Utilizador("", devstr, device.getVirtIp()));
            }
        }

        ListaCiclistasMapaAdapter adapter = new ListaCiclistasMapaAdapter(VisualizarCiclistasMapa.this, listaCiclistas);
        listaObjectosCiclistas.setAdapter(adapter);

        listaObjectosCiclistas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                listaObjectosCiclistas.setVisibility(View.GONE);
                outputMsg.setVisibility(View.VISIBLE);
                inputMsg.setVisibility(View.VISIBLE);
                inputMsg.setHint("Digite uma mensagem");
                new OutgoingCommTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, listaCiclistas.get(position).getSenha());

            }
        });

    }

    private View.OnClickListener listenerSendButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (inputMsg.getText().toString().isEmpty()) {
                Toast.makeText(VisualizarCiclistasMapa.this, "Digite uma mensagem , campo vazio", Toast.LENGTH_SHORT).show();
            } else {
                enviarMsgOuPontos = true;
                new SendCommTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, inputMsg.getText().toString());
            }
        }
    };

    private View.OnClickListener listenerDisconnectButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mCliSocket != null) {
                try {
                    mCliSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mCliSocket = null;
            guiUpdateInitState();
        }
    };


    private View.OnClickListener listenerSendPointsButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            enviarMsgOuPontos = false;
            EditText input = new EditText(VisualizarCiclistasMapa.this);
            input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL); // Permite números e ponto/virgula

            // Criação do AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(VisualizarCiclistasMapa.this);
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

                        SharedPreferencesUtil.getUtilizador(VisualizarCiclistasMapa.this, new SharedPreferencesUtil.UtilizadorCallback() {
                            @Override
                            public void onSuccess(Utilizador utilizador) {
                                if (utilizador.getPontos() < Double.valueOf(inputText)) {
                                    Toast.makeText(VisualizarCiclistasMapa.this, "Não foi possível enviar a quantidade de pontos desejada , pontos insuficientes", Toast.LENGTH_LONG).show();
                                } else if (Double.valueOf(inputText) < 0) {
                                    Toast.makeText(VisualizarCiclistasMapa.this, "Não foi possível enviar a quantidade de pontos desejada , quantidade incorrecta (negativa)", Toast.LENGTH_LONG).show();
                                } else {
                                    new SendCommTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, inputText);
                                }
                            }
                        });


                    } else {
                        Toast.makeText(VisualizarCiclistasMapa.this, "Porfavor insira o número de pontos que pretende enviar", Toast.LENGTH_SHORT).show();
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
    };


    public class OutgoingCommTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            outputMsg.setText("Connecting...");
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                if (mCliSocket == null || mCliSocket.isClosed()) {
                    mCliSocket = new SimWifiP2pSocket(params[0], Integer.parseInt(getString(R.string.port)));
                }
                //cria um novo socket de comunicação usando o Wi-Fi Direct (a classe SimWifiP2pSocket). Esse socket se conecta ao dispositivo especificado no campo de entrada (com o nome ou IP) e à porta definida no arquivo de recursos (R.string.port).
            } catch (UnknownHostException e) {
                return "Unknown Host:" + e.getMessage();
            } catch (IOException e) {
                return "IO error:" + e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                guiUpdateInitState();
                outputMsg.setText(result);
            } else {
                findViewById(R.id.desconectarBtn).setEnabled(true);
                findViewById(R.id.enviarPontosBtn).setEnabled(true);
                findViewById(R.id.enviarMensagemBtn).setEnabled(true);
                findViewById(R.id.inputMsg).setEnabled(true);
                findViewById(R.id.procurarDispositivosBtn).setEnabled(false);
                findViewById(R.id.iconeConta).setEnabled(false);
                inputMsg.setHint("Digite uma mensagem");
                inputMsg.setText("");
                outputMsg.setText("");
            }
        }
    }


    public class SendCommTask extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... msg) {
            try {
                if (mCliSocket != null && !mCliSocket.isClosed()) {

                    if (enviarMsgOuPontos) {

                        Log.d(TAG, "Enviando mensagem: " + msg[0]);
                        outputMsg.append("Enviado: " + msg[0] + "\n\n");
                        mCliSocket.getOutputStream().write((msg[0] + "\n").getBytes());

                    } else {
                        Log.d(TAG, "Enviando Pontos: " + msg[0]);

                        SharedPreferencesUtil.getUtilizador(VisualizarCiclistasMapa.this, new SharedPreferencesUtil.UtilizadorCallback() {
                            @Override
                            public void onSuccess(Utilizador utilizador) {
                                //
                                Utilizador utilizadorActual = utilizador;
                                utilizadorActual.setPontos(utilizadorActual.getPontos() - Double.valueOf(msg[0]));
                                SharedPreferencesUtil.saveUtilizador(VisualizarCiclistasMapa.this, utilizadorActual);

                                utilizadorApi.save(utilizadorActual).enqueue(new Callback<Utilizador>() {
                                    @Override
                                    public void onResponse(Call<Utilizador> call, Response<Utilizador> response) {

                                        if (response.isSuccessful() && response.body() != null) {

                                            runOnUiThread(() -> {
                                                Toast.makeText(VisualizarCiclistasMapa.this, "Pontos atualizados com sucesso", Toast.LENGTH_SHORT).show();
                                                Toast.makeText(VisualizarCiclistasMapa.this, "Pontos enviados com sucesso", Toast.LENGTH_SHORT).show();
                                            });

                                        } else {
                                            runOnUiThread(() -> {
                                                Toast.makeText(VisualizarCiclistasMapa.this, "Erro ao atualizar pontos usuario de origem " + response.code(), Toast.LENGTH_SHORT).show();
                                            });
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Utilizador> call, Throwable throwable) {
                                        runOnUiThread(() -> {
                                            Toast.makeText(VisualizarCiclistasMapa.this, "Erro de rede ao atualizar pontos usuario de origem", Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                });
                                //
                                //envioPontos#@12 é apenas um sinal para identificar que essa mensagem está a ser enviada com a finalidade de mandar pontos

                            }
                        });

                        mCliSocket.getOutputStream().write(("envioPontos#@12," + msg[0] + "," + nomeDispositivoActual + "\n").getBytes());

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "errooo enviar mensagem: ");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            inputMsg.setText("");
            inputMsg.setHint("Digite uma mensagem");
        }
    }


}
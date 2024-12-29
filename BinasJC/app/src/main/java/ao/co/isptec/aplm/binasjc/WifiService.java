package ao.co.isptec.aplm.binasjc;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.os.Messenger;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ao.co.isptec.aplm.binasjc.model.Utilizador;
import ao.co.isptec.aplm.binasjc.retrofit.RetrofitService;
import ao.co.isptec.aplm.binasjc.retrofit.UtilizadorApi;
import ao.co.isptec.aplm.binasjc.shared.SharedPreferencesUtil;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//Serviço que  inicializa o gerenciamento de sockets para comunicação via Wi-Fi Direct utilizando a biblioteca SimWifiP2p
//responsável por vincular a app aos métodos de SimWifiP2PService
//obtendo assim o manager , channel , etc
//esse serviço também é responsável por executar uma tarefa assíncrona que permite captar pontos vindos de outros dispositivos via wifi-direct ou captar mensagens

public class WifiService extends Service {

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public WifiService getService() {
            return WifiService.this;
        }
    }

    public static final String TAG = "AppWifi";
    private RetrofitService retrofitService;
    private UtilizadorApi utilizadorApi;

    private SimWifiP2pManager mManager;
    private SimWifiP2pManager.Channel mChannel;
    private Messenger mService;
    private boolean mBound = false;
    private SimWifiP2pSocketServer mSrvSocket;

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            mManager = new SimWifiP2pManager(mService);
            mChannel = mManager.initialize(getApplicationContext(), Looper.getMainLooper(), null);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
            mManager = null;
            mChannel = null;
            mBound = false;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        SimWifiP2pSocketManager.Init(getApplicationContext());

        // Conecte-se ao serviço SimWifiP2p
        Intent intent = new Intent(getApplicationContext(), SimWifiP2pService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        retrofitService = new RetrofitService();
        utilizadorApi = retrofitService.getRetrofit().create(UtilizadorApi.class);

        // Inicie a AsyncTask para escutar conexões
        new IncommingCommTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBound) {
            unbindService(mConnection);
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public SimWifiP2pManager getManager() {
        return mManager;
    }

    public SimWifiP2pManager.Channel getmChannel() {
        return mChannel;
    }

    public class IncommingCommTask extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG, "IncommingCommTask started (" + this.hashCode() + ").");

            try {
                mSrvSocket = new SimWifiP2pSocketServer(Integer.parseInt(getString(R.string.port)));
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (mSrvSocket != null) {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        SimWifiP2pSocket sock = mSrvSocket.accept();
                        Log.d(TAG, "Conexão estabelecida!");

                        BufferedReader sockIn = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                        String message;

                        while ((message = sockIn.readLine()) != null) {

                            if (message.contains("envioPontos#@12")) {
                                String[] msgs = message.split(",");

                                SharedPreferencesUtil.getUtilizador(getApplicationContext(), new SharedPreferencesUtil.UtilizadorCallback() {
                                    @Override
                                    public void onSuccess(Utilizador utilizador) {
                                        double pontosRecebidos = Double.valueOf(msgs[1]);
                                        String dispositivoQueEnviou = msgs[2];

                                        Utilizador utilizadorActual = utilizador;

                                        utilizadorActual.setPontos(utilizadorActual.getPontos() + pontosRecebidos);
                                        SharedPreferencesUtil.saveUtilizador(getApplicationContext(), utilizadorActual);

                                        utilizadorApi.save(utilizadorActual).enqueue(new Callback<Utilizador>() {
                                            @Override
                                            public void onResponse(Call<Utilizador> call, Response<Utilizador> response) {

                                                if (response.isSuccessful() && response.body() != null) {

                                                    new AlertDialog.Builder(new ContextThemeWrapper(ActivityContextProvider.getCurrentActivity(), androidx.appcompat.R.style.Theme_AppCompat))
                                                            .setTitle("Recebimento de pontos")
                                                            .setMessage("Recebeu " + pontosRecebidos + " pontos de " + dispositivoQueEnviou)
                                                            .setNeutralButton("Dismiss", (dialog, which) -> {
                                                                // Fechar o diálogo
                                                            })
                                                            .show();


                                                } else {
                                                    Log.d("Erro", "Erro ao atualizar pontos usuário de destino: ");
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<Utilizador> call, Throwable throwable) {
                                                Log.d("Erro", "Erro de rede ao atualizar pontos usuário de destino");
                                            }
                                        });
                                    }
                                });



                            } else {
                                Log.d(TAG, "Mensagem recebida: " + message);
                                publishProgress(message);
                            }

                        }

                    } catch (IOException e) {
                        Log.d("Erro no socket:", e.getMessage());
                        break;
                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if (ActivityContextProvider.getOutputMsg() != null) {
                ActivityContextProvider.getOutputMsg().append("Recebido: " + values[0] + "\n\n");
            }
        }

    }
}
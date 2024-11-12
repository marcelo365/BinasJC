package ao.co.isptec.aplm.binasjc;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.logging.Level;
import java.util.logging.Logger;

import ao.co.isptec.aplm.binasjc.model.Bicicleta;
import ao.co.isptec.aplm.binasjc.model.ReservaBicicleta;
import ao.co.isptec.aplm.binasjc.model.Utilizador;
import ao.co.isptec.aplm.binasjc.retrofit.BicicletaApi;
import ao.co.isptec.aplm.binasjc.retrofit.EstacaoApi;
import ao.co.isptec.aplm.binasjc.retrofit.ReservaBicicletaApi;
import ao.co.isptec.aplm.binasjc.retrofit.RetrofitService;
import ao.co.isptec.aplm.binasjc.retrofit.UtilizadorApi;
import ao.co.isptec.aplm.binasjc.shared.SharedPreferencesUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisualizarBicicleta extends AppCompatActivity {

    private TextView nomeUsuario;
    private Utilizador utilizadorActual;
    private Bicicleta bicicletaVisualizar;
    private TextView nomeBicicleta;
    private TextView nomeEstacao;
    private TextView disponibilidade;

    private RetrofitService retrofitService;
    private ReservaBicicletaApi reservaBicicletaApi;
    private BicicletaApi bicicletaApi;
    private AppCompatButton reservarCancelarBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_visualizar_bicicleta);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        retrofitService = new RetrofitService();
        reservaBicicletaApi = retrofitService.getRetrofit().create(ReservaBicicletaApi.class);
        bicicletaApi = retrofitService.getRetrofit().create(BicicletaApi.class);

        bicicletaVisualizar = (Bicicleta) getIntent().getSerializableExtra("bicicleta");
        nomeUsuario = findViewById(R.id.nomeUsuarioVisualizarBicicleta);
        nomeEstacao = findViewById(R.id.nomeEstacaoVisualizarBicicleta);
        disponibilidade = findViewById(R.id.disponibilidade);
        nomeBicicleta = findViewById(R.id.nomeBicicleta);
        reservarCancelarBtn = findViewById(R.id.reservarCancelarBtn);

        nomeBicicleta.setText(bicicletaVisualizar.getNome());

        utilizadorActual = SharedPreferencesUtil.getUtilizador(getApplicationContext());
        if (utilizadorActual != null) {
            nomeUsuario.setText(utilizadorActual.getUsername());
        }

        nomeEstacao.setText(bicicletaVisualizar.getEstacao().getNome());
        disponibilidade.setText((bicicletaVisualizar.getDisponibilidade() == 1) ? "Disponível" : "Indisponível");
        disponibilidade.setTextColor((bicicletaVisualizar.getDisponibilidade() == 1) ? getResources().getColor(R.color.verde) : getResources().getColor(R.color.vermelho));


        //

        SharedPreferencesUtil.verificarBicicletaReservada(getApplicationContext(), new SharedPreferencesUtil.ReservaBicicletaCallback() {
            @Override
            public void onReturn(ReservaBicicleta reservaBicicleta) {

                if (reservaBicicleta == null) {
                    reservarCancelarBtn.setText("Reservar");
                    reservarCancelarBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            reservarBicicleta(v);
                        }
                    });

                } else if (reservaBicicleta.getBicicleta().getId() == bicicletaVisualizar.getId()) {
                    reservarCancelarBtn.setText("Cancelar Reserva");
                    reservarCancelarBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cancelarReservaBicicleta(v);
                        }
                    });

                } else {
                    reservarCancelarBtn.setText("Reservar");
                    reservarCancelarBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            reservarBicicleta(v);
                        }
                    });
                }
            }
        });

        //


    }

    public void voltar(View view) {
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent); // Define o resultado como OK
        finish(); // Finaliza a atividade
    }

    public void reservarBicicleta(View view) {

        SharedPreferencesUtil.verificarBicicletaReservada(getApplicationContext(), new SharedPreferencesUtil.ReservaBicicletaCallback() {
            @Override
            public void onReturn(ReservaBicicleta reservaBicicleta) {

                if (reservaBicicleta == null) {

                    bicicletaVisualizar.setDisponibilidade(0);
                    bicicletaApi.save(bicicletaVisualizar).enqueue(new Callback<Bicicleta>() {
                        @Override
                        public void onResponse(Call<Bicicleta> call, Response<Bicicleta> response) {

                            if (response.isSuccessful() && response.body() != null) {

                                //

                                reservaBicicletaApi.save(new ReservaBicicleta(bicicletaVisualizar, utilizadorActual)).enqueue(new Callback<ReservaBicicleta>() {
                                    @Override
                                    public void onResponse(Call<ReservaBicicleta> call, Response<ReservaBicicleta> response) {

                                        if (response.isSuccessful() && response.body() != null) {
                                            Toast.makeText(VisualizarBicicleta.this, "Reserva de Bicicleta concluída", Toast.LENGTH_SHORT).show();
                                            voltar(view);
                                        } else {
                                            Toast.makeText(VisualizarBicicleta.this, "Erro ao salvar reserva de bicicleta " + response.code(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ReservaBicicleta> call, Throwable throwable) {
                                        Toast.makeText(VisualizarBicicleta.this, "Erro de rede ao salvar reserva da bicicleta", Toast.LENGTH_SHORT).show();
                                        Logger.getLogger(VisualizarBicicleta.class.getName()).log(Level.SEVERE, "Error ocurred", throwable);
                                    }
                                });

                                //

                            } else {
                                Toast.makeText(VisualizarBicicleta.this, "Erro ao alterar disponibilidade da bicicleta" + response.code(), Toast.LENGTH_SHORT).show();
                            }


                        }

                        @Override
                        public void onFailure(Call<Bicicleta> call, Throwable throwable) {
                            Toast.makeText(VisualizarBicicleta.this, "Erro de rede ao alterar disponibilidade da bicicleta", Toast.LENGTH_SHORT).show();
                            Logger.getLogger(VisualizarBicicleta.class.getName()).log(Level.SEVERE, "Error ocurred", throwable);
                        }
                    });


                } else {
                    Toast.makeText(VisualizarBicicleta.this, "Você já possui uma bicicleta reservada , não pode reservar outra", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    public void cancelarReservaBicicleta(View view) {

        SharedPreferencesUtil.verificarBicicletaReservada(getApplicationContext(), new SharedPreferencesUtil.ReservaBicicletaCallback() {
            @Override
            public void onReturn(ReservaBicicleta reservaBicicleta) {

                if (reservaBicicleta != null) {

                    bicicletaVisualizar.setDisponibilidade(1);
                    bicicletaApi.save(bicicletaVisualizar).enqueue(new Callback<Bicicleta>() {
                        @Override
                        public void onResponse(Call<Bicicleta> call, Response<Bicicleta> response) {

                            if (response.isSuccessful() && response.body() != null) {

                                //
                                reservaBicicleta.setEstado(0);
                                reservaBicicletaApi.save(reservaBicicleta).enqueue(new Callback<ReservaBicicleta>() {
                                    @Override
                                    public void onResponse(Call<ReservaBicicleta> call, Response<ReservaBicicleta> response) {

                                        if (response.isSuccessful() && response.body() != null) {
                                            Toast.makeText(VisualizarBicicleta.this, "Cancelamento da reserva de bicicleta concluída", Toast.LENGTH_SHORT).show();
                                            voltar(view);
                                        } else {
                                            Toast.makeText(VisualizarBicicleta.this, "Erro ao cancelar reserva de bicicleta " + response.code(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ReservaBicicleta> call, Throwable throwable) {
                                        Toast.makeText(VisualizarBicicleta.this, "Erro de rede ao cancelar reserva da bicicleta", Toast.LENGTH_SHORT).show();
                                        Logger.getLogger(VisualizarBicicleta.class.getName()).log(Level.SEVERE, "Error ocurred", throwable);
                                    }
                                });

                                //

                            } else {
                                Toast.makeText(VisualizarBicicleta.this, "Erro ao alterar disponibilidade da bicicleta" + response.code(), Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<Bicicleta> call, Throwable throwable) {
                            Toast.makeText(VisualizarBicicleta.this, "Erro de rede ao alterar disponibilidade da bicicleta", Toast.LENGTH_SHORT).show();
                            Logger.getLogger(VisualizarBicicleta.class.getName()).log(Level.SEVERE, "Error ocurred", throwable);
                        }
                    });

                }

            }
        });

    }


}
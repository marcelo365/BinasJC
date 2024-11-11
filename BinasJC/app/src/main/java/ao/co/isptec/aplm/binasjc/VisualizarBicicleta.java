package ao.co.isptec.aplm.binasjc;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import ao.co.isptec.aplm.binasjc.model.Bicicleta;
import ao.co.isptec.aplm.binasjc.model.Utilizador;
import ao.co.isptec.aplm.binasjc.shared.SharedPreferencesUtil;

public class VisualizarBicicleta extends AppCompatActivity {

    private TextView nomeUsuario;
    private Utilizador utilizadorActual;
    private TextView nomeBicicleta;
    private TextView nomeEstacao;
    private TextView disponibilidade;

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

        Bicicleta bicicleta = (Bicicleta) getIntent().getSerializableExtra("bicicleta");
        nomeUsuario = findViewById(R.id.nomeUsuarioVisualizarBicicleta);
        nomeEstacao = findViewById(R.id.nomeEstacaoVisualizarBicicleta);
        disponibilidade = findViewById(R.id.disponibilidade);
        nomeBicicleta = findViewById(R.id.nomeBicicleta);
        nomeBicicleta.setText(bicicleta.getNome());

        utilizadorActual = SharedPreferencesUtil.getUtilizador(getApplicationContext());
        if (utilizadorActual != null) {
            nomeUsuario.setText(utilizadorActual.getUsername());
        }

        nomeEstacao.setText(bicicleta.getEstacao().getNome());
        disponibilidade.setText((bicicleta.getDisponibilidade() == 1) ? "Disponível" : "Indisponível");
        disponibilidade.setTextColor((bicicleta.getDisponibilidade() == 1) ? getResources().getColor(R.color.verde) : getResources().getColor(R.color.vermelho));

    }

    public void voltar(View view) {
        finish();
    }
}
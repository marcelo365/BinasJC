package ao.co.isptec.aplm.binasjc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class VisualizarCiclistasMapa extends AppCompatActivity {

    private ListView listaObjectosCiclistas;
    private ArrayList<Usuario> listaCiclistas;

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

        listaObjectosCiclistas = findViewById(R.id.listaObjectosCiclistas);
        listaCiclistas = new ArrayList<>();
        listaCiclistas.add(new Usuario("Marcelo Rocha"));
        listaCiclistas.add(new Usuario("Hélder da Silva"));
        listaCiclistas.add(new Usuario("Ernesto Amândio"));
        ListaCiclistasMapaAdapter adapter = new ListaCiclistasMapaAdapter(VisualizarCiclistasMapa.this, listaCiclistas);
        listaObjectosCiclistas.setAdapter(adapter);
    }

    public void irVisualizarMapa(View view) {
        finish();
    }

    public void irTelaMensagens(View view) {
        Intent intent = new Intent(this , TelaMensagens.class);
        startActivity(intent);
    }
}
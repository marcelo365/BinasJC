package ao.co.isptec.aplm.binasjc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class VisualizarEstacao extends AppCompatActivity {


    private ArrayList<Bicicleta> listaBicicletas;
    private ListView listaObjectos;
    private TextView nomeEstacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_visualizar_estacao);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Estacao estacao = (Estacao) getIntent().getSerializableExtra("estacao");
        listaObjectos = findViewById(R.id.listaTrajectos);
        nomeEstacao = findViewById(R.id.nomeBicicleta);
        nomeEstacao.setText(estacao.getNome().toString());


        listaBicicletas = new ArrayList<>();
        listaBicicletas.add(new Bicicleta("BMX-270" , "Andromeda"));
        listaBicicletas.add(new Bicicleta("Track 90-0" , "Andromeda"));
        listaBicicletas.add(new Bicicleta("Bib 89-98" , "Andromeda"));
        ListaBicicletasAdapter adapter = new ListaBicicletasAdapter(VisualizarEstacao.this , listaBicicletas);
        listaObjectos.setAdapter(adapter);


        listaObjectos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(VisualizarEstacao.this , VisualizarBicicleta.class);
                intent.putExtra("bicicleta", listaBicicletas.get(position));
                startActivity(intent);
            }
        });




    }
}
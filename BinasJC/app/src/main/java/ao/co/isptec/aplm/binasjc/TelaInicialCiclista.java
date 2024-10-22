package ao.co.isptec.aplm.binasjc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class TelaInicialCiclista extends AppCompatActivity {

    private AppCompatButton btnEstacoes;
    private AppCompatButton btnCiclistas;
    private AppCompatButton btnBicicletas;
    private EditText searchInput;


    private ArrayList<Estacao> listaEstacoes;
    private ArrayList<Bicicleta> listaBicicletas;
    private ArrayList<Usuario> listaCiclistas;
    private ListView listaObjectos;

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

        listaObjectos = findViewById(R.id.listaTrajectos);
        btnBicicletas = findViewById(R.id.btnBicicletas);
        btnCiclistas = findViewById(R.id.btnCiclistas);
        btnEstacoes = findViewById(R.id.btnEstacoes);
        searchInput = findViewById(R.id.searchInput);
        irEstacoes(null);
    }

    public void irEstacoes(View view) {

        listaEstacoes = new ArrayList<>();
        listaEstacoes.add(new Estacao("Andromeda", 13, 89));
        listaEstacoes.add(new Estacao("Saturno", 13, 89));
        listaEstacoes.add(new Estacao("Marte", 13, 89));
        ListaEstacoesAdapter adapter = new ListaEstacoesAdapter(TelaInicialCiclista.this, listaEstacoes);
        listaObjectos.setAdapter(adapter);

        btnEstacoes.setBackgroundTintList(getResources().getColorStateList(R.color.azulFundoBotaoClicado, getTheme()));
        btnCiclistas.setBackgroundTintList(null);
        btnBicicletas.setBackgroundTintList(null);
        searchInput.setHint(R.string.p_estacao);

        listaObjectos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TelaInicialCiclista.this, VisualizarEstacao.class);
                intent.putExtra("estacao", listaEstacoes.get(position));
                startActivity(intent);
            }
        });

    }


    public void irBicicletas(View view) {
        listaBicicletas = new ArrayList<>();
        listaBicicletas.add(new Bicicleta("BMX-270", "Andromeda"));
        listaBicicletas.add(new Bicicleta("Track 78", "Andromeda"));
        listaBicicletas.add(new Bicicleta("HJi 89", "Andromeda"));
        ListaBicicletasAdapter adapter = new ListaBicicletasAdapter(TelaInicialCiclista.this, listaBicicletas);
        listaObjectos.setAdapter(adapter);

        btnBicicletas.setBackgroundTintList(getResources().getColorStateList(R.color.azulFundoBotaoClicado, getTheme()));
        btnCiclistas.setBackgroundTintList(null);
        btnEstacoes.setBackgroundTintList(null);
        searchInput.setHint(R.string.p_bicicleta);

        listaObjectos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TelaInicialCiclista.this, VisualizarBicicleta.class);
                intent.putExtra("bicicleta", listaBicicletas.get(position));
                startActivity(intent);
            }
        });

    }

    public void irCiclistas(View view) {
        listaCiclistas = new ArrayList<>();
        listaCiclistas.add(new Usuario("Marcelo Rocha"));
        listaCiclistas.add(new Usuario("Hélder da Silva"));
        listaCiclistas.add(new Usuario("Ernesto Amândio"));
        ListaCiclistasAdapter adapter = new ListaCiclistasAdapter(TelaInicialCiclista.this, listaCiclistas);
        listaObjectos.setAdapter(adapter);

        btnCiclistas.setBackgroundTintList(getResources().getColorStateList(R.color.azulFundoBotaoClicado, getTheme()));
        btnBicicletas.setBackgroundTintList(null);
        btnEstacoes.setBackgroundTintList(null);
        searchInput.setHint(R.string.p_ciclista);

        listaObjectos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TelaInicialCiclista.this, VisualizarCiclista.class);
                intent.putExtra("ciclista", listaCiclistas.get(position));
                startActivity(intent);
            }
        });

    }


    public void irMinhaConta(View view){
        Intent intent = new Intent(this , MinhaConta.class);
        startActivity(intent);
    }


}
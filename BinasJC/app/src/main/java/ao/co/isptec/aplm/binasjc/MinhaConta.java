package ao.co.isptec.aplm.binasjc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MinhaConta extends AppCompatActivity {

    private ListView listaObjectosTrajectos;
    private ArrayList<Trajectoria> listaTrajectorias;

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


        listaObjectosTrajectos = findViewById(R.id.listaMensagens);

        listaTrajectorias = new ArrayList<>();
        listaTrajectorias.add(new Trajectoria("09/08/2028" , 4.90f));
        listaTrajectorias.add(new Trajectoria("08/08/2023" , 9.1f));
        listaTrajectorias.add(new Trajectoria("01/04/2020" , 9.8f));
        ListaTrajectoriasAdapter adapter = new ListaTrajectoriasAdapter(  MinhaConta.this, listaTrajectorias);
        listaObjectosTrajectos.setAdapter(adapter);

        listaObjectosTrajectos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MinhaConta.this, VisualizarTrajectoria.class);
                intent.putExtra("trajectoria", listaTrajectorias.get(position));
                startActivity(intent);
            }
        });


    }

    public void voltar(View view){
        finish();
    }

}
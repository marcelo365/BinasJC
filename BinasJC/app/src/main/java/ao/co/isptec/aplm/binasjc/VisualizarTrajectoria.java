package ao.co.isptec.aplm.binasjc;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import ao.co.isptec.aplm.binasjc.model.Utilizador;
import ao.co.isptec.aplm.binasjc.shared.SharedPreferencesUtil;

public class VisualizarTrajectoria extends AppCompatActivity implements OnMapReadyCallback {

    private SupportMapFragment mapFragment;
    private TextView nomeUsuario;
    private Utilizador utilizadorActual;

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

        utilizadorActual = SharedPreferencesUtil.getUtilizador(getApplicationContext());
        if (utilizadorActual != null) {
            nomeUsuario.setText(utilizadorActual.getUsername());
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


    }
}
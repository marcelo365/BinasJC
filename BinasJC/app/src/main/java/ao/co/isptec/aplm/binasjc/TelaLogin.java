package ao.co.isptec.aplm.binasjc;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TelaLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView linkRegistrar = findViewById(R.id.linkRegistar);
        linkRegistrar.setText(Html.fromHtml("<u>Registe-se</u>"));

    }

    public void irRegistrar(View view){
        Intent intent = new Intent(this , TelaRegisto.class);
        startActivity(intent);
    }


    public void irUsuario(View view){
        Intent intent = new Intent(this , TelaInicialCiclista.class);
        startActivity(intent);
    }

}
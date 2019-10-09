package com.mvelasquezp.clife.clientes.registro;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import com.mvelasquezp.clife.clientes.R;

public class PoliticaDatos extends AppCompatActivity implements View.OnClickListener {

    WebView PoliticaPrivacidadWebview;
    Button PoliticaPrivacidadAcepta, PoliticaPrivacidadRechaza;

    String rucdni, puntos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_politica_datos);
        IniciarComponentes();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.PoliticaPrivacidadAcepta:
                new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Política de privacidad")
                    .setMessage("Por favor, confirma que estás de acuerdo con nuestra Política de protección de datos personales para continuar.")
                    .setPositiveButton("Estoy de acuerdo", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent IntentDatosContacto = new Intent(PoliticaDatos.this, DatosContacto.class);
                                IntentDatosContacto.putExtra("rucdni", rucdni);
                                IntentDatosContacto.putExtra("puntos", puntos);
                            startActivity(IntentDatosContacto);
                            finish();
                        }

                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
                break;
            case R.id.PoliticaPrivacidadRechaza:
                new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Política de privacidad")
                    .setMessage("No has aceptado la política de privacidad. ¿Deseas salir de Life App?")
                    .setPositiveButton("Si, salir", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
                break;
            default: break;
        }
    }

    //metodos

    private void IniciarComponentes() {
        PoliticaPrivacidadWebview = (WebView) findViewById(R.id.PoliticaPrivacidadWebview);
        PoliticaPrivacidadAcepta = (Button) findViewById(R.id.PoliticaPrivacidadAcepta);
        PoliticaPrivacidadRechaza = (Button) findViewById(R.id.PoliticaPrivacidadRechaza);
        //listeners
        PoliticaPrivacidadAcepta.setOnClickListener(this);
        PoliticaPrivacidadRechaza.setOnClickListener(this);
        //datos
        rucdni = getIntent().getStringExtra("rucdni");
        puntos = getIntent().getStringExtra("puntos");
        //WebView
        PoliticaPrivacidadWebview.loadUrl("http://webpedidos.corporacionlife.com.pe/app-clientes/extras/politica-privacidad");
    }
}

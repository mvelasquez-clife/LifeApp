package com.mvelasquezp.clife.clientes.registro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mvelasquezp.clife.clientes.R;
import com.mvelasquezp.clife.clientes.activities.HTMLViewer;
import com.mvelasquezp.clife.clientes.activities.Login;
import com.mvelasquezp.clife.clientes.tools.Constantes;
import com.mvelasquezp.clife.clientes.ws.Asynchtask;
import com.mvelasquezp.clife.clientes.ws.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class IngresaRucDni extends AppCompatActivity implements View.OnClickListener, Asynchtask {

    EditText RegistroRucDniNumero;
    ImageButton RegistroRucDniBusca;
    LinearLayout RegistroClienteEncontrado, RegistroClienteExiste, RegistroClienteNoEncontrado;
    TextView RegistroRazonSocial, RegistroDireccion;
    Button RegistroIniciar, RegistroLogin, RegistroCancela;

    String RucDniCliente, ParticipaPuntos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_registro_rucdni);
        IniciarComponentes();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.RegistroRucDniBusca:
                ValidarRucDni();
                break;
            case R.id.RegistroIniciar:
                Intent IntentPoliticaDatos = new Intent(IngresaRucDni.this, PoliticaDatos.class);
                    IntentPoliticaDatos.putExtra("rucdni", RucDniCliente);
                    IntentPoliticaDatos.putExtra("puntos", ParticipaPuntos);
                startActivity(IntentPoliticaDatos);
                finish();
                break;
            case R.id.RegistroLogin:
                finish();
                break;
            case R.id.RegistroCancela:
                finish();
                break;
            default: break;
        }
    }

    @Override
    public void processFinish(String result) {
        JSONObject json;
        try {
            json = new JSONObject(result);
            if(json.getBoolean(Constantes.WS_STATE_PARAM)) {
                switch(json.getInt(Constantes.WS_REQUEST_ID)) {
                    case Constantes.WS_REQUEST_RUCDNI_CLIENTE:
                        int encontrado = json.getJSONObject(Constantes.WS_DATA_ATTR).getInt("encontrado");
                        if(encontrado == 0) {
                            JSONObject jsCliente = json.getJSONObject(Constantes.WS_DATA_ATTR).getJSONObject("cliente");
                            RucDniCliente = jsCliente.getString("rucdni");
                            ParticipaPuntos = jsCliente.getString("puntos");
                            RegistroRazonSocial.setText(jsCliente.getString("rzsocial"));
                            RegistroDireccion.setText(jsCliente.getString("direccion"));
                            RegistroClienteEncontrado.setVisibility(View.VISIBLE);
                        }
                        else {
                            RegistroClienteExiste.setVisibility(View.VISIBLE);
                        }
                        break;
                    default: break;
                }
            }
            else {
                Toast.makeText(this, json.getString(Constantes.WS_MESSAGE), Toast.LENGTH_LONG).show();
            }
        }
        catch(JSONException e) {
            Log.e(Constantes.APP_NAME, e.getLocalizedMessage());
            Intent WebViewIntent = new Intent(this, HTMLViewer.class);
                WebViewIntent.putExtra("html", result);
            startActivity(WebViewIntent);
        }
    }

    //metodos

    private void IniciarComponentes() {
        RegistroRucDniNumero = (EditText) findViewById(R.id.RegistroRucDniNumero);
        RegistroRucDniBusca = (ImageButton) findViewById(R.id.RegistroRucDniBusca);
        RegistroClienteEncontrado = (LinearLayout) findViewById(R.id.RegistroClienteEncontrado);
        RegistroClienteExiste = (LinearLayout) findViewById(R.id.RegistroClienteExiste);
        RegistroClienteNoEncontrado = (LinearLayout) findViewById(R.id.RegistroClienteNoEncontrado);
        RegistroRazonSocial = (TextView) findViewById(R.id.RegistroRazonSocial);
        RegistroDireccion = (TextView) findViewById(R.id.RegistroDireccion);
        RegistroIniciar = (Button) findViewById(R.id.RegistroIniciar);
        RegistroLogin = (Button) findViewById(R.id.RegistroLogin);
        RegistroCancela = (Button) findViewById(R.id.RegistroCancela);
        //eventos
        RegistroRucDniBusca.setOnClickListener(IngresaRucDni.this);
        RegistroIniciar.setOnClickListener(IngresaRucDni.this);
        RegistroLogin.setOnClickListener(IngresaRucDni.this);
        RegistroCancela.setOnClickListener(IngresaRucDni.this);
    }

    private void ValidarRucDni() {
        Map<String, String> post = new HashMap<String, String>();
            post.put("rucdni", RegistroRucDniNumero.getText().toString());
        WsValidarRucDni(post);
    }

    //llamadas al ws

    private void WsValidarRucDni(Map<String, String> post) {
        String URL = getResources().getString(R.string.base_url) + getResources().getString(R.string.validar_ruc_cliente);
        WebService ws = new WebService(URL, post, IngresaRucDni.this, IngresaRucDni.this);
        ws.execute();
    }
}

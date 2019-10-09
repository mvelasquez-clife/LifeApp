package com.mvelasquezp.clife.clientes.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mvelasquezp.clife.clientes.R;
import com.mvelasquezp.clife.clientes.session.SessionHelper;
import com.mvelasquezp.clife.clientes.tools.Constantes;
import com.mvelasquezp.clife.clientes.ws.Asynchtask;
import com.mvelasquezp.clife.clientes.ws.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class InicioLocalNuevo extends AppCompatActivity implements View.OnClickListener, Asynchtask {

    SessionHelper sHelper;

    EditText NvoLocalNombre, NvoLocalDireccion;
    Button NvoDepBtnGuardar, NvoDepCancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_inicio_local_nuevo);
        IniciarComponentes();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.NvoDepBtnGuardar:
                if(ValidarDatosLocal()) {
                    GuardarDatosLocal();
                }
                else {
                    Toast.makeText(InicioLocalNuevo.this, "Ingrese correctamente el nombre y dirección del local", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.NvoDepCancelar:
                setResult(RESULT_CANCELED);
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
                    case Constantes.WS_REQUEST_REGISTRA_LOCAL:
                        Toast.makeText(InicioLocalNuevo.this, "¡Local registrado!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                        break;
                    default: break;
                }
            }
            else {
                Toast.makeText(InicioLocalNuevo.this, json.getString(Constantes.WS_MESSAGE), Toast.LENGTH_LONG).show();
            }
        }
        catch(JSONException e) {
            Log.e(Constantes.APP_NAME,e.getLocalizedMessage());
            Intent WebViewIntent = new Intent(this, HTMLViewer.class);
            WebViewIntent.putExtra("html", result);
            startActivity(WebViewIntent);
        }
    }

    //metodos

    private void IniciarComponentes() {
        sHelper = new SessionHelper(InicioLocalNuevo.this);
        //
        NvoLocalNombre = (EditText) findViewById(R.id.NvoLocalNombre);
        NvoLocalDireccion = (EditText) findViewById(R.id.NvoLocalDireccion);
        NvoDepBtnGuardar = (Button) findViewById(R.id.NvoDepBtnGuardar);
        NvoDepCancelar = (Button) findViewById(R.id.NvoDepCancelar);
        NvoDepBtnGuardar.setOnClickListener(InicioLocalNuevo.this);
        NvoDepCancelar.setOnClickListener(InicioLocalNuevo.this);
    }

    private boolean ValidarDatosLocal() {
        return !NvoLocalNombre.getText().toString().isEmpty() && !NvoLocalDireccion.getText().toString().isEmpty();
    }

    private void GuardarDatosLocal() {
        Map<String, String> post = new HashMap<String, String>();
            post.put("salon", String.valueOf(sHelper.getSesion().getCodigo()));
            post.put("nombre", NvoLocalNombre.getText().toString());
            post.put("direccion", NvoLocalDireccion.getText().toString());
            post.put("latitud", String.valueOf(Constantes.NU_lATITUD_CLIFE));
            post.put("longitud", String.valueOf(Constantes.NU_LONGITUD_CLIFE));
        WsGuardarDatosLocal(post);
    }

    //ws

    private void WsGuardarDatosLocal(Map<String, String> post) {
        String URL = getResources().getString(R.string.base_url) + getResources().getString(R.string.registra_local);
        WebService ws = new WebService(URL, post, InicioLocalNuevo.this, InicioLocalNuevo.this);
        ws.execute();
    }
}

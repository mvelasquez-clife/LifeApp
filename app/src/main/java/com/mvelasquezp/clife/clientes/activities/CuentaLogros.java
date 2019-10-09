package com.mvelasquezp.clife.clientes.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mvelasquezp.clife.clientes.R;
import com.mvelasquezp.clife.clientes.entities.DboLogro;
import com.mvelasquezp.clife.clientes.layouts.Logro;
import com.mvelasquezp.clife.clientes.layouts.Separator;
import com.mvelasquezp.clife.clientes.session.SessionHelper;
import com.mvelasquezp.clife.clientes.tools.Constantes;
import com.mvelasquezp.clife.clientes.ws.Asynchtask;
import com.mvelasquezp.clife.clientes.ws.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CuentaLogros extends AppCompatActivity implements Asynchtask {

    SessionHelper sHelper;

    TextView CtaLogrosNombre;
    LinearLayout CtaLogrosContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_cuenta_logros);
        IniciarComponentes();
    }

    @Override
    public void processFinish(String result) {
        JSONObject json;
        try {
            json = new JSONObject(result);
            if(json.getBoolean(Constantes.WS_STATE_PARAM)) {
                switch(json.getInt(Constantes.WS_REQUEST_ID)) {
                    case Constantes.WS_REQUEST_LISTA_LOGROS:
                        JSONArray jsLogros = json.getJSONObject(Constantes.WS_DATA_ATTR).getJSONArray("logros");
                        EscribirListaPromociones(jsLogros);
                        break;
                    case Constantes.WS_REQUEST_RECLAMA_LOGRO:
                        CargarListaLogros();
                        Toast.makeText(CuentaLogros.this, "¡Puntos recibidos!", Toast.LENGTH_SHORT).show();
                        break;
                    default: break;
                }
            }
            else {
                Toast.makeText(CuentaLogros.this, json.getString(Constantes.WS_MESSAGE), Toast.LENGTH_LONG).show();
            }
        }
        catch(JSONException e) {
            Intent WebViewIntent = new Intent(CuentaLogros.this, HTMLViewer.class);
Log.e(Constantes.APP_NAME, e.getLocalizedMessage());
            WebViewIntent.putExtra("html", result);
            startActivity(WebViewIntent);
        }
    }

    //metodos

    private void EscribirListaPromociones(JSONArray logros) throws JSONException {
        CtaLogrosContainer.removeAllViews();
        int numLogros = logros.length();
        for(int i = 0; i < numLogros; i++) {
            if(i > 0) CtaLogrosContainer.addView(new Separator(CuentaLogros.this));
            JSONObject iLogro = logros.getJSONObject(i);
            final Logro v = new Logro(CuentaLogros.this);
                v.AsignarLogro(iLogro);
                v.AsignarEvento(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DboLogro dboLogro = v.getLogro();
                        ReclamarLogro(dboLogro.getId());
                    }
                });
            CtaLogrosContainer.addView(v);
        }
    }

    private void IniciarComponentes() {
        CtaLogrosNombre  = (TextView) findViewById(R.id.CtaLogrosNombre);
        CtaLogrosContainer = (LinearLayout) findViewById(R.id.CtaLogrosContainer);
        sHelper = new SessionHelper(CuentaLogros.this);
        CtaLogrosNombre.setText(sHelper.getSesion().getRsocial());
        CargarListaLogros();
    }

    private void CargarListaLogros() {
        Map<String, String> post = new HashMap<String, String>();
            post.put("cliente", sHelper.getSesion().getCodigo());
            post.put("empresa", String.valueOf(sHelper.getSesion().getEmpresa()));
            post.put("tipo", sHelper.getSesion().getTpusuario());
        WsIniciarComponentes(post);
    }

    private void ReclamarLogro(int logro) {
        Map<String, String> post = new HashMap<String, String>();
            post.put("logro", String.valueOf(logro));
            post.put("cliente", sHelper.getSesion().getCodigo());
            post.put("empresa", String.valueOf(sHelper.getSesion().getEmpresa()));
            post.put("tipo", sHelper.getSesion().getTpusuario());
        WsReclamarLogro(post);
    }

    //llaados ws

    private void WsIniciarComponentes(Map<String, String> post) {
        String URL = getResources().getString(R.string.base_url) + getResources().getString(R.string.lista_logros);
        WebService ws = new WebService(URL, post, CuentaLogros.this, CuentaLogros.this);
        ws.execute();
    }

    private void WsReclamarLogro(Map<String, String> post) {
        String URL = getResources().getString(R.string.base_url) + getResources().getString(R.string.reclama_logro);
        WebService ws = new WebService(URL, post, CuentaLogros.this, CuentaLogros.this);
        ws.execute();
    }
}

package com.mvelasquezp.clife.clientes.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mvelasquezp.clife.clientes.R;
import com.mvelasquezp.clife.clientes.layouts.ItemLista;
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

public class InicioLocales extends AppCompatActivity implements View.OnClickListener, Asynchtask {

    SessionHelper sHelper;

    TextView InicioLocalesNombre;
    ImageButton InicioLocalesAtras, InicioLocalesNuevo;
    LinearLayout InicioLocalesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_inicio_locales);
        IniciarComponentes();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.InicioLocalesAtras:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.InicioLocalesNuevo:
                Intent IntentNuevoLocal = new Intent(InicioLocales.this, InicioLocalNuevo.class);
                startActivityForResult(IntentNuevoLocal, Constantes.REQUEST_INICIO_LOCAL_NUEVO);
                break;
            default: break;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constantes.REQUEST_INICIO_LOCAL_NUEVO) {
            if(resultCode == RESULT_OK) {
                CargarLocales();
            }
        }
    }

    @Override
    public void processFinish(String result) {
        JSONObject json;
        try {
            json = new JSONObject(result);
            if(json.getBoolean(Constantes.WS_STATE_PARAM)) {
                switch(json.getInt(Constantes.WS_REQUEST_ID)) {
                    case Constantes.WS_REQUEST_LISTA_LOCALES:
                        JSONArray jsLocales = json.getJSONObject(Constantes.WS_DATA_ATTR).getJSONArray("locales");
                        EscribirListaLocales(jsLocales);
                        break;
                    default: break;
                }
            }
            else {
                Toast.makeText(InicioLocales.this, json.getString(Constantes.WS_MESSAGE), Toast.LENGTH_LONG).show();
            }
        }
        catch(JSONException e) {
            Intent WebViewIntent = new Intent(this, HTMLViewer.class);
            WebViewIntent.putExtra("html", result);
            startActivity(WebViewIntent);
        }
    }

    //metodos

    private void IniciarComponentes() {
        InicioLocalesNombre = (TextView) findViewById(R.id.InicioLocalesNombre);
        InicioLocalesAtras = (ImageButton) findViewById(R.id.InicioLocalesAtras);
        InicioLocalesNuevo = (ImageButton) findViewById(R.id.InicioLocalesNuevo);
        InicioLocalesContainer = (LinearLayout) findViewById(R.id.InicioLocalesContainer);
        sHelper = new SessionHelper(InicioLocales.this);
        InicioLocalesNombre.setText(sHelper.getSesion().getRsocial());
        //listeners
        InicioLocalesAtras.setOnClickListener(this);
        InicioLocalesNuevo.setOnClickListener(this);
        //
        CargarLocales();
    }

    private void EscribirListaLocales(JSONArray arrLocales) throws JSONException {
        InicioLocalesContainer.removeAllViews();
        int numLocales = arrLocales.length();
        for(int i = 0; i < numLocales; i++) {
            if(i > 0) InicioLocalesContainer.addView(new Separator(InicioLocales.this));
            JSONObject iLocal = arrLocales.getJSONObject(i);
            final int cLocal = Integer.parseInt(iLocal.getString("local"));
            final String cNombre = iLocal.getString("nombre");
            String descripcion = "Registrado el " + iLocal.getString("registro");
            ItemLista item = new ItemLista(InicioLocales.this);
                item.setImageVisible(false);
                item.setLabels(cNombre, "", descripcion);
                item.setOnClickListenerEvent(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent IntentDependientesLocal = new Intent(InicioLocales.this, InicioLocalesDependientes.class);
                            IntentDependientesLocal.putExtra("codigo", cLocal);
                            IntentDependientesLocal.putExtra("local", cNombre);
                        startActivityForResult(IntentDependientesLocal, Constantes.REQUEST_INICIO_DEPENDIENTES_LOCAL);
                    }
                });
            InicioLocalesContainer.addView(item);
        }
    }

    private void CargarLocales() {
        Map<String, String> post = new HashMap<String, String>();
            post.put("salon", String.valueOf(sHelper.getSesion().getCodigo()));
        WsCargarLocales(post);
    }

    //ws

    private void WsCargarLocales(Map<String, String> post) {
        String URL = getResources().getString(R.string.base_url) + getResources().getString(R.string.lista_locales);
        WebService ws = new WebService(URL, post, InicioLocales.this, InicioLocales.this);
        ws.execute();
    }
}

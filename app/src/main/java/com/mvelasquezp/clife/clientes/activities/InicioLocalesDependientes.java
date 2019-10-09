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

public class InicioLocalesDependientes extends AppCompatActivity implements Asynchtask, View.OnClickListener {

    int CodigoLocal;
    String NombreLocal;
    SessionHelper sHelper;

    TextView InicioLocalesDependientesNombre;
    ImageButton InicioLocalesDependientesAtras, InicioLocalesDependientesNuevo;
    LinearLayout InicioLocalesDependientesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_inicio_locales_dependientes);
        IniciarComponentes();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.InicioLocalesDependientesAtras:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.InicioLocalesDependientesNuevo:
                Intent IntentNuevoDependiente = new Intent(InicioLocalesDependientes.this, InicioLocalNuevoDependiente.class);
                startActivityForResult(IntentNuevoDependiente, Constantes.REQUEST_INICIO_DEPENDIENTES_NUEVO);
                break;
            default: break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constantes.REQUEST_INICIO_DEPENDIENTES_NUEVO) {
            if(resultCode == RESULT_OK) {
                CargarDependientesLocal();
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
                    case Constantes.WS_REQUEST_LISTA_DEPENDIENTES_LOCAL:
                        JSONArray jsDependientes = json.getJSONObject(Constantes.WS_DATA_ATTR).getJSONArray("dependientes");
                        EscribirListaDependientes(jsDependientes);
                        break;
                    default: break;
                }
            }
            else {
                Toast.makeText(InicioLocalesDependientes.this, json.getString(Constantes.WS_MESSAGE), Toast.LENGTH_LONG).show();
            }
        }
        catch(JSONException e) {
            Log.e(Constantes.APP_NAME,e.getLocalizedMessage());
            /*Intent WebViewIntent = new Intent(this, HTMLViewer.class);
            WebViewIntent.putExtra("html", result);
            startActivity(WebViewIntent);*/
        }
    }

    //metodos

    private void IniciarComponentes() {
        sHelper = new SessionHelper(InicioLocalesDependientes.this);
        Intent back = getIntent();
        CodigoLocal = back.getIntExtra("codigo",0);
        NombreLocal = back.hasExtra("local") ? back.getStringExtra("local") : "Todos los locales";
        //
        InicioLocalesDependientesNombre = (TextView) findViewById(R.id.InicioLocalesDependientesNombre);
        InicioLocalesDependientesAtras = (ImageButton) findViewById(R.id.InicioLocalesDependientesAtras);
        InicioLocalesDependientesNuevo = (ImageButton) findViewById(R.id.InicioLocalesDependientesNuevo);
        InicioLocalesDependientesContainer = (LinearLayout) findViewById(R.id.InicioLocalesDependientesContainer);
        InicioLocalesDependientesAtras.setOnClickListener(InicioLocalesDependientes.this);
        InicioLocalesDependientesNuevo.setOnClickListener(InicioLocalesDependientes.this);
        InicioLocalesDependientesNombre.setText(NombreLocal);
        //
        CargarDependientesLocal();
    }

    private void EscribirListaDependientes(JSONArray jsDependientes) throws JSONException {
        InicioLocalesDependientesContainer.removeAllViews();
        int numDependientes = jsDependientes.length();
        for(int i = 0; i < numDependientes; i++) {
            if(i > 0) InicioLocalesDependientesContainer.addView(new Separator(InicioLocalesDependientes.this));
            JSONObject iDependiente = jsDependientes.getJSONObject(i);
                final int cCodigo = Integer.parseInt(iDependiente.getString("codigo"));
                final String iNombre = iDependiente.getString("dependiente");
                final String iNlocal = iDependiente.getString("nlocal");
                final String iTelefono = "Tlf. " + iDependiente.getString("telefono");
                final int cLocal = Integer.parseInt(iDependiente.getString("clocal"));
            ItemLista item = new ItemLista(InicioLocalesDependientes.this);
                item.setLabels(iNombre, "", iNlocal + " - " + iTelefono);
                item.setImageVisible(true);
                item.setOnClickListenerEvent(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent IntentDetalleDependiente = new Intent(InicioLocalesDependientes.this, InicioLocalesDependientesDetalle.class);
                            IntentDetalleDependiente.putExtra("salon", String.valueOf(sHelper.getSesion().getCodigo()));
                            IntentDetalleDependiente.putExtra("local", cLocal);
                            IntentDetalleDependiente.putExtra("dependiente", cCodigo);
                        startActivity(IntentDetalleDependiente);
                    }
                });
                //item.setButtonVisible(false);
            InicioLocalesDependientesContainer.addView(item);
        }
    }

    private void CargarDependientesLocal() {
        Map<String, String> post = new HashMap<String, String>();
            post.put("salon", String.valueOf(sHelper.getSesion().getCodigo()));
            post.put("local", String.valueOf(CodigoLocal));
        WsCargarDependientesLocal(post);
    }

    //ws

    private void WsCargarDependientesLocal(Map<String, String> post) {
        String URL = getResources().getString(R.string.base_url) + getResources().getString(R.string.lista_dependientes_local);
        WebService ws = new WebService(URL, post, InicioLocalesDependientes.this, InicioLocalesDependientes.this);
        ws.execute();
    }
}

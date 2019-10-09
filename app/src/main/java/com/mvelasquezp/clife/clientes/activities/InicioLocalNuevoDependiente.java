package com.mvelasquezp.clife.clientes.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.mvelasquezp.clife.clientes.R;
import com.mvelasquezp.clife.clientes.session.SessionHelper;
import com.mvelasquezp.clife.clientes.tools.Constantes;
import com.mvelasquezp.clife.clientes.tools.LinkedHashMapAdapter;
import com.mvelasquezp.clife.clientes.ws.Asynchtask;
import com.mvelasquezp.clife.clientes.ws.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class InicioLocalNuevoDependiente extends AppCompatActivity implements View.OnClickListener, Asynchtask {

    SessionHelper sHelper;

    Spinner NvoDepLocal;
    ImageView NvoDepImagenPerfil;
    ImageButton NvoDepEditarImg;
    EditText NvoDepDni, NvoDepNombres, NvoDepApepat, NvoDepApemat, NvoDepEmail, NvoDepTelefono;
    Button NvoDepBtnGuardar, NvoDepCancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_inicio_local_ndependiente);
        InitComponents();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.NvoDepBtnGuardar:
                if(ValidarFormulario()) {
                    RegistraDependiente();
                }
                else {
                    Toast.makeText(InicioLocalNuevoDependiente.this, "Ingrese los datos correctamente", Toast.LENGTH_SHORT).show();
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
                    case Constantes.WS_REQUEST_LISTA_LOCALES:
                        JSONArray jsLocales = json.getJSONObject(Constantes.WS_DATA_ATTR).getJSONArray("locales");
                        EscribirListaLocales(jsLocales);
                        break;
                    case Constantes.WS_REQUEST_REGISTRA_DEPENDIENTE:
                        Toast.makeText(InicioLocalNuevoDependiente.this, "¡Dependiente registrado!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                        break;
                    default: break;
                }
            }
            else {
                Toast.makeText(InicioLocalNuevoDependiente.this, json.getString(Constantes.WS_MESSAGE), Toast.LENGTH_LONG).show();
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

    private void InitComponents() {
        sHelper = new SessionHelper(InicioLocalNuevoDependiente.this);
        NvoDepLocal = (Spinner) findViewById(R.id.NvoDepLocal);
        NvoDepImagenPerfil = (ImageView) findViewById(R.id.NvoDepImagenPerfil);
        NvoDepEditarImg = (ImageButton) findViewById(R.id.NvoDepEditarImg);
        NvoDepDni = (EditText) findViewById(R.id.NvoDepDni);
        NvoDepNombres = (EditText) findViewById(R.id.NvoDepNombres);
        NvoDepApepat = (EditText) findViewById(R.id.NvoDepApepat);
        NvoDepApemat = (EditText) findViewById(R.id.NvoDepApemat);
        NvoDepEmail = (EditText) findViewById(R.id.NvoDepEmail);
        NvoDepTelefono = (EditText) findViewById(R.id.NvoDepTelefono);
        NvoDepBtnGuardar = (Button) findViewById(R.id.NvoDepBtnGuardar);
        NvoDepCancelar = (Button) findViewById(R.id.NvoDepCancelar);
        NvoDepBtnGuardar.setOnClickListener(this);
        NvoDepCancelar.setOnClickListener(this);
        //
        CargarLocales();
    }

    private boolean ValidarFormulario() {
        Pattern MailPattern = Pattern.compile("^.+@.+\\..+$");
        boolean IsOk = NvoDepDni.getText().toString().length() <= 8
                && MailPattern.matcher(NvoDepEmail.getText().toString()).find()
                && (NvoDepTelefono.getText().toString().length() == 7 || NvoDepTelefono.getText().toString().length() == 9)
                && !NvoDepNombres.getText().toString().isEmpty()
                && !NvoDepApepat.getText().toString().isEmpty()
                && !NvoDepApemat.getText().toString().isEmpty();
        return IsOk;
    }

    private void EscribirListaLocales(JSONArray jsLocales) throws JSONException {
        int numLocales = jsLocales.length();
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        for(int i = 0; i < numLocales; i++) {
            JSONObject iLocal = jsLocales.getJSONObject(i);
            map.put(iLocal.getString("local"), iLocal.getString("nombre"));
        }
        LinkedHashMapAdapter<String, String> adapter = new LinkedHashMapAdapter<String, String>(getApplicationContext(), R.layout.spinner, map);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        NvoDepLocal.setAdapter(adapter);
    }

    private void RegistraDependiente() {
        Map.Entry<String, String> item = (Map.Entry<String, String>) NvoDepLocal.getSelectedItem();
        Map<String, String> post = new HashMap<String, String>();
            post.put("salon", String.valueOf(sHelper.getSesion().getCodigo()));
            post.put("local", item.getKey());
            post.put("dni", NvoDepDni.getText().toString());
            post.put("apepat", NvoDepApepat.getText().toString());
            post.put("apemat", NvoDepApemat.getText().toString());
            post.put("nombres", NvoDepNombres.getText().toString());
            post.put("mail", NvoDepEmail.getText().toString());
            post.put("telefono", NvoDepTelefono.getText().toString());
        WsRegistraDependiente(post);
    }

    private void CargarLocales() {
        Map<String, String> post = new HashMap<String, String>();
            post.put("salon", String.valueOf(sHelper.getSesion().getCodigo()));
        WsCargarLocales(post);
    }

    //ws

    private void WsRegistraDependiente(Map<String, String> post) {
        String URL = getResources().getString(R.string.base_url) + getResources().getString(R.string.registra_dependiente);
        WebService ws = new WebService(URL, post, InicioLocalNuevoDependiente.this, InicioLocalNuevoDependiente.this);
        ws.execute();
    }

    private void WsCargarLocales(Map<String, String> post) {
        String URL = getResources().getString(R.string.base_url) + getResources().getString(R.string.lista_locales);
        WebService ws = new WebService(URL, post, InicioLocalNuevoDependiente.this, InicioLocalNuevoDependiente.this);
        ws.execute();
    }
}

package com.mvelasquezp.clife.clientes.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mvelasquezp.clife.clientes.R;
import com.mvelasquezp.clife.clientes.dependientes.IngresoDependiente;
import com.mvelasquezp.clife.clientes.dependientes.InicioAdministrador;
import com.mvelasquezp.clife.clientes.dependientes.InicioDependiente;
import com.mvelasquezp.clife.clientes.registro.IngresaRucDni;
import com.mvelasquezp.clife.clientes.session.Sesion;
import com.mvelasquezp.clife.clientes.session.SessionHelper;
import com.mvelasquezp.clife.clientes.tools.AppHelper;
import com.mvelasquezp.clife.clientes.tools.Constantes;
import com.mvelasquezp.clife.clientes.ws.Asynchtask;
import com.mvelasquezp.clife.clientes.ws.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements View.OnClickListener, Asynchtask {

    Button lgnIngresar, lgnDependiente;
    TextView lgnClave, lgnNuevo;
    EditText lgnEtUsuario, lgnEtClave;

    AppHelper helper;
    SessionHelper sHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        //
        IniciarComponentes();
    }

    private void IniciarComponentes() {
        sHelper = new SessionHelper(Login.this);
        if(sHelper.SesionIniciada()) {
            LanzarActividadPrincipal();
        }
        else {
            //
            lgnIngresar = (Button) findViewById(R.id.lgnIngresar);
            lgnDependiente = (Button) findViewById(R.id.lgnDependiente);
            lgnClave = (TextView) findViewById(R.id.lgnClave);
            lgnNuevo = (TextView) findViewById(R.id.lgnNuevo);
            lgnEtUsuario = (EditText) findViewById(R.id.lgnEtUsuario);
            lgnEtClave = (EditText) findViewById(R.id.lgnEtClave);
            //
            lgnIngresar.setOnClickListener(this);
            lgnDependiente.setOnClickListener(this);
            lgnClave.setOnClickListener(this);
            lgnNuevo.setOnClickListener(this);
            //
            helper = new AppHelper();
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.lgnIngresar:
                ValidarUsuario();
                break;
            case R.id.lgnNuevo:
                /*Intent IntentPrueba = new Intent(this, Prueba.class);
                startActivity(IntentPrueba);*/
                Intent IntentRucDni = new Intent(Login.this, IngresaRucDni.class);
                startActivity(IntentRucDni);
                break;
            case R.id.lgnDependiente:
                Intent IntentDependiente = new Intent(Login.this, IngresoDependiente.class);
                startActivity(IntentDependiente);
                finish();
                break;
        }
    }

    @Override
    public void processFinish(String result) {
        JSONObject json;
        try {
            json = new JSONObject(result);
            if(json.getBoolean(Constantes.WS_STATE_PARAM)) {
                switch(json.getInt(Constantes.WS_REQUEST_ID)) {
                    case Constantes.WS_REQUEST_LOGIN:
                        JSONObject usuario = json.getJSONObject(Constantes.WS_DATA_ATTR).getJSONObject("usuario");
                        String token = json.getJSONObject(Constantes.WS_DATA_ATTR).getString("token");
                        Sesion usesion = new Sesion();
                            usesion.setCodigo(usuario.getString("co_rucdni"));
                            usesion.setEmpresa(Integer.parseInt(usuario.getString("co_empresa")));
                            usesion.setNcomercial(usuario.getString("de_nombre_comercial"));
                            usesion.setRsocial(usuario.getString("de_razon_social"));
                            usesion.setFregistro(helper.StringToDate(usuario.getString("fe_suscripcion"),Constantes.DATETIME_FORMAT));
                            usesion.setEmail(usuario.getString("de_email"));
                            usesion.setTelefono(usuario.getString("de_telefono"));
                            usesion.setToken(token);
                            usesion.setTpusuario("S");
                            usesion.setPuntos_habilitados(usuario.getString("st_puntos").equals("S"));
                            usesion.setLocal(0);
                        sHelper.saveSession(usesion);
                        LanzarActividadPrincipal();
                        break;
                    default: break;
                }
            }
            else {
                Toast.makeText(this, json.getString(Constantes.WS_MESSAGE), Toast.LENGTH_LONG).show();
                lgnIngresar.setEnabled(true);
            }
        }
        catch(JSONException e) {
Log.e(Constantes.APP_NAME, e.getLocalizedMessage());
            Intent WebViewIntent = new Intent(this, HTMLViewer.class);
            WebViewIntent.putExtra("html", result);
            startActivity(WebViewIntent);
            lgnIngresar.setEnabled(true);
        }
    }

    //metodos

    private void LanzarActividadPrincipal() {
        Intent IntentMain;
        String tpUsuario = sHelper.getSesion().getTpusuario();
        switch(tpUsuario) {
            case "S":
                IntentMain = new Intent(Login.this, Main.class);
                startActivity(IntentMain);
                break;
            case "A":
                IntentMain = new Intent(Login.this, InicioAdministrador.class);
                startActivity(IntentMain);
                break;
            case "D":
                IntentMain = new Intent(Login.this, InicioDependiente.class);
                startActivity(IntentMain);
                break;
            default:
                IntentMain = new Intent(Login.this, HTMLViewer.class);
                IntentMain.putExtra("html","Error...");
                startActivity(IntentMain);
                break;
        }
        finish();
    }

    private void ValidarUsuario() {
        String usuario = lgnEtUsuario.getText().toString();
        String clave = lgnEtClave.getText().toString();
        Map<String, String> post = new HashMap<String, String>();
            post.put("co_cliente", usuario);
            post.put("co_empresa", "11");
            post.put("password", clave);
        DoLogin(post);
    }

    //ws

    public void DoLogin(Map<String, String> post) {
        String URL = getResources().getString(R.string.base_url) + getResources().getString(R.string.login);
Log.d(Constantes.APP_NAME, URL);
        WebService ws = new WebService(URL, post, Login.this, Login.this);
        ws.execute();
    }
}

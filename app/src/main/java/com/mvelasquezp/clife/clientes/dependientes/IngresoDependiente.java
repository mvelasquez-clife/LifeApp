package com.mvelasquezp.clife.clientes.dependientes;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mvelasquezp.clife.clientes.R;
import com.mvelasquezp.clife.clientes.activities.HTMLViewer;
import com.mvelasquezp.clife.clientes.frg_dependiente.Inicio;
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

public class IngresoDependiente extends AppCompatActivity implements View.OnClickListener, Asynchtask {

    Button DepIngresoCapturar, DepIngresoSalir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_dep_ingreso);
        //
        IniciarComponentes();
    }

    @Override
    public void processFinish(String result) {
        JSONObject json;
        try {
            json = new JSONObject(result);
            if(json.getBoolean(Constantes.WS_STATE_PARAM)) {
                switch(json.getInt(Constantes.WS_REQUEST_ID)) {
                    case Constantes.WS_REQUEST_AUTH_DEPENDIENTE:
                        JSONObject usuario = json.getJSONObject(Constantes.WS_DATA_ATTR).getJSONObject("dependiente");
                        SessionHelper sHelper = new SessionHelper(IngresoDependiente.this);
                        AppHelper helper = new AppHelper();
                        String key = json.getJSONObject(Constantes.WS_DATA_ATTR).getString("key");
                        Sesion usesion = new Sesion();
                            usesion.setCodigo(usuario.getString("salon"));
                            usesion.setDependiente(usuario.getString("dependiente"));
                            usesion.setEmpresa(Integer.parseInt(usuario.getString("empresa")));
                            usesion.setNcomercial(usuario.getString("nlocal"));
                            usesion.setRsocial(usuario.getString("nombres") + " " + usuario.getString("apellido_p") + " " + usuario.getString("apellido_m"));
                            usesion.setFregistro(helper.StringToDate(usuario.getString("fregistro"),Constantes.DATETIME_FORMAT));
                            usesion.setEmail(usuario.getString("email"));
                            usesion.setTelefono(usuario.getString("telefono"));
                            usesion.setTpusuario(usuario.getString("tipo"));
                            usesion.setSalon(usuario.getString("salon"));
                            usesion.setLocal(Integer.parseInt(usuario.getString("clocal")));
                            usesion.setToken(key);
                        sHelper.saveSession(usesion);
                        //AQUI LANZA LA PANTALLA PRINCIPAL
                        if(usesion.getTpusuario().equals("D")) {
                            Intent IntentInicio = new Intent(IngresoDependiente.this, InicioDependiente.class);
                            startActivity(IntentInicio);
                        }
                        else { //o sea, usesion.getTpusuario().equals("A") XD
                            Intent IntentAdministrador = new Intent(IngresoDependiente.this, InicioAdministrador.class);
                            startActivity(IntentAdministrador);
                        }
                        finish();
                        break;
                    default: break;
                }
            }
            else {
                Toast.makeText(this, json.getString(Constantes.WS_MESSAGE), Toast.LENGTH_LONG).show();
            }
        }
        catch(JSONException e) {
            Intent WebViewIntent = new Intent(this, HTMLViewer.class);
            WebViewIntent.putExtra("html", result);
            startActivity(WebViewIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constantes.PERMISSION_IMAGES: {
                int grantSize = grantResults.length;
                if (grantSize > 0) {
                    boolean ok = true;
                    for(int i = 0; i < grantSize; i++) {
                        ok = ok && grantResults[i] == PackageManager.PERMISSION_GRANTED;
                    }
                    if(ok) {
                        LeerCodigoQr();
                    }
                }
                else {
                    Toast.makeText(IngresoDependiente.this, "Necesita proporcionar los permisos para acceder a la cámara y galería de imágenes para continuar", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constantes.REQUEST_QR_INTENT) {
            if (resultCode == RESULT_OK) {
                String key = data.getStringExtra(Constantes.QR_ACTION_RESULT);
                //String format = data.getStringExtra("SCAN_RESULT_FORMAT");
                AutenticarDependiente(key);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.DepIngresoCapturar:
                VerificaPermisosImagen();
                break;
            case R.id.DepIngresoSalir:
                finish();
                break;
            default: break;
        }
    }

    //metodos

    private void IniciarComponentes() {
        DepIngresoCapturar = (Button) findViewById(R.id.DepIngresoCapturar);
        DepIngresoSalir = (Button) findViewById(R.id.DepIngresoSalir);
        //
        DepIngresoCapturar.setOnClickListener(IngresoDependiente.this);
        DepIngresoSalir.setOnClickListener(IngresoDependiente.this);
    }

    private void VerificaPermisosImagen() {
        boolean READ_EXTERNAL_STORAGE_PERMISSION = ContextCompat.checkSelfPermission(IngresoDependiente.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean WRITE_EXTERNAL_STORAGE_PERMISSION = ContextCompat.checkSelfPermission(IngresoDependiente.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean CAMERA_PERMISSION = ContextCompat.checkSelfPermission(IngresoDependiente.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        if(!READ_EXTERNAL_STORAGE_PERMISSION || !WRITE_EXTERNAL_STORAGE_PERMISSION || !CAMERA_PERMISSION) {
            String[] ArrayPermisos = { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA };
            ActivityCompat.requestPermissions(this, ArrayPermisos, Constantes.PERMISSION_IMAGES);
        }
        else {
            LeerCodigoQr();
        }
    }

    private void LeerCodigoQr() {
        try {
            Intent intent = new Intent(Constantes.QR_PACKAGE_NAME);
                //intent.putExtra(Constantes.QR_ACTION_NAME, Constantes.QR_ACTION_VALUE);
            startActivityForResult(intent, Constantes.REQUEST_QR_INTENT);
        }
        catch (Exception e) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("LifeApp")
                    .setMessage("Se requiere un app adicional para leer códigos QR.")
                    .setPositiveButton("Descargar QR Code Scanner", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Uri marketUri = Uri.parse(Constantes.QR_MARKET_URI);
                            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                            startActivity(marketIntent);
                        }

                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        }
    }

    private void AutenticarDependiente(String key) {
        Map<String, String> post = new HashMap<String, String>();
            post.put("key", key);
        WsAutenticarDependiente(post);
    }

    //ws

    public void WsAutenticarDependiente(Map<String, String> post) {
        String URL = getResources().getString(R.string.base_url) + getResources().getString(R.string.auth_dependiente);
Log.d(Constantes.APP_NAME, URL);
Log.d(Constantes.APP_NAME, post.toString());
        WebService ws = new WebService(URL, post, IngresoDependiente.this, IngresoDependiente.this);
        ws.execute();
    }
}

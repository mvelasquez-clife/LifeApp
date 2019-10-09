package com.mvelasquezp.clife.clientes.activities;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class PuntosAsignar extends AppCompatActivity implements View.OnClickListener, Asynchtask {

    Button AsignaPuntosDepCapturar, AsignaPuntosDepSalir;
    SessionHelper sessionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_puntos_asignar);
        IniciarComponentes();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.AsignaPuntosDepCapturar:
                VerificaPermisosImagen();
                break;
            case R.id.AsignaPuntosDepSalir:
                setResult(RESULT_CANCELED);
                finish();
                break;
            default: break;
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
                    Toast.makeText(PuntosAsignar.this, "Necesita proporcionar los permisos para acceder a la cámara y galería de imágenes para continuar", Toast.LENGTH_SHORT).show();
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
                CargarInfoDependiente(key);
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
                    case Constantes.WS_REQUEST_INFO_ASIGNAR_PTS_DEP:
                        String nombre = json.getJSONObject(Constantes.WS_DATA_ATTR).getString("nombre");
                        final String key = json.getJSONObject(Constantes.WS_DATA_ATTR).getString("key");
                        AlertDialog.Builder builder = new AlertDialog.Builder(PuntosAsignar.this);
                        builder.setTitle(nombre);
                        ViewGroup viewGroup = (ViewGroup) getWindow().getDecorView().getRootView();
                        View viewInflated = LayoutInflater.from(PuntosAsignar.this).inflate(R.layout.ctrl_number_picker,viewGroup,false);
                        final EditText CtrlNumberPickerInput = (EditText) viewInflated.findViewById(R.id.CtrlNumberPickerInput);
                        builder.setView(viewInflated);
                        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int PuntosParaAsignar = Integer.parseInt(CtrlNumberPickerInput.getText().toString());
                                dialog.dismiss();
                                AsignarPuntajeDependiente(key, PuntosParaAsignar);
                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                        break;
                    case Constantes.WS_REQUEST_ASIGNA_PTS_DEP:
                        Toast.makeText(PuntosAsignar.this, "¡Puntos asignados!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                        break;
                    default: break;
                }
            }
            else {
                Toast.makeText(PuntosAsignar.this, json.getJSONObject(Constantes.WS_DATA_ATTR).getString(Constantes.WS_MESSAGE), Toast.LENGTH_LONG).show();
            }
        }
        catch(JSONException e) {
            Intent WebViewIntent = new Intent(PuntosAsignar.this, HTMLViewer.class);
            WebViewIntent.putExtra("html", result);
            startActivity(WebViewIntent);
        }
    }

    //metodos

    private void IniciarComponentes() {
        sessionHelper = new SessionHelper(PuntosAsignar.this);
        AsignaPuntosDepCapturar = (Button) findViewById(R.id.AsignaPuntosDepCapturar);
        AsignaPuntosDepSalir = (Button) findViewById(R.id.AsignaPuntosDepSalir);
        AsignaPuntosDepCapturar.setOnClickListener(this);
        AsignaPuntosDepSalir.setOnClickListener(this);
    }



    private void VerificaPermisosImagen() {
        boolean READ_EXTERNAL_STORAGE_PERMISSION = ContextCompat.checkSelfPermission(PuntosAsignar.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean WRITE_EXTERNAL_STORAGE_PERMISSION = ContextCompat.checkSelfPermission(PuntosAsignar.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean CAMERA_PERMISSION = ContextCompat.checkSelfPermission(PuntosAsignar.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
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

    private void CargarInfoDependiente(String key) {
        Map<String, String> post = new HashMap<String, String>();
        post.put("key", key);
        WsCargarInfoDependiente(post);
    }

    private void AsignarPuntajeDependiente(String key, int puntaje) {
        Map<String, String> post = new HashMap<String, String>();
        post.put("key", key);
        post.put("puntos", String.valueOf(puntaje));
        post.put("codigo", String.valueOf(sessionHelper.getSesion().getCodigo()));
        WsAsignarPuntajeDependiente(post);
    }

    //ws

    private void WsCargarInfoDependiente(Map<String, String> post) {
        String URL = getResources().getString(R.string.base_url) + getResources().getString(R.string.info_asignar_puntos_dep);
        WebService ws = new WebService(URL, post, PuntosAsignar.this, PuntosAsignar.this);
        ws.execute();
    }

    private void WsAsignarPuntajeDependiente(Map<String, String> post) {
        String URL = getResources().getString(R.string.base_url) + getResources().getString(R.string.asignar_puntos_dependiente);
        WebService ws = new WebService(URL, post, PuntosAsignar.this, PuntosAsignar.this);
        ws.execute();
    }
}

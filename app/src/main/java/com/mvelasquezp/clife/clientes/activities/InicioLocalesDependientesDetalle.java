package com.mvelasquezp.clife.clientes.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.mvelasquezp.clife.clientes.R;
import com.mvelasquezp.clife.clientes.session.SessionHelper;
import com.mvelasquezp.clife.clientes.tools.Constantes;
import com.mvelasquezp.clife.clientes.ws.Asynchtask;
import com.mvelasquezp.clife.clientes.ws.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class InicioLocalesDependientesDetalle extends AppCompatActivity implements Asynchtask, View.OnClickListener {

    String salon;
    int local, dependiente;
    String key;
    SessionHelper sHelper;

    TextView DepDetalleNombre, DepDetalleDni, DepDetalleLocal, DepDetalleEmail, DepDetalleTelefono, DepDetalleTpusuario;
    ImageView DepDetalleQr;
    Button DepDetalleBtnVolver, DepDetalleAscender;
    RelativeLayout DepDetalleRlAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_inicio_locales_dep_detalle);
        IniciarComponentes();
    }

    @Override
    public void processFinish(String result) {
Log.d(Constantes.APP_NAME, result);
        JSONObject json, jsDependiente;
        try {
            json = new JSONObject(result);
            if(json.getBoolean(Constantes.WS_STATE_PARAM)) {
                switch(json.getInt(Constantes.WS_REQUEST_ID)) {
                    case Constantes.WS_REQUEST_DATOS_DEPENDIENTE:
                        jsDependiente = json.getJSONObject(Constantes.WS_DATA_ATTR).getJSONObject("datos");
                        MostrarDatosDependiente(jsDependiente);
                        break;
                    case Constantes.WS_REQUEST_ACTUALIZA_PRIVILEGIOS:
                        if(json.getJSONObject(Constantes.WS_DATA_ATTR).getString("result").equals("S")) {
                            jsDependiente = json.getJSONObject(Constantes.WS_DATA_ATTR).getJSONObject("datos");
                            MostrarDatosDependiente(jsDependiente);
                            Toast.makeText(InicioLocalesDependientesDetalle.this, "Permisos actualizados", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            new AlertDialog.Builder(this)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setTitle("Convertir en administrador")
                                    .setMessage("El local seleccionado ya posee un administrador. Si continúa, los permisos del administrador anterior serán cancelados y pasarán a el dependiente seleccionado. ¿Desea continuar?")
                                    .setPositiveButton("Si, continuar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ActualizarPermisosDependiente(true);
                                        }

                                    })
                                    .setNegativeButton("No", null)
                                    .show();
                        }
                        break;
                    default: break;
                }
            }
            else {
                Toast.makeText(InicioLocalesDependientesDetalle.this, json.getString(Constantes.WS_MESSAGE), Toast.LENGTH_LONG).show();
            }
        }
        catch(JSONException e) {
            Log.e(Constantes.APP_NAME,e.getLocalizedMessage());
            Intent WebViewIntent = new Intent(InicioLocalesDependientesDetalle.this, HTMLViewer.class);
            WebViewIntent.putExtra("html", result);
            startActivity(WebViewIntent);
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.DepDetalleBtnVolver:
                finish();
                break;
            case R.id.DepDetalleAscender:
                ConvertirEnAdministrador();
                break;
            default: break;
        }
    }

    //metodos

    private void IniciarComponentes() {
        sHelper = new SessionHelper(InicioLocalesDependientesDetalle.this);
        DepDetalleNombre = (TextView) findViewById(R.id.DepDetalleNombre);
        DepDetalleDni = (TextView) findViewById(R.id.DepDetalleDni);
        DepDetalleLocal = (TextView) findViewById(R.id.DepDetalleLocal);
        DepDetalleEmail = (TextView) findViewById(R.id.DepDetalleEmail);
        DepDetalleTelefono = (TextView) findViewById(R.id.DepDetalleTelefono);
        DepDetalleTpusuario = (TextView) findViewById(R.id.DepDetalleTpusuario);
        DepDetalleQr = (ImageView) findViewById(R.id.DepDetalleQr);
        DepDetalleBtnVolver = (Button) findViewById(R.id.DepDetalleBtnVolver);
        DepDetalleAscender = (Button) findViewById(R.id.DepDetalleAscender);
        DepDetalleRlAdmin = (RelativeLayout) findViewById(R.id.DepDetalleRlAdmin);
        Intent back = getIntent();
        salon = back.getStringExtra("salon");
        local = back.getIntExtra("local",0);
        dependiente = back.getIntExtra("dependiente",0);
        CargarDatosDependiente();
        DepDetalleBtnVolver.setOnClickListener(InicioLocalesDependientesDetalle.this);
        DepDetalleAscender.setOnClickListener(InicioLocalesDependientesDetalle.this);
    }

    private void MostrarDatosDependiente(JSONObject iDependiente) throws JSONException {
        String tpUsuario = "-";
        if(iDependiente.getString("tipo").equals("A")) {
            tpUsuario = "Administrador del local";
            DepDetalleRlAdmin.setVisibility(View.GONE);
        }
        else if(iDependiente.getString("tipo").equals("D")) {
            tpUsuario = "Dependiente";
            if(sHelper.getSesion().getTpusuario() == "S") DepDetalleRlAdmin.setVisibility(View.VISIBLE);
        }
        DepDetalleNombre.setText(iDependiente.getString("nombres") + " " + iDependiente.getString("apellido_p") + " " + iDependiente.getString("apellido_m"));
        DepDetalleDni.setText("DNI: " + iDependiente.getString("codigo"));
        DepDetalleLocal.setText(iDependiente.getString("nlocal"));
        DepDetalleEmail.setText(iDependiente.getString("email"));
        DepDetalleTelefono.setText(iDependiente.getString("telefono"));
        DepDetalleTpusuario.setText(tpUsuario);
        key = iDependiente.getString("key");
        GeneraQrDependiente();
    }

    private void ConvertirEnAdministrador() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Convertir en administrador")
                .setMessage("Al convertir un usuario en administrador, le otorgará los permisos para asignar puntos por reventa a los dependientes del salón que administran. ¿Desea continuar?")
                .setPositiveButton("Si, convertir en administrador", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActualizarPermisosDependiente(false);
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    private void CargarDatosDependiente() {
        Map<String, String> post = new HashMap<String, String>();
            post.put("salon", String.valueOf(salon));
            post.put("local", String.valueOf(local));
            post.put("dependiente", String.valueOf(dependiente));
        WsCargarDatosDependiente(post);
    }

    private void GeneraQrDependiente() {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(key, BarcodeFormat.QR_CODE, 400, 400);
            DepDetalleQr.setImageBitmap(bitmap);
        }
        catch(Exception e) {
            Log.e(Constantes.APP_NAME, e.getMessage());
        }
    }

    private void ActualizarPermisosDependiente(boolean forzar) {
        Map<String, String> post = new HashMap<String, String>();
            post.put("salon", String.valueOf(salon));
            post.put("local", String.valueOf(local));
            post.put("dependiente", String.valueOf(dependiente));
            post.put("forzar", forzar ? "S" : "N");
        WsActualizarPermisosDependiente(post);
    }

    //ws

    public void WsCargarDatosDependiente(Map<String, String> post) {
Log.d(Constantes.APP_NAME, post.toString());
        String URL = getResources().getString(R.string.base_url) + getResources().getString(R.string.datos_dependiente);
Log.d(Constantes.APP_NAME, URL);
        WebService ws = new WebService(URL, post, InicioLocalesDependientesDetalle.this, InicioLocalesDependientesDetalle.this);
        ws.execute();
    }

    public void WsActualizarPermisosDependiente(Map<String, String> post) {
        Log.d(Constantes.APP_NAME, post.toString());
        String URL = getResources().getString(R.string.base_url) + getResources().getString(R.string.cambiar_permisos_dependiente);
        Log.d(Constantes.APP_NAME, URL);
        WebService ws = new WebService(URL, post, InicioLocalesDependientesDetalle.this, InicioLocalesDependientesDetalle.this);
        ws.execute();
    }

    /*
    try {
  BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
  Bitmap bitmap = barcodeEncoder.encodeBitmap("content", BarcodeFormat.QR_CODE, 400, 400);
  ImageView imageViewQrCode = (ImageView) findViewById(R.id.qrCode);
  imageViewQrCode.setImageBitmap(bitmap);
} catch(Exception e) {

}
     */
}

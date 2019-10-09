package com.mvelasquezp.clife.clientes.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mvelasquezp.clife.clientes.R;
import com.mvelasquezp.clife.clientes.session.Sesion;
import com.mvelasquezp.clife.clientes.session.SessionHelper;
import com.mvelasquezp.clife.clientes.tools.Constantes;
import com.mvelasquezp.clife.clientes.ws.Asynchtask;
import com.mvelasquezp.clife.clientes.ws.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PromocionCanje extends AppCompatActivity implements View.OnClickListener, Asynchtask {

    SessionHelper sessionHelper;
    int campania, PuntajeDisponible, PuntajeRequerido;
    String producto, NombreProducto, coDireEnti;

    ImageButton promCanjeAtras;
    TextView promCanjeProducto, promCanjeCosto, promCanjeDisponible, promCanjeSaldo;
    EditText promCanjeDireccion, promCanjeTelefono;
    Button promCanjeReclamar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_promocion_canje);
        IniciarComponentes();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.promCanjeAtras:
                setResult(RESULT_CANCELED);
                finish();
            case R.id.promCanjeReclamar:
                GenerarPedido();
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
                    case Constantes.WS_REQUEST_RECLAMA_PREMIOS:
                        Toast.makeText(PromocionCanje.this, "Se ha generado el pedido, y será enviado a su dirección en los próximos días", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                        break;
                    case Constantes.WS_REQUEST_CARGA_DIRECCION:
                        String sDireccion = json.getJSONObject(Constantes.WS_DATA_ATTR).getJSONObject("direccion").getString("descripcion");
                        coDireEnti = json.getJSONObject(Constantes.WS_DATA_ATTR).getJSONObject("direccion").getString("codigo");
                        promCanjeDireccion.setText(sDireccion);
                        break;
                    default: break;
                }
            }
            else {
                Toast.makeText(PromocionCanje.this, json.getString(Constantes.WS_MESSAGE), Toast.LENGTH_LONG).show();
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
        sessionHelper = new SessionHelper(PromocionCanje.this);
        promCanjeAtras = (ImageButton) findViewById(R.id.promCanjeAtras);
        promCanjeProducto = (TextView) findViewById(R.id.promCanjeProducto);
        promCanjeCosto = (TextView) findViewById(R.id.promCanjeCosto);
        promCanjeDisponible = (TextView) findViewById(R.id.promCanjeDisponible);
        promCanjeSaldo = (TextView) findViewById(R.id.promCanjeSaldo);
        promCanjeDireccion = (EditText) findViewById(R.id.promCanjeDireccion);
        promCanjeTelefono = (EditText) findViewById(R.id.promCanjeTelefono);
        promCanjeReclamar = (Button) findViewById(R.id.promCanjeReclamar);
        //
        Intent back = getIntent();
        campania = back.getIntExtra("campania", Constantes.DEFAULT_INT);
        PuntajeDisponible = back.getIntExtra("disponible", Constantes.DEFAULT_INT);
        PuntajeRequerido = back.getIntExtra("requerido", Constantes.DEFAULT_INT);
        producto = back.getStringExtra("coproducto");
        NombreProducto = back.getStringExtra("nomproducto");
        //
        promCanjeProducto.setText(NombreProducto);
        promCanjeCosto.setText(PuntajeRequerido + " puntos");
        promCanjeDisponible.setText(PuntajeDisponible + " puntos");
        promCanjeSaldo.setText((PuntajeDisponible - PuntajeRequerido) + " puntos");
        promCanjeAtras.setOnClickListener(this);
        promCanjeReclamar.setOnClickListener(this);
        //
        CargarDireccionLocal();
    }

    private void GenerarPedido() {
        Sesion sesion = sessionHelper.getSesion();
        String iSalon = "", iDependiente = "";
        int iLocal = 0;
        String tpUsuario = sesion.getTpusuario();
        switch(tpUsuario) {
            case "S":
                iSalon = sesion.getCodigo();
                iLocal = sesion.getLocal_seleccionado();
                iDependiente = "0";
                break;
            case "D":
                iSalon = sesion.getSalon();
                iLocal = sesion.getLocal();
                iDependiente = sesion.getDependiente();
                break;
            default:
                Toast.makeText(PromocionCanje.this, "Ocurrió un error al recuperar el tipo de usuario", Toast.LENGTH_LONG).show();
                return;
        }
        Map<String, String> post = new HashMap<String, String>();
            post.put("campania", String.valueOf(campania));
            post.put("producto", producto);
            post.put("salon", iSalon);
            post.put("local", String.valueOf(iLocal));
            post.put("dependiente", iDependiente);
            post.put("tipo", tpUsuario);
            post.put("direccion", promCanjeDireccion.getText().toString());
            post.put("telefono", promCanjeTelefono.getText().toString());
            post.put("codireccion", coDireEnti);
        WsGenerarPedido(post);
    }

    private void CargarDireccionLocal() {
        Sesion sesion = sessionHelper.getSesion();
        String iSalon = "";
        int iLocal = 0;
        String tpUsuario = sesion.getTpusuario();
        switch(tpUsuario) {
            case "S":
                iSalon = sesion.getCodigo();
                iLocal = sesion.getLocal_seleccionado();
                break;
            case "D":
                iSalon = sesion.getSalon();
                iLocal = sesion.getLocal();
                break;
            default:
                Toast.makeText(PromocionCanje.this, "Ocurrió un error al recuperar el tipo de usuario", Toast.LENGTH_LONG).show();
                return;
        }
        Map<String, String> post = new HashMap<String, String>();
            post.put("salon", iSalon);
            post.put("local", String.valueOf(iLocal));
        WsCargarDireccionLocal(post);
    }

    //ws

    private void WsGenerarPedido(Map<String, String> post) {
        String URL = getResources().getString(R.string.base_url) + getResources().getString(R.string.reclamar_premio);
        WebService ws = new WebService(URL, post, PromocionCanje.this, PromocionCanje.this);
        ws.execute();
    }

    private void WsCargarDireccionLocal(Map<String, String> post) {
        String URL = getResources().getString(R.string.base_url) + getResources().getString(R.string.direccion_local);
        WebService ws = new WebService(URL, post, PromocionCanje.this, PromocionCanje.this);
        ws.execute();
    }
}

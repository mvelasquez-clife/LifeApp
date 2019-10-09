package com.mvelasquezp.clife.clientes.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class PromocionDetalle extends AppCompatActivity implements View.OnClickListener, Asynchtask {

    SessionHelper sessionHelper;
    int campania, PuntajeDisponible, PuntajeRequerido;
    String producto, NombreProducto;

    TextView promdetTitulo, promdetDescripcion, promdetCondiciones;
    Button detpromReservar;
    ImageView promdetImage;
    ImageButton promdetAtras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_promo_detalle);
        IniciarComponentes();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.promdetAtras:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.detpromReservar:
                Intent IntentReclamaPremio = new Intent(PromocionDetalle.this, PromocionCanje.class);
                    IntentReclamaPremio.putExtra("campania", campania);
                    IntentReclamaPremio.putExtra("disponible", PuntajeDisponible);
                    IntentReclamaPremio.putExtra("requerido", PuntajeRequerido);
                    IntentReclamaPremio.putExtra("coproducto", producto);
                    IntentReclamaPremio.putExtra("nomproducto", NombreProducto);
                startActivityForResult(IntentReclamaPremio, Constantes.REQUEST_RECLAMA_PREMIO);
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
                    case Constantes.WS_REQUEST_DETALLE_PREMIOS:
                        PuntajeDisponible = json.getJSONObject(Constantes.WS_DATA_ATTR).getInt("puntaje");
                        JSONObject detalle = json.getJSONObject(Constantes.WS_DATA_ATTR).getJSONObject("premio");
                        PuntajeRequerido = Integer.parseInt(detalle.getString("puntos"));
                        NombreProducto = detalle.getString("producto");
                        promdetTitulo.setText(NombreProducto);
                        promdetDescripcion.setText(detalle.getString("descripcion"));
                        promdetCondiciones.setText("- Canjéalo por " + PuntajeRequerido + " puntos\n- Una vez cuentes con los puntos necesarios, deberás generar un pedido mediante la opción 'Canjear este premio'\nStock máximo: " + detalle.getString("stock") + " unidad(es)");
                        if(PuntajeDisponible < PuntajeRequerido) {
                            int faltante = PuntajeRequerido - PuntajeDisponible;
                            detpromReservar.setText("Te faltan " + faltante + " puntos para conseguir este premio");
                        }
                        else {
                            detpromReservar.setEnabled(true);
                        }
                        detpromReservar.setVisibility(View.VISIBLE);
                        break;
                    default: break;
                }
            }
            else {
                Toast.makeText(PromocionDetalle.this, json.getString(Constantes.WS_MESSAGE), Toast.LENGTH_LONG).show();
            }
        }
        catch(JSONException e) {
            Intent WebViewIntent = new Intent(this, HTMLViewer.class);
            WebViewIntent.putExtra("html", result);
            startActivity(WebViewIntent);
        }
    }

    //REQUEST_RECLAMA_PREMIO

    //metodos

    private void IniciarComponentes() {
        Intent back = getIntent();
        campania = back.getIntExtra("campania", Constantes.DEFAULT_INT);
        producto = back.getStringExtra("producto");
        if(campania != Constantes.DEFAULT_INT) {
            sessionHelper = new SessionHelper(PromocionDetalle.this);
            promdetTitulo = (TextView) findViewById(R.id.promdetTitulo);
            promdetDescripcion = (TextView) findViewById(R.id.promdetDescripcion);
            promdetCondiciones = (TextView) findViewById(R.id.promdetCondiciones);
            detpromReservar = (Button) findViewById(R.id.detpromReservar);
            promdetImage = (ImageView) findViewById(R.id.promdetImage);
            promdetAtras = (ImageButton) findViewById(R.id.promdetAtras);
            detpromReservar.setOnClickListener(this);
            promdetAtras.setOnClickListener(this);
            CargarDetallePromocion();
        }
        else {
            Toast.makeText(PromocionDetalle.this, "La promoción seleccionada es incorrecta o ya no se encuentra disponible. Por favor, intente nuevamente", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        }
    }

    private void CargarDetallePromocion() {
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
                Toast.makeText(PromocionDetalle.this, "Ocurrió un error al recuperar el tipo de usuario", Toast.LENGTH_LONG).show();
                return;
        }
        Map<String, String> post = new HashMap<String, String>();
            post.put("campania", String.valueOf(campania));
            post.put("producto", producto);
            post.put("salon", iSalon);
            post.put("local", String.valueOf(iLocal));
            post.put("dependiente", iDependiente);
            post.put("tipo", tpUsuario);
        WsCargarDetallePromocion(post);
    }

    //ws

    private void WsCargarDetallePromocion(Map<String, String> post) {
        //WS_REQUEST_CUENTA_CORRIENTE
        String URL = getResources().getString(R.string.base_url) + getResources().getString(R.string.descripcion_premio);
        WebService ws = new WebService(URL, post, PromocionDetalle.this, PromocionDetalle.this);
        ws.execute();
    }
}

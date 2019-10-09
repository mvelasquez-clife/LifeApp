package com.mvelasquezp.clife.clientes.dueno;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
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

public class PedidoCalifica extends AppCompatActivity implements Asynchtask, SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    String documento, CodigoPedido, CodigoVendedor;
    SessionHelper sessionHelper;

    ImageButton pedidoCalificaAtras;
    TextView pedidoCalificaDocumento, pedidoCalificaVendedor, pedidoCalificaFecha, pedidoCalificaImporte, pedidoCalificaNumPtsVendedor, pedidoCalificaNumPtsEnvio, pedidoCalificaNumPtsProductos, pedidoCalificaCalificado;
    SeekBar pedidoCalificaPtsVendedor, pedidoCalificaPtsEnvio, pedidoCalificaPtsProductos;
    EditText pedidoCalificaComentarios;
    LinearLayout pedidoCalificaFormulario, pedidoCalificaDetallePedido;
    Button pedidoCalificaPagar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_pedido_califica);
        IniciarComponentes();
    }

    @Override
    public void processFinish(String result) {
        JSONObject json;
        try {
            json = new JSONObject(result);
            if(json.getBoolean(Constantes.WS_STATE_PARAM)) {
                switch(json.getInt(Constantes.WS_REQUEST_ID)) {
                    case Constantes.WS_REQUEST_DETALLE_FACTURA:
                        JSONObject pedido = json.getJSONObject(Constantes.WS_DATA_ATTR).getJSONObject("pedido");
                        CodigoPedido = pedido.getString("pedido");
                        CodigoVendedor = pedido.getString("cvendedor");
                        pedidoCalificaVendedor.setText(CodigoVendedor + " - " + pedido.getString("nvendedor"));
                        pedidoCalificaFecha.setText(pedido.getString("fecha"));
                        pedidoCalificaImporte.setText("S/ " + Double.parseDouble(pedido.getString("importe")));
                        EscribirDetallePedido(json.getJSONObject(Constantes.WS_DATA_ATTR).getJSONArray("detalle"));
                        break;
                    case Constantes.WS_REQUEST_CALIFICA_PEDIDO:
                        Toast.makeText(PedidoCalifica.this, "Calificación enviada", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                        break;
                    default: break;
                }
            }
            else {
                Toast.makeText(PedidoCalifica.this, json.getString(Constantes.WS_MESSAGE), Toast.LENGTH_LONG).show();
            }
        }
        catch(JSONException e) {
            Log.e(Constantes.APP_NAME,e.getLocalizedMessage());
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        switch(seekBar.getId()) {
            case R.id.pedidoCalificaPtsVendedor:
                pedidoCalificaNumPtsVendedor.setText(String.valueOf(progress));
                if(progress <= 2) {
                    pedidoCalificaNumPtsVendedor.setTextColor(getResources().getColor(R.color.textDanger));
                }
                else if(progress <= 4) {
                    pedidoCalificaNumPtsVendedor.setTextColor(getResources().getColor(R.color.textWarning));
                }
                else {
                    pedidoCalificaNumPtsVendedor.setTextColor(getResources().getColor(R.color.textSuccess));
                }
                break;
            case R.id.pedidoCalificaPtsEnvio:
                pedidoCalificaNumPtsEnvio.setText(String.valueOf(progress));
                if(progress <= 2) {
                    pedidoCalificaNumPtsEnvio.setTextColor(getResources().getColor(R.color.textDanger));
                }
                else if(progress <= 4) {
                    pedidoCalificaNumPtsEnvio.setTextColor(getResources().getColor(R.color.textWarning));
                }
                else {
                    pedidoCalificaNumPtsEnvio.setTextColor(getResources().getColor(R.color.textSuccess));
                }
                break;
            case R.id.pedidoCalificaPtsProductos:
                pedidoCalificaNumPtsProductos.setText(String.valueOf(progress));
                if(progress <= 2) {
                    pedidoCalificaNumPtsProductos.setTextColor(getResources().getColor(R.color.textDanger));
                }
                else if(progress <= 4) {
                    pedidoCalificaNumPtsProductos.setTextColor(getResources().getColor(R.color.textWarning));
                }
                else {
                    pedidoCalificaNumPtsProductos.setTextColor(getResources().getColor(R.color.textSuccess));
                }
                break;
            default:
                Log.d(Constantes.APP_NAME, "gg");
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //
    }

    //metodos

    private void IniciarComponentes() {
        Intent back = getIntent();
        sessionHelper = new SessionHelper(PedidoCalifica.this);
        documento = back.getStringExtra("codigo");
        pedidoCalificaAtras = (ImageButton) findViewById(R.id.pedidoCalificaAtras);
        pedidoCalificaDocumento = (TextView) findViewById(R.id.pedidoCalificaDocumento);
        pedidoCalificaVendedor = (TextView) findViewById(R.id.pedidoCalificaVendedor);
        pedidoCalificaFecha = (TextView) findViewById(R.id.pedidoCalificaFecha);
        pedidoCalificaImporte = (TextView) findViewById(R.id.pedidoCalificaImporte);
        pedidoCalificaNumPtsVendedor = (TextView) findViewById(R.id.pedidoCalificaNumPtsVendedor);
        pedidoCalificaNumPtsEnvio = (TextView) findViewById(R.id.pedidoCalificaNumPtsEnvio);
        pedidoCalificaNumPtsProductos = (TextView) findViewById(R.id.pedidoCalificaNumPtsProductos);
        pedidoCalificaCalificado = (TextView) findViewById(R.id.pedidoCalificaCalificado);
        pedidoCalificaPtsVendedor = (SeekBar) findViewById(R.id.pedidoCalificaPtsVendedor);
        pedidoCalificaPtsEnvio = (SeekBar) findViewById(R.id.pedidoCalificaPtsEnvio);
        pedidoCalificaPtsProductos = (SeekBar) findViewById(R.id.pedidoCalificaPtsProductos);
        pedidoCalificaComentarios = (EditText) findViewById(R.id.pedidoCalificaComentarios);
        pedidoCalificaPagar = (Button) findViewById(R.id.pedidoCalificaPagar);
        pedidoCalificaFormulario = (LinearLayout) findViewById(R.id.pedidoCalificaFormulario);
        pedidoCalificaDetallePedido = (LinearLayout) findViewById(R.id.pedidoCalificaDetallePedido);
        //
        if(back.getStringExtra("calificado").equals("S")) {
            pedidoCalificaFormulario.setVisibility(View.GONE);
            pedidoCalificaCalificado.setVisibility(View.VISIBLE);
        }
        else {
            pedidoCalificaFormulario.setVisibility(View.VISIBLE);
            pedidoCalificaCalificado.setVisibility(View.GONE);
        }
        pedidoCalificaDocumento.setText("Pedido " + documento);
        pedidoCalificaAtras.setOnClickListener(PedidoCalifica.this);
        pedidoCalificaPagar.setOnClickListener(PedidoCalifica.this);
        pedidoCalificaPtsVendedor.setOnSeekBarChangeListener(PedidoCalifica.this);
        pedidoCalificaPtsEnvio.setOnSeekBarChangeListener(PedidoCalifica.this);
        pedidoCalificaPtsProductos.setOnSeekBarChangeListener(PedidoCalifica.this);
        CargarDatosPedido();
    }

    private void EscribirDetallePedido(JSONArray detalle) throws JSONException {
        int numItems = detalle.length();
        for(int i = 0; i < numItems; i++) {
            if(i > 0) pedidoCalificaDetallePedido.addView(new Separator(PedidoCalifica.this));
            JSONObject producto = detalle.getJSONObject(i);
            ItemLista item = new ItemLista(PedidoCalifica.this);
                item.setImageVisible(false);
                item.setButtonVisible(false);
                item.setLabels(producto.getString("nombre"),producto.getString("puntos") + " puntos",producto.getString("cantidad") + " unidades");
                item.ConfiguraItemPuntaje(producto.getString("promocional"));
            pedidoCalificaDetallePedido.addView(item);
        }
    }

    private void CargarDatosPedido() {
        Map<String, String> post = new HashMap<String, String>();
            post.put("documento", documento);
        WsCargarDatosPedido(post);
    }

    private void GuardarCalificacionPedido() {
        pedidoCalificaDetallePedido.removeAllViews();
        Map<String, String> post = new HashMap<String, String>();
            post.put("pedido", CodigoPedido);
            post.put("cliente", sessionHelper.getSesion().getCodigo());
            post.put("vendedor", CodigoVendedor);
            post.put("cvendedor", String.valueOf(pedidoCalificaPtsVendedor.getProgress()));
            post.put("cproductos", String.valueOf(pedidoCalificaPtsEnvio.getProgress()));
            post.put("cenvio", String.valueOf(pedidoCalificaPtsProductos.getProgress()));
            post.put("comentarios", pedidoCalificaComentarios.getText().toString());
        WsGuardarCalificacionPedido(post);
    }

    //ws

    private void WsCargarDatosPedido(Map<String, String> post) {
        String URL = getResources().getString(R.string.base_url) + getResources().getString(R.string.detalle_factura);
        WebService ws = new WebService(URL, post, PedidoCalifica.this, PedidoCalifica.this);
        ws.execute();
    }

    private void WsGuardarCalificacionPedido(Map<String, String> post) {
        String URL = getResources().getString(R.string.base_url) + getResources().getString(R.string.califica_pedido);
        WebService ws = new WebService(URL, post, PedidoCalifica.this, PedidoCalifica.this);
        ws.execute();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.pedidoCalificaAtras:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.pedidoCalificaPagar:
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Guardar calificación")
                        .setMessage("Una vez guardada, no podrá modificar su calificación. ¿Desea continuar?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                GuardarCalificacionPedido();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
                break;
            default: break;
        }
    }
}

package com.mvelasquezp.clife.clientes.frg_dueno;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mvelasquezp.clife.clientes.R;
import com.mvelasquezp.clife.clientes.dueno.PedidoCalifica;
import com.mvelasquezp.clife.clientes.controls.Circle;
import com.mvelasquezp.clife.clientes.controls.CircleAngleAnimation;
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

public class Pedidos extends Fragment implements Asynchtask {

    LinearLayout frgPedidosContainer;
    Circle frgPedidosCircle;
    ImageView FrgPedidosEmoji, frgPedidosNotificaciones;
    TextView frgPedidosCliente, frgPedidosRuc, FrgPedidosImporteUltimo, frgPedidosFechaUltimo, FrgPedidosCalificacion;

    SessionHelper sHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the webview for this fragment
        return inflater.inflate(R.layout.frg_dno_pedidos, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InitComponents();
        //
        /*frgPedidosNotificaciones.setBackgroundResource(R.drawable.anim_bell_light);
        AnimationDrawable bellAnimation = (AnimationDrawable) frgPedidosNotificaciones.getBackground();
        bellAnimation.start();*/
    }

    @Override
    public void processFinish(String result) {
        JSONObject json;
        try {
            json = new JSONObject(result);
            if(json.getBoolean(Constantes.WS_STATE_PARAM)) {
                switch(json.getInt(Constantes.WS_REQUEST_ID)) {
                    case Constantes.WS_REQUEST_ULTIMOS_PEDIDOS:
                        int jsCantidad = json.getJSONObject(Constantes.WS_DATA_ATTR).getInt("cantidad");
                        if(jsCantidad > 0) {
                            EscribirListaPedidos(json.getJSONObject(Constantes.WS_DATA_ATTR).getJSONArray("pedidos"));
                        }
                        else {
                            frgPedidosCircle.ModifyLineColor(R.color.textDanger);
                            FrgPedidosEmoji.setImageDrawable(getResources().getDrawable(R.drawable.ic_califica_1));
                            FrgPedidosImporteUltimo.setText("S/ 0.00");
                            frgPedidosFechaUltimo.setText("No tiene pedidos");
                            FrgPedidosCalificacion.setText("0 pts");
                            CircleAngleAnimation animation = new CircleAngleAnimation(frgPedidosCircle, 360);
                            animation.setDuration(1000);
                            frgPedidosCircle.startAnimation(animation);
                        }
                        break;
                    default: break;
                }
            }
            else {
                Toast.makeText(getContext(), json.getString(Constantes.WS_MESSAGE), Toast.LENGTH_LONG).show();
            }
        }
        catch(JSONException e) {
            Log.e(Constantes.APP_NAME,e.getLocalizedMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case Constantes.REQUEST_CALIFICA_PEDIDO:
                if(resultCode == Activity.RESULT_OK) {
                    CargarPedidos();
                }
                break;
            default: break;
        }
    }

    //metodos

    private void InitComponents() {
        Activity activity = getActivity();
        sHelper = new SessionHelper(getContext());
        frgPedidosCliente = activity.findViewById(R.id.frgPedidosCliente);
        frgPedidosRuc = activity.findViewById(R.id.frgPedidosRuc);
        frgPedidosContainer = activity.findViewById(R.id.frgPedidosContainer);
        frgPedidosCircle = activity.findViewById(R.id.frgPedidosCircle);
        FrgPedidosImporteUltimo = activity.findViewById(R.id.FrgPedidosImporteUltimo);
        frgPedidosFechaUltimo = activity.findViewById(R.id.frgPedidosFechaUltimo);
        FrgPedidosEmoji = activity.findViewById(R.id.FrgPedidosEmoji);
        frgPedidosNotificaciones = activity.findViewById(R.id.frgPedidosNotificaciones);
        FrgPedidosCalificacion = activity.findViewById(R.id.FrgPedidosCalificacion);
        EscribirCabecera();
        CargarPedidos();
    }

    private void EscribirCabecera() {
        frgPedidosCliente.setText(sHelper.getSesion().getRsocial());
        frgPedidosRuc.setText("RUC " + String.valueOf(sHelper.getSesion().getCodigo()));
    }

    private void EscribirListaPedidos(JSONArray jsPedidos) throws JSONException {
        frgPedidosContainer.removeAllViews();
        int jsCantidad = jsPedidos.length();
        int cantidadCalificados = 0;
        //
        JSONObject jsUltimo = jsPedidos.getJSONObject(0);
            Double ulImporte = Double.parseDouble(jsUltimo.getString("importe"));
            String ulFecha = jsUltimo.getString("fecha");
            String ulPedido = jsUltimo.getString("documento");
        FrgPedidosImporteUltimo.setText("S/ " + String.format("%,.2f", ulImporte));
        frgPedidosFechaUltimo.setText(ulPedido + "\ndel " + ulFecha);
        //
        double CalificacionPromedio = 0;
        for(int i = 0; i < jsCantidad; i++) {
            if(i > 0) frgPedidosContainer.addView(new Separator(getContext()));
            JSONObject iPedido = jsPedidos.getJSONObject(i);
            ItemLista item = new ItemLista(getContext());
                final String iDocumento = iPedido.getString("documento");
                final double iCalificacion = Double.parseDouble(iPedido.getString("calificacion"));
                String iInfo = "S/ " + String.format("%,.2f", Double.parseDouble(iPedido.getString("importe")));
                String sCalificacion = String.format("%.2f", iCalificacion);
                String iDescripcion = "Del " + iPedido.getString("fecha") + ", " + (iCalificacion > 0 ? ("tu calificación: ♥ " + sCalificacion) : "sin calificar");
                item.setLabels(iDocumento, iInfo, iDescripcion);
                item.setImageVisible(false);
                item.setOnClickListenerEvent(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*if(iCalificacion > 0) Toast.makeText(getContext(), "El pedido seleccionado ya fue calificado", Toast.LENGTH_SHORT).show();
                        else {
                            Intent IntentCalificacion = new Intent(getContext(), PedidoCalifica.class);
                            IntentCalificacion.putExtra("codigo", iDocumento);
                            startActivityForResult(IntentCalificacion, Constantes.REQUEST_CALIFICA_PEDIDO);
                        }*/
                        Intent IntentCalificacion = new Intent(getContext(), PedidoCalifica.class);
                            IntentCalificacion.putExtra("calificado", iCalificacion > 0 ? "S" : "N");
                            IntentCalificacion.putExtra("codigo", iDocumento);
                        startActivityForResult(IntentCalificacion, Constantes.REQUEST_CALIFICA_PEDIDO);
                    }
                });
                if(iCalificacion > 0) cantidadCalificados++;
                CalificacionPromedio += iCalificacion;
            frgPedidosContainer.addView(item);
        }
        if(cantidadCalificados > 0) CalificacionPromedio /= cantidadCalificados;
        String fCalificacion = String.format("%.1f", CalificacionPromedio);
        if(CalificacionPromedio < 1) {
            frgPedidosCircle.ModifyLineColor(R.color.textDanger);
            FrgPedidosEmoji.setImageDrawable(getResources().getDrawable(R.drawable.ic_califica_1));
        }
        else if(CalificacionPromedio < 2) {
            frgPedidosCircle.ModifyLineColor(R.color.textDanger);
            FrgPedidosEmoji.setImageDrawable(getResources().getDrawable(R.drawable.ic_califica_2));
        }
        else if(CalificacionPromedio < 3) {
            frgPedidosCircle.ModifyLineColor(R.color.textWarning);
            FrgPedidosEmoji.setImageDrawable(getResources().getDrawable(R.drawable.ic_califica_3));
        }
        else if(CalificacionPromedio < 4) {
            frgPedidosCircle.ModifyLineColor(R.color.textWarning);
            FrgPedidosEmoji.setImageDrawable(getResources().getDrawable(R.drawable.ic_califica_4));
        }
        else {
            frgPedidosCircle.ModifyLineColor(R.color.textSuccess);
            FrgPedidosEmoji.setImageDrawable(getResources().getDrawable(R.drawable.ic_califica_5));
        }
        FrgPedidosCalificacion.setText(fCalificacion + " pts");
        CircleAngleAnimation animation = new CircleAngleAnimation(frgPedidosCircle, 360);
        animation.setDuration(1000);
        frgPedidosCircle.startAnimation(animation);
    }

    private void CargarPedidos() {
        Map<String, String> post = new HashMap<String, String>();
            post.put("cliente", String.valueOf(sHelper.getSesion().getCodigo()));
            post.put("empresa", String.valueOf(sHelper.getSesion().getEmpresa()));
        WsCargarPedidos(post);
    }

    //ws

    private void WsCargarPedidos(Map<String, String> post) {
        String URL = getResources().getString(R.string.base_url) + getResources().getString(R.string.ultimos_pedidos);
        WebService ws = new WebService(URL, post, getActivity(), Pedidos.this);
        ws.execute();
    }
}

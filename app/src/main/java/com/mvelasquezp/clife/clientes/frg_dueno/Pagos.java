package com.mvelasquezp.clife.clientes.frg_dueno;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mvelasquezp.clife.clientes.R;
import com.mvelasquezp.clife.clientes.dueno.DocumentoDetalle;
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

public class Pagos extends Fragment implements Asynchtask {

    Circle frgPagosCircle;
    LinearLayout frgPagosContainer;
    TextView frgPagosRuc,frgPagosCliente,frgPagosConsumo,frgPagosDeuda;

    SessionHelper sHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the webview for this fragment
        return inflater.inflate(R.layout.frg_dno_pagos, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InitComponents();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void processFinish(String result) {
        JSONObject json;
        try {
            json = new JSONObject(result);
            if(json.getBoolean(Constantes.WS_STATE_PARAM)) {
                switch(json.getInt(Constantes.WS_REQUEST_ID)) {
                    case Constantes.WS_REQUEST_CUENTA_CORRIENTE:
                        JSONArray jsDocumentos = json.getJSONObject(Constantes.WS_DATA_ATTR).getJSONArray("documentos");
                        JSONObject info = json.getJSONObject(Constantes.WS_DATA_ATTR).getJSONObject("info");
                        //frgPagosCliente.setText(info.getString("cliente"));
                        //frgPagosRuc.setText("RUC " + info.getString("ruc"));
                        frgPagosConsumo.setText("S/ " + String.format("%,.2f", info.getDouble("consumo")));
                        frgPagosDeuda.setText("S/ " + String.format("%,.2f", info.getDouble("deuda")));
                        int numDocumentos = jsDocumentos.length();
                        frgPagosContainer.removeAllViews();
                        for(int i = 0; i < numDocumentos; i++) {
                            if(i > 0) frgPagosContainer.addView(new Separator(getContext()));
                            JSONObject iDocumento = jsDocumentos.getJSONObject(i);
                            final String iCodigoFactura = iDocumento.getString("co_documento");
                            final String iCodigoVendedor = iDocumento.getString("cadena");
                            final String iNombreVendedor = iDocumento.getString("de_vendedor");
                            double iImporteFactura = Double.parseDouble(iDocumento.getString("im_saldo"));
                            ItemLista item = new ItemLista(getContext());
                                item.setImageVisible(false);
                                String descripcion = "Fecha emisión " + iDocumento.getString("fecha_char") + "\nVendedor: " + iDocumento.getString("de_vendedor") + "\n" + iDocumento.getString("de_cond_pago") + ", vence el " + iDocumento.getString("fec_venc");
                                String codocumento = iDocumento.getString("co_documento");
                                int dias_vencido = iDocumento.getInt("nu_dias_vencido");
                                if (dias_vencido > 0) {
                                    codocumento += (" - " + dias_vencido + " día(s) vencido");
                                    if (dias_vencido > 60) item.ResaltaTprim();
                                }
                                item.setLabels(codocumento, "Deuda: S/ " + String.format("%,.2f", iImporteFactura), descripcion);
                                if(iDocumento.getString("cadena").equals("S")) {
                                    item.resaltarTsec(R.color.textDanger);
                                }
                                else {
                                    item.resaltarTsec(R.color.textTitle);
                                }
                                item.setOnClickListenerEvent(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent IntentDocumentDetalle = new Intent(getContext(), DocumentoDetalle.class);
                                            IntentDocumentDetalle.putExtra("documento", iCodigoFactura);
                                            IntentDocumentDetalle.putExtra("vendedor", iCodigoVendedor);
                                            IntentDocumentDetalle.putExtra("nvendedor", iNombreVendedor);
                                        startActivityForResult(IntentDocumentDetalle, Constantes.REQUEST_DETALLE_DOCUMENTO);
                                    }
                                });
                            frgPagosContainer.addView(item);
                        }
                        //
                        if(info.getDouble("deuda") > 100) frgPagosCircle.ModifyLineColor(R.color.textDanger);
                        CircleAngleAnimation animation = new CircleAngleAnimation(frgPagosCircle, 360);
                        animation.setDuration(1000);
                        frgPagosCircle.startAnimation(animation);
                        //
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
            /*Intent WebViewIntent = new Intent(this, HTMLViewer.class);
            WebViewIntent.putExtra("html", result);
            startActivity(WebViewIntent);*/
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constantes.REQUEST_DETALLE_DOCUMENTO) {
            if(resultCode == Activity.RESULT_OK) {
                CircleAngleAnimation animation = new CircleAngleAnimation(frgPagosCircle, 360);
                animation.setDuration(1000);
                frgPagosCircle.startAnimation(animation);
            }
        }
    }

    //metodos

    private void InitComponents() {
        Activity activity = getActivity();
        sHelper = new SessionHelper(getContext());
        frgPagosCircle = (Circle) activity.findViewById(R.id.frgPagosCircle);
        frgPagosContainer = (LinearLayout) activity.findViewById(R.id.frgPagosContainer);
        frgPagosRuc = (TextView) activity.findViewById(R.id.frgPagosRuc);
        frgPagosCliente = (TextView) activity.findViewById(R.id.frgPagosCliente);
        frgPagosConsumo = (TextView) activity.findViewById(R.id.frgPagosConsumo);
        frgPagosDeuda = (TextView) activity.findViewById(R.id.frgPagosDeuda);
        //
        CargarDocumentos();
        EscribirCabecera();
    }

    private void EscribirCabecera() {
        frgPagosCliente.setText(sHelper.getSesion().getRsocial());
        frgPagosRuc.setText("RUC " + String.valueOf(sHelper.getSesion().getCodigo()));
    }

    private void CargarDocumentos() {
        Map<String, String> post = new HashMap<String, String>();
            post.put("codigo", String.valueOf(sHelper.getSesion().getCodigo()));
        WsCargarDocumentos(post);
    }

    //ws

    private void WsCargarDocumentos(Map<String, String> post) {
        //WS_REQUEST_CUENTA_CORRIENTE
        String URL = getResources().getString(R.string.base_url) + getResources().getString(R.string.cta_cte);
        WebService ws = new WebService(URL, post, getActivity(), Pagos.this);
        ws.execute();
    }
}

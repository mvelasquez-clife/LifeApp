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
import android.widget.Toast;

import com.mvelasquezp.clife.clientes.R;
import com.mvelasquezp.clife.clientes.activities.HTMLViewer;
import com.mvelasquezp.clife.clientes.activities.PromocionDetalle;
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

public class Promociones extends Fragment implements Asynchtask {

    SessionHelper sessionHelper;

    LinearLayout frgPromocionesContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the webview for this fragment
        return inflater.inflate(R.layout.frg_dno_promociones, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InitComponents();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case Constantes.REQUEST_DETALLE_PROMO:
                CargarPromociones();
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
                    case Constantes.WS_REQUEST_LISTA_PREMIOS:
                        JSONArray jsPremios = json.getJSONObject(Constantes.WS_DATA_ATTR).getJSONArray("premios");
                        EscribirListaPromociones(jsPremios);
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
            Intent WebViewIntent = new Intent(getContext(), HTMLViewer.class);
            WebViewIntent.putExtra("html", result);
            startActivity(WebViewIntent);
        }
    }

    //metodos

    private void InitComponents() {
        sessionHelper = new SessionHelper(getContext());
        Activity activity = getActivity();
        frgPromocionesContainer = (LinearLayout) activity.findViewById(R.id.frgPromocionesContainer);
        CargarPromociones();
    }

    private void EscribirListaPromociones(JSONArray promociones) throws JSONException {
        int numPromociones = promociones.length();
        frgPromocionesContainer.removeAllViews();
        for(int i = 0; i < numPromociones; i++) {
            if(i > 0) frgPromocionesContainer.addView(new Separator(getContext()));
            JSONObject iPromocion = promociones.getJSONObject(i);
                String iTitulo = iPromocion.getString("producto");
                String iInfo = iPromocion.getString("puntos") + " pts";
                String iDescripcion = iPromocion.getString("stock") + " restantes, expira el " + iPromocion.getString("expira");
                final int iCampania = Integer.parseInt(iPromocion.getString("campania"));
                final String iProducto = iPromocion.getString("codigo");
            ItemLista item = new ItemLista(getContext());
            item.setLabels(iTitulo, iInfo, iDescripcion);
            item.setOnClickListenerEvent(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent IntentPromocionDetalle = new Intent(getContext(), PromocionDetalle.class);
                        IntentPromocionDetalle.putExtra("campania", iCampania);
                        IntentPromocionDetalle.putExtra("producto", iProducto);
                    startActivityForResult(IntentPromocionDetalle, Constantes.REQUEST_DETALLE_PROMO);
                }
            });
            frgPromocionesContainer.addView(item);
        }
    }

    private void CargarPromociones() {
        Map<String, String> post = new HashMap<String, String>();
            post.put("tipo", sessionHelper.getSesion().getTpusuario());
        WsCargarPromociones(post);
    }

    //ws

    private void WsCargarPromociones(Map<String, String> post) {
        //WS_REQUEST_CUENTA_CORRIENTE
        String URL = getResources().getString(R.string.base_url) + getResources().getString(R.string.premios_disponibles);
        WebService ws = new WebService(URL, post, getActivity(), Promociones.this);
Log.d(Constantes.APP_NAME, URL);
Log.d(Constantes.APP_NAME, post.toString());
        ws.execute();
    }
}

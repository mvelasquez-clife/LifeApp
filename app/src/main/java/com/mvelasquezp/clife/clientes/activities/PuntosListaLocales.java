package com.mvelasquezp.clife.clientes.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

public class PuntosListaLocales extends AppCompatActivity implements View.OnClickListener, Asynchtask {

    SessionHelper sHelper;
    int PuntosDisponibles = 0;

    ImageButton RepartePuntosAtras;
    TextView RepartePuntosDisponibles;
    LinearLayout RepartePuntosContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_puntos_locales);
        IniciarComponentes();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.RepartePuntosAtras:
                setResult(RESULT_CANCELED);
                finish();
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
                    case Constantes.WS_REQUEST_INFO_REPARTE_PUNTOS:
                        PuntosDisponibles = json.getJSONObject(Constantes.WS_DATA_ATTR).getInt("puntaje");
                        JSONArray jsLocales = json.getJSONObject(Constantes.WS_DATA_ATTR).getJSONArray("locales");
                        EscribirListaLocales(jsLocales);
                        RepartePuntosDisponibles.setText(PuntosDisponibles + " punto(s) disponible(s)");
                        break;
                    case Constantes.WS_REQUEST_ASIGNA_PUNTAJE:
                        PuntosDisponibles = json.getJSONObject(Constantes.WS_DATA_ATTR).getInt("puntaje");
                        JSONArray arrLocales = json.getJSONObject(Constantes.WS_DATA_ATTR).getJSONArray("locales");
                        EscribirListaLocales(arrLocales);
                        RepartePuntosDisponibles.setText(PuntosDisponibles + " punto(s) disponible(s)");
                        Toast.makeText(PuntosListaLocales.this, "¡Se asignaron los puntos al local seleccionado!", Toast.LENGTH_SHORT).show();
                        break;
                    default: break;
                }
            }
            else {
                Toast.makeText(PuntosListaLocales.this, json.getString(Constantes.WS_MESSAGE), Toast.LENGTH_LONG).show();
            }
        }
        catch(JSONException e) {
            Intent WebViewIntent = new Intent(this, HTMLViewer.class);
            WebViewIntent.putExtra("html", result);
            startActivity(WebViewIntent);
        }
    }

    //metodos

    private void IniciarComponentes() {
        sHelper = new SessionHelper(PuntosListaLocales.this);
        //
        RepartePuntosAtras = (ImageButton) findViewById(R.id.RepartePuntosAtras);
        RepartePuntosDisponibles = (TextView) findViewById(R.id.RepartePuntosDisponibles);
        RepartePuntosContainer = (LinearLayout) findViewById(R.id.RepartePuntosContainer);
        //
        RepartePuntosAtras.setOnClickListener(this);
        CargaInfoReparticion();
    }

    private void EscribirListaLocales(JSONArray arrLocales) throws JSONException {
        RepartePuntosContainer.removeAllViews();
        int numLocales = arrLocales.length();
        for(int i = 0; i < numLocales; i++) {
            if(i > 0) RepartePuntosContainer.addView(new Separator(PuntosListaLocales.this));
            JSONObject iLocal = arrLocales.getJSONObject(i);
                final int cLocal = Integer.parseInt(iLocal.getString("local"));
                final String cNombre = iLocal.getString("nombre");
                String descripcion = "Puntos actuales: " + iLocal.getString("puntos") + " puntos";
            ItemLista item = new ItemLista(PuntosListaLocales.this);
                item.setImageVisible(false);
                item.setLabels(cNombre, "", descripcion);
            item.setOnClickListenerEvent(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PuntosListaLocales.this);
                        builder.setTitle(cNombre);
                    ViewGroup viewGroup = (ViewGroup) getWindow().getDecorView().getRootView();
                    View viewInflated = LayoutInflater.from(PuntosListaLocales.this).inflate(R.layout.ctrl_number_picker,viewGroup,false);
                    final EditText CtrlNumberPickerInput = (EditText) viewInflated.findViewById(R.id.CtrlNumberPickerInput);
                    builder.setView(viewInflated);
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int PuntosParaAsignar = Integer.parseInt(CtrlNumberPickerInput.getText().toString());
                            if(PuntosParaAsignar <= PuntosDisponibles) {
                                dialog.dismiss();
                                AsignarPuntajeLocal(cLocal, PuntosParaAsignar);
                            }
                            else {
                                Toast.makeText(PuntosListaLocales.this, "No se puede asignar " + PuntosParaAsignar + " puntos. Recuerde que sólo posee " + PuntosDisponibles + " puntos disponibles.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
            });
            RepartePuntosContainer.addView(item);
        }
    }

    private void CargaInfoReparticion() {
        Map<String, String> post = new HashMap<String, String>();
            post.put("salon", String.valueOf(sHelper.getSesion().getCodigo()));
        WsCargaInfoReparticion(post);
    }

    private void AsignarPuntajeLocal(int local, int puntaje) {
        Map<String, String> post = new HashMap<String, String>();
            post.put("salon", sHelper.getSesion().getCodigo());
            post.put("local", String.valueOf(local));
            post.put("puntos", String.valueOf(puntaje));
        WsAsignarPuntajeLocal(post);
    }

    //ws

    private void WsCargaInfoReparticion(Map<String, String> post) {
        String URL = getResources().getString(R.string.base_url) + getResources().getString(R.string.info_reparte_puntaje);
        WebService ws = new WebService(URL, post, PuntosListaLocales.this, PuntosListaLocales.this);
        ws.execute();
    }

    private void WsAsignarPuntajeLocal(Map<String, String> post) {
        String URL = getResources().getString(R.string.base_url) + getResources().getString(R.string.asignar_puntaje);
        WebService ws = new WebService(URL, post, PuntosListaLocales.this, PuntosListaLocales.this);
        ws.execute();
    }
}

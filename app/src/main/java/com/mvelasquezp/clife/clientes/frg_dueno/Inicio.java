package com.mvelasquezp.clife.clientes.frg_dueno;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mvelasquezp.clife.clientes.R;
import com.mvelasquezp.clife.clientes.activities.HTMLViewer;
import com.mvelasquezp.clife.clientes.activities.InicioLocales;
import com.mvelasquezp.clife.clientes.activities.InicioLocalesDependientes;
import com.mvelasquezp.clife.clientes.activities.PuntosAsignar;
import com.mvelasquezp.clife.clientes.activities.PuntosListaLocales;
import com.mvelasquezp.clife.clientes.controls.Circle;
import com.mvelasquezp.clife.clientes.controls.CircleAngleAnimation;
import com.mvelasquezp.clife.clientes.session.Sesion;
import com.mvelasquezp.clife.clientes.session.SessionHelper;
import com.mvelasquezp.clife.clientes.tools.Constantes;
import com.mvelasquezp.clife.clientes.tools.LinkedHashMapAdapter;
import com.mvelasquezp.clife.clientes.ws.Asynchtask;
import com.mvelasquezp.clife.clientes.ws.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Inicio extends Fragment implements Asynchtask, View.OnClickListener {

    int PosicionActualLocal = -1;
    int PuntosDisponibles = 0;
    SessionHelper sHelper;

    //Spinner frgInicioLocal;
    TextView frgTvInicioLocal;
    Circle frgInicioCircle;
    TextView frgInicioNombre, frgInicioPuntaje, FrgInicioCantidadLocales, FrgInicioCantidadDependientes;
    Button FrgInicioVerLocales, FrgInicioVerDependientes, FrgInicioRepartir, FrgInicioAsignar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the webview for this fragment
        return inflater.inflate(R.layout.frg_dno_inicio, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InitComponents();
    }

    @Override
    public void processFinish(String result) {
        JSONObject json;
        try {
            json = new JSONObject(result);
            if(json.getBoolean(Constantes.WS_STATE_PARAM)) {
                switch(json.getInt(Constantes.WS_REQUEST_ID)) {
                    case Constantes.WS_REQUEST_INFO_CLIENTE:
                        JSONObject data = json.getJSONObject(Constantes.WS_DATA_ATTR);
                        PuntosDisponibles = data.getInt("puntaje");
                        frgInicioPuntaje.setText(PuntosDisponibles + " puntos");
                        FrgInicioCantidadLocales.setText(String.valueOf(data.getInt("locales")));
                        FrgInicioCantidadDependientes.setText(String.valueOf(data.getInt("dependientes")));
                        JSONArray jsLocales = data.getJSONArray("lista");
                        break;
                    case Constantes.WS_REQUEST_STOCK_PUNTAJE:
                        int puntaje = json.getJSONObject(Constantes.WS_DATA_ATTR).getInt("puntaje");
                        frgInicioPuntaje.setText(puntaje + " puntos");
                        CircleAngleAnimation animation = new CircleAngleAnimation(frgInicioCircle, 360);
                        animation.setDuration(1000);
                        frgInicioCircle.startAnimation(animation);
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

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.FrgInicioVerLocales:
                Intent IntentLocales = new Intent(getContext(), InicioLocales.class);
                startActivityForResult(IntentLocales, Constantes.REQUEST_INICIO_LOCALES);
                break;
            case R.id.FrgInicioVerDependientes:
                Intent IntentDependientes = new Intent(getContext(), InicioLocalesDependientes.class);
                startActivityForResult(IntentDependientes, Constantes.REQUEST_INICIO_DEPENDIENTES_LOCAL);
                break;
            case R.id.FrgInicioRepartir:
                if(PuntosDisponibles > 0) {
                    Intent IntentPuntosListaLocales = new Intent(getContext(), PuntosListaLocales.class);
                    startActivityForResult(IntentPuntosListaLocales, Constantes.REQUEST_PUNTOS_LISTA_LOCALES);
                }
                else {
                    Toast.makeText(getContext(), "No tienes puntos para repartir. Recuerda que recibirás puntos por cada compra que hagas :)", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.FrgInicioAsignar:
                Intent IntentAsignar = new Intent(getContext(), PuntosAsignar.class);
                startActivityForResult(IntentAsignar, Constantes.REQUEST_ASIGNAR_PUNTOS_DEPENDIENTE);
                break;
            default: break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case Constantes.REQUEST_INICIO_LOCALES:
                CargarInfoCliente();
                break;
            case Constantes.REQUEST_INICIO_DEPENDIENTES_LOCAL:
                CargarInfoCliente();
                break;
            case Constantes.REQUEST_PUNTOS_LISTA_LOCALES:
                CargarInfoCliente();
                if(resultCode == Activity.RESULT_OK) {
                    Toast.makeText(getContext(), "Puntos repartidos con éxito", Toast.LENGTH_SHORT).show();
                }
                break;
            case Constantes.REQUEST_ASIGNAR_PUNTOS_DEPENDIENTE:
                //if(resultCode == Activity.RESULT_OK) {
                CargarInfoCliente();
                //}
                break;
            default: break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(frgInicioCircle != null) {
            CircleAngleAnimation animation = new CircleAngleAnimation(frgInicioCircle, 360);
            animation.setDuration(1000);
            frgInicioCircle.startAnimation(animation);
        }
    }

    //metodos

    private void InitComponents() {
        Activity activity = getActivity();
        sHelper = new SessionHelper(getContext());
        frgTvInicioLocal = (TextView) activity.findViewById(R.id.frgTvInicioLocal);
        frgInicioCircle = (Circle) activity.findViewById(R.id.frgInicioCircle);
        frgInicioNombre = (TextView) activity.findViewById(R.id.frgInicioNombre);
        frgInicioPuntaje = (TextView) activity.findViewById(R.id.frgInicioPuntaje);
        FrgInicioCantidadLocales = (TextView) activity.findViewById(R.id.FrgInicioCantidadLocales);
        FrgInicioCantidadDependientes = (TextView) activity.findViewById(R.id.FrgInicioCantidadDependientes);
        FrgInicioVerLocales = (Button) activity.findViewById(R.id.FrgInicioVerLocales);
        FrgInicioVerDependientes = (Button) activity.findViewById(R.id.FrgInicioVerDependientes);
        FrgInicioRepartir = (Button) activity.findViewById(R.id.FrgInicioRepartir);
        FrgInicioAsignar = (Button) activity.findViewById(R.id.FrgInicioAsignar);
        //
        CargarInfoCliente();
        frgTvInicioLocal.setText(sHelper.getSesion().getRsocial());
        frgInicioNombre.setText("Bienvenido nuevamente a " + Constantes.APP_NAME);
        FrgInicioVerLocales.setOnClickListener(this);
        FrgInicioVerDependientes.setOnClickListener(this);
        FrgInicioRepartir.setOnClickListener(this);
        FrgInicioAsignar.setOnClickListener(this);
    }

    private void CargarInfoCliente() {
        Map<String, String> post = new HashMap<String, String>();
            post.put("salon", String.valueOf(sHelper.getSesion().getCodigo()));
        WsCargarInfoCliente(post);
    }

    //ws

    private void WsCargarInfoCliente(Map<String, String> post) {
        //WS_REQUEST_CUENTA_CORRIENTE
        String URL = getResources().getString(R.string.base_url) + getResources().getString(R.string.info_cliente);
        WebService ws = new WebService(URL, post, getActivity(), Inicio.this);
        ws.execute();
    }
}

package com.mvelasquezp.clife.clientes.frg_administrador;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mvelasquezp.clife.clientes.R;
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

    SessionHelper sHelper;

    Circle frgAdmInicioCircle;
    TextView frgAdmInicioNombreLocal, frgAdmInicioNombre, frgAdmInicioPuntaje, frgAdmInicioCantidadDependientes;
    Button frgAdmInicioVerDependientes, frgAdmInicioAsignar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the webview for this fragment
        return inflater.inflate(R.layout.frg_adm_inicio, container, false);
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
                    case Constantes.WS_REQUEST_STOCK_PUNTAJE:
                        int puntaje = json.getJSONObject(Constantes.WS_DATA_ATTR).getInt("puntaje");
                        frgAdmInicioCantidadDependientes.setText(String.valueOf(json.getJSONObject(Constantes.WS_DATA_ATTR).getInt("dependientes")));
                        frgAdmInicioPuntaje.setText(puntaje + " puntos");
                        CircleAngleAnimation animation = new CircleAngleAnimation(frgAdmInicioCircle, 360);
                        animation.setDuration(1000);
                        frgAdmInicioCircle.startAnimation(animation);
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
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.frgAdmInicioVerDependientes:
                Intent IntentDependientes = new Intent(getContext(), InicioLocalesDependientes.class);
                startActivity(IntentDependientes);
                break;
            case R.id.frgAdmInicioAsignar:
                Intent IntentAsignar = new Intent(getContext(), PuntosAsignar.class);
                startActivityForResult(IntentAsignar, Constantes.REQUEST_ASIGNAR_PUNTOS_DEPENDIENTE);
                break;
            default: break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case Constantes.REQUEST_ASIGNAR_PUNTOS_DEPENDIENTE:
                CargarInfoLocal();
                break;
            default: break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(frgAdmInicioCircle != null) {
            CircleAngleAnimation animation = new CircleAngleAnimation(frgAdmInicioCircle, 360);
            animation.setDuration(1000);
            frgAdmInicioCircle.startAnimation(animation);
        }
    }

    //metodos

    private void InitComponents() {
        Activity activity = getActivity();
        sHelper = new SessionHelper(getContext());
        frgAdmInicioCircle = (Circle) activity.findViewById(R.id.frgAdmInicioCircle);
        frgAdmInicioNombreLocal = (TextView) activity.findViewById(R.id.frgAdmInicioNombreLocal);
        frgAdmInicioNombre = (TextView) activity.findViewById(R.id.frgAdmInicioNombre);
        frgAdmInicioPuntaje = (TextView) activity.findViewById(R.id.frgAdmInicioPuntaje);
        frgAdmInicioCantidadDependientes = (TextView) activity.findViewById(R.id.frgAdmInicioCantidadDependientes);
        frgAdmInicioVerDependientes = (Button) activity.findViewById(R.id.frgAdmInicioVerDependientes);
        frgAdmInicioAsignar = (Button) activity.findViewById(R.id.frgAdmInicioAsignar);
        //
        CargarInfoLocal();
        frgAdmInicioNombreLocal.setText(sHelper.getSesion().getNcomercial());
        frgAdmInicioNombre.setText("Hola de nuevo, " + sHelper.getSesion().getRsocial());
        frgAdmInicioVerDependientes.setOnClickListener(this);
        frgAdmInicioAsignar.setOnClickListener(this);
    }

    private void CargarInfoLocal() {
        Map<String, String> post = new HashMap<String, String>();
            post.put("salon", String.valueOf(sHelper.getSesion().getCodigo()));
            post.put("local", String.valueOf(sHelper.getSesion().getLocal()));
        WsCargarInfoLocal(post);
    }

    //ws

    private void WsCargarInfoLocal(Map<String, String> post) {
        //WS_REQUEST_CUENTA_CORRIENTE
        String URL = getResources().getString(R.string.base_url) + getResources().getString(R.string.stock_puntos);
Log.d(Constantes.APP_NAME, URL);
Log.d(Constantes.APP_NAME, post.toString());
        WebService ws = new WebService(URL, post, getActivity(), Inicio.this);
        ws.execute();
    }
}

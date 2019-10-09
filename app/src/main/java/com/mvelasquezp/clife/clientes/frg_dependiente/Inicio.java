package com.mvelasquezp.clife.clientes.frg_dependiente;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mvelasquezp.clife.clientes.R;
import com.mvelasquezp.clife.clientes.activities.HTMLViewer;
import com.mvelasquezp.clife.clientes.controls.Circle;
import com.mvelasquezp.clife.clientes.controls.CircleAngleAnimation;
import com.mvelasquezp.clife.clientes.dependientes.RecibirPuntaje;
import com.mvelasquezp.clife.clientes.session.SessionHelper;
import com.mvelasquezp.clife.clientes.tools.Constantes;
import com.mvelasquezp.clife.clientes.ws.Asynchtask;
import com.mvelasquezp.clife.clientes.ws.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Inicio extends Fragment implements Asynchtask, View.OnClickListener {
    
    SessionHelper sessionHelper;

    Circle FrgDepInicioCircle;
    TextView FrgDepInicioNombre, FrgDepInicioPuntaje;
    Button FrgDepInicioReclamar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frg_dep_inicio, container, false);
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
                    case Constantes.WS_REQUEST_INFO_DEPENDIENTE:
                        int puntos = json.getJSONObject(Constantes.WS_DATA_ATTR).getInt("puntos");
                        String nombre = json.getJSONObject(Constantes.WS_DATA_ATTR).getString("nombre");
                        FrgDepInicioNombre.setText("Bienvenido, " + nombre);
                        FrgDepInicioPuntaje.setText(String.valueOf(puntos) + " puntos");
                        break;
                    default: break;
                }
            }
            else {
                Toast.makeText(getContext(), json.getString(Constantes.WS_MESSAGE), Toast.LENGTH_LONG).show();
            }
        }
        catch(JSONException e) {
            Intent WebViewIntent = new Intent(getContext(), HTMLViewer.class);
            WebViewIntent.putExtra("html", result);
            startActivity(WebViewIntent);
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.FrgDepInicioReclamar:
                Intent IntentReclamaPuntaje = new Intent(getContext(), RecibirPuntaje.class);
                startActivityForResult(IntentReclamaPuntaje, Constantes.REQUEST_RECLAMA_PUNTAJE);
                break;
            default: break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case Constantes.REQUEST_RECLAMA_PUNTAJE:
                if(resultCode == Activity.RESULT_OK) {
                    CargarInfoDependiente();
                }
                break;
            default: break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(FrgDepInicioCircle != null) {
            CircleAngleAnimation animation = new CircleAngleAnimation(FrgDepInicioCircle, 360);
            animation.setDuration(1000);
            FrgDepInicioCircle.startAnimation(animation);
        }
    }

    //metodos

    private void InitComponents() {
        sessionHelper = new SessionHelper(getContext());
        Activity activity = getActivity();
        FrgDepInicioNombre = (TextView) activity.findViewById(R.id.FrgDepInicioNombre);
        FrgDepInicioCircle = (Circle) activity.findViewById(R.id.FrgDepInicioCircle);
        FrgDepInicioPuntaje = (TextView) activity.findViewById(R.id.FrgDepInicioPuntaje);
        FrgDepInicioReclamar = (Button) activity.findViewById(R.id.FrgDepInicioReclamar);
        FrgDepInicioReclamar.setOnClickListener(Inicio.this);
        CargarInfoDependiente();
    }

    private void CargarInfoDependiente() {
        Map<String, String> post = new HashMap<String, String>();
            post.put("key", sessionHelper.getSesion().getToken());
        WsCargarInfoDependiente(post);
    }

    //ws

    private void WsCargarInfoDependiente(Map<String, String> post) {
        String URL = getResources().getString(R.string.base_url) + getResources().getString(R.string.info_dependiente);
        WebService ws = new WebService(URL, post, getActivity(), Inicio.this);
        ws.execute();
    }
}

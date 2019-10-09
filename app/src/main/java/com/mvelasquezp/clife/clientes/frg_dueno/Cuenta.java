package com.mvelasquezp.clife.clientes.frg_dueno;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mvelasquezp.clife.clientes.R;
import com.mvelasquezp.clife.clientes.activities.CuentaLogros;
import com.mvelasquezp.clife.clientes.activities.HTMLViewer;
import com.mvelasquezp.clife.clientes.activities.Login;
import com.mvelasquezp.clife.clientes.session.SessionHelper;
import com.mvelasquezp.clife.clientes.tools.Constantes;
import com.mvelasquezp.clife.clientes.ws.Asynchtask;
import com.mvelasquezp.clife.clientes.ws.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Cuenta extends Fragment implements View.OnClickListener, Asynchtask {

    SessionHelper sHelper;

    ImageView FrgCuentaImagenPerfil;
    ImageButton FrgCuentaEditarImg, FrgCuentaBtnCumpleanios, FrgCuentaBtnLlamada;
    TextView FrgCuentaRzsocial, FrgCuentaRucDni, FrgCuentaVendedor, FrgCuentaVndtlf, FrgCuentaLblBlockVendedorHeader;
    EditText FrgCuentaEmail, FrgCuentaTelefono, FrgCuentaCumpleanios;
    Button FrgCuentaBtnGuardar, /*FrgCuentaBtnLogros, */FrgCuentaBtnLogout;
    RelativeLayout FrgCuentaLblBlockVendedorNombre, FrgCuentaLblBlockVendedorTelefono;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the webview for this fragment
        return inflater.inflate(R.layout.frg_dno_cuenta, container, false);
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
                    case Constantes.WS_REQUEST_INFO_PERFIL:
                        JSONObject jsCliente = json.getJSONObject(Constantes.WS_DATA_ATTR).getJSONObject("cliente");
                        FrgCuentaEmail.setText(jsCliente.getString("email"));
                        FrgCuentaTelefono.setText(jsCliente.getString("telefono"));
                        FrgCuentaCumpleanios.setText(jsCliente.getString("fnacimiento"));
                        //datos del vendedor
                        if(sHelper.getSesion().getTpusuario().equals("S")) {
                            JSONObject jsVendedor = json.getJSONObject(Constantes.WS_DATA_ATTR).getJSONObject("vendedor");
                            FrgCuentaVendedor.setText(jsVendedor.getString("nombre"));
                            FrgCuentaVndtlf.setText(jsVendedor.getString("telefono"));
                            if(jsVendedor.getString("telefono").equals("-")) {
                                FrgCuentaBtnLlamada.setEnabled(false);
                            }
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
            Intent WebViewIntent = new Intent(getContext(), HTMLViewer.class);
            WebViewIntent.putExtra("html", result);
            startActivity(WebViewIntent);
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.FrgCuentaBtnLlamada:
                String NumeroTelefono = FrgCuentaVndtlf.getText().toString();
                if(!NumeroTelefono.equals("-")) {
                    VerificaPermisosLlamada();
                }
                break;
            /*case R.id.FrgCuentaBtnLogros:
                Intent IntentLogros = new Intent(getContext(), CuentaLogros.class);
                startActivity(IntentLogros);
                break;*/
            case R.id.FrgCuentaBtnLogout:
                new AlertDialog.Builder(getContext())
                        .setMessage("¿Desea cerrar la sesión? Si lo hace, deberá autenticarse nuevamente la próxima vez.")
                        .setCancelable(false)
                        .setPositiveButton("Si, cerrar mi sesión", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new SessionHelper(getContext()).doLogout();
                                getActivity().finish();
                                getActivity().startActivity(new Intent(getContext(),Login.class));
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constantes.PERMISSION_IMAGES: {
                int grantSize = grantResults.length;
                if (grantSize > 0) {
                    boolean ok = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(ok) {
                        LlamadaVendedor();
                    }
                }
                else {
                    Toast.makeText(getContext(), "Para utilizar la función de llamada al vendedor, deberá proporcionar los permisos para realizar llamadas.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    //metodos

    private void InitComponents() {
        Activity activity = getActivity();
        FrgCuentaImagenPerfil = (ImageView) activity.findViewById(R.id.FrgCuentaImagenPerfil);
        FrgCuentaEditarImg = (ImageButton) activity.findViewById(R.id.FrgCuentaEditarImg);
        FrgCuentaBtnCumpleanios = (ImageButton) activity.findViewById(R.id.FrgCuentaBtnCumpleanios);
        FrgCuentaBtnLlamada = (ImageButton) activity.findViewById(R.id.FrgCuentaBtnLlamada);
        FrgCuentaRzsocial = (TextView) activity.findViewById(R.id.FrgCuentaRzsocial);
        FrgCuentaRucDni = (TextView) activity.findViewById(R.id.FrgCuentaRucDni);
        FrgCuentaVendedor = (TextView) activity.findViewById(R.id.FrgCuentaVendedor);
        FrgCuentaVndtlf = (TextView) activity.findViewById(R.id.FrgCuentaVndtlf);
        FrgCuentaLblBlockVendedorHeader = (TextView) activity.findViewById(R.id.FrgCuentaLblBlockVendedorHeader);
        FrgCuentaLblBlockVendedorNombre = (RelativeLayout) activity.findViewById(R.id.FrgCuentaLblBlockVendedorNombre);
        FrgCuentaLblBlockVendedorTelefono = (RelativeLayout) activity.findViewById(R.id.FrgCuentaLblBlockVendedorTelefono);
        FrgCuentaEmail = (EditText) activity.findViewById(R.id.FrgCuentaEmail);
        FrgCuentaCumpleanios = (EditText) activity.findViewById(R.id.FrgCuentaCumpleanios);
        FrgCuentaTelefono = (EditText) activity.findViewById(R.id.FrgCuentaTelefono);
        FrgCuentaBtnGuardar = (Button) activity.findViewById(R.id.FrgCuentaBtnGuardar);
        //FrgCuentaBtnLogros = (Button) activity.findViewById(R.id.FrgCuentaBtnLogros);
        FrgCuentaBtnLogout = (Button) activity.findViewById(R.id.FrgCuentaBtnLogout);
        //
        sHelper = new SessionHelper(getContext());
        if(sHelper.getSesion().getTpusuario().equals("S")) {
            FrgCuentaRucDni.setText("RUC/DNI: " + sHelper.getSesion().getCodigo());
        }
        else {
            FrgCuentaLblBlockVendedorHeader.setVisibility(View.GONE);
            FrgCuentaLblBlockVendedorNombre.setVisibility(View.GONE);
            FrgCuentaLblBlockVendedorTelefono.setVisibility(View.GONE);
            FrgCuentaRucDni.setText("RUC/DNI: " + sHelper.getSesion().getDependiente());
        }
        FrgCuentaRzsocial.setText(sHelper.getSesion().getRsocial());
        FrgCuentaEmail.setText(sHelper.getSesion().getEmail());
        FrgCuentaTelefono.setText(sHelper.getSesion().getTelefono());
        //
        FrgCuentaBtnGuardar.setOnClickListener(Cuenta.this);
        //FrgCuentaBtnLogros.setOnClickListener(Cuenta.this);
        FrgCuentaBtnLogout.setOnClickListener(Cuenta.this);
        FrgCuentaBtnLlamada.setOnClickListener(Cuenta.this);
        //
        CargarDatosUsuario();
    }

    private void LlamadaVendedor() {
        String NumeroTelefono = FrgCuentaVndtlf.getText().toString();
        Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + NumeroTelefono));
        getActivity().startActivity(intent);
    }

    private void VerificaPermisosLlamada() {
        boolean READ_CALL_PHONE_PERMISSION = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
        if(READ_CALL_PHONE_PERMISSION) {
            LlamadaVendedor();
        }
        else {
            String[] ArrayPermisos = { Manifest.permission.CALL_PHONE };
            ActivityCompat.requestPermissions(getActivity(), ArrayPermisos, Constantes.PERMISSION_CALL_PHONE);
        }
    }

    private void CargarDatosUsuario() {
        Map<String, String> post = new HashMap<String, String>();
            post.put("empresa", String.valueOf(sHelper.getSesion().getEmpresa()));
            post.put("tipo", sHelper.getSesion().getTpusuario());
            if(sHelper.getSesion().getTpusuario().equals("S")) {
                post.put("codigo", sHelper.getSesion().getCodigo());
            }
            else {
                post.put("key", sHelper.getSesion().getToken());
            }
        WsCargarDatosUsuario(post);
    }

    //metodos ws

    private void WsCargarDatosUsuario(Map<String, String> post) {
        String URL = getResources().getString(R.string.base_url) + getResources().getString(R.string.info_perfil);
Log.d(Constantes.APP_NAME, URL);
Log.d(Constantes.APP_NAME, post.toString());
        WebService ws = new WebService(URL, post, getContext(), Cuenta.this);
        ws.execute();
    }
}

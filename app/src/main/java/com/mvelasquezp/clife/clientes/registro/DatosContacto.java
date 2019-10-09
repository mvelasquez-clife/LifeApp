package com.mvelasquezp.clife.clientes.registro;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mvelasquezp.clife.clientes.R;
import com.mvelasquezp.clife.clientes.activities.HTMLViewer;
import com.mvelasquezp.clife.clientes.tools.Constantes;
import com.mvelasquezp.clife.clientes.ws.Asynchtask;
import com.mvelasquezp.clife.clientes.ws.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DatosContacto extends AppCompatActivity implements Asynchtask, View.OnClickListener {

    String rucdni, puntos;

    TextView DatosContactoRazonSocial, DatosContactoNombreComercial, DatosContactoDireccion;
    EditText DatosContactoEmail, DatosContactoTelefono, DatosContactoCumpleanios, DatosContactoClave, DatosContactoClaveRepite;
    ImageButton DatosContactoDatePicker;
    Button DatosContactoConfirmar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_datos_contacto);
        IniciarComponentes();
    }

    @Override
    public void processFinish(String result) {
        JSONObject json;
        try {
            json = new JSONObject(result);
            if(json.getBoolean(Constantes.WS_STATE_PARAM)) {
                switch(json.getInt(Constantes.WS_REQUEST_ID)) {
                    case Constantes.WS_REQUEST_DATOS_CLIENTE:
                        JSONObject cliente = json.getJSONObject(Constantes.WS_DATA_ATTR).getJSONObject("cliente");
                        DatosContactoRazonSocial.setText(cliente.getString("rzsocial"));
                        DatosContactoNombreComercial.setText(cliente.getString("ncomercial"));
                        DatosContactoDireccion.setText(cliente.getString("direccion"));
                        DatosContactoEmail.setText(cliente.getString("email").equals("null") ? "" : cliente.getString("email"));
                        DatosContactoTelefono.setText(cliente.getString("telefono"));
                        puntos = cliente.getString("puntos");
                        break;
                    case Constantes.WS_REQUEST_GUARDAR_INFO_CLIENTE:
                        String email = json.getJSONObject(Constantes.WS_DATA_ATTR).getString("email");
                        Intent IntentCompletaRegistro = new Intent(DatosContacto.this, CompletaRegistro.class);
                            IntentCompletaRegistro.putExtra("email", email);
                        startActivity(IntentCompletaRegistro);
                        finish();
                        break;
                    default: break;
                }
            }
            else {
                Toast.makeText(this, json.getString(Constantes.WS_MESSAGE), Toast.LENGTH_LONG).show();
            }
        }
        catch(JSONException e) {
            Intent WebViewIntent = new Intent(this, HTMLViewer.class);
            WebViewIntent.putExtra("html", result);
            startActivity(WebViewIntent);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.DatosContactoDatePicker:
                Calendar c = Calendar.getInstance();
                int mes = c.get(Calendar.MONTH);
                int dia = c.get(Calendar.DAY_OF_MONTH);
                int anio = c.get(Calendar.YEAR);
                DatePickerDialog recogerFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        final int mesActual = month + 1;
                        String diaFormateado = (dayOfMonth < 10) ? "0" + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);
                        String mesFormateado = (mesActual < 10) ? "0" + String.valueOf(mesActual) : String.valueOf(mesActual);
                        DatosContactoCumpleanios.setText(diaFormateado + "-" + mesFormateado + "-" + year);
                    }
                },anio, mes, dia);
                recogerFecha.show();
                break;
            case R.id.DatosContactoConfirmar:
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Guardar mis datos")
                        .setMessage("¿Deseas guardar los datos ingresados?")
                        .setPositiveButton("Si, salir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                GuardarDatosCliente();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
                break;
            default: break;
        }
    }

    //eventos

    private void IniciarComponentes() {
        rucdni = getIntent().getStringExtra("rucdni");
        puntos = getIntent().getStringExtra("puntos");
        //elementos
        DatosContactoRazonSocial = (TextView) findViewById(R.id.DatosContactoRazonSocial);
        DatosContactoNombreComercial = (TextView) findViewById(R.id.DatosContactoNombreComercial);
        DatosContactoDireccion = (TextView) findViewById(R.id.DatosContactoDireccion);
        DatosContactoEmail = (EditText) findViewById(R.id.DatosContactoEmail);
        DatosContactoTelefono = (EditText) findViewById(R.id.DatosContactoTelefono);
        DatosContactoCumpleanios = (EditText) findViewById(R.id.DatosContactoCumpleanios);
        DatosContactoClave = (EditText) findViewById(R.id.DatosContactoClave);
        DatosContactoClaveRepite = (EditText) findViewById(R.id.DatosContactoClaveRepite);
        DatosContactoDatePicker = (ImageButton) findViewById(R.id.DatosContactoDatePicker);
        DatosContactoConfirmar = (Button) findViewById(R.id.DatosContactoConfirmar);
        //eventos
        DatosContactoDatePicker.setOnClickListener(this);
        DatosContactoConfirmar.setOnClickListener(this);
        //carga los datos
        CargarDatosCliente();
    }

    private void CargarDatosCliente() {
        Map<String, String> post = new HashMap<String, String>();
            post.put("rucdni", rucdni);
        WsCargarDatosCliente(post);
    }

    private void GuardarDatosCliente() {
        String email = DatosContactoEmail.getText().toString();
        String telefono = DatosContactoTelefono.getText().toString();
        String cumpleanios = DatosContactoCumpleanios.getText().toString();
        String clave = DatosContactoClave.getText().toString();
        String rclave = DatosContactoClaveRepite.getText().toString();
        if(email.equals("")) {
            Toast.makeText(DatosContacto.this, "Ingrese un email válido", Toast.LENGTH_SHORT).show();
            return;
        }
        if(telefono.equals("")) {
            Toast.makeText(DatosContacto.this, "Debe ingresar un teléfono de contacto", Toast.LENGTH_SHORT).show();
            return;
        }
        if(cumpleanios.equals("")) {
            Toast.makeText(DatosContacto.this, "Ingrese su fecha de cumpleaños", Toast.LENGTH_SHORT).show();
            return;
        }
        if(clave.equals("")) {
            Toast.makeText(DatosContacto.this, "Debe ingresar una contraseña para finalizar con el registro", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!clave.equals(rclave)) {
            Toast.makeText(DatosContacto.this, "Las claves ingresadas deben ser iguales", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> post = new HashMap<String, String>();
            post.put("rucdni", rucdni);
            post.put("email", email);
            post.put("telefono", telefono);
            post.put("cumpleanios", cumpleanios);
            post.put("clave", clave);
        WsGuardarDatosCliente(post);
    }

    //llamados al ws

    private void WsCargarDatosCliente(Map<String, String> post) {
        String URL = getResources().getString(R.string.base_url) + getResources().getString(R.string.cargar_info_cliente);
        WebService ws = new WebService(URL, post, DatosContacto.this, DatosContacto.this);
        ws.execute();
    }

    private void WsGuardarDatosCliente(Map<String, String> post) {
        String URL = getResources().getString(R.string.base_url) + getResources().getString(R.string.guardar_info_cliente);
        WebService ws = new WebService(URL, post, DatosContacto.this, DatosContacto.this);
        ws.execute();
    }
}

package com.mvelasquezp.clife.clientes.dueno;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mvelasquezp.clife.clientes.R;
import com.mvelasquezp.clife.clientes.layouts.Docpago;
import com.mvelasquezp.clife.clientes.tools.Constantes;
import com.mvelasquezp.clife.clientes.ws.Asynchtask;
import com.mvelasquezp.clife.clientes.ws.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DocumentoDetalle extends AppCompatActivity implements View.OnClickListener, Asynchtask {

    ImageButton docdetAtras;
    LinearLayout docdetPagos;
    TextView promdetTitulo, promdetImporte, promdetSaldo, promdetFvence, docdetLabelDetalle;
    Button docdetPagar;

    String documento;
    String vendedor;
    double SaldoDocumento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_doc_detalle);
        IniciarComponentes();
        CargaPagosDocumento();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.docdetAtras:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.docdetPagar:
                Intent IntentDocumentoPago = new Intent(DocumentoDetalle.this, DocumentoPago.class);
                    IntentDocumentoPago.putExtra("documento", documento);
                    IntentDocumentoPago.putExtra("importe", SaldoDocumento);
                startActivityForResult(IntentDocumentoPago, Constantes.REQUEST_PAGO_DOCUMENTO);
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
                    case Constantes.WS_REQUEST_DETALLE_PAGOS:
                        JSONArray jsPagos = json.getJSONObject(Constantes.WS_DATA_ATTR).getJSONArray("pagos");
                        JSONObject jsDocumento = json.getJSONObject(Constantes.WS_DATA_ATTR).getJSONObject("info");
                        EscribirListaPagos(jsPagos, jsDocumento);
                        break;
                    default: break;
                }
            }
            else {
                Toast.makeText(this, json.getString(Constantes.WS_MESSAGE), Toast.LENGTH_LONG).show();
            }
        }
        catch(JSONException e) {
            Log.e(Constantes.APP_NAME, e.getLocalizedMessage());
        }
        //
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constantes.REQUEST_PAGO_DOCUMENTO) {
            if(resultCode == RESULT_OK) {
                CargaPagosDocumento();
            }
        }
    }

    //

    //metodos

    private void IniciarComponentes() {
        docdetAtras = (ImageButton) findViewById(R.id.docdetAtras);
        docdetPagos = (LinearLayout) findViewById(R.id.docdetPagos);
        docdetAtras.setOnClickListener(this);
        promdetTitulo = (TextView) findViewById(R.id.promdetTitulo);
        promdetImporte = (TextView) findViewById(R.id.promdetImporte);
        promdetSaldo = (TextView) findViewById(R.id.promdetSaldo);
        promdetFvence = (TextView) findViewById(R.id.promdetFvence);
        docdetLabelDetalle = (TextView) findViewById(R.id.docdetLabelDetalle);
        docdetPagar = (Button) findViewById(R.id.docdetPagar);
        documento = getIntent().getStringExtra("documento");
        vendedor = getIntent().getStringExtra("vendedor");
        promdetTitulo.setText(documento);
        //
        docdetPagar.setOnClickListener(this);
    }

    private void EscribirListaPagos(JSONArray jsPagos, JSONObject jsDocumento) throws JSONException {
        docdetPagos.removeAllViews();
        int numPagos = jsPagos.length();
        Double importe = 0.0;
        Double saldo = 0.0;
        String fvence = "";
        //cabecera
        double tImporte = Double.parseDouble(jsDocumento.getString("importe"));
        double tSaldo = Double.parseDouble(jsDocumento.getString("deuda"));
        String tVence = jsDocumento.getString("fvence");
        //
        if(numPagos > 0) {
            for(int i = 0; i < numPagos; i++) {
                JSONObject iPago = jsPagos.getJSONObject(i);
                /*if(i == 0) {
                    importe = Double.parseDouble(iPago.getString("importe"));
                    saldo = importe;
                    fvence = iPago.getString("fvence");
                }
                else docdetPagos.addView(new Separator(DocumentoDetalle.this));*/
                Docpago item = new Docpago(DocumentoDetalle.this);
                String iLeft = "Pago en" + iPago.getString("detalle") + ", del " + iPago.getString("fecha");
                Double iImporte = Double.parseDouble(iPago.getString("pago"));
                String iRight = "S/ " + String.format("%,.2f", iImporte);
                item.setLabels(iLeft, iRight);
                docdetPagos.addView(item);
                //saldo -= iImporte;
            }
        }
        else {
            docdetLabelDetalle.setText("El documento no registra pagos");
        }
        /*promdetImporte.setText(String.format("%,.2f", importe));
        promdetSaldo.setText(String.format("%,.2f", saldo));
        promdetFvence.setText(fvence);*/
        promdetImporte.setText(String.format("%,.2f", tImporte));
        promdetSaldo.setText(String.format("%,.2f", tSaldo));
        promdetFvence.setText(tVence);
        SaldoDocumento = tSaldo;
    }

    private void CargaPagosDocumento() {
        Map<String, String> post = new HashMap<String, String>();
            post.put("documento", documento);
            post.put("vendedor", vendedor);
        WsCargaPagosDocumento(post);
    }

    //WS

    public void WsCargaPagosDocumento(Map<String, String> post) {
        String URL = getResources().getString(R.string.base_url) + getResources().getString(R.string.pagos_documento);
Log.d(Constantes.APP_NAME, URL);
Log.d(Constantes.APP_NAME, post.toString());
        WebService ws = new WebService(URL, post, DocumentoDetalle.this, DocumentoDetalle.this);
        ws.execute();
    }
}

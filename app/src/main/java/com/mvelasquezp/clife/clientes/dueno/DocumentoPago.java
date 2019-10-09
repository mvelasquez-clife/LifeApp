package com.mvelasquezp.clife.clientes.dueno;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.mvelasquezp.clife.clientes.Culqi.Card;
import com.mvelasquezp.clife.clientes.Culqi.Token;
import com.mvelasquezp.clife.clientes.Culqi.TokenCallback;
import com.mvelasquezp.clife.clientes.R;
import com.mvelasquezp.clife.clientes.activities.HTMLViewer;
import com.mvelasquezp.clife.clientes.session.SessionHelper;
import com.mvelasquezp.clife.clientes.tools.AppHelper;
import com.mvelasquezp.clife.clientes.tools.Constantes;
import com.mvelasquezp.clife.clientes.tools.Validation;
import com.mvelasquezp.clife.clientes.ws.Asynchtask;
import com.mvelasquezp.clife.clientes.ws.WebService;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class DocumentoPago extends AppCompatActivity implements View.OnClickListener, Asynchtask {

    String documento;
    double importe, ImporteOriginal;
    Validation validation;
    private String cameraFilePath;
    SessionHelper sessionHelper;
    AppHelper helper;
    ProgressDialog progress;

    TextView docPagoDocumento, docPagoImporte, kind_card;
    TextView docPagoNroTarjetaLabel, docPagoMesVenceLabel, docPagoTarjetaAnioLabel, docPagoTarjetaCvvLabel, docPagoNombreTitularLabel, docPagoEmailLabel;
    LinearLayout docPagoFormTarjeta;
    ImageView docPagoLogoTarjeta;
    ImageButton docPagoEditarImporte, docPagoAtras;
    Button docPagoPagoTarjeta, docPagoPoliticaDatos;
    EditText docPagoNroTarjeta, docPagoMesVence, docPagoTarjetaAnio, docPagoTarjetaCvv, docPagoNombreTitular, docPagoEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_doc_pago);
        IniciarComponentes();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.docPagoPagoTarjeta:
                PagarConTarjeta();
                break;
            case R.id.docPagoEditarImporte:
                FormularioIngresoNuevoImporte();
                break;
            case R.id.docPagoPoliticaDatos:
                Intent WebViewIntent = new Intent(this, HTMLViewer.class);
                WebViewIntent.putExtra("url", getResources().getString(R.string.privacy_url));
                startActivity(WebViewIntent);
                break;
            case R.id.docPagoAtras:
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
                    case Constantes.WS_REQUEST_PROCESA_PAGO:
                        ConfirmaPagoTarjeta();
                        break;
                    default: break;
                }
            }
            else {
                //Toast.makeText(DocumentoPago.this, json.getString(Constantes.WS_MESSAGE), Toast.LENGTH_LONG).show();
                AlertDialog.Builder builder1 = new AlertDialog.Builder(DocumentoPago.this);
                builder1.setMessage(json.getString(Constantes.WS_MESSAGE));
                builder1.setCancelable(false);
                builder1.setPositiveButton(
                    "Revisar los datos de mi tarjeta",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                AlertDialog alert11 = builder1.create();
                alert11.show();
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
        docPagoDocumento = (TextView) findViewById(R.id.docPagoDocumento);
        docPagoImporte = (TextView) findViewById(R.id.docPagoImporte);
        kind_card = (TextView) findViewById(R.id.kind_card);
        docPagoFormTarjeta = (LinearLayout) findViewById(R.id.docPagoFormTarjeta);
        docPagoLogoTarjeta = (ImageView) findViewById(R.id.docPagoLogoTarjeta);
        docPagoEditarImporte = (ImageButton) findViewById(R.id.docPagoEditarImporte);
        docPagoAtras = (ImageButton) findViewById(R.id.docPagoAtras);
        docPagoPagoTarjeta = (Button) findViewById(R.id.docPagoPagoTarjeta);
        docPagoNroTarjeta = (EditText) findViewById(R.id.docPagoNroTarjeta);
        docPagoMesVence = (EditText) findViewById(R.id.docPagoMesVence);
        docPagoTarjetaAnio = (EditText) findViewById(R.id.docPagoTarjetaAnio);
        docPagoTarjetaCvv = (EditText) findViewById(R.id.docPagoTarjetaCvv);
        docPagoNombreTitular = (EditText) findViewById(R.id.docPagoNombreTitular);
        docPagoEmail = (EditText) findViewById(R.id.docPagoEmail);
        docPagoNroTarjetaLabel = (TextView) findViewById(R.id.docPagoNroTarjetaLabel);
        docPagoMesVenceLabel = (TextView) findViewById(R.id.docPagoMesVenceLabel);
        docPagoTarjetaAnioLabel = (TextView) findViewById(R.id.docPagoTarjetaAnioLabel);
        docPagoTarjetaCvvLabel = (TextView) findViewById(R.id.docPagoTarjetaCvvLabel);
        docPagoNombreTitularLabel = (TextView) findViewById(R.id.docPagoNombreTitularLabel);
        docPagoEmailLabel = (TextView) findViewById(R.id.docPagoEmailLabel);
        docPagoPoliticaDatos = (Button) findViewById(R.id.docPagoPoliticaDatos);
        sessionHelper = new SessionHelper(DocumentoPago.this);
        helper = new AppHelper();
        validation = new Validation(DocumentoPago.this);
        docPagoLogoTarjeta.setImageDrawable(null);
        docPagoPagoTarjeta.setOnClickListener(this);
        docPagoEditarImporte.setOnClickListener(this);
        docPagoAtras.setOnClickListener(this);
        docPagoPoliticaDatos.setOnClickListener(this);
        docPagoNroTarjeta.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0){
                    docPagoTarjetaCvv.setEnabled(true);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                String text = docPagoNroTarjeta.getText().toString();
                if(s.length() == 0) {
                    docPagoNroTarjeta.setBackgroundResource(R.drawable.border_error);
                }

                if(validation.luhn(text)) {
                    docPagoNroTarjeta.setBackgroundResource(R.drawable.border_sucess);
                } else {
                    docPagoNroTarjeta.setBackgroundResource(R.drawable.border_error);
                }

                int cvv = validation.bin(text, kind_card, docPagoLogoTarjeta);
                if(cvv > 0) {
                    docPagoTarjetaCvv.setFilters(new InputFilter[]{new InputFilter.LengthFilter(cvv)});
                    docPagoTarjetaCvv.setEnabled(true);
                } else {
                    docPagoTarjetaCvv.setEnabled(false);
                    docPagoTarjetaCvv.setText("");
                }
                docPagoNroTarjetaLabel.setVisibility(text.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });
        //
        docPagoTarjetaAnio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String text = docPagoTarjetaAnio.getText().toString();
                if(validation.year(text)){
                    docPagoTarjetaAnio.setBackgroundResource(R.drawable.border_error);
                } else {
                    docPagoTarjetaAnio.setBackgroundResource(R.drawable.border_sucess);
                }
                //
                docPagoTarjetaAnioLabel.setVisibility(text.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });
        //
        docPagoMesVence.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String text = docPagoMesVence.getText().toString();
                if(validation.month(text)){
                    docPagoMesVence.setBackgroundResource(R.drawable.border_error);
                } else {
                    docPagoMesVence.setBackgroundResource(R.drawable.border_sucess);
                }
                //
                docPagoMesVenceLabel.setVisibility(text.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });
        //
        docPagoTarjetaCvv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                String text = docPagoTarjetaCvv.getText().toString();
                docPagoTarjetaCvvLabel.setVisibility(text.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });
        //
        docPagoNombreTitular.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                String text = docPagoNombreTitular.getText().toString();
                docPagoNombreTitularLabel.setVisibility(text.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });
        //
        docPagoEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                String text = docPagoEmail.getText().toString();
                docPagoEmailLabel.setVisibility(text.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });
        //
        Intent back = getIntent();
        documento = back.getStringExtra("documento");
        importe = back.getDoubleExtra("importe",-1);
        ImporteOriginal = importe;
        docPagoDocumento.setText("Importe a pagar - Documento " + documento);
        docPagoImporte.setText("S/ " + String.format("%,.2f", importe));
        docPagoPagoTarjeta.setText("Pagar ahora [S/ " + String.format("%,.2f",importe) + "]");
        //
        progress = new ProgressDialog(this);
        progress.setMessage("Validando informacion de la tarjeta");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

    }

    private void PagarConTarjeta() {
        String iNroTarjeta = docPagoNroTarjeta.getText().toString();
        String iCvvTarjeta = docPagoTarjetaCvv.getText().toString();
        int iMesVence = Integer.parseInt(docPagoMesVence.getText().toString());
        int iAnioVence = Integer.parseInt(docPagoTarjetaAnio.getText().toString());
        String iEmail = docPagoEmail.getText().toString();
        progress.show();
        Card card = new Card(iNroTarjeta,iCvvTarjeta,iMesVence,iAnioVence,iEmail);

        Token token = new Token(getResources().getString(R.string.culqi_public_key));

        token.createToken(DocumentoPago.this, card, new TokenCallback() {
            @Override
            public void onSuccess(JSONObject token) {
                try {
                    String cToken = token.get("id").toString();
                    ProcesarPagoTarjeta(cToken);
                }
                catch(JSONException ex) {
                }
                finally {
                    progress.hide();
                }
            }
            @Override
            public void onError(Exception error) {
                progress.hide();
            }
        });
    }

    private void ConfirmaPagoTarjeta() {
        new AlertDialog.Builder(DocumentoPago.this)
                .setTitle("Pago confirmado")
                .setMessage("Tu pago ha sido recibido y procesado correctamente. Ya puedes cerrar esta ventana.")
                .setCancelable(false)
                .setPositiveButton("Ententido", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(RESULT_OK);
                        finish();
                    }
                }).show();
    }

    private void ProcesarPagoTarjeta(String cToken) {
        Map<String, String> post = new HashMap<String, String>();
            post.put("ctoken", cToken);
            post.put("importe", String.valueOf(importe));
            post.put("descripcion", "Abono cta. cte. - " + documento);
            post.put("email", docPagoEmail.getText().toString());
            post.put("factura", documento);
            post.put("cliente", sessionHelper.getSesion().getCodigo());
            post.put("empresa", String.valueOf(sessionHelper.getSesion().getEmpresa()));
        WsProcesarPagoTarjeta(post);
    }
    
    private void FormularioIngresoNuevoImporte() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(DocumentoPago.this);
        builder.setTitle("Ingresar importe");
        ViewGroup viewGroup = (ViewGroup) getWindow().getDecorView().getRootView();
        View viewInflated = LayoutInflater.from(DocumentoPago.this).inflate(R.layout.ctrl_importe_picker,viewGroup,false);
        final EditText CtrlImportePickerInput = (EditText) viewInflated.findViewById(R.id.CtrlImportePickerInput);
        builder.setView(viewInflated);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                double NuevoImporte = Double.parseDouble(CtrlImportePickerInput.getText().toString());
                dialog.dismiss();
                ActualizarImporte(NuevoImporte);
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

    private void ActualizarImporte(double importe) {
        if(importe <= ImporteOriginal) {
            this.importe = importe;
            docPagoImporte.setText("S/ " + String.format("%,.2f", importe));
            docPagoPagoTarjeta.setText("Pagar ahora [S/ " + String.format("%,.2f", importe) + "]");
        }
        else {
            Toast.makeText(DocumentoPago.this,"El importe debe ser menor a S/ " + String.format("%,.2f", importe),Toast.LENGTH_SHORT).show();
        }
    }

    //ws

    private void WsProcesarPagoTarjeta(Map<String, String> post) {
        String URL = getResources().getString(R.string.base_url) + getResources().getString(R.string.procesar_pago);
        WebService ws = new WebService(URL, post, DocumentoPago.this, DocumentoPago.this);
        ws.execute();
    }
}

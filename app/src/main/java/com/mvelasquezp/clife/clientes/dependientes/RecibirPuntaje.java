package com.mvelasquezp.clife.clientes.dependientes;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.mvelasquezp.clife.clientes.R;
import com.mvelasquezp.clife.clientes.session.SessionHelper;
import com.mvelasquezp.clife.clientes.tools.Constantes;

public class RecibirPuntaje extends AppCompatActivity implements View.OnClickListener {

    SessionHelper sessionHelper;

    ImageView RecPuntosQr;
    Button RecPuntosBtnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_dep_rec_puntaje);
        IniciarComponentes();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    //metodos

    private void IniciarComponentes() {
        sessionHelper = new SessionHelper(RecibirPuntaje.this);
        RecPuntosQr = (ImageView) findViewById(R.id.RecPuntosQr);
        RecPuntosBtnVolver = (Button) findViewById(R.id.RecPuntosBtnVolver);
        RecPuntosBtnVolver.setOnClickListener(RecibirPuntaje.this);
        GeneraQrDependiente();
    }

    private void GeneraQrDependiente() {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(sessionHelper.getSesion().getToken(), BarcodeFormat.QR_CODE, 400, 400);
            RecPuntosQr.setImageBitmap(bitmap);
        }
        catch(Exception e) {
            Log.d(Constantes.APP_NAME, e.getMessage());
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.RecPuntosBtnVolver:
                setResult(RESULT_OK);
                finish();
                break;
            default: break;
        }
    }
}

package com.mvelasquezp.clife.clientes.layouts;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mvelasquezp.clife.clientes.R;

public class Docpago extends RelativeLayout {

    TextView ctrDocpagoDescripcion, ctrDocpagoImporte;

    public Docpago(Context context) {
        super(context);
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(infService);
        inflater.inflate(R.layout.ctrl_docpago, this, true);
        IniciarComponentes();
    }

    public void IniciarComponentes() {
        ctrDocpagoDescripcion = (TextView) findViewById(R.id.ctrDocpagoDescripcion);
        ctrDocpagoImporte = (TextView) findViewById(R.id.ctrDocpagoImporte);
    }

    public void setLabels(String left, String right) {
        ctrDocpagoDescripcion.setText(left);
        ctrDocpagoImporte.setText(right);
    }
}

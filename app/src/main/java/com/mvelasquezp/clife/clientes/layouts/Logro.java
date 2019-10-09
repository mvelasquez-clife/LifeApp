package com.mvelasquezp.clife.clientes.layouts;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mvelasquezp.clife.clientes.R;
import com.mvelasquezp.clife.clientes.entities.DboLogro;

import org.json.JSONException;
import org.json.JSONObject;

public class Logro extends RelativeLayout {

    DboLogro logro;

    TextView ctrlLogroNombre, ctrlLogroDescripcion, ctrlLogroAvance, ctrlLogroLblListo;
    LinearLayout ctrlLogroLayAvance;
    SeekBar ctrlLogroProgreso;
    Button ctrlLogroReclamar;

    public Logro(Context context) {
        super(context);
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(infService);
        inflater.inflate(R.layout.ctrl_logro_item, this, true);
        IniciarComponentes();
    }

    private void IniciarComponentes() {
        ctrlLogroNombre = (TextView) findViewById(R.id.ctrlLogroNombre);
        ctrlLogroDescripcion = (TextView) findViewById(R.id.ctrlLogroDescripcion);
        ctrlLogroAvance = (TextView) findViewById(R.id.ctrlLogroAvance);
        ctrlLogroLblListo = (TextView) findViewById(R.id.ctrlLogroLblListo);
        ctrlLogroLayAvance = (LinearLayout) findViewById(R.id.ctrlLogroLayAvance);
        ctrlLogroProgreso = (SeekBar) findViewById(R.id.ctrlLogroProgreso);
        ctrlLogroReclamar = (Button) findViewById(R.id.ctrlLogroReclamar);
    }

    public void AsignarLogro(JSONObject jsLogro) throws JSONException {
        logro = new DboLogro();
        logro.setId(jsLogro.getInt("id"));
            logro.setNombre(jsLogro.getString("logro"));
            logro.setDescripcion(jsLogro.getString("descripcion"));
            logro.setAvance(Integer.parseInt(jsLogro.getString("avance")));
            logro.setRequisito(Integer.parseInt(jsLogro.getString("requisito")));
            logro.setPuntos(Integer.parseInt(jsLogro.getString("puntos")));
            logro.setDisponible(jsLogro.getString("disponible").equals("S"));
            logro.setReclamado(jsLogro.getString("reclamado").equals("S"));
        ctrlLogroProgreso.setMax(logro.getRequisito());
        ctrlLogroProgreso.setProgress(logro.getAvance());
        ctrlLogroAvance.setText(logro.getAvance() + " / " + logro.getRequisito());
        if(logro.isReclamado()) {
            ctrlLogroLblListo.setVisibility(VISIBLE);
        }
        else {
            if(logro.isDisponible()) {
                ctrlLogroReclamar.setText("Reclamar " + logro.getPuntos() + " punto(s)");
                ctrlLogroReclamar.setVisibility(VISIBLE);
            }
            else {
                ctrlLogroLayAvance.setVisibility(VISIBLE);
            }
        }
        //
        ctrlLogroNombre.setText(logro.getNombre() + " [" + logro.getPuntos() + " pts]");
        ctrlLogroDescripcion.setText(logro.getDescripcion());
    }

    public void AsignarEvento(OnClickListener listener) {
        if(!logro.isReclamado() && logro.isDisponible()) {
            ctrlLogroReclamar.setOnClickListener(listener);
        }
    }

    public DboLogro getLogro() {
        return this.logro;
    }
}

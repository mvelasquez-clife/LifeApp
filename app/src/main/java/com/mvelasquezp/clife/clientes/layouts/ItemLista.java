package com.mvelasquezp.clife.clientes.layouts;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mvelasquezp.clife.clientes.R;

public class ItemLista extends RelativeLayout {

    ImageView ctrListaImagen;
    TextView ctrListaTprim, ctrListaTsec, ctrListaTbottom;
    ImageButton ctrListaAction;

    public ItemLista(Context context) {
        super(context);
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(infService);
        inflater.inflate(R.layout.ctrl_lista, this, true);
        IniciarComponentes();
    }

    private void IniciarComponentes() {
        ctrListaImagen = (ImageView) findViewById(R.id.ctrListaImagen);
        ctrListaTprim = (TextView) findViewById(R.id.ctrListaTprim);
        ctrListaTsec = (TextView) findViewById(R.id.ctrListaTsec);
        ctrListaTbottom = (TextView) findViewById(R.id.ctrListaTbottom);
        ctrListaAction = (ImageButton) findViewById(R.id.ctrListaAction);
        //ctrListaTsec.setTextColor(getResources().getColor(R.color.textDanger));
    }

    public void setImageVisible(boolean visible) {
        ctrListaImagen.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setButtonVisible(boolean visible) {
        ctrListaAction.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setLabels(String titulo, String info, String descripcion) {
        ctrListaTprim.setText(titulo);
        ctrListaTsec.setText(info);
        ctrListaTbottom.setText(descripcion);
    }

    public void ConfiguraItemPuntaje(String condicion) {
        ctrListaTprim.setTextColor(getResources().getColor(R.color.textHighlight));
        if(condicion.equals("S")) {
            ctrListaTsec.setTextColor(getResources().getColor(R.color.textSuccess));
        }
        else {
            ctrListaTsec.setTextColor(getResources().getColor(R.color.textDanger));
        }
        ctrListaTbottom.setTextColor(getResources().getColor(R.color.textMuted));
    }

    public void ResaltaTprim () {
        ctrListaTprim.setTextColor(getResources().getColor(R.color.btn_red_light));
    }

    public void resaltarTsec(int color) {
        ctrListaTsec.setTextColor(getResources().getColor(color));
    }

    public void setOnClickListenerEvent(OnClickListener listener) {
        ctrListaAction.setOnClickListener(listener);
    }
}

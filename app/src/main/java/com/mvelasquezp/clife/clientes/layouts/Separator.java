package com.mvelasquezp.clife.clientes.layouts;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.mvelasquezp.clife.clientes.R;

public class Separator extends LinearLayout {

    public Separator(Context context) {
        super(context);
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(infService);
        inflater.inflate(R.layout.ctrl_separator, this, true);
    }
}

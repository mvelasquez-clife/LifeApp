package com.mvelasquezp.clife.clientes.frg_dueno;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mvelasquezp.clife.clientes.R;

public class Error extends Fragment {

    public Error() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the webview for this fragment
        return inflater.inflate(R.layout.frg_dno_error, container, false);
    }
}

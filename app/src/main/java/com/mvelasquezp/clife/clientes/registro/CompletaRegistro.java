package com.mvelasquezp.clife.clientes.registro;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mvelasquezp.clife.clientes.R;

public class CompletaRegistro extends AppCompatActivity implements View.OnClickListener {

    TextView RegistroCompletoCorreo;
    Button RegistroCompletoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_completa_registro);
        IniciarComponentes();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.RegistroCompletoLogin:
                finish();
                break;
            default: break;
        }
    }

    //metodos

    private void IniciarComponentes() {
        RegistroCompletoCorreo = (TextView) findViewById(R.id.RegistroCompletoCorreo);
        RegistroCompletoLogin = (Button) findViewById(R.id.RegistroCompletoLogin);
        //
        RegistroCompletoCorreo.setText(getIntent().getStringExtra("email"));
        RegistroCompletoLogin.setOnClickListener(CompletaRegistro.this);
    }
}

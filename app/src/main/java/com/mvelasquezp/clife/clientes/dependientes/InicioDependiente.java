package com.mvelasquezp.clife.clientes.dependientes;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.mvelasquezp.clife.clientes.R;
import com.mvelasquezp.clife.clientes.frg_dueno.Cuenta;
import com.mvelasquezp.clife.clientes.frg_dueno.Error;
import com.mvelasquezp.clife.clientes.frg_dueno.Promociones;
import com.mvelasquezp.clife.clientes.frg_dependiente.Inicio;

public class InicioDependiente extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigation;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_dep_inicio);
        IniciarComponentes();
    }

    //metodos prro!

    private void IniciarComponentes() {
        bottomNavigation = (BottomNavigationView) findViewById(R.id.dep_navigator);
        bottomNavigation.setOnNavigationItemSelectedListener(this);
        fragmentManager = getSupportFragmentManager();
        //lanza el fragment por defecto
        bottomNavigation.setSelectedItemId(R.id.dependiente_home);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment;
        switch(menuItem.getItemId()) {
            case R.id.dependiente_home:
                fragment = new Inicio();
                break;
            case R.id.dependiente_promociones:
                fragment = new Promociones();
                break;
            case R.id.dependiente_cuenta:
                fragment = new Cuenta();
                break;
            default:
                fragment = new Error();
                break;
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.dep_container, fragment).commit();
        return true;
    }
}

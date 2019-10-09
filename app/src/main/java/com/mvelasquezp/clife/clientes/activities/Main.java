package com.mvelasquezp.clife.clientes.activities;

import android.content.Intent;
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
import com.mvelasquezp.clife.clientes.frg_dueno.Inicio;
import com.mvelasquezp.clife.clientes.frg_dueno.Pagos;
import com.mvelasquezp.clife.clientes.frg_dueno.Pedidos;
import com.mvelasquezp.clife.clientes.frg_dueno.Promociones;
import com.mvelasquezp.clife.clientes.session.Sesion;
import com.mvelasquezp.clife.clientes.session.SessionHelper;

public class Main extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigation;
    private FragmentManager fragmentManager;
    SessionHelper sHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        IniciarComponentes();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment;
        switch(menuItem.getItemId()) {
            case R.id.navigation_home:
                fragment = new Inicio();
                break;
            case R.id.navigation_promociones:
                fragment =  new Promociones();
                break;
            case R.id.navigation_pagos:
                fragment = new Pagos();
                break;
            case R.id.navigation_pedidos:
                fragment = new Pedidos();
                break;
            case R.id.navigation_cuenta:
                fragment = new Cuenta();
                break;
            default:
                fragment = new Error();
                break;
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment).commit();
        return true;
    }

    //metodos prro!

    private void IniciarComponentes() {
        sHelper = new SessionHelper(Main.this);
        Sesion sesion = sHelper.getSesion();
        bottomNavigation = (BottomNavigationView) findViewById(R.id.navigator);
        bottomNavigation.setOnNavigationItemSelectedListener(this);
        fragmentManager = getSupportFragmentManager();
        //lanza el fragment por defecto
        /*FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, new Inicio()).commit();*/
        if(sesion.isPuntos_habilitados()) {
            bottomNavigation.setSelectedItemId(R.id.navigation_home);
        }
        else {
            bottomNavigation.getMenu().findItem(R.id.navigation_home).setVisible(false);
            bottomNavigation.getMenu().findItem(R.id.navigation_promociones).setVisible(false);
            bottomNavigation.setSelectedItemId(R.id.navigation_pedidos);
        }
        //lanza el banner publicitario
        if (!sesion.isPublicidad()) {
            sesion.setPublicidad(true);
            sHelper.saveSession(sesion);
            Intent IntentPublicidad = new Intent(Main.this, HTMLViewer.class);
                IntentPublicidad.putExtra("url", "http://192.168.11.138/clientes/public/extras/campania/1");
                //IntentPublicidad.putExtra("url", "http://webpedidos.corporacionlife.com.pe/clientes/extras/campania/1");
            startActivity(IntentPublicidad);
        }
    }
}

package com.mvelasquezp.clife.clientes.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mvelasquezp.clife.clientes.R;
import com.mvelasquezp.clife.clientes.controls.Circle;
import com.mvelasquezp.clife.clientes.controls.CircleAngleAnimation;

public class Prueba extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba);
        //
        Circle circle = (Circle) findViewById(R.id.circle);
        CircleAngleAnimation animation = new CircleAngleAnimation(circle, 360);
        animation.setDuration(1000);
        circle.startAnimation(animation);
    }
}

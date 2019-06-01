package com.tesis.ags_r4.activity;

import android.app.Activity;
import android.os.Bundle;

public class SalirActivity extends Activity{

    public static Activity salir;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        salir=this;
    }

}

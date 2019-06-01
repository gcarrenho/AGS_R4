package com.ags.guideme.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;

import com.ags.guideme.AgsIntents;
import com.ags.guideme.R;

public class SeleccionGuiado extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Intent intent = getIntent();
		setContentView(R.layout.select_lugar);
		Window window = getWindow();
		final Activity activity = this;

		//Los editText estan en camelcase, los textview con " _ "
		View  porLug = window.findViewById(R.id.button_lugar_cargado);
		View porDir = window.findViewById(R.id.button_buscar_por_direc);
		
		porLug.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent guiar = new Intent(activity, AgsIntents.getSelecCityActivity());
				guiar.putExtra("boton", "guiar");
				activity.startActivity(guiar);
			}
			
		});
		
		porDir.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent guiar = new Intent(activity, AgsIntents.getGuiarPorDireccion());
				activity.startActivity(guiar);
			}
			
		});
	}
	
	
	
}

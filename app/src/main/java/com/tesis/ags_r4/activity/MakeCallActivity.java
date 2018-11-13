package com.tesis.ags_r4.activity;

import java.util.LinkedList;
import java.util.List;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.tesis.ags_r4.AgsIntents;
import com.tesis.ags_r4.Contacto;
import com.tesis.ags_r4.R;


public class MakeCallActivity extends Activity {

	public static final String APP_EXIT_KEY = "APP_EXIT_KEY";
	Contacto contact;
	List<Contacto> listContact= new LinkedList<Contacto>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean exit = false;
		Intent intent = getIntent();
		if(intent != null){
			if(intent.getExtras() != null && intent.getExtras().containsKey(APP_EXIT_KEY)){
				exit = true;
			}
		}
		
		Window window = getWindow();
		final Activity activity = this;
		contact= new Contacto(this);
		contact.open();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_llamadas);
		Toast.makeText(getBaseContext(), getResources().getString(R.string.volver), Toast.LENGTH_LONG)
		.show();
		
		View buttonEmer = (View) window.findViewById(R.id.button_emer);		
		buttonEmer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//hacer la llamada al contacto

				listContact=contact.getAllContact();
				if(listContact.isEmpty()){
					Toast.makeText(getBaseContext(), getResources().getString(R.string.no_contactos), Toast.LENGTH_LONG)
					.show();
				}else{
					String lname=listContact.get(0).getLastname();
					String tel=listContact.get(0).getTel();
					Toast.makeText(getBaseContext(), getResources().getString(R.string.llamar_a)+lname.toString(), Toast.LENGTH_LONG)
					.show();

					Intent call = new Intent(android.content.Intent.ACTION_CALL,Uri.parse("tel:"+tel)); 
					startActivity(call);
				}
				contact.close();	
				finish();
			}
		});
		
		View buttonCallLugar = (View) window.findViewById(R.id.button_call_lugar);

		buttonCallLugar.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				final Intent call = new Intent(activity, AgsIntents.getSelecCityActivity());
				call.putExtra("boton", "llamar");
				activity.startActivity(call);
			}
		});
				
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		contact.close();
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		contact.open();
	}
	
	
}

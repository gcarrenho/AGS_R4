package com.ags.guideme.activity;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import com.ags.guideme.Contacto;
import com.ags.guideme.R;

public class AdminContactActivity extends Activity{

	public static final String APP_EXIT_KEY = "APP_EXIT_KEY";
	Contacto contact;
	List<Contacto> listContact=new LinkedList<Contacto>();
	EditText oldlname,oldTel;
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
		Toast.makeText(getBaseContext(), getResources().getString(R.string.volver), Toast.LENGTH_LONG)
		.show();
		Window window = getWindow();
		final Activity activity = this;
		contact=new Contacto(this);
		contact.open();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.dato_contacto);
		
		listContact=contact.getAllContact();
		
		if (!listContact.isEmpty()){
			oldlname= (EditText) findViewById(R.id.textNomContact);
			oldTel= (EditText) findViewById(R.id.textTelContact);
			oldlname.setText(listContact.get(0).getLastname());
			oldTel.setText(listContact.get(0).getTel());
		}
		
		View buttonAcept = (View) window.findViewById(R.id.aceptarButtom);
		
				
		buttonAcept.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText lname= (EditText) findViewById(R.id.textNomContact);
				EditText tel= (EditText) findViewById(R.id.textTelContact);
				contact.setLastname(lname.getText().toString().toLowerCase());
				contact.setTel(tel.getText().toString().toLowerCase());
				//Antes de cargar verificar si hay alguno cargado para traerlo y remplazarlo
				if (!listContact.isEmpty()){	
					
					contact.actualizaContact(oldlname.getText().toString().toLowerCase(), contact, oldTel.getText().toString().toLowerCase());
					Toast.makeText(getBaseContext(), R.string.contact_act, Toast.LENGTH_LONG)
					.show();
				}else{
					contact.createContact(contact);
					Toast.makeText(getBaseContext(), R.string.contact_carg, Toast.LENGTH_LONG)
					.show();
				}
				contact.close();
				finish();
			}
		});
		
		View buttonCancel = (View) window.findViewById(R.id.cancelarButtom);

		buttonCancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				contact.close();
				finish();	
			}
		});
		
		
	}
}

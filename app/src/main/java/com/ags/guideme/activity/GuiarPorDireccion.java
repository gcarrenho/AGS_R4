package com.ags.guideme.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.ags.guideme.MapaToGuide;
import com.ags.guideme.R;
import com.ags.guideme.location.MyLocationListener;

/**
 * This class get location(latitud and longitud) from address, and then
 * execute the guiar mapa used this latitud and longitud, for this you
 * should pass the parameters latitud and longitud to guiar mapa */
@SuppressLint({ "NewApi", "Override" })
public class GuiarPorDireccion  extends AppCompatActivity {

	public static final String TAG = "GuiarPorDireccion";
	private LocationManager locManager;
	private double latitud,longitud;
	EditText direccion;
	EditText ciudad;
	EditText provincia;
	private static final int LOCATION_REQUEST=0;

	private static String[] LOCATION_PERMS={
			Manifest.permission.ACCESS_FINE_LOCATION
	};
	/**
	 * Root of the layout of this Activity.
	 */
	private View mLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ingreso_direccion);
		mLayout = findViewById(R.id.ingreso_dir_layout);
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Window window = getWindow();
		final Activity activity = this;

		//Los editText estan en camelcase, los textview con " _ "
		direccion = (EditText) window.findViewById(R.id.textCalleyAltura);
		ciudad = (EditText) window.findViewById(R.id.textCiudad);
		provincia = (EditText) window.findViewById(R.id.textProvincia);
		View  aceptar = window.findViewById(R.id.aceptarButtom);
		View cancelar = window.findViewById(R.id.cancelarButtom);
		final Intent intent=new Intent(this,MapaToGuide.class);
		final List<String> permissionsNeeded = new ArrayList<String>();
		final List<String> permissionsList = new ArrayList<String>();

		/**********/
		aceptar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());    
			    try {
			    	  List<Address> geoResults = geoCoder.getFromLocationName(direccion.getText().toString()+ciudad.getText().toString()+","+provincia.getText().toString(), 2);
			    	  if (geoResults.size()==0) {
			  	        //geoResults = geoCoder.getFromLocationName(direccion.getText().toString()+ciudad.getText().toString()+","+provincia.getText().toString(), 1);
			    		  Toast.makeText(getBaseContext(),getResources().getString(R.string.no_ubicado),Toast.LENGTH_LONG).show();
			    	  }
			  	    if (geoResults.size()>0) {
			  	        Address addr = geoResults.get(0); 
			  	        latitud= addr.getLatitude();
			  	        longitud= addr.getLongitude();
						/********/
						/*if (!addPermission(permissionsList, Manifest.permission.INTERNET))
							permissionsNeeded.add("Internet");
						if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
							permissionsNeeded.add("Acceso a localizacion");

						if (permissionsList.size() > 0) {
							if (permissionsNeeded.size() > 0) {
								// Need Rationale
								String message = "Necesitas otorgar permisos para " + permissionsNeeded.get(0);
								for (int i = 1; i < permissionsNeeded.size(); i++)
									message = message + ", " + permissionsNeeded.get(i);
								showMessageOKCancel(message,
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												ActivityCompat.requestPermissions(GuiarPorDireccion.this, permissionsList.toArray(new String[permissionsList.size()]),
														REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);

											}
										});
								return;
							}
							ActivityCompat.requestPermissions(GuiarPorDireccion.this, permissionsList.toArray(new String[permissionsList.size()]),
									REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
							return;
						}*/
						intent.putExtra("lat",latitud);
						intent.putExtra("lng", (Double) longitud);
						intent.putExtra("city", ciudad.getText().toString().toLowerCase());
						activity.startActivity(intent);
						/*****/
			  	    }
			    } catch (IOException e) {
			    	Toast.makeText(getBaseContext(),getResources().getString(R.string.no_ubicado),Toast.LENGTH_LONG).show();
			    }


			}

		});

		cancelar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}

		});



	}

	@Override
	protected void onResume() {
		super.onResume();
		configGps();
	}


	public void configGps() {

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			return;
		}
		if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			Toast.makeText(getBaseContext(), "GPS DESACTIVADO", Toast.LENGTH_LONG)
					.show();
			Intent settingsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			this.startActivityForResult(settingsIntent, 0);
		}

	}








}

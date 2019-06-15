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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.ags.guideme.MapaToGuide;
import com.ags.guideme.R;
import com.ags.guideme.location.MyLocationListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

/**
 * This class get location(latitud and longitud) from address, and then
 * execute the guiar mapa used this latitud and longitud, for this you
 * should pass the parameters latitud and longitud to guiar mapa */
@SuppressLint({ "NewApi", "Override" })
public class GuiarPorDireccion  extends AppCompatActivity {

	public static final String TAG = "GuiarPorDireccion";
	private LocationManager locManager;
	private double latitud, longitud;
	EditText direccion;
	//EditText ciudad;
	//EditText provincia;
	private MyLocationListener locListener;
	private static final int LOCATION_REQUEST = 0;

	private LocationCallback mLocationCallback;
	private FusedLocationProviderClient mFusedLocationProviderClient;

	private static String[] LOCATION_PERMS = {
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
		//ciudad = (EditText) window.findViewById(R.id.textCiudad);
		//provincia = (EditText) window.findViewById(R.id.textProvincia);
		View aceptar = window.findViewById(R.id.aceptarButtom);
		View cancelar = window.findViewById(R.id.cancelarButtom);
		final Intent intent = new Intent(this, MapaToGuide.class);
		final List<String> permissionsNeeded = new ArrayList<String>();
		final List<String> permissionsList = new ArrayList<String>();

		mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

		mLocationCallback = new LocationCallback() {
			@Override
			public void onLocationResult(LocationResult locationResult) {
				if (locationResult == null) {
					return;
				}
				Location location = locationResult.getLocations().get(0);
				latitud=location.getLatitude();
				longitud=location.getLongitude();

			}

		};

		/**********/
		aceptar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
				stopLocationUpdates();

				try {
					List<Address> geoResults = geoCoder.getFromLocationName(direccion.getText().toString() + getLocality(latitud,longitud) + "," + getState(latitud,longitud), 2);
					if (geoResults.size() == 0) {
						//geoResults = geoCoder.getFromLocationName(direccion.getText().toString()+ciudad.getText().toString()+","+provincia.getText().toString(), 1);
						Toast.makeText(getBaseContext(), getResources().getString(R.string.no_ubicado), Toast.LENGTH_LONG).show();
					}
					if (geoResults.size() > 0) {
						Address addr = geoResults.get(0);
						latitud = addr.getLatitude();
						longitud = addr.getLongitude();
						Log.i("Latitud despues",String.valueOf(latitud));
						Log.i("Longitud despues",String.valueOf(longitud));


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
						intent.putExtra("lat", latitud);
						intent.putExtra("lng", (Double) longitud);
						//intent.putExtra("city", ciudad.getText().toString().toLowerCase());
						activity.startActivity(intent);
						/*****/
					}
				} catch (IOException e) {
					Toast.makeText(getBaseContext(), getResources().getString(R.string.no_ubicado), Toast.LENGTH_LONG).show();
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
		startLocationUpdates();
	}


	public void configGps() {

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			return;
		}
		if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Toast.makeText(getBaseContext(), "GPS DESACTIVADO", Toast.LENGTH_LONG)
					.show();
			Intent settingsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			this.startActivityForResult(settingsIntent, 0);
		}

	}


	public String getState(Double lat, Double logn) {
		try {
			Geocoder geocoder = new Geocoder(this, Locale.getDefault());
			List<Address> list = geocoder.getFromLocation(lat, logn, 1);
			if (!list.isEmpty()) {
				Address address = list.get(0);
				return address.getAdminArea();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";

	}

	//metodo que retorna la localidad
	public String getLocality(Double lat, Double logn) {
		try {
			Geocoder geocoder = new Geocoder(this, Locale.getDefault());
			List<Address> list = geocoder.getFromLocation(lat, logn, 1);
			if (!list.isEmpty()) {
				Address address = list.get(0);
				return address.getLocality();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";

	}


	private void stopLocationUpdates() {
		mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
	}

	@SuppressLint("MissingPermission")
	private void startLocationUpdates() {
		LocationRequest locationRequest = LocationRequest.create()
				.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
				.setInterval(30 * 1000)
				.setFastestInterval(6 * 10000);
		mFusedLocationProviderClient.requestLocationUpdates(locationRequest,
				mLocationCallback, null /* Looper */);
	}

}

/*	class Task1 extends AsyncTask<String, Void, String> {

		@Override
		protected void onPressExceute(){
			progressBar.setVisibility(View.VISIBLE);
			btnIniciarSesion.setEnabled(false);
		}


		@Override
		protected ArrayList doInBackground(String... strings) {
			locListener = new MyLocationListener() {
				@Override
				public void onLocationChanged(Location loc) {
					if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {

					}
				}
			};

			try {
				Geocoder geocoder = new Geocoder(this, Locale.getDefault());
				List<Address> list = geocoder.getFromLocation(lat, logn, 1);
				if (!list.isEmpty()) {
					Address address = list.get(0);
					return address.getAddressLine(0);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		@Override
		protected String doInBackground(String... strings) {
			return null;
		}

		@Override
		protected void onPostExecute(String s){
			progressBar.setVisibility(VIEW.INVISIBLE);
			btnIniciarSesion.setEnable(true);
			Intent intent = new Intent(MainActivity.this, ResultadoActivity.class);
			intent.putExtra("usuario",txtUsuario.getText().toString());
			startActivity(intent)
		}
	}

}*/


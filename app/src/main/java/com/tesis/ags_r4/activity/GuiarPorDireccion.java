package com.tesis.ags_r4.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.tesis.ags_r4.GuiarMapa;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.tesis.ags_r4.R;
import com.tesis.ags_r4.location.MyLocationListener;

/**
 * This class get location(latitud and longitud) from address, and then
 * execute the guiar mapa used this latitud and longitud, for this you
 * should pass the parameters latitud and longitud to guiar mapa */
@SuppressLint({ "NewApi", "Override" })
public class GuiarPorDireccion  extends AppCompatActivity {

	public static final String TAG = "GuiarPorDireccion";
	private LocationManager locManager;
	private MyLocationListener locListener;
	Pair latLong = null;
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
		final Intent intent=new Intent(this,GuiarMapa.class);
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
			  	        /*intent.putExtra("lat",latitud);
						intent.putExtra("lng", (Double) longitud);
						intent.putExtra("city", ciudad.getText().toString());*/
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
						//activity.startActivity(intent);
			  	    }    
			    } catch (IOException e) {
			    	 //Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_LONG).show();
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
		//configGps();
	}


	/**
	 * Called when the 'show camera' button is clicked.
	 * Callback is defined in resource layout definition.
	 */
	public void showLocation(View view) {
		Log.i(TAG, "Guiado by direction accept button pressed. Checking permission.");
		// BEGIN_INCLUDE(camera_permission)
		// Check if the Camera permission is already available.
		/*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {

			requestLocationPermission();

		} else {*/
			// Location permissions is already available, show the guiado.
			Log.i(TAG,"CAMERA permission has already been granted. Displaying camera preview.");
			//LLamar para iniciar actividad de mapaguiado
			StartActivityGuiado();
		//}
		// END_INCLUDE(camera_permission)

	}

	/**
	 * Requests the Location permission.
	 * If the permission has been denied previously, a SnackBar will prompt the user to grant the
	 * permission, otherwise it is requested directly.
	 */
	/*private void requestLocationPermission() {
		Log.i(TAG, "CAMERA permission has NOT been granted. Requesting permission.");

		// BEGIN_INCLUDE(camera_permission_request)
		if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)) {
			// Provide an additional rationale to the user if the permission was not granted
			// and the user would benefit from additional context for the use of the permission.
			// For example if the user has previously denied the permission.
			Log.i(TAG,"Displaying camera permission rationale to provide additional context.");
			Snackbar.make(mLayout,  "Displaying camera permission rationale to provide additional context.",
					Snackbar.LENGTH_INDEFINITE)
			.setAction("Ok", new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					ActivityCompat.requestPermissions(GuiarPorDireccion.this,
							new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
							LOCATION_REQUEST);
				}
			})
			.show();
		} else {

			// Camera permission has not been granted yet. Request it directly.
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
					LOCATION_REQUEST);
		}
		// END_INCLUDE(camera_permission_request)
	}*/


	/**
	 * Callback received when a permissions request has been completed.
	 */
	/*@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults) {

		if (requestCode ==  LOCATION_REQUEST) {
			// BEGIN_INCLUDE(permission_result)
			// Received permission result for camera permission.
			Log.i(TAG, "Received response for Location permission request.");

			// Check if the only required permission has been granted
			if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// Camera permission has been granted, preview can be displayed
				Log.i(TAG, "CAMERA permission has now been granted. Showing preview.");
				Toast.makeText(getBaseContext(), "Camera Permission has been granted. Preview can now be opened.",
						Toast.LENGTH_SHORT).show();
			} else {
				Log.i(TAG, "CAMERA permission was NOT granted.");
				Toast.makeText(getBaseContext(), "CAMERA permission was NOT granted.",
						Toast.LENGTH_SHORT).show();
			}
			// END_INCLUDE(permission_result)

		}else {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}*/


	private void StartActivityGuiado() {
		/*getSupportFragmentManager().beginTransaction()
                .replace(R.id.sample_content_fragment, ContactsFragment.newInstance())
                .addToBackStack("contacts")
                .commit();*/
		final Activity activity = this;
		final Intent intent=new Intent(this,GuiarMapa.class);
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
				Toast.makeText(getBaseContext(),"Latitud "+latitud,Toast.LENGTH_LONG).show();
				intent.putExtra("lat",latitud);
				intent.putExtra("lng", (Double) longitud);
				intent.putExtra("city", ciudad.getText().toString().toLowerCase());
				activity.startActivity(intent);
			}    
		} catch (IOException e) {
			//Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_LONG).show();
			Toast.makeText(getBaseContext(),getResources().getString(R.string.no_ubicado),Toast.LENGTH_LONG).show();
		}	
	}

	private boolean addPermission(List<String> permissionsList, String permission) {
		if (ContextCompat.checkSelfPermission(GuiarPorDireccion.this, permission) != PackageManager.PERMISSION_GRANTED) {
			permissionsList.add(permission);
			// Check for Rationale Option
			if (!ActivityCompat.shouldShowRequestPermissionRationale(GuiarPorDireccion.this, permission))
				return false;
		}
		return true;
	}

	private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
		new AlertDialog.Builder(GuiarPorDireccion.this)
				.setMessage(message)
				.setPositiveButton(getResources().getString(R.string.aceptar), okListener)
				.setNegativeButton(getResources().getString(R.string.cancelar), null)
				.create()
				.show();
	}

	/*@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		switch (requestCode) {
			//Para un solo permiso
            /*case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(LoginActivity.this, "Permission OK", Toast.LENGTH_SHORT).show();
                    new GetClass(getBaseContext()).execute();


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(LoginActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);*/
		/*	case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
				Map<String, Integer> perms = new HashMap<String, Integer>();
				// Initial
				perms.put(Manifest.permission.INTERNET, PackageManager.PERMISSION_GRANTED);
				perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
				// Fill with results
				for (int i = 0; i < permissions.length; i++)
					perms.put(permissions[i], grantResults[i]);
				// Check for ACCESS_FINE_LOCATION
				if (perms.get(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
						&& perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
					// All Permissions Granted
					Toast.makeText(GuiarPorDireccion.this, "Permisos ok", Toast.LENGTH_SHORT)
							.show();

				} else {
					// Permission Denied
					Toast.makeText(GuiarPorDireccion.this, "Algun permiso fue denegado", Toast.LENGTH_SHORT)
							.show();
				}
			}
			break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
				// other 'case' lines to check for other
				// permissions this app might request
		}
	}*/

}

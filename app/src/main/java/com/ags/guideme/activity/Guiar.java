package com.ags.guideme.activity;;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


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
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.ags.guideme.GuiarMapa;
import com.ags.guideme.MapaToGuide;
import com.ags.guideme.R;
import com.ags.guideme.location.MyLocationListener;

/**
 * This class get location(latitud and longitud) from address, and then
 * execute the guiar mapa used this latitud and longitud, for this you
 * should pass the parameters latitud and longitud to guiar mapa */
@SuppressLint({ "NewApi", "Override" })
public class Guiar extends FragmentActivity {

    public static final String TAG = "GuiarPorDireccion";
    private LocationManager locManager;
    private MyLocationListener locListener;
    Pair latLong = null;
    private double latitud,longitud;
    EditText direccion;
    EditText ciudad;
    EditText provincia;
    private static final int LOCATION_REQUEST=0;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

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
		aceptar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
			    try {
			    	  List<Address> geoResults = geoCoder.getFromLocationName(direccion.getText().toString()+ciudad.getText().toString()+","+provincia.getText().toString(), 2);
			    	  if (geoResults.size()==0) {
			  	        //geoResults = geoCoder.getFromLocationName(direccion.getText().toString()+ciudad.getText().toString()+","+provincia.getText().toString(), 1);
			    		  Toast.makeText(getBaseContext(),"No se ha Podidio Encontrar, Compruebe la Busqueda",Toast.LENGTH_LONG).show();
			    	  }
			  	    if (geoResults.size()>0) {
			  	        Address addr = geoResults.get(0);
			  	        latitud= addr.getLatitude();
			  	        longitud= addr.getLongitude();
			  	        intent.putExtra("lat",latitud);
						intent.putExtra("lng", (Double) longitud);
						intent.putExtra("city", ciudad.getText().toString());
						activity.startActivity(intent);
			  	    }
			    } catch (IOException e) {
			    	 //Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_LONG).show();
			    	Toast.makeText(getBaseContext(),"No se ha Podidio Encontrar, Compruebe la Busqueda y/o Conectividad",Toast.LENGTH_LONG).show();
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
                Toast.makeText(getBaseContext(),"No se ha Podidio Encontrar, Compruebe la Busqueda",Toast.LENGTH_LONG).show();
            }
            if (geoResults.size()>0) {
                Address addr = geoResults.get(0);
                latitud= addr.getLatitude();
                longitud= addr.getLongitude();
                intent.putExtra("lat",latitud);
                intent.putExtra("lng", (Double) longitud);
                intent.putExtra("city", ciudad.getText().toString());
                activity.startActivity(intent);
            }
        } catch (IOException e) {
            //Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            Toast.makeText(getBaseContext(),"No se ha Podidio Encontrar, Compruebe la Busqueda y/o Conectividad",Toast.LENGTH_LONG).show();
        }
    }
}

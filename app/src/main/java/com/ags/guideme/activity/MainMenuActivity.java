package com.ags.guideme.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;

import com.ags.guideme.AgsIntents;
import com.ags.guideme.R;


public class MainMenuActivity extends Activity {

	public static final String APP_EXIT_KEY = "APP_EXIT_KEY";
	final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 125;

	//Esto solamente para hacer la animacion
	public static void onCreateMainMenu(Window window, final Activity activity){

	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean exit = false;
		if(getIntent() != null){
			Intent intent = getIntent();
			if(intent.getExtras() != null && intent.getExtras().containsKey(APP_EXIT_KEY)){
				exit = true;
			}
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		onCreateMainMenu(getWindow(), this);
		Window window = getWindow();
		final Activity activity = this;

		/********/

		/*******/
		View guiarButton = window.findViewById(R.id.guiaButton);
		guiarButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Ventana para seleccionar el lugar
				//una vez seleccionado, calcular la distancia desde mi ubicacion.
				// si la distancia no supera tantos metros(determinar cuantos) guiar caminando
				//sino identificar que garita de colectivo frena cerca yendo desde mi ubicacion. 
				//final Intent guiar = new Intent(activity, AgsIntents.getGuiarMapa());
				//MODIFICAR ACA, LLAMAR A LA VENTANA NUEVA DE SELECCIONAR LUGAR CARGADO O
				//ESCRIBIR DIRECCION
				final Intent selectGuiado = new Intent(activity, AgsIntents.getSeleccionGuiado());
				activity.startActivity(selectGuiado);
				
			}
		});

		//Evento si se realiza una pulzacion prolongada en el boton
		guiarButton.setOnLongClickListener(new View.OnLongClickListener(){
			public boolean onLongClick(View v) {
				return true;
			}

		});

		//Se presiono el boton abm
		View abmButton = window.findViewById(R.id.abmButton);
		abmButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {


				final Intent abm = new Intent(activity, AgsIntents.getAbmActivity());
				activity.startActivity(abm);
			}
		});

		//Evento que se ejecuta si se realiza un click prolongado sobre el boton.
		abmButton.setOnLongClickListener(new View.OnLongClickListener(){
			public boolean onLongClick(View v) {
				return true;
			}

		});

		//Se presiono el boton hacer llamada
		View llamadaButton = window.findViewById(R.id.LlamadaButton);
		llamadaButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent makeCall = new Intent(activity, AgsIntents.getMakeCallActivity());
				activity.startActivity(makeCall);
			}
		});

		//Se presiono el boton salir
		View salirButton = window.findViewById(R.id.salirButton);
		salirButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				System.exit(0);
			}
		});

		//Evento que se ejecuta si se realiza un click prolongado sobre el boton
		salirButton.setOnLongClickListener(new View.OnLongClickListener(){
			public boolean onLongClick(View v) {
				return true;
			}

		});


		View infButton = window.findViewById(R.id.informButton);
		infButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent inf = new Intent(activity, AgsIntents.getInfActivity());
				inf.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				activity.startActivity(inf);
			}
		});

		//Evento que se ejecuta si se realiza un click prolongado sobre el boton
		infButton.setOnLongClickListener(new View.OnLongClickListener(){
			public boolean onLongClick(View v) {
				return true;
			}

		});
	}


	@Override
	protected void onResume() {
		final List<String> permissionsNeeded = new ArrayList<String>();
		final List<String> permissionsList = new ArrayList<String>();
		super.onResume();
		if (!addPermission(permissionsList, Manifest.permission.INTERNET))
			permissionsNeeded.add("Internet");
		if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
			permissionsNeeded.add("Escribir en memoria");
		if (!addPermission(permissionsList, Manifest.permission.CALL_PHONE))
			permissionsNeeded.add("Realizar llamadas");
		if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
			permissionsNeeded.add("Acceso a localizacion");

		if (permissionsList.size() > 0) {
			if (permissionsNeeded.size() > 0) {
				// Need Rationale
				String message = getResources().getString(R.string.necesita_permisos) + permissionsNeeded.get(0);
				for (int i = 1; i < permissionsNeeded.size(); i++)
					message = message + ", " + permissionsNeeded.get(i);
				showMessageOKCancel(message,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								ActivityCompat.requestPermissions(MainMenuActivity.this, permissionsList.toArray(new String[permissionsList.size()]),
										REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);

							}
						});
				return;
			}
			ActivityCompat.requestPermissions(MainMenuActivity.this, permissionsList.toArray(new String[permissionsList.size()]),
					REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
			return;
		}
	}

	public void setLocation(Location loc) {
		//Obtener la direccion de la calle a partir de la latitud y la longitud 
		if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
			try {
				Geocoder geocoder = new Geocoder(this, Locale.getDefault());
				List<Address> list = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
				if (!list.isEmpty()) {
					Address address = list.get(0);
					Toast.makeText(getBaseContext(), "Mi Direccion es: "+address.getAddressLine(0), Toast.LENGTH_LONG)
					.show();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean addPermission(List<String> permissionsList, String permission) {
		if (ContextCompat.checkSelfPermission(MainMenuActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
			permissionsList.add(permission);
			// Check for Rationale Option
			if (!ActivityCompat.shouldShowRequestPermissionRationale(MainMenuActivity.this, permission))
				return false;
		}
		return true;
	}

	private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
		new AlertDialog.Builder(MainMenuActivity.this)
				.setMessage(message)
				.setPositiveButton(R.string.aceptar, okListener)
				.setNegativeButton(R.string.cancelar, null)
				.create()
				.show();
	}

	@Override
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
			case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
				Map<String, Integer> perms = new HashMap<String, Integer>();
				// Initial
				perms.put(Manifest.permission.INTERNET, PackageManager.PERMISSION_GRANTED);
				/*perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);*/
				perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
				perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
				perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
				// Fill with results
				for (int i = 0; i < permissions.length; i++)
					perms.put(permissions[i], grantResults[i]);
				// Check for ACCESS_FINE_LOCATION
				if (perms.get(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
						/*&& perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED*/
						&& perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
						&& perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
						&& perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
					// All Permissions Granted
					Toast.makeText(MainMenuActivity.this, "Permisos ok", Toast.LENGTH_SHORT)
							.show();
					//new GetClass(getBaseContext()).execute();
					//insertDummyContact();
				} else {
					// Permission Denied
					Toast.makeText(MainMenuActivity.this, "Algun permiso fue denegado", Toast.LENGTH_SHORT)
							.show();
				}
			}
			break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
				// other 'case' lines to check for other
				// permissions this app might request
		}
	}

}

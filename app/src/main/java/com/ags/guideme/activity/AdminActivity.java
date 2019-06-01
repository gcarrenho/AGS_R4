package com.tesis.ags_r4.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Toast;
import com.tesis.ags_r4.AgsIntents;

import com.tesis.ags_r4.GuiarMapa;
import com.tesis.ags_r4.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminActivity extends Activity {

	public static final String APP_EXIT_KEY = "APP_EXIT_KEY";
	final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

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
		final List<String> permissionsNeeded = new ArrayList<String>();
		final List<String> permissionsList = new ArrayList<String>();
		Window window = getWindow();
		final Activity activity = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.admin);
		Toast.makeText(getBaseContext(), getResources().getString(R.string.volver), Toast.LENGTH_LONG)
		.show();
		
		View contact = (View) window.findViewById(R.id.button_cargar_numero);
		
		contact.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				/**********/
								/*if (!addPermission(permissionsList, Manifest.permission.INTERNET))
									permissionsNeeded.add("Internet");*/
						if (!addPermission(permissionsList, Manifest.permission.READ_CONTACTS))
							permissionsNeeded.add(getResources().getString(R.string.leer_contactos));
						if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
							permissionsNeeded.add(getResources().getString(R.string.escribir_en_memoria));
						/*if (!addPermission(permissionsList, Manifest.permission.CALL_PHONE))
							permissionsNeeded.add("Realizar llamadas");*/

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
														ActivityCompat.requestPermissions(AdminActivity.this, permissionsList.toArray(new String[permissionsList.size()]),
																REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);

													}
												});
										return;
									}
									ActivityCompat.requestPermissions(AdminActivity.this, permissionsList.toArray(new String[permissionsList.size()]),
											REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
									return;
								}
				final Intent adminContact = new Intent(activity, AgsIntents.getAdminContactActivity());
				activity.startActivity(adminContact);
			}
		});
		
	}

	private boolean addPermission(List<String> permissionsList, String permission) {
		if (ContextCompat.checkSelfPermission(AdminActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
			permissionsList.add(permission);
			// Check for Rationale Option
			if (!ActivityCompat.shouldShowRequestPermissionRationale(AdminActivity.this, permission))
				return false;
		}
		return true;
	}

	private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
		new AlertDialog.Builder(AdminActivity.this)
				.setMessage(message)
				.setPositiveButton(getResources().getString(R.string.aceptar), okListener)
				.setNegativeButton(getResources().getString(R.string.cancelar), null)
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
				perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
				perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
				// Fill with results
				for (int i = 0; i < permissions.length; i++)
					perms.put(permissions[i], grantResults[i]);
				// Check for ACCESS_FINE_LOCATION
				if (perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
						&& perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
					// All Permissions Granted
					Toast.makeText(AdminActivity.this, getResources().getString(R.string.permiso_exitoso), Toast.LENGTH_SHORT)
							.show();
					final Activity activity = this;
					final Intent intent=new Intent(this,GuiarMapa.class);
					final Intent adminContact = new Intent(activity, AgsIntents.getAdminContactActivity());
					activity.startActivity(adminContact);
				} else {
					// Permission Denied
					Toast.makeText(AdminActivity.this, getResources().getString(R.string.permiso_denegado), Toast.LENGTH_SHORT)
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

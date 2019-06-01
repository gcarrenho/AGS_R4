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
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.Preference;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView.BufferType;
import android.location.Address;

import com.ags.guideme.Lugar;
import com.ags.guideme.R;
import com.ags.guideme.location.MyLocationListener;


@SuppressLint("NewApi")
public class CargarActivity extends Activity implements OnInitListener {

	public static final String APP_EXIT_KEY = "APP_EXIT_KEY";
	private static final BufferType EDITABLE = null;
	private Lugar lugarBd;
	private TextToSpeech tts;
	private int campo;
	private double latitud = 0.0, longitud = 0.0;
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1;
	private Preference about;
	private LocationManager locManager;
	private MyLocationListener locListener;
	protected PowerManager.WakeLock wakelock;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean exit = false;
		Intent intent = getIntent();
		if (intent != null) {
			if (intent.getExtras() != null && intent.getExtras().containsKey(APP_EXIT_KEY)) {
				exit = true;
			}
		}
		final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		this.wakelock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "etiqueta");
		wakelock.acquire();
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		lugarBd = new Lugar(this);
		lugarBd.open();
		tts = new TextToSpeech(this, this);
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locListener = new MyLocationListener() {
			@Override
			public void onLocationChanged(Location loc) {
				if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
					latitud = loc.getLatitude();
					longitud = loc.getLongitude();
				}
			}
		};
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.cargar_lugar);
		Window window = getWindow();
		String accion = intent.getStringExtra("accion");
		if (accion.equals("editar")) {
			String nombre = intent.getStringExtra("nombre");
			String tipo = intent.getStringExtra("tipo");
			String telefono = intent.getStringExtra("tel");
			double lat = intent.getDoubleExtra("lat", 0.0);
			double lon = intent.getDoubleExtra("long", 0.0);
			String localidad = intent.getStringExtra("city");
			EditText nom = (EditText) findViewById(R.id.textNomLugar);
			nom.setText(nombre);
			EditText cat = (EditText) findViewById(R.id.textTipoLugar);
			cat.setText(tipo);
			EditText tel = (EditText) findViewById(R.id.textTelLugar);
			tel.setText(telefono);
			//con estos datos llenar los campos
			this.editar(nombre, lat, lon, localidad);
		} else {
			this.cargar();
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return;
			}
			locManager.removeUpdates(locListener);
		}
		View cancelarButton = window.findViewById(R.id.cancelarButtom);
		//Evento que escucha el click sobre el boton cancelar
		cancelarButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				lugarBd.close();
				setResult(RESULT_CANCELED);
				if (ActivityCompat.checkSelfPermission(CargarActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CargarActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
					// TODO: Consider calling
					//    ActivityCompat#requestPermissions
					// here to request the missing permissions, and then overriding
					//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
					//                                          int[] grantResults)
					// to handle the case where the user grants the permission. See the documentation
					// for ActivityCompat#requestPermissions for more details.
					return;
				}
				locManager.removeUpdates(locListener);
				finish();
			}
		});

		//Evento que escucha un click prolongado sobre el boton cancelar
		cancelarButton.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				return true;
			}

		});
	}


	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 * Llamada cuando la actividad va a empezar a
	 * interactuar con el usuario, en este punto es 
	 * el último punto antes de que el usuario ya vea la actividad
	 * y pueda empezar a interactuar con ella. Siempre despues de un 
	 * onResume() viene un onPause().
	 */
	@Override
	protected void onResume() {
		super.onResume();
		this.configGps();
	}


	/*
	 *  LLamada cuando el sistema va a empezar una nueva actividad.
	 *  Ésta necesita parar animaciones, y parar todo lo que esté haciendo.
	 *  Hay que intentar que esta llamada dure poco tiempo, porque hasta que no 
	 *  se ejecute este método no arranca la siguiente actividad. Después de esta 
	 *  llamada puede venir un onResume() si la actividad vuelve a primer plano o
	 *  un onStop() si se hace invisible para el usuario.
	 *   
	 */

	@Override
	protected void onPause() {
		super.onPause();
		//locManager.removeUpdates(locListener);
		if (this.wakelock != null && wakelock.isHeld()) {
			this.wakelock.release();
		}
	}


	@Override
	protected void onStop() {
		super.onStop();
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		locManager.removeUpdates(locListener);
	}


	@Override
	public void onInit(int code) {
		if (code == TextToSpeech.SUCCESS) {
			tts.setLanguage(Locale.getDefault());
		} else {
			tts = null;
			Toast.makeText(this, "Failed to initialize TTS engine.", Toast.LENGTH_SHORT).show();

		}

	}

	private void startVoiceRecognitionActivity() {
		// Definición del intent para realizar en análisis del mensaje
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		// Indicamos el modelo de lenguaje para el intent
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		// Definimos el mensaje que aparecerá 
		// intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Diga, Llamar a ...");
		// Lanzamos la actividad esperando resultados

		//HAY UN PROBLEMA LA MINA DEL TALKBACK DICE "BUSQUDA DE GOOGLE", CUANDO ESTA RECONOCIENDO.
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}

	//Recogemos los resultados del reconocimiento de voz
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
			//El intent nos envia un ArrayList aunque en este caso solo 
			//utilizaremos la pos.0
			if (campo == 1) {
				ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				EditText nombre = (EditText) findViewById(R.id.textNomLugar);
				nombre.setText(matches.get(0));


			} else if (campo == 2) {
				ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				EditText tipo = (EditText) findViewById(R.id.textTipoLugar);
				tipo.setText(matches.get(0));
			} else if (campo == 3) {
				ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				EditText tel = (EditText) findViewById(R.id.textTelLugar);
				tel.setText(matches.get(0));
			}
		}
	}


	//Metodo que se encarga del alta de un nuevo lugar
	public void cargar() {
		Window window = getWindow();
		//this.eventosSobreCampos();
		//Aceptar dar de alta en la base de datos
		//cancelar, salir volver ?atras?
		View aceptarButton = window.findViewById(R.id.aceptarButtom);

		//Evento que escucha el click sobre el boton aceptar
		aceptarButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Falta obtener las coordenadas y setearlas..
				EditText nombre = (EditText) findViewById(R.id.textNomLugar);
				lugarBd.setNombre(nombre.getText().toString().toLowerCase());
				EditText cat = (EditText) findViewById(R.id.textTipoLugar);
				lugarBd.setCategoria(cat.getText().toString().toLowerCase());
				EditText tel = (EditText) findViewById(R.id.textTelLugar);
				String telefono = tel.getText().toString().replace(" ", "");//El tel va a ser dictado eliminar espacios en blanco.
				if (telefono.isEmpty()) {
					telefono = "0";
				}

				//final int telInt = Integer.parseInt(telefono);
				lugarBd.setTelefono(telefono);
				if (latitud == 0.0 && longitud == 0.0) {
					Toast.makeText(getBaseContext(), R.string.obt_loc, Toast.LENGTH_LONG)
							.show();
				} else if (cat.getText().toString().length() == 0) {
					Toast.makeText(getBaseContext(), R.string.adv_tdl, Toast.LENGTH_LONG)
							.show();
				} else if (nombre.getText().toString().length() == 0) {
					Toast.makeText(getBaseContext(), R.string.adv_cn, Toast.LENGTH_LONG)
							.show();
				} else {
					try {
						String localidad = ObtLocalidad(latitud, longitud).toLowerCase();
						if (lugarBd.ExisteLugar(nombre.getText().toString().toLowerCase(), localidad)) {//true si tiene algo
							//lanzar un cartel diciendo que ya existe cargado un lugar con dicho nombre poner otro
							//identificador , y poner  el foco en el campo nombre	
							Toast.makeText(getBaseContext(), R.string.exi_l, Toast.LENGTH_LONG)
									.show();

						} else {
							lugarBd.setLocalidad(localidad);

							//Toast.makeText(getBaseContext(), R.string.loc_sat, Toast.LENGTH_LONG)
							//.show();
							lugarBd.setLatitud(latitud);
							lugarBd.setLongitud(longitud);
							//lugarBd.setLocalidad(localidad);
							lugarBd.createLugar(lugarBd);
							Toast.makeText(getBaseContext(), R.string.l_carg, Toast.LENGTH_LONG)
									.show();
							lugarBd.close();
							finish();
						}
					} catch (Throwable t) {
						Log.e("ERROOOOOOOR", "Mensaje de error", t);
					}

				}
			}
		});

		//Evento que escucha un click prolongado sobre el boto aceptar
		aceptarButton.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				return true;
			}

		});
	}


	//Metedo que busca el lugar, si existe remplaza los campos por los actuales
	//y actualiza con los nuevos datos.
	public void editar(final String nombre, final double lat, final double lon, final String localidad) {
		Window window = getWindow();
		//this.eventosSobreCampos();
		//Aceptar dar de alta en la base de datos
		View aceptarButton = window.findViewById(R.id.aceptarButtom);

		//Evento que escucha el click sobre el boton aceptar
		aceptarButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText nom = (EditText) findViewById(R.id.textNomLugar);
				lugarBd.setNombre(nom.getText().toString().toLowerCase());
				EditText cat = (EditText) findViewById(R.id.textTipoLugar);
				lugarBd.setCategoria(cat.getText().toString().toLowerCase());
				EditText tel = (EditText) findViewById(R.id.textTelLugar);
				lugarBd.setLatitud(lat);
				lugarBd.setLongitud(lon);
				lugarBd.setLocalidad(localidad);
				String telefono = tel.getText().toString().replace(" ", "");
				;//El tel va a ser dictado eliminar espacios en blanco.
				if (telefono.isEmpty()) {
					telefono = "0";
				}
				//final int telInt = Integer.parseInt(telefono);
				lugarBd.setTelefono(telefono);

				if (lugarBd.ExisteLugar(nombre.toLowerCase(), localidad)) {
					//si existe actualizar la bd,
					//Actualizar Lista de lugares.
					lugarBd.actualizaLugar(nombre, lugarBd, localidad);
					setResult(RESULT_OK);
					finish();

				} else {//si no existe lanzar un cartel

				}

			}
		});

		//Evento que escucha un click prolongado sobre el boto aceptar
		aceptarButton.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				return true;
			}

		});
	}


	/**
	 * Metodo encargado de bloquear el teclado virtual
	 * y activar el reconocedor de voz para la carga de los campos.
	 */
	public void eventosSobreCampos() {
		Window window = getWindow();
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		final List<Lugar> listaLugares = lugarBd.getAllLugares();
		window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		//Eventos sobre los campos de texto.
		//----------------------------------
		//Evento sobre campo de texto nombre del lugar
		final View lugarTexNom = window.findViewById(R.id.textNomLugar);
		//Se sobreescribe el evento de cuando se cambia el foco para que
		//no figure el teclado virtual en pantalla
		lugarTexNom.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				lugarTexNom.post(new Runnable() {
					@Override
					public void run() {
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(lugarTexNom.getWindowToken(), 0);
					}
				});
			}
		});

		//Evento que escucha el click sobre el campo
		lugarTexNom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				campo = 1;
				startVoiceRecognitionActivity();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(lugarTexNom.getWindowToken(), 0);
			}
		});


		//Evento que escucha un click prolongado sobre el campo de texto
		lugarTexNom.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				//Abrir el reconocedor de voz
				return true;
			}

		});

		//Evento sobre el campo de texto Tipo de Lugar
		final View tipoLugarText = window.findViewById(R.id.textTipoLugar);

		//Se sobreescribe el evento de cuando se cambia el foco para que
		//no figure el teclado virtual en pantalla
		tipoLugarText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				tipoLugarText.post(new Runnable() {
					@Override
					public void run() {
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(tipoLugarText.getWindowToken(), 0);
					}
				});
			}
		});

		//Evento que escucha el click sobre el campo
		tipoLugarText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Abrir el reconocedor de voz
				campo = 2;
				startVoiceRecognitionActivity();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(tipoLugarText.getWindowToken(), 0);
			}
		});

		//Evento que escucha un click prolongado sobre el campo de texto
		tipoLugarText.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				return true;
			}

		});

		//Evento sobre el campo de texto Telefono del lugar
		final View telLugarText = window.findViewById(R.id.textTelLugar);

		telLugarText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				telLugarText.post(new Runnable() {
					@Override
					public void run() {
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(telLugarText.getWindowToken(), 0);
					}
				});
			}
		});

		//Evento que escucha el click sobre el campo
		telLugarText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				campo = 3;
				//Abrir el reconocedor de vos
				startVoiceRecognitionActivity();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(telLugarText.getWindowToken(), 0);
			}
		});

		//Evento que escucha un click prolongado sobre el campo de texto
		telLugarText.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				return true;
			}

		});
	}


	public void configGps() {

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) locListener);

		if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			Toast.makeText(getBaseContext(), "GPS DESACTIVADO", Toast.LENGTH_LONG)
			.show(); 
			Intent settingsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			this.startActivityForResult(settingsIntent, 0);
		}

		/*LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		MyLocationListener locListener = new MyLocationListener();
		locListener.setMainActivity(this);
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,(LocationListener) locListener);

		Criteria criterio = new Criteria();
	    criterio.setCostAllowed(false);
	    criterio.setAltitudeRequired(false);
	    criterio.setAccuracy(Criteria.ACCURACY_FINE);


	    String proveedor = locManager.getBestProvider(criterio, true);*/
		// log.("Mejor proveedor: " + proveedor + "\n");
		//log("Comenzamos con la última localización conocida:");
		// Location localizacion = locManager.getLastKnownLocation(proveedor);
		//LocationProvider info = locManager.getProvider(proveedor);
	}		
	
	public String ObtLocalidad(Double lat, Double logn){
		Address address=null;
		try{
			Geocoder geocoder = new Geocoder(this, Locale.getDefault());
			List<Address> list = geocoder.getFromLocation(lat, logn, 1);
			
			if (!list.isEmpty()) {
				address = list.get(0);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return address.getLocality();
	}
}

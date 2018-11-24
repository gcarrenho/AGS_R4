package com.tesis.ags_r4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.tesis.ags_r4.file.MakeFile;
import com.tesis.ags_r4.location.MyLocation;
import com.tesis.ags_r4.location.MyLocationListener;
import com.tesis.ags_r4.navigation.GMapV2Direction;
import com.tesis.ags_r4.navigation.GetDirectionsAsyncTask;
import com.tesis.ags_r4.navigation.Instructions;

public class GuiarMapa extends AppCompatActivity implements OnMapReadyCallback,
		ActivityCompat.OnRequestPermissionsResultCallback {
	/**
	 * Note that this may be null if the Google Play services APK is not available.
	 */
	/** Referencia al TAG de log. */
	private static final String TAG = "[DirectoAndroidV2_EJ2]";
	private LocationManager locManager;
	private MyLocationListener locListener;
	private double lat, mlat, lngDest = 0, latDest = 0;
	private double lng, mlng, latLineStopDest, lngLineStopDest, latLineStopMy, lngLineStopMy;
	private MyLocation mloc;
	/** Nombre del proveedor de localización. */
	private transient String proveedor;
	private static float[] results = new float[2];
	private MakeFile mfile = new MakeFile();
	private ArrayList<Instructions> inst = new ArrayList<Instructions>();
	private String textInst = new String();
	private String localidad = new String();
	private Marker marker;
	private int acces, stop = 0, llegoDest = 0;
	private int l = 1, cartel = 1, colec;
	private SupportMapFragment mMapSupport;

	/**
	 * Request code for location permission request.
	 *
	 * @see #onRequestPermissionsResult(int, String[], int[])
	 */
	private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

	/**
	 * Flag indicating whether a requested permission has been denied after returning in
	 * {@link #onRequestPermissionsResult(int, String[], int[])}.
	 */
	private boolean mPermissionDenied = false;

	private GoogleMap mMap;

	/**
	 * Root of the layout of this Activity.
	 */
	private View mLayout;

	//private SensorManager mSensorManager;
	static final int sensor = SensorManager.SENSOR_ORIENTATION;
	protected PowerManager.WakeLock wakelock;


	private static final int LOCATION_REQUEST = 0;

	//Conectar con el gps y obtener ubicacion.
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		setContentView(R.layout.guiar);
		SupportMapFragment mapFragment =
				(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
		//TENGO QUE DIFERENCIAR SI VIENE DE LUGAR O POR PONER LA DIRECCION
		lat = intent.getDoubleExtra("lat", 0.0);//00 valor por defecto si no viene nada
		lng = intent.getDoubleExtra("lng", 0.0);
		localidad = intent.getStringExtra("city").toLowerCase();
		// Creamos el objeto para acceder al servicio de sensores CUANDO TENGA UN MOVIL CON DICHO SENSOR PODRE PROBAR.
		/*SensorManager sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		List<Sensor> listaSensores = sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
		listaSensores = sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
		if (!listaSensores.isEmpty()) {
		       Sensor orientationSensor = listaSensores.get(0);
		       sensorManager.registerListener(this, orientationSensor,SensorManager.SENSOR_DELAY_FASTEST);
		}else{
		 Toast.makeText(getBaseContext(), "No hay sensores", Toast.LENGTH_LONG)
    		.show();
		}*/

		final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		this.wakelock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "etiqueta");
		wakelock.acquire();
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locListener = new MyLocationListener() {
			@Override
			public void onLocationChanged(Location loc) {//Cada vez que cambia mi posicion  le voy a informar que debe hacer..
				if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
					mlat = loc.getLatitude();
					mlng = loc.getLongitude();
					//Cuando obtenga la posicion dibujo la ruta. y Digo el recorrido a hacer.
					//una vez que ya la dibuje, cada vez que obtenga la posicion hago  otra cosa.
					if (acces == 0) {
						//setUpMapIfNeeded();
						setUpMap();
						acces = 1;
					} else {
						setLocation(loc);
					}

				}
			}
		};
	}


	//Metodo para para de escuchar los sensores(deberia hacerse en destroy, pause, y cuando finaliza el recorrido)
	//mSensorManager.unregisterListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION));

	@Override
	protected void onResume() {
		super.onResume();
		//Modificado ya que no se tiene mas el archivo de la pagina de la empresa de colectivo ya que la misma no lo brinda
		/*if (localidad.equals("río cuarto")) {
			if (!mfile.fileExist()) {
				Toast.makeText(getBaseContext(), "No Existe el Archivo con el Recorrido de los Colectivos", Toast.LENGTH_LONG)
						.show();
				Toast.makeText(getBaseContext(), "Vuelva Atras y Dirijase al Menú de Información Para Actualizar Recorridos", Toast.LENGTH_LONG)
						.show();
			} else {
				configGps();
				setUpMapIfNeeded();//tengo que obtener la lat y lng de entrada y hacer lo que hago
				mMapSupport = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
				mMapSupport.getMapAsync(this);
			}
		} *///else {
		configGps();
		mMapSupport = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
		mMapSupport.getMapAsync(this);
		//}
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
		this.wakelock.release();
		finish();
	}

	private void configGps() {
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
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 25000, 0, (LocationListener) locListener);

		if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Toast.makeText(getBaseContext(), getResources().getString(R.string.gps_desactivado), Toast.LENGTH_LONG)
					.show();
			Intent settingsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			this.startActivityForResult(settingsIntent, 0);
		}

		//Obtengo mi ubicacion y el punto destino que ya lo tengo, calculo la distancia 
		//para saber si guio hastta parada de colectivo o caminando
	}

	/**
	 * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
	 * installed) and the map has not already been instantiated.. This will ensure that we only ever
	 * call {@link #setUpMap()} once when {@link #mMap} is not null.
	 * <p>
	 * If it isn't installed {@link SupportMapFragment} (and
	 * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
	 * install/update the Google Play services APK on their device.
	 * <p>
	 * A user can return to this FragmentActivity after following the prompt and correctly
	 * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
	 * have been completely destroyed during this process (it is likely that it would only be
	 * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
	 * method in {@link #onResume()} to guarantee that it will be called.
	 */
	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map))
					.getMapAsync(new OnMapReadyCallback() {
						@Override
						public void onMapReady(GoogleMap googleMap) {
							mMap = googleMap;

							// Check if we were successful in obtaining the map.
							if (mMap != null) {
								setUpMap();
							}
						}
					});
		}
	}

	@Override
	public void onMapReady(GoogleMap map) {
		mMap = map;
		setUpMap();
	}

	/**
	 * Ponemos como marcador mi ubicacion y a donde deseo dirigirme, calculo la distancia.
	 *
	 * <p>
	 * This should only be called once and when we are sure that {@link #mMap} is not null.
	 */
	private void setUpMap() {

		mMap.getUiSettings().setZoomControlsEnabled(false); 		 
		/*mlat=-33.124064;
		mlng=-64.341153;
		lat=-33.114355;
		lng=-64.309523;*/
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mlat, mlng), 12));
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
		mMap.setMyLocationEnabled(true);
		//Construye la ruta, desde una latitud-longitud hasta otra latitud-longitud
		//tengo que construir desde mi ubicacion actual hasta la parada de colectivo si se 
		//superan ciertos metros, y sino hasta el lugar si esta cerca.
		//No TENEMOS MAS EL ARCHIVO CON LOS RECORRIDO DE LOS COLECTIVOS, LA EMPRESA NO LOS BRINDA MAS CAMBIO LA MODALIDAD
		//ES UNA IMAGEN EN LUGAR DE XML CON LAT Y LONG
		/*if(localidad.equals("río cuarto")){
			if(this.dist(mlat,mlng, lat,lng)<=1000){
				//Recorrido desde donde estoy hasta el lugar
				findDirections(mlat,mlng,lat,lng, GMapV2Direction.MODE_WALKING );
				colec=0;
			}else{
				colec=1;
				ArrayList<Pair> listLine = new ArrayList<Pair>();
				String coor;
				for(int i=1;i<19;i++){
					if (i==1 || i==2 || i==8 || i==9){
						coor=mfile.recuperar(String.valueOf(i)+"r");
						Pair linCor=new Pair(coor,String.valueOf(i)+"r");
						listLine.add(linCor);
						coor=mfile.recuperar(String.valueOf(i)+"v");
						linCor=new Pair(coor,String.valueOf(i)+"v");
						listLine.add(linCor);
					}else{
						coor=mfile.recuperar(String.valueOf(i));
						Pair linCor=new Pair(coor,String.valueOf(i));
						listLine.add(linCor);	
					}
				}

				int i=0;
				double mCurrentDist;
				double lngMy=0;
				double latMy=0;
				double currentDist;
				double minDist=10000;
				double minDistDest=10000;
				double minDistDestOrg;
				String[] minCord=null;
				int linea=0;
				while (i<listLine.size()){
					// -33.116864, -64.328614//quirico -33.125940, -64.342047//racedo
					//-33.11872566884502, -64.3260448927967//trece
					//-33.123802, -64.342134 LAVALLE 25 DE MAYO
					//-33.11325535,-64.308815 MI CASA
					String[] listLatLng=String.valueOf((listLine.get(i)).first).split(",0");
					int recList;
					if(mlat>lat){//el destino esta al sur mio
						if (listLine.get(i).second.equals("6") || listLine.get(i).second.equals("2r") || listLine.get(i).second.equals("2v")
								|| listLine.get(i).second.equals("3") || listLine.get(i).second.equals("4") || listLine.get(i).second.equals("14")){
							//Recorro segunda mitad de la lista de recorrido de colectivo
							//para todos menos el 6,2,3,4,14
							recList=1;
						}else{
							//recorro la primer parte de la lista
							recList=0;
						}

					}else{//el destino esta al norte mio
						if (listLine.get(i).second.equals("6") || listLine.get(i).second.equals("2r") || listLine.get(i).second.equals("2v")
								|| listLine.get(i).second.equals("3") || listLine.get(i).second.equals("4") || listLine.get(i).second.equals("14")){
							//Recorro P mitad de la lista de recorrido de colectivo
							//para todos menos el 6,2,3,4,14 ver que la condicion es negada	
							recList=0;
						}else{

							//recorro la segunda parte de la lista
							recList=1;
						}
					}

					mCurrentDist=minDist(mlat,mlng ,listLatLng,1,recList);//mlat mlng mi ubicacion
					currentDist=minDist(lat, lng,listLatLng,0,recList);//lat lng la del lugar
					if (mCurrentDist<currentDist){
						minDistDestOrg=currentDist-mCurrentDist; //Diferencia entre la menor distancia de origen y la mejor de destino
					}else{
						minDistDestOrg=mCurrentDist-currentDist;// la mas chica es la mejor de todas
					}
					if (minDistDestOrg<minDist){//currentDist<minDistDest && mCurrentDist<minDist
						//minDistDest=currentDist;
						//minDist=mCurrentDist;
						minDist=minDistDestOrg;

						minCord=listLatLng;
						linea=i; //Aca tengo la linea de colectivo que debera tomarse
						//double latOr=-64.337958;//la de origen es mi ubcacion
						//double lngOr=-33.121881;
						lngMy=lngLineStopMy;//en latLineStopMy tengo la latitud a la parada mas cercana a mi ubicacion
						latMy=latLineStopMy;
						lngDest=lngLineStopDest;//en latLineStopDest tengo la latitud mas cercana a la parada del destino
						latDest=latLineStopDest;//en latLineStop tengo la long mas cercana a la parada del destino
					}
					i++;
				}

				mMap.addMarker(new MarkerOptions()
				.position(new LatLng(lat, lng))
				.title("Destino"));

				mMap.addMarker(new MarkerOptions()
				.position(new LatLng(mlat, mlng))
				.title("Origen"));

				mMap.addMarker(new MarkerOptions()
				.position(new LatLng(latDest,lngDest))
				.title("Parada destino"));
				//-33.112120, -64.308465 Barrio;
				//-33.107446, -64.321168
				findDirections(mlat,mlng ,latMy, lngMy, GMapV2Direction.MODE_WALKING );
				//findDirections(-33.124945,-64.345854,lngDest, latDest, GMapV2Direction.MODE_WALKING );//cuando se baja recalculo la ruta.
				this.drawMap(minCord);
				Toast.makeText(getBaseContext(), R.string.calc_rec, Toast.LENGTH_LONG)
				.show();
				Toast.makeText(getBaseContext(), "Linea a Tomar, "+String.valueOf((listLine.get(linea)).second), Toast.LENGTH_LONG)
				.show();
			}
		}*///else{
			
			findDirections(mlat,mlng,lat,lng, GMapV2Direction.MODE_WALKING );
			colec=0;
		//}
	}

	public void findDirections(double fromPositionLat, double fromPositionLong, double toPositionLat, double toPositionLong, String mode)
	{ 
		Map<String, String> map = new HashMap<String, String>();
		map.put(GetDirectionsAsyncTask.USER_CURRENT_LAT, String.valueOf(fromPositionLat));
		map.put(GetDirectionsAsyncTask.USER_CURRENT_LONG, String.valueOf(fromPositionLong));
		map.put(GetDirectionsAsyncTask.DESTINATION_LAT, String.valueOf(toPositionLat));
		map.put(GetDirectionsAsyncTask.DESTINATION_LONG, String.valueOf(toPositionLong));
		map.put(GetDirectionsAsyncTask.DIRECTIONS_MODE, mode);

		//GetDirectionsAsyncTask asyncTask = new GetDirectionsAsyncTask(this);


		//asyncTask.execute(map);

	}

	public void handleGetDirectionsResult(ArrayList directionPoints, ArrayList<Instructions> listInst)
	{
		Polyline newPolyline;
		//GoogleMap mMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.RED);
		for(int i = 0 ; i < directionPoints.size() ; i++)
		{
			rectLine.add((LatLng) directionPoints.get(i)); 
		}
		newPolyline = mMap.addPolyline(rectLine);

		inst=listInst;
		textInst= new String();
		fixListInst(inst);
		Toast.makeText(getBaseContext(),textInst, Toast.LENGTH_LONG)
		.show();
		Toast.makeText(getBaseContext(),getResources().getString(R.string.repito)+textInst, Toast.LENGTH_LONG)
		.show();
	}


	//cada vez que cambia de posicion se ejecuta este metodo
	// ir informando que instruccion debe realizar una vez que llega a destino pasar al paso de abajo.
	// y cuando este a 200m del lugar avisar q se baje(cuando esta arriba del lugar) informar calles
	public void setLocation(Location loc) {
		if(inst.isEmpty()){
			Toast.makeText(getBaseContext(), getResources().getString(R.string.no_intrucciones), Toast.LENGTH_LONG)
			.show();
		}else if(inst.size()-1==0){

			if(colec==1){
				if(cartel==1){
					Toast.makeText(getBaseContext(),getResources().getString(R.string.parada_colectivo), Toast.LENGTH_LONG)
					.show();
					cartel=0;
				}
				if(this.dist(loc.getLatitude(), loc.getLongitude(),latDest,lngDest)>60 && this.dist(loc.getLatitude(), loc.getLongitude(),latDest,lngDest)<=200){
					Toast.makeText(getBaseContext(),getResources().getString(R.string.toc_timbre), Toast.LENGTH_LONG)
					.show();
				}else if (this.dist(loc.getLatitude(), loc.getLongitude(),latDest,lngDest)<=60){
					//recalcular la ruta
					//pensar cuando recalculo, la lista se actualiza o se incrementa en tamano de intruccions
					//las variables hay que reiniciarlas..
					findDirections(lat,lng,loc.getLatitude(), loc.getLongitude(), GMapV2Direction.MODE_WALKING );
					llegoDest=1;
					l=1;
				}else{
					//podemos informar donde esta.. pero esto lo vemos..
					int latitud = (int) (loc.getLatitude() * 1E6);
					int longitud = (int) (loc.getLongitude() * 1E6);

					//Obtener la direccion de la calle a partir de la latitud y la longitud 
					if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
						Toast.makeText(getBaseContext(),getResources().getString(R.string.ubic)+this.CurrentLocation(loc.getLatitude(), loc.getLongitude()), Toast.LENGTH_LONG)
						.show();
					}
				}
			}else{
				if(cartel==1){
					Toast.makeText(getBaseContext(),getResources().getString(R.string.destino), Toast.LENGTH_LONG)
					.show();
					cartel=0;
				}else{
					Toast.makeText(getBaseContext(),getResources().getString(R.string.recorrido_fin), Toast.LENGTH_LONG)
					.show();
				}
			}	

		}else{
			if (inst.size()>l){
				if (this.dist(loc.getLatitude(), loc.getLongitude(),Double.parseDouble(inst.get(l).getLat()),Double.parseDouble(inst.get(l).getLng()))<=20.00){
					//digo que hay que hacer
					Toast.makeText(getBaseContext(), inst.get(l).getInstruction(), Toast.LENGTH_LONG)
					.show();
					Toast.makeText(getBaseContext(),getResources().getString(R.string.repito)+ inst.get(l).getInstruction(), Toast.LENGTH_LONG)
					.show();
					//y a lat y lng le doy el siguiente de la lista
					/*Toast.makeText(getBaseContext(),"En este momento esta a 20 metros del proximo punto ", Toast.LENGTH_LONG)
		    		.show();*/
					l++;
				}
				else{
					//Me fijo si se saltio algun punto y esta cerca del destino
					if(this.dist(loc.getLatitude(), loc.getLongitude(),Double.parseDouble(inst.get(inst.size()-1).getLat()),Double.parseDouble(inst.get(inst.size()-1).getLng()))<=20.00){
						if(colec==1 && llegoDest==0){
							Toast.makeText(getBaseContext(),getResources().getString(R.string.parada_colectivo), Toast.LENGTH_LONG)
							.show();
							stop=1;
							l=inst.size();
						}else{
							Toast.makeText(getBaseContext(),getResources().getString(R.string.dest_prox)+" "+getResources().getString(R.string.recorrido_fin), Toast.LENGTH_LONG)
							.show();
						}
						
					}else{
						//le digo donde esta parado
						int latitud = (int) (loc.getLatitude() * 1E6);
						int longitud = (int) (loc.getLongitude() * 1E6);

						//Obtener la direccion de la calle a partir de la latitud y la longitud 
						if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
							Toast.makeText(getBaseContext(),getResources().getString(R.string.se_encuentra)+this.CurrentLocation(loc.getLatitude(), loc.getLongitude()), Toast.LENGTH_LONG)
							.show();
						}
					}
				}
			}else if(inst.size()==l && stop==0){
				if(this.dist(loc.getLatitude(), loc.getLongitude(),Double.parseDouble(inst.get(inst.size()-1).getLat()),Double.parseDouble(inst.get(inst.size()-1).getLng()))<=20.00){
					Toast.makeText(getBaseContext(),getResources().getString(R.string.dest_prox), Toast.LENGTH_LONG)
					.show();
					stop=1;
				}else{
					int latitud = (int) (loc.getLatitude() * 1E6);
					int longitud = (int) (loc.getLongitude() * 1E6);

					//Obtener la direccion de la calle a partir de la latitud y la longitud 
					if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
						Toast.makeText(getBaseContext(),getResources().getString(R.string.se_encuentra)+this.CurrentLocation(loc.getLatitude(), loc.getLongitude()), Toast.LENGTH_LONG)
						.show();
					}
				}
			}else if(inst.size()==l && stop==1 && llegoDest==0 && colec==1){
				if(this.dist(loc.getLatitude(), loc.getLongitude(),latDest,lngDest)>60 && this.dist(loc.getLatitude(), loc.getLongitude(),latDest,lngDest)<=280){
					Toast.makeText(getBaseContext(),getResources().getString(R.string.toc_timbre), Toast.LENGTH_LONG)
					.show();
				}else if (this.dist(loc.getLatitude(), loc.getLongitude(),latDest,lngDest)<=60){
					//recalcular la ruta
					//pensar cuando recalculo, la lista se actualiza o se incrementa en tamano de intruccions
					//las variables hay que reiniciarlas..
					//inst=new ArrayList<Instructions>();
					findDirections(lat,lng,loc.getLatitude(), loc.getLongitude(), GMapV2Direction.MODE_WALKING );
					llegoDest=1;
					l=1;
				}else{
					//podemos informar donde esta.. pero esto lo vemos..
					int latitud = (int) (loc.getLatitude() * 1E6);
					int longitud = (int) (loc.getLongitude() * 1E6);

					//Obtener la direccion de la calle a partir de la latitud y la longitud 
					if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
						Toast.makeText(getBaseContext(),getResources().getString(R.string.se_encuentra)+this.CurrentLocation(loc.getLatitude(), loc.getLongitude()), Toast.LENGTH_LONG)
						.show();
						
					}
				}
			}else if(inst.size()<=l && stop==1 && llegoDest==1){
				if (this.dist(loc.getLatitude(), loc.getLongitude(),lat,lng)<=30){
					Toast.makeText(getBaseContext(),getResources().getString(R.string.dest_prox)+" "+getResources().getString(R.string.recorrido_fin), Toast.LENGTH_LONG)
					.show();
				}
			}
		}
	}	
	
	public String CurrentLocation(Double lat, Double logn){
		try {
			Geocoder geocoder = new Geocoder(this, Locale.getDefault());
			List<Address> list = geocoder.getFromLocation(lat, logn, 1);
			if (!list.isEmpty()) {
				Address address = list.get(0);
				return  address.getAddressLine(0);				
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
		
	}


	/**
	 * Enables the My Location layer if the fine location permission has been granted.
	 */
	private void enableMyLocation() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {
			// Permission to access the location is missing.
			PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
					Manifest.permission.ACCESS_FINE_LOCATION, true);
		} else if (mMap != null) {
			// Access to the location has been granted to the app.
			mMap.setMyLocationEnabled(true);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
										   @NonNull int[] grantResults) {
		if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
			return;
		}

		if (PermissionUtils.isPermissionGranted(permissions, grantResults,
				Manifest.permission.ACCESS_FINE_LOCATION)) {
			// Enable the my location layer if the permission has been granted.
			enableMyLocation();
		} else {
			// Display the missing permission error dialog when the fragments resume.
			mPermissionDenied = true;
		}
	}

	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		if (mPermissionDenied) {
			// Permission was not granted, display error dialog.
			showMissingPermissionError();
			mPermissionDenied = false;
		}
	}

	/**
	 * Displays a dialog with error message explaining that the location permission is missing.
	 */
	private void showMissingPermissionError() {
		PermissionUtils.PermissionDeniedDialog
				.newInstance(true).show(getSupportFragmentManager(), "dialog");
	}
	
	


	private double dist(double mlat, double mlng,double lat, double lng){
		//mloc=new MyLocation("GPS");
		MyLocation.distanceBetween(mlat, mlng, lat, lng, results);
		return results[0];
	}


	//CALCULAR LA DISTANCIA DE DONDE QUIERO IR Y COMPARAR LA LAT Y LNG
	//DEL DESTINO CON LOS ARCHIVOS DE LINEAS PARA VER CUAL ESTA MAS CERCA
	//Y CUAL MAS CERCA DEL ORIGEN TMB.(OJO CON TEMA IDA, VUELTA)
	//UNA VEZ LISTO ESTO, DIBUJAR RECORRIDO HASTA LA MEJOR OPCION(PARADA)
	//Y LUEGO AVISAR CUANDO SE ESTE CERCA DEL DESTINOO..
	//Hacer un Metodo que tome un arreglo de latitud y longitud
	//y dibuje el recorrido en el mapa.
	public void drawMap(String[] listLatLng){
		//String coor=mfile.recuperar("17");
		//String[] listLatLng=coor.split(",0");
		int i=0;
		double distCont=0.0;
		Polyline newPolyline;
		PolylineOptions line = new PolylineOptions().width(3).color(Color.BLUE);
		while (i<listLatLng.length-1){
			String[] ltln=listLatLng[i].split(",");
			double lng=Double.parseDouble(ltln[0]);
			double lat=Double.parseDouble(ltln[1]);
			line.add(new LatLng(lat, lng));
			i++;
		}
		this.drawStops(listLatLng);
		//line.add(new LatLng(33.10933423127904,-64.30096289906105),new LatLng(-33.123873,-64.348993));
		newPolyline = mMap.addPolyline(line);
	}


	//Dibujo las paradas estimativas de colectivo cada 200 metros.
	public void drawStops(String[] listLatLng ){
		int i=0;
		double distCont=0.0;

		while (i<listLatLng.length-2){
			String[] ltln=listLatLng[i].split(",");
			double lng=Double.parseDouble(ltln[0]);
			double lat=Double.parseDouble(ltln[1]);
			double lngDest=Double.parseDouble(listLatLng[i+1].split(",")[0]);
			double latDest=Double.parseDouble(listLatLng[i+1].split(",")[1]);
			distCont=this.dist(lat, lng, latDest, lngDest)+distCont;
			if(distCont>=200){
				//poner parada
				mMap.addMarker(new MarkerOptions().position(new LatLng(latDest,lngDest)).title("Stop")
						.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_bus_light)));
				distCont=0.0;
			}
			i++;
		}
	}

	//Minima distancia entre la parada de colectivo y mi ubicacion y la de destino
	public double minDist(double lat, double lng,String[] listLatLng,int m, int recList){
		int j=0;
		double minDist=10000;
		if(recList==0){//si es 0 busco en la primera parte de la lista
			while (j<((listLatLng.length)/2)){
				String[] ltln=listLatLng[j].split(",");
				double latLine=Double.parseDouble(ltln[1]);
				double lngLine=Double.parseDouble(ltln[0]);
				double currentDist=this.dist(lat,lng, latLine, lngLine);
				if (currentDist<minDist){
					minDist=currentDist;
					if(m==1){//Diferencio si es 1 para obtener la parada mas cercana a mi ubicacion y no a la del destino.
						latLineStopMy=latLine;
						lngLineStopMy=lngLine;
					}else{
						latLineStopDest=latLine;
						lngLineStopDest=lngLine;
					}
				}
				j++;
			}
		}else{
			j=((listLatLng.length)/2);
			while (j<(listLatLng.length-1)){
				String[] ltln=listLatLng[j].split(",");
				double latLine=Double.parseDouble(ltln[1]);
				double lngLine=Double.parseDouble(ltln[0]);
				double currentDist=this.dist(lat,lng, latLine, lngLine);
				if (currentDist<minDist){
					minDist=currentDist;
					if(m==1){//Diferencio si es 1 para obtener la parada mas cercana a mi ubicacion y no a la del destino.
						latLineStopMy=latLine;
						lngLineStopMy=lngLine;
					}else{
						latLineStopDest=latLine;
						lngLineStopDest=lngLine;
					}
				}
				j++;
			}
		}
		return minDist;
	}


	//Metodo que recorre la lista y agrega la palabra metros, pasos a la distancia,
	//Insrucciones borra lo que esta entre <....>
	// pasaje de duracion en segundos a minutos dependiendo..
	public void fixListInst(ArrayList<Instructions> inst){
		int i=0; 
		while(i<inst.size()){
			if(Integer.parseInt(inst.get(i).getDistance())<1000){
				inst.get(i).setDistance(inst.get(i).getDistance()+getResources().getString(R.string.metros)+Math.round(Integer.parseInt(inst.get(i).getDistance())/0.6) +getResources().getString(R.string.pasos));
			}else{
				inst.get(i).setDistance(Math.round(Integer.parseInt(inst.get(i).getDistance())/1000)+getResources().getString(R.string.kilom)+Math.round(Integer.parseInt(inst.get(i).getDistance())/1000/0.6) +getResources().getString(R.string.pasos));
			}
			int j=0;
			String instruc=inst.get(i).getInstruction();
			String newInstr=new String();
			while(j<instruc.length()){
				if(instruc.charAt(j)=='<'){
					while(instruc.charAt(j)!='>'){
						j++;
					}
					j++;
				}else{
					newInstr=newInstr+instruc.charAt(j);	
					j++;
				}
			}
			inst.get(i).setInstruction(newInstr);
			textInst=textInst+".\n"+inst.get(i).getInstruction()+" a "+inst.get(i).getDistance();
			i++;
		}
	}


	//HASTA QUE NO CONSIGA UN TELEFONO CON SENSOR DE ORIENTACION, NO VOY A PODER PROBAR DICHO SENSOR  
	/*
	private String getDireccion(float values) {
		String txtDirection = "";
		if (values < 22)
			txtDirection = "Norte";
		else if (values >= 22 && values < 67)
			txtDirection = "Noreste";
		else if (values >= 67 && values < 112)
			txtDirection = "Este";
		else if (values >= 112 && values < 157)
			txtDirection = "Sureste";
		else if (values >= 157 && values < 202)
			txtDirection = "Sur";
		else if (values >= 202 && values < 247)
			txtDirection = "Suroeste";
		else if (values >= 247 && values < 292)
			txtDirection = "Oeste";
		else if (values >= 292 && values < 337)
			txtDirection = "Noroeste";
		else if (values >= 337)
			txtDirection = "Norte";

		return txtDirection;
	}*/	
	// Metodo que escucha el cambio de los sensores
	//@Override
	/*public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			String txt = "\n\nSensor: ";
			Toast.makeText(getBaseContext(),"Escuchandoooo", Toast.LENGTH_LONG)
			.show();
			synchronized (this) {
				Log.d("sensor", event.sensor.getName());

				//if (event.sensor.getType()==Sensor.TYPE_ORIENTATION) {

					txt += "orientation\n";
					txt += "\n azimut: " + getDireccion(event.values[0]);
					txt += "\n y: " + event.values[1] + "Œ";
					txt += "\n z: " + event.values[2] + "Œ";
					//orientacion.setText(txt);
					Toast.makeText(getBaseContext(),"Va con Direcciona al "+ getDireccion(event.values[0]), Toast.LENGTH_LONG)
		    		.show();
				//}
			}	

		}



		// Metodo que escucha el cambio de sensibilidad de los sensores	
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub

		}*/

}

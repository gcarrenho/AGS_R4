package com.ags.guideme;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.ags.guideme.location.MyLocation;
import com.ags.guideme.navigation.GMapV2Direction;
import com.ags.guideme.navigation.GetDirectionsAsyncTask;
import com.ags.guideme.navigation.Instructions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MapaToGuide extends AppCompatActivity
        implements OnMapReadyCallback {

    private static final String TAG = MapaToGuide.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private String[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;
    private LocationCallback mLocationCallback;
    private ArrayList<Instructions> inst ;
    private String textInst = new String();
    private double lat,lng;
    private static float[] results = new float[2];
    private int i=0;
    int acces=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        lat = intent.getDoubleExtra("lat", 0.0);//00 valor por defecto si no viene nada
        lng = intent.getDoubleExtra("lng", 0.0);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);

        // Construct a GeoDataClient.
        //mGeoDataClient = Places.getGeoDataClient(this, null);
        mGeoDataClient = Places.getGeoDataClient(this);


        // Construct a PlaceDetectionClient.
        //mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                Location location = locationResult.getLocations().get(0);
                    // Update UI with location data
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.ubic) + " " + CurrentLocation(location.getLatitude(), location.getLongitude()), Toast.LENGTH_LONG)
                            .show();
                if (acces == 1) {
                    setLocation(location);
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(),
                                location.getLongitude()), DEFAULT_ZOOM));

            }

            ;
        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (true) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Sets up the options menu.
     * @param menu The options menu.
     * @return Boolean.
     */
   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.current_place_menu, menu);
        return true;
    }

    /**
     * Handles a click on the menu option to get a place.
     * @param item The menu item to handle.
     * @return Boolean.
     */
   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_get_place) {
            showCurrentPlace();
        }
        return true;
    }*/

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
       /* mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout) findViewById(R.id.map), false);

                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());

                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });*/

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();



    }

    public void findDirections(double fromPositionLat, double fromPositionLong, double toPositionLat, double toPositionLong, String mode)
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put(GetDirectionsAsyncTask.USER_CURRENT_LAT, String.valueOf(fromPositionLat));
        map.put(GetDirectionsAsyncTask.USER_CURRENT_LONG, String.valueOf(fromPositionLong));
        map.put(GetDirectionsAsyncTask.DESTINATION_LAT, String.valueOf(toPositionLat));
        map.put(GetDirectionsAsyncTask.DESTINATION_LONG, String.valueOf(toPositionLong));
        map.put(GetDirectionsAsyncTask.DIRECTIONS_MODE, mode);
        Log.i("DIRECTIOOON","");
        GetDirectionsAsyncTask asyncTask = new GetDirectionsAsyncTask(this);


        asyncTask.execute(map);

    }

    public void handleGetDirectionsResult(ArrayList directionPoints, ArrayList<Instructions> listInst)
    {
        Polyline newPolyline;
        PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.RED);
        for(int i = 0 ; i < directionPoints.size() ; i++)
        {
            rectLine.add((LatLng) directionPoints.get(i));
        }
        newPolyline = mMap.addPolyline(rectLine);

        inst= new ArrayList<Instructions>();
        inst=listInst;
        textInst= new String();
        Log.d(TAG, "Instrucciones originales. "+inst.get(0).getDistance()+" "+getResources().getString(R.string.metros)+" "+Math.round(Integer.parseInt(inst.get(0).getDistance())/0.6));
        fixListInst(inst);
        Toast.makeText(getBaseContext(),textInst, Toast.LENGTH_LONG)
                .show();
    }

    //Metodo que recorre la lista y agrega la palabra metros, pasos a la distancia,
    //Insrucciones borra lo que esta entre <....>
    // pasaje de duracion en segundos a minutos dependiendo..
    public void fixListInst(ArrayList<Instructions> inst){
        int i=0;
        while(i<inst.size()){
            if(Integer.parseInt(inst.get(i).getDistance())<1000){
                inst.get(i).setDistance(inst.get(i).getDistance()+" "+getResources().getString(R.string.metros)+" "+Math.round(Integer.parseInt(inst.get(i).getDistance())/0.6)+" "+getResources().getString(R.string.pasos));
            }else{
                inst.get(i).setDistance(Math.round(Integer.parseInt(inst.get(i).getDistance())/1000)+" "+getResources().getString(R.string.kilom)+" "+Math.round(Integer.parseInt(inst.get(i).getDistance())/0.6)+" "+getResources().getString(R.string.pasos));
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
    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();

                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            findDirections(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude(),lat,lng, GMapV2Direction.MODE_WALKING );
                            acces = 1;
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public String CurrentLocation(Double lat, Double logn) {
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
        return "";

    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */
   /* private void showCurrentPlace() {
        if (mMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            Toast.makeText(getBaseContext(),"ACA2", Toast.LENGTH_LONG)
                    .show();
            @SuppressWarnings("MissingPermission") final
            Task<PlaceLikelihoodBufferResponse> placeResult =
                    mPlaceDetectionClient.getCurrentPlace(null);
            placeResult.addOnCompleteListener
                    (new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

                                // Set the count, handling cases where less than 5 entries are returned.
                                int count;
                                if (likelyPlaces.getCount() < M_MAX_ENTRIES) {
                                    count = likelyPlaces.getCount();
                                } else {
                                    count = M_MAX_ENTRIES;
                                }

                                int i = 0;
                                mLikelyPlaceNames = new String[count];
                                mLikelyPlaceAddresses = new String[count];
                                mLikelyPlaceAttributions = new String[count];
                                mLikelyPlaceLatLngs = new LatLng[count];

                                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                                    // Build a list of likely places to show the user.
                                    mLikelyPlaceNames[i] = (String) placeLikelihood.getPlace().getName();
                                    mLikelyPlaceAddresses[i] = (String) placeLikelihood.getPlace()
                                            .getAddress();
                                    mLikelyPlaceAttributions[i] = (String) placeLikelihood.getPlace()
                                            .getAttributions();
                                    mLikelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                                    i++;
                                    if (i > (count - 1)) {
                                        break;
                                    }
                                }

                                // Release the place likelihood buffer, to avoid memory leaks.
                                likelyPlaces.release();

                                // Show a dialog offering the user the list of likely places, and add a
                                // marker at the selected place.
                                openPlacesDialog();

                            } else {
                                Log.e(TAG, "Exception: %s", task.getException());
                            }
                        }
                    });
        } else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.");

            // Add a default marker, because the user hasn't selected a place.
            mMap.addMarker(new MarkerOptions()
                    .title("Default Location")
                    .position(mDefaultLocation)
                    .snippet("No places found, because location permission is disabled."));

            // Prompt the user for permission.
            getLocationPermission();
        }
    }
*/
    /**
     * Displays a form allowing the user to select a place from a list of likely places.
     */
 /*   private void openPlacesDialog() {
        // Ask the user to choose the place where they are now.
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The "which" argument contains the position of the selected item.
                LatLng markerLatLng = mLikelyPlaceLatLngs[which];
                String markerSnippet = mLikelyPlaceAddresses[which];
                if (mLikelyPlaceAttributions[which] != null) {
                    markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[which];
                }

                // Add a marker for the selected place, with an info window
                // showing information about that place.
                mMap.addMarker(new MarkerOptions()
                        .title(mLikelyPlaceNames[which])
                        .position(markerLatLng)
                        .snippet(markerSnippet));

                // Position the map's camera at the location of the marker.
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                        DEFAULT_ZOOM));
            }
        };

        // Display the dialog.
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Choose a place")
                .setItems(mLikelyPlaceNames, listener)
                .show();
    }
*/

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
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


    public void setLocation(Location loc) {
            //Distancia en metros
        if(i<inst.size()) {
            double currentDistToPoint = this.dist(loc.getLatitude(), loc.getLongitude(), Double.valueOf(inst.get(i).getLat()), Double.valueOf(inst.get(i).getLng()));
            double lastDistLocationKnow=this.dist(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), Double.valueOf(inst.get(i).getLat()), Double.valueOf(inst.get(i).getLng()));
            Log.i("Distancia vieja",String.valueOf(lastDistLocationKnow));
            Log.i("Distancia corriente",String.valueOf(currentDistToPoint));

            //Si la distancia actual contra el punto destino es menor que la
            //ultima distancia conocida contra el punto destino
            //significa que me estoy acercando
            if (currentDistToPoint <lastDistLocationKnow){
                if(currentDistToPoint<=50){//dar la instruccion e incrementar el i
                    Toast.makeText(getBaseContext(),inst.get(i+1).getInstruction()+" a "+currentDistToPoint+" metros", Toast.LENGTH_LONG)
                            .show();
                    i++;
                }
            }else{//me alejo, recalcular el recorrido y limpiar las lista de instrucciones y poner el i=0
                findDirections(loc.getLatitude(), loc.getLongitude(),lat,lng, GMapV2Direction.MODE_WALKING );
                i=0;
            }
        }else{//Puede que finalizo el recorrido o no hay instrucciones
            Toast.makeText(getBaseContext(),"Recorrido finalizado", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private double dist(double mlat, double mlng,double lat, double lng){
        MyLocation.distanceBetween(mlat, mlng, lat, lng, results);
        return results[0];
    }


}

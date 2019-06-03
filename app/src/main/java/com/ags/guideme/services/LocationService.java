package com.ags.guideme.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationService extends Service{

    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private LocationCallback mLocationCallback;


    @SuppressLint("MissingPermission")
    public void startLocationUpdates() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(30 * 1000)
                .setFastestInterval(6 * 10000);
        mFusedLocationProviderClient.requestLocationUpdates(locationRequest,
                mLocationCallback, null /* Looper */);
    }

    public void stopLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

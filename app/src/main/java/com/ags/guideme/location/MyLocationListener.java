package com.ags.guideme.location;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.ags.guideme.GuiarMapa;


public class MyLocationListener implements LocationListener {
	GuiarMapa guiarActivity;

	public GuiarMapa getGuiarActivity() {
		return guiarActivity;
	}

	public void setGuiarActivity(GuiarMapa guiarActivity) {
		this.guiarActivity = guiarActivity;
	}

	@Override
	public void onLocationChanged(Location loc) {
		// Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
		// debido a la deteccion de un cambio de ubicacion
		// Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
		// debido a la deteccion de un cambio de ubicacion
		loc.getLatitude();
		loc.getLongitude();
		guiarActivity.setLocation(loc);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// Este metodo se ejecuta cuando el GPS es desactivado
		//messageTextView.setText("GPS Desactivado");
		//Toast.makeText(getBaseContext(),"GPS Desactivado", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
		// Este metodo se ejecuta cuando el GPS es activado
		//messageTextView.setText("GPS Activado");
		//Toast.makeText(getBaseContext(), "GPS Activado", Toast.LENGTH_LONG).show();

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// Este metodo se ejecuta cada vez que se detecta un cambio en el
		// status del proveedor de localizacion (GPS)
		// Los diferentes Status son:
		// OUT_OF_SERVICE -> Si el proveedor esta fuera de servicio
		// TEMPORARILY_UNAVAILABLE -> Temporalmente no disponible pero se
		// espera que este disponible en breve
		// AVAILABLE -> Disponible
	}


}

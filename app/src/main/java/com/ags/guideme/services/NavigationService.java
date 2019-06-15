package com.ags.guideme.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class NavigationService extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}


	/*public static class NavigationServiceBinder extends Binder {

	}
	// global id don't conflict with others
	private final static int NOTIFICATION_SERVICE_ID = 5;
	public final static String OSMAND_STOP_SERVICE_ACTION  = "OSMAND_STOP_SERVICE_ACTION"; //$NON-NLS-1$
	public final static String NAVIGATION_START_SERVICE_PARAM = "NAVIGATION_START_SERVICE_PARAM"; 

	private NavigationServiceBinder binder = new NavigationServiceBinder();


	private int serviceOffInterval;
	private String serviceOffProvider;
	private int serviceError;

	private OsmandSettings settings;

	private Handler handler;

	private static WakeLock lockStatic;
	private PendingIntent pendingIntent;
	private BroadcastReceiver broadcastReceiver;
	private boolean startedForNavigation;

	private static Method mStartForeground;
	private static Method mStopForeground;
	private static Method mSetForeground;
	private OsmAndLocationProvider locationProvider;

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	protected synchronized static PowerManager.WakeLock getLock(Context context) {
		if (lockStatic == null) {
			PowerManager mgr = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			lockStatic = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "OsmandServiceLock");
		}
		return lockStatic;
	}


	@Override
	public void onLocationChanged(Location location) {
		if(l != null && !settings.MAP_ACTIVITY_ENABLED.get()){
			net.osmand.Location location = OsmAndLocationProvider.convertLocation(l,(OsmandApplication) getApplication());
			if(!isContinuous()){
				// unregister listener and wait next time
				LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
				locationManager.removeUpdates(this);
				WakeLock lock = getLock(this);
				if (lock.isHeld()) {
					lock.release();
				}
			}
			locationProvider.setLocationFromService(location, isContinuous());
		}

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		Toast.makeText(getBaseContext(),"El servicio de navegación de fondo necesita que un proveedor de localización esté encendido.", Toast.LENGTH_LONG)
        .show();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}*/

}

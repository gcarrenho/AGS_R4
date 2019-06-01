package com.ags.guideme.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.ags.guideme.AgsIntents;
import com.ags.guideme.R;
import com.ags.guideme.file.MakeFile;
import com.ags.guideme.navigation.GetDirectionBusAsyncTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class InfActivity extends Activity{

	private MakeFile mfile=new MakeFile();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean exit = false;
		Intent intent = getIntent();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.informacion);
		Toast.makeText(getBaseContext(), getResources().getString(R.string.volver), Toast.LENGTH_LONG)
		.show();
		Window window = getWindow();
		final Activity activity = this;
		View recButton = window.findViewById(R.id.button_rec);
		//Evento que escucha el click sobre el boton recuperar
		recButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getBaseContext(), R.string.modoDeUso,
						Toast.LENGTH_SHORT).show();
			}
		});

		View buttonConfig = window.findViewById(R.id.button_config);
		//Evento que escucha el click sobre el boton recuperar
		buttonConfig.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent config = new Intent(activity, AgsIntents.getAdminActivity());
				activity.startActivity(config);			
			}
		});


	}

	public void findDirections(double fromPositionLat, double fromPositionLong, double toPositionLat, double toPositionLong, String mode)
	{
		Map<String, String> map = new HashMap<String, String>();
		/* map.put(GetDirectionBusAsyncTask.USER_CURRENT_LAT, String.valueOf(fromPositionLat));
	        map.put(GetDirectionBusAsyncTask.USER_CURRENT_LONG, String.valueOf(fromPositionLong));
	        map.put(GetDirectionBusAsyncTask.DESTINATION_LAT, String.valueOf(toPositionLat));
	        map.put(GetDirectionBusAsyncTask.DESTINATION_LONG, String.valueOf(toPositionLong));
	        map.put(GetDirectionBusAsyncTask.DIRECTIONS_MODE, mode);*/

		GetDirectionBusAsyncTask asyncTask = new GetDirectionBusAsyncTask(this);
		asyncTask.execute(map);
		/* Map<String, String> map = new HashMap<String, String>();
	         map.put(GetDirectionsAsyncTask.USER_CURRENT_LAT, String.valueOf(fromPositionLat));
	         map.put(GetDirectionsAsyncTask.USER_CURRENT_LONG, String.valueOf(fromPositionLong));
	         map.put(GetDirectionsAsyncTask.DESTINATION_LAT, String.valueOf(toPositionLat));
	         map.put(GetDirectionsAsyncTask.DESTINATION_LONG, String.valueOf(toPositionLong));
	         map.put(GetDirectionsAsyncTask.DIRECTIONS_MODE, mode);

	         GetDirectionsAsyncTask asyncTask = new GetDirectionsAsyncTask(this);
	         asyncTask.execute(map);*/

	}

	public void handleGetDirectionsResult(ArrayList directionPoints)
	{
		// Polyline newPolyline;
		//GoogleMap mMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		//PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.RED);
		//for(int i = 0 ; i < directionPoints.size() ; i++)
		//{
		//rectLine.add((LatLng) directionPoints.get(i));
		//}
		/*Toast.makeText(getBaseContext(), "Mi Direccion es: "+directionPoints.get(0), Toast.LENGTH_LONG)
			.show();*/


		/*EditText text=(EditText)findViewById(R.id.editText1);
			text.setText(String.valueOf(directionPoints.get(2)));*/
	}
}

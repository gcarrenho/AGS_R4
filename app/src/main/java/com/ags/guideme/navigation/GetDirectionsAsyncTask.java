package com.ags.guideme.navigation;

import java.util.*;

import org.w3c.dom.Document;

import com.google.android.gms.maps.model.LatLng;
import com.ags.guideme.GuiarMapa;
import com.ags.guideme.MapaToGuide;
import com.ags.guideme.R;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


public class GetDirectionsAsyncTask extends AsyncTask<Map<String, String>, Object, ArrayList>{

	public static final String USER_CURRENT_LAT = "user_current_lat";
	public static final String USER_CURRENT_LONG = "user_current_long";
	public static final String DESTINATION_LAT = "destination_lat";
	public static final String DESTINATION_LONG = "destination_long";
	public static final String DIRECTIONS_MODE = "directions_mode";
	private MapaToGuide activity;
	private Exception exception;
	private ProgressDialog progressDialog;
	private ArrayList<Instructions> docInstr=new ArrayList<Instructions>();

	public GetDirectionsAsyncTask(MapaToGuide guiarMapa)
	{
		super();
		this.activity = guiarMapa;
	}

	public void onPreExecute()
	{
		progressDialog = new ProgressDialog(activity);
		progressDialog.setMessage("Calculando recorrido");
		progressDialog.show();
	}

	@Override
	public void onPostExecute(ArrayList result)
	{
		progressDialog.dismiss();
		if (exception == null)
		{
			activity.handleGetDirectionsResult(result,docInstr);
		}
		else
		{
			processException();
		}
	}

	@Override
	protected ArrayList doInBackground(Map<String, String>... params)
	{
		Map<String, String> paramMap = params[0];
		try
		{
			LatLng fromPosition = new LatLng(Double.valueOf(paramMap.get(USER_CURRENT_LAT)) , Double.valueOf(paramMap.get(USER_CURRENT_LONG)));
			LatLng toPosition = new LatLng(Double.valueOf(paramMap.get(DESTINATION_LAT)) , Double.valueOf(paramMap.get(DESTINATION_LONG)));

			Log.i("Queriendo clacular","");

			GMapV2Direction md = new GMapV2Direction();
			Log.i("Queriendo clacular","");

			Document doc = md.getDocument(fromPosition, toPosition, paramMap.get(DIRECTIONS_MODE));
			docInstr=md.getInstructions(doc);
			Log.i("volvi del doc","");

			ArrayList directionPoints = md.getDirection(doc);
			Log.i("tengo los point","");

			return directionPoints;
		}
		catch (Exception e)
		{
			Log.i("Exception: ",e.getMessage());
			exception = e;
			return null;
		}
	}

	private void processException()
	{
		Toast.makeText(activity, activity.getString(R.string.error_when_retrieving_data), Toast.LENGTH_LONG).show();
	}

	public void setDocInstr(ArrayList<Instructions> docInstr) {
		this.docInstr = docInstr;
	}

	public ArrayList<Instructions> getDocInstr() {
		return docInstr;
	}
}

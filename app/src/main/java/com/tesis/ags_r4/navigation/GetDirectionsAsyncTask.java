package com.tesis.ags_r4.navigation;

import java.util.*;

import org.w3c.dom.Document;

import com.google.android.gms.maps.model.LatLng;
import com.tesis.ags_r4.GuiarMapa;
import com.tesis.ags_r4.R;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;


public class GetDirectionsAsyncTask extends AsyncTask<Map<String, String>, Object, ArrayList>{

	public static final String USER_CURRENT_LAT = "user_current_lat";
	public static final String USER_CURRENT_LONG = "user_current_long";
	public static final String DESTINATION_LAT = "destination_lat";
	public static final String DESTINATION_LONG = "destination_long";
	public static final String DIRECTIONS_MODE = "directions_mode";
	private GuiarMapa activity;
	private Exception exception;
	private ProgressDialog progressDialog;
	private ArrayList<Instructions> docInstr=new ArrayList<Instructions>();

	public GetDirectionsAsyncTask(GuiarMapa guiarMapa)
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
			GMapV2Direction md = new GMapV2Direction();
			Document doc = md.getDocument(fromPosition, toPosition, paramMap.get(DIRECTIONS_MODE));
			docInstr=md.getInstructions(doc);  

			ArrayList directionPoints = md.getDirection(doc);
			return directionPoints;
		}
		catch (Exception e)
		{
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

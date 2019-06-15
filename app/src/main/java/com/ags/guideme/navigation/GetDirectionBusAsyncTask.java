package com.ags.guideme.navigation;

import java.util.ArrayList;
import java.util.Map;

import org.w3c.dom.Document;

import com.ags.guideme.R;
import com.ags.guideme.activity.InfActivity;
import com.ags.guideme.file.MakeFile;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

public class GetDirectionBusAsyncTask extends AsyncTask<Map<String, String>, Object, ArrayList>{

	private InfActivity activity;
	private Exception exception;
	private ProgressDialog progressDialog;
	private MakeFile mfile=new MakeFile();

	public GetDirectionBusAsyncTask(InfActivity activity)
	{
		super();
		this.activity = activity;
	}

	public void onPreExecute()
	{
		progressDialog = new ProgressDialog(activity);
		progressDialog.setMessage("Actualizando Informacion");
		progressDialog.show();
	}

	@Override
	public void onPostExecute(ArrayList result)
	{
		progressDialog.dismiss();
		if (exception == null)
		{
			activity.handleGetDirectionsResult(result);
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
			ArrayList ar=new ArrayList();
			GBusDirection md = new GBusDirection();
			Document doc;
			//guardar en algun archivo o en la bd
			for(int i=1;i<19;i++){
				if (i==1 || i==2 || i==8 || i==9){
					doc=md.getDocument(String.valueOf(i)+"r");
					String directionPoints = md.getCoorValue(doc);
					ar.add(directionPoints);
					//Agregar
					mfile.grabar(String.valueOf(i)+"r", directionPoints);
					doc=md.getDocument(String.valueOf(i)+"v");
					directionPoints = md.getCoorValue(doc);
					ar.add(directionPoints);
					mfile.grabar(String.valueOf(i)+"v", directionPoints);
					//Agregar
				}else{
					doc=md.getDocument(String.valueOf(i));
					String directionPoints = md.getCoorValue(doc);
					ar.add(directionPoints);
					//Agregar
					mfile.grabar(String.valueOf(i), directionPoints);
				}

				//a medida que vamos descargando, vamos a ir guardandolo en un archivo
				//usando como nombre el numero de linea.
			}
			return ar;
		}
		catch (Exception e)
		{
			exception = e;
			return null;
		}
	}

	private void processException()
	{
		Toast.makeText(activity, activity.getString(R.string.error_when_retrieving_data), 3000).show();
	}

}

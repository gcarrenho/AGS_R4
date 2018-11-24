package com.tesis.ags_r4.navigation;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.tesis.ags_r4.R;

public class GMapV2Direction{

	public final static String MODE_DRIVING = "driving";
	public final static String MODE_WALKING = "walking";

	public GMapV2Direction() { }

	public Document getDocument(LatLng start, LatLng end, String mode) {
		String url = "https://maps.googleapis.com/maps/api/directions/xml?"
				+ "origin=" + start.latitude + "," + start.longitude
				+ "&destination=" + end.latitude + "," + end.longitude
				+ "&mode=" + mode +"&sensor=false&language="+Locale.getDefault().toString()+"&units=metric"+"&key=AIzaSyCcCDHv23TZ09gmeB7dizXXfwFPWytC2uc" ;

		Log.i("Url direction",url);
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpPost httpPost = new HttpPost(url);
			HttpResponse response = httpClient.execute(httpPost, localContext);
			InputStream in = response.getEntity().getContent();
			//String res = convertStreamToString(in);

			//Log.i("Respuesta ",res);
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(in);
			//Log.i("Respuesta ",doc.getXmlEncoding());
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getDurationText (Document doc) {
		NodeList nl1 = doc.getElementsByTagName("duration");
		Node node1 = nl1.item(0);
		NodeList nl2 = node1.getChildNodes();
		Node node2 = nl2.item(getNodeIndex(nl2, "text"));
		Log.i("DurationText", node2.getTextContent());
		return node2.getTextContent();
	}

	public int getDurationValue (Document doc) {
		NodeList nl1 = doc.getElementsByTagName("duration");
		Node node1 = nl1.item(0);
		NodeList nl2 = node1.getChildNodes();
		Node node2 = nl2.item(getNodeIndex(nl2, "value"));
		Log.i("DurationValue", node2.getTextContent());
		return Integer.parseInt(node2.getTextContent());
	}

	public String getDistanceText (Document doc) {
		NodeList nl1 = doc.getElementsByTagName("distance");
		Node node1 = nl1.item(0);
		NodeList nl2 = node1.getChildNodes();
		Node node2 = nl2.item(getNodeIndex(nl2, "text"));
		Log.i("DistanceText", node2.getTextContent());
		return node2.getTextContent();
	}

	public int getDistanceValue (Document doc) {
		NodeList nl1 = doc.getElementsByTagName("distance");
		Node node1 = nl1.item(0);
		NodeList nl2 = node1.getChildNodes();
		Node node2 = nl2.item(getNodeIndex(nl2, "value"));
		Log.i("DistanceValue", node2.getTextContent());
		return Integer.parseInt(node2.getTextContent());
	}

	public String getStartAddress (Document doc) {
		NodeList nl1 = doc.getElementsByTagName("start_address");
		Node node1 = nl1.item(0);
		Log.i("StartAddress", node1.getTextContent());
		return node1.getTextContent();
	}

	public String getEndAddress (Document doc) {
		NodeList nl1 = doc.getElementsByTagName("end_address");
		Node node1 = nl1.item(0);
		Log.i("StartAddress", node1.getTextContent());
		return node1.getTextContent();
	}

	public String getCopyRights (Document doc) {
		NodeList nl1 = doc.getElementsByTagName("copyrights");
		Node node1 = nl1.item(0);
		Log.i("CopyRights", node1.getTextContent());
		return node1.getTextContent();
	}

	public ArrayList<LatLng> getDirection (Document doc) {
		NodeList nl1, nl2, nl3;
		ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();
		nl1 = doc.getElementsByTagName("step");
		if (nl1.getLength() > 0) {
			for (int i = 0; i < nl1.getLength(); i++) {
				Node node1 = nl1.item(i);
				nl2 = node1.getChildNodes();

				Node locationNode = nl2.item(getNodeIndex(nl2, "start_location"));
				nl3 = locationNode.getChildNodes();
				Node latNode = nl3.item(getNodeIndex(nl3, "lat"));
				double lat = Double.parseDouble(latNode.getTextContent());
				Node lngNode = nl3.item(getNodeIndex(nl3, "lng"));
				double lng = Double.parseDouble(lngNode.getTextContent());
				listGeopoints.add(new LatLng(lat, lng));

				locationNode = nl2.item(getNodeIndex(nl2, "polyline"));
				nl3 = locationNode.getChildNodes();
				latNode = nl3.item(getNodeIndex(nl3, "points"));
				ArrayList<LatLng> arr = decodePoly(latNode.getTextContent());
				for(int j = 0 ; j < arr.size() ; j++) {
					listGeopoints.add(new LatLng(arr.get(j).latitude, arr.get(j).longitude));
				}

				locationNode = nl2.item(getNodeIndex(nl2, "end_location"));
				nl3 = locationNode.getChildNodes();
				latNode = nl3.item(getNodeIndex(nl3, "lat"));
				lat = Double.parseDouble(latNode.getTextContent());
				lngNode = nl3.item(getNodeIndex(nl3, "lng"));
				lng = Double.parseDouble(lngNode.getTextContent());
				listGeopoints.add(new LatLng(lat, lng));
			}
		}

		return listGeopoints;
	}










	//Metodo que extrae del xml, la distancia, el tiempo estimado que demorara, y la instruccion a seguir
	//Anda perfecto
	public ArrayList<Instructions> getInstructions (Document doc) {
		NodeList nl1, nl2, nl3;
		ArrayList<Instructions> listInst = new ArrayList<Instructions>();
		nl1 = doc.getElementsByTagName("step");
		if (nl1.getLength() > 0) {
			for (int i = 0; i < nl1.getLength(); i++) {
				Instructions ins=new Instructions();
				Node node1 = nl1.item(i);
				nl2 = node1.getChildNodes();

				Node distanceNode = nl2.item(getNodeIndex(nl2, "distance"));
				nl3 = distanceNode.getChildNodes();
				Node valueNode = nl3.item(getNodeIndex(nl3, "value"));//valor en metros
				ins.setDistance(valueNode.getTextContent());//guardar este valor en la lista(registro)Vamos a crear una clase que contenga lo que necesito.
				//listGeopoints.add(new LatLng(lat, lng));

				Node startLocNode = nl2.item(getNodeIndex(nl2, "end_location"));
				nl3 = startLocNode.getChildNodes();
				valueNode = nl3.item(getNodeIndex(nl3, "lat"));//latitud
				ins.setLat(valueNode.getTextContent());
				valueNode = nl3.item(getNodeIndex(nl3, "lng"));//lngitud
				ins.setLng(valueNode.getTextContent());

				Node durationNode = nl2.item(getNodeIndex(nl2, "duration"));
				nl3 = durationNode.getChildNodes();
				valueNode = nl3.item(getNodeIndex(nl3, "text"));//valor en segundos
				ins.setDuration(valueNode.getTextContent());

				Node instructionNode = nl2.item(getNodeIndex(nl2, "html_instructions"));
				ins.setInstruction(instructionNode.getTextContent());//Agregar la instruccion en la clase. y una vez que tengo todo lo que quiero, en la lista
				listInst.add(ins);

			}
		}

		return listInst;
	}



	private int getNodeIndex(NodeList nl, String nodename) {
		for(int i = 0 ; i < nl.getLength() ; i++) {
			if(nl.item(i).getNodeName().equals(nodename))
				return i;
		}
		return -1;
	}

	private ArrayList<LatLng> decodePoly(String encoded) {
		ArrayList<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;
		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;                 shift += 5;             } while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;
			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;                 shift += 5;             } while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
			poly.add(position);
		}
		return poly;
	}

	private String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line).append('\n');
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

}
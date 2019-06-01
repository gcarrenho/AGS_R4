package com.ags.guideme.navigation;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

public class GBusDirection {

	public final static String MODE_DRIVING = "driving";
	public final static String MODE_WALKING = "walking";

	public GBusDirection() { 

	}

	public Document getDocument(String linea) {
		String url = "http://satcrc.com.ar/kml/"+linea+".kml";

		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpPost httpPost = new HttpPost(url);
			HttpResponse response = httpClient.execute(httpPost, localContext);
			InputStream in = response.getEntity().getContent();
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(in);
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getCoorValue (Document doc) {
		NodeList nl1 = doc.getElementsByTagName("Document");
		Node node1 = nl1.item(0);
		NodeList nl2 = node1.getChildNodes();
		Node node2 = nl2.item(getNodeIndex(nl2, "Placemark"));
		NodeList nl3=node2.getChildNodes();
		Node node3=nl3.item(getNodeIndex(nl3, "LineString"));
		NodeList nl4=node3.getChildNodes();
		Node node4=nl4.item(getNodeIndex(nl4, "coordinates"));
		Log.i("CordenadasValues", node1.getTextContent());
		return node4.getTextContent();
	}

	private int getNodeIndex(NodeList nl, String nodename) {
		for(int i = 0 ; i < nl.getLength() ; i++) {
			if(nl.item(i).getNodeName().equals(nodename))
				return i;
		}
		return -1;
	}
}

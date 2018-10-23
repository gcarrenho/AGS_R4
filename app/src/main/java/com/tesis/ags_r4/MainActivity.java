package com.tesis.ags_r4;

import java.util.Locale;

import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


//Esta Clase no se usa
public class MainActivity extends ActionBarActivity implements OnInitListener {

	private TextToSpeech tts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tts = new TextToSpeech(this, this);
		setContentView(R.layout.activity_main);
	}

	//	@Override
	//	public boolean onCreateOptionsMenu(Menu menu) {
	//
	//		// Inflate the menu; this adds items to the action bar if it is present.
	//		getMenuInflater().inflate(R.menu.main, menu);
	//		return true;
	//	}
	//
	//	@Override
	//	public boolean onOptionsItemSelected(MenuItem item) {
	//		// Handle action bar item clicks here. The action bar will
	//		// automatically handle clicks on the Home/Up button, so long
	//		// as you specify a parent activity in AndroidManifest.xml.
	//		int id = item.getItemId();
	//		if (id == R.id.action_settings) {
	//			return true;
	//		}
	//		return super.onOptionsItemSelected(item);
	//	}

	public void lanzarOp(View v){
		Intent i=new Intent(this,Opciones.class);
		//nombrar todas las opciones que tiene para elegir
		tts.speak("Elija la opción que desea, Cargar, Visitar, llamar, Ayuda", TextToSpeech.QUEUE_FLUSH, null);
		startActivity(i);
	}

	@Override
	public void onInit(int code) {
		if (code==TextToSpeech.SUCCESS) {
			tts.setLanguage(Locale.getDefault());
		} else {
			tts = null;
			Toast.makeText(this, "Failed to initialize TTS engine.",

					Toast.LENGTH_SHORT).show();

		}

	}

}

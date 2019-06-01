package com.ags.guideme;

import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class SintetizadorDeVoz extends Activity implements OnInitListener{

	private TextToSpeech tts;

	public SintetizadorDeVoz(){
		tts = new TextToSpeech(this, this);
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

	public void dictarMenu(){
		if (tts!=null) {
			//recorrer el enum y dictar uno por uno.
			String text ="Holaa";
			if (text!=null) {
				if (!tts.isSpeaking()) {
					tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);

				}

			}

		}
	}


}

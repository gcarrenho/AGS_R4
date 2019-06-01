package com.ags.guideme;

import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.widget.Toast;
import android.widget.EditText;


public class Ayuda extends Activity implements OnInitListener{

	private TextToSpeech tts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ayuda);
		
		tts=new TextToSpeech(this,this);

		/*if (tts!=null) {
			//recorrer el enum y dictar uno por uno.
			String text =((EditText)findViewById(R.id.editText1)).getText().toString();
			if (text!=null) {
				if (!tts.isSpeaking()) {
					tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);

				}

			}

		}
		Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // acciones que se ejecutan tras los milisegundos
            	startVoiceRecognitionActivity();
            }
            }, MILISEGUNDOS_ESPERA);*/
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

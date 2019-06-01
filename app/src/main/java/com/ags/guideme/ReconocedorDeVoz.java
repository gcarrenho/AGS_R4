package com.ags.guideme;


import java.util.ArrayList;
import java.util.Locale;
import java.util.Vector;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

public class ReconocedorDeVoz extends Activity implements OnInitListener {

	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1;
	private TextToSpeech tts;

	public ReconocedorDeVoz(){
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



	private void startVoiceRecognitionActivity() {
		// Definici�n del intent para realizar en an�lisis del mensaje
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		// Indicamos el modelo de lenguaje para el intent
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		// Definimos el mensaje que aparecer� 
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Diga, Llamar a ...");
		// Lanzamos la actividad esperando resultados
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}



	@Override
	//Recogemos los resultados del reconocimiento de voz
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//Si el reconocimiento a sido bueno
		if(requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK){
			//El intent nos envia un ArrayList aunque en este caso solo 
			//utilizaremos la pos.0
			ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			//Separo el texto en palabras.
			String [ ] palabras = matches.get(0).toString().split(" ");
			//ACA HACER EL ANALISIS PARA LO QUE RECONOZCAMOS(CARGAR, VISITAR,LLAMAR, AYUDA).
			if(palabras[0].equals("llamar")||palabras[0].equals("Samar")){
				/*for(int a=0;a<nombres.size();a++){
					//Busco el nombre que es la tercera posicion (LLAMAR A LORENA)
					if(nombres.get(a).equals(palabras[2])){
						//Si la encuentra recojo el numero telf en el otro
						//vector que coincidira con la posicion ya que 
						//los hemos rellenado a la vez.	
						Intent callIntent = new Intent(Intent.ACTION_CALL);
						callIntent.setData(Uri.parse("tel:"+telefonos.get(a)));
						//Realizo la llamada
						startActivity(callIntent);
						break;
					}
				}*/
			}
		}
	}


}

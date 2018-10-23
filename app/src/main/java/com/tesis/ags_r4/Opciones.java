package com.tesis.ags_r4;



import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.widget.Toast;

import com.tesis.ags_r4.R;

//NO SE USA
//Esta clase debe escuchar la opcion que el usuario desea efectuar
// una vez que reconoce la opcion debe ejecutarla y abrir el evento correspondiente.
public class Opciones extends Activity implements OnInitListener{
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1;
	private TextToSpeech tts;
	public static int MILISEGUNDOS_ESPERA = 5000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guiar_elegir);
		tts=new TextToSpeech(this,this);
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				// acciones que se ejecutan tras los milisegundos
				startVoiceRecognitionActivity();
			}
		}, MILISEGUNDOS_ESPERA);
		//larga el reconocedor, pero cuando la mina sigue hablando

		//findViewById(R.id.button1).setOnClickListener(this);//activa escuchador del click
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
			//Si la primera palabra es LLAMAR
			Log.d("PALABRAAAA", palabras[0]);
			if(palabras[0].equals("Cargar")){
				//Actividad para cargar el sitio nuevo.
			}
			else if(palabras[0].equals("Visitar")){
				//Actividad para visitar algun sitio
			}  
			else if(palabras[0].equals("llamar")||palabras[0].equals("Samar")){
				//abrir otra actividad para efectuar la llamada
				if (tts!=null) {
					String text ="Llamando a ";
					if (text!=null) {
						if (!tts.isSpeaking()) {
							tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);

						}

					}
				}	
			}
			else if(palabras[0].equals("ayuda")){
				//Actividad que explica el uso de la aplicacion
				Intent i=new Intent(this,Ayuda.class);
				startActivity(i);
			}
			/*for(int a=0;a<nombres.size();a++){
					//Busco el nombre que es la tercera posicion (LLAMAR A LORENA)
					if(nombres.get(a).equals(palabras[2])){
						//Si la encuentra recojo el numero telf en el otro
						//vector que coincidira con la posicion ya que 
						//los hemos rellenado a la vez.
						if (tts!=null) {
							String text ="Llamando a "+palabras[2] ;
							if (text!=null) {
								if (!tts.isSpeaking()) {
									tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);

								}

							}

						}	
						Intent callIntent = new Intent(Intent.ACTION_CALL);
						callIntent.setData(Uri.parse("tel:"+telefonos.get(a)));
						//Realizo la llamada
						startActivity(callIntent);
						break;
					}
				}*/
		}
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

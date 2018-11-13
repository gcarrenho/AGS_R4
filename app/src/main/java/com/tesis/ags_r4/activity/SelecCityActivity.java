package com.tesis.ags_r4.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.tesis.ags_r4.AgsIntents;
import com.tesis.ags_r4.ItemAdapter;
import com.tesis.ags_r4.Lugar;
import com.tesis.ags_r4.R;


public class SelecCityActivity extends Activity{
	List<String> listCity;
	private ListView listViewCity;
	private Lugar lugarBd;
	private List<Lugar> l;
	private static final int ELIMINAR_REQUEST_CODE = 0;
	private static final int EDITAR_REQUEST_CODE = 1;

	public static final String APP_EXIT_KEY = "APP_EXIT_KEY";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean exit = false;
		Intent intent = getIntent();
		if(intent != null){

			if(intent.getExtras() != null && intent.getExtras().containsKey(APP_EXIT_KEY)){
				exit = true;
			}
		}
		Window window = getWindow();
		final Activity activity = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		lugarBd= new Lugar(this);
		lugarBd.open();
		//DEBERIA TRAER SOLAMENTE LAS CIUDADES(LISTADO SIMPLE), DEPUES ABRIR Y TRAER LOS
		//LUGARES DE DICHA CIUDAD
		l=lugarBd.getAllLugares();		
		setContentView(R.layout.list_citys);
		createListCity();
		//creo lista con ciudades
		listViewCity=(ListView) findViewById(R.id.listView);
		final ItemAdapter adapter = new ItemAdapter(this,listCity);
		listViewCity.setAdapter(adapter);
		
		String boton=intent.getStringExtra("boton");
		
		 if (boton.equalsIgnoreCase("llamar")){//apreto boton llamar
				//abrir el evento para editar el lugar
				if(l.isEmpty()){
					Toast.makeText(this, R.string.no_lugar,Toast.LENGTH_SHORT).show();
				}
				 listViewCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			            @Override
			            public void onItemClick(AdapterView<?> adapter, View view,
			                    int position, long arg) {	         
			                // Obtengo city
			                String city = (String) listViewCity.getAdapter().getItem(position);
			                final Intent  cat= new Intent(activity, AgsIntents.getSelecCatActivity());
			                cat.putExtra("boton", "llamar");
			                cat.putExtra("city", city);
							activity.startActivity(cat);
			            }
			        });
			}
		 else if(boton.equalsIgnoreCase("eliminar")){//apreto boton eliminar
			//eliminar el lugar de la base de datos..
			if(l.isEmpty()){
				Toast.makeText(getBaseContext(),R.string.no_lugar, Toast.LENGTH_LONG)
				.show();
			}//abrir activity con expandableList y llevar que boton es y la ciudad...
			 listViewCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		            @Override
		            public void onItemClick(AdapterView<?> adapter, View view,
		                    int position, long arg) {	         
		                // Obtengo city
		                String city = (String) listViewCity.getAdapter().getItem(position);
		                Toast.makeText(getBaseContext(),"Ciudad "+city, Toast.LENGTH_LONG).show();
		                final Intent  cat= new Intent(activity, AgsIntents.getSelecCatActivity());
		                cat.putExtra("boton", "eliminar");
		                cat.putExtra("city", city);
						activity.startActivity(cat);
		            }
		        });
		 
//			expListView.setOnChildClickListener(new OnChildClickListener() {
//
//				public boolean onChildClick(ExpandableListView parent, View v,
//						int groupPosition, int childPosition, long id) {
//					String nombre=(String) expListAdapter.getChild(groupPosition, childPosition);
//					lugarBd.open();
//					lugarBd.deleteLugar(nombre);
//					Toast.makeText(getBaseContext(),"Lugar "+nombre+" Eliminado Correctamente", Toast.LENGTH_LONG)
//					.show();			              
//					actualizarLista();
//					return true;
//				}
//			});

		}else if (boton.equalsIgnoreCase("editar")){//apreto boton editar
			//abrir el evento para editar el lugar
			if(l.isEmpty()){
				Toast.makeText(this, R.string.no_lugar,Toast.LENGTH_SHORT).show();
			}
			 listViewCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		            @Override
		            public void onItemClick(AdapterView<?> adapter, View view,
		                    int position, long arg) {	         
		                // Obtengo city
		                String city = (String) listViewCity.getAdapter().getItem(position);
		                final Intent  cat= new Intent(activity, AgsIntents.getSelecCatActivity());
		                cat.putExtra("boton", "editar");
		                cat.putExtra("city", city);
						activity.startActivity(cat);
		            }
		        });
		}else{//el boton es para guiar
			if(l.isEmpty()){
				Toast.makeText(getBaseContext(), R.string.no_lugar, Toast.LENGTH_LONG)
				.show();
			}
			 listViewCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		            @Override
		            public void onItemClick(AdapterView<?> adapter, View view,
		                    int position, long arg) {	         
		                // Obtengo city
		                String city = (String) listViewCity.getAdapter().getItem(position);
		                Toast.makeText(getBaseContext(),"Ciudad "+city, Toast.LENGTH_LONG).show();
		                final Intent  cat= new Intent(activity, AgsIntents.getSelecCatActivity());
		                cat.putExtra("boton", "guiar");
		                cat.putExtra("city", city);
						activity.startActivity(cat);
		            }
		        });
		}
	}

	
	
	//Metodo que crea la lista de las ciudades existentes.
		private void createListCity(){
			listCity= new ArrayList<String>();
			int i=0;
			while (i<l.size()){
				if(!listCity.contains(l.get(i).getLocalidad())){
					listCity.add(l.get(i).getLocalidad());
				}
				i++;
			}
		}
		
	//Metodo que crea la lista de las categorias existentes.
//	private void createListCat(){
//		listCat= new ArrayList<String>();
//		int i=0;
//		while (i<l.size()){
//			if(!listCat.contains(l.get(i).getCategoria())){
//				listCat.add(l.get(i).getCategoria());
//			}
//			i++;
//		}
//	}
	
	

	//Metodo que hace un mapeo entre las categorias y los lugares que estan el esa misma categoria.
//	private void createCollecctionLugares(){
//		List<String> nomLugar;
//		collLugares = new LinkedHashMap<String, List<String>>();
//		int i=0,j;
//		while (i<listCat.size()){
//			j=0;
//			nomLugar=new ArrayList<String>();
//			while (j<l.size()){
//				if (listCat.get(i).equals(l.get(j).getCategoria())){
//					nomLugar.add(l.get(j).getNombre());
//
//				}
//				j++;
//			}
//			collLugares.put(listCat.get(i),nomLugar);
//			i++;
//		}
//	}
	
//	//Metodo que hace un mapeo entre las ciudades y sus lugares que estan el esa misma categoria.
//		private void createCollecctionLugares(){
//			List<String> cat;
//			collLugares = new LinkedHashMap<String, List<String>>();
//			int i=0,j;
//			while (i<listCity.size()){
//				j=0;
//				nomLugar=new ArrayList<String>();
//				while (j<l.size()){
//					if (listCity.get(i).equals(l.get(j).getCategoria())){
//						nomLugar.add(l.get(j).getNombre());
//
//					}
//					j++;
//				}
//				collLugares.put(listCity.get(i),nomLugar);
//				i++;
//			}
//		}

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		// Comprobamos si el resultado de la segunda actividad es "RESULT_CANCELED".
//		if (resultCode == RESULT_CANCELED) {
//			// Si es así mostramos mensaje de cancelado por pantalla.
//			Toast.makeText(this, "Accion Cancelada", Toast.LENGTH_SHORT)
//			.show();
//		} else {
//			// De lo contrario, recogemos el resultado de la segunda actividad.
//			//String resultado = data.getExtras().getString("RESULTADO");
//			// Y tratamos el resultado en función de si se lanzó para rellenar el
//			// nombre o el apellido.
//			lugarBd.open();
//			this.actualizarLista();
//			lugarBd.close();
//			Toast.makeText(getBaseContext(),"Lugar Editado Correctamente", Toast.LENGTH_LONG)
//			.show();
//		}
//	}

//	public void actualizarLista(){
//		l=lugarBd.getAllLugares();
//		createListCat();
//		createCollecctionLugares();
//		final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(this, listCat, collLugares);
//		expListView.setAdapter(expListAdapter);
//	}

	@Override
	protected void onRestart() {
		super.onRestart();
		lugarBd.open();

	}

	@Override
	protected void onPause() {
		super.onPause();
	}
}

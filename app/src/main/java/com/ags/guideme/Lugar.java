package com.ags.guideme;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ags.guideme.db.datosBd;

public class Lugar implements Serializable {
	private static final long serialVersionUID = 729654300829771466L;
	private String nombre;
	private String categoria = "";
	private int tel; //NO USARLO
	private String telefono; 
	private double latitud;
	private double longitud;
	private String localidad;
	private boolean stored = false;
	private Map<String, List<Lugar>> lugaresGroups = null;
	private static final String DATABASE_NAME = "datos";
	private static final String DATABASE_TABLE = "lugares";
	private static final int DATABASE_VERSION = 3;
	public static final String KEY_NOMBRE = "nombre";
	public static final String KEY_TIPO = "tipo";
	public static final String KEY_TEL ="tel";
	public static final String KEY_TELEFONO ="telefono";
	public static final String KEY_LAT ="latitud";
	public static final String KEY_LONG ="longitud";
	public static final String KEY_LOC ="localidad";
	public static final String KEY_ROWID ="_id";


	private datosBd mDbHelper;
	private SQLiteDatabase mDb;
	private static final String SQLUpdateV2 = "ALTER TABLE lugares ADD COLUMN localidad TEXT NOT NULL DEFAULT 'río cuarto'";
	private static final String SQLUpdateV3 = "ALTER TABLE lugares ADD COLUMN telefono TEXT";
	private static final String DATABASE_CREATE ="create table " + DATABASE_TABLE + " ("
			+ KEY_ROWID + " INTEGER primary key autoincrement, "
			+ KEY_NOMBRE + " TEXT not null, "
			+ KEY_TIPO + " TEXT not null, "+KEY_TEL+" INTEGER, "+KEY_LAT+" REAL, "+KEY_LONG+" REAL );";//,"+KEY_LOC+" TEXT

	private final Context mCtx;

	public Lugar(Context ctx){
		this.mCtx=ctx;
	}

	public double getLatitud() {
		return latitud;
	}

	public boolean isStored() {
		return stored;
	}
	public void setStored(boolean stored) {
		this.stored = stored;
	}

	public void setLatitud(double latitud) {
		this.latitud = latitud;
	}

	public double getLongitud() {
		return longitud;
	}

	public void setLongitud(double longitud) {
		this.longitud = longitud;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getTel() {
		return tel;
	}

	public void setTel(int tel) {
		this.tel = tel;
	}

	@Override
	public String toString() {
		return "Lugar " + getNombre(); 
	}


	public Lugar open() throws android.database.SQLException {
		mDbHelper = new datosBd(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	public long createLugar(Lugar l) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NOMBRE, l.getNombre());
		initialValues.put(KEY_TIPO, l.getCategoria());
		initialValues.put(KEY_TEL, l.getTel());
		initialValues.put(KEY_LAT, l.getLatitud());
		initialValues.put(KEY_LONG, l.getLongitud());
		initialValues.put(KEY_LOC, l.getLocalidad());
		initialValues.put(KEY_TELEFONO, l.getTelefono());
		return mDb.insert(DATABASE_TABLE, null, initialValues);
	}

	public boolean deleteLugar(String nom) {
		return	mDb.delete(DATABASE_TABLE, KEY_NOMBRE + "=" +"'"+nom+"'",null) > 0;

	}

	public Cursor ferchAllLugares() {
		return mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID,
				KEY_NOMBRE, KEY_TIPO, KEY_TEL, KEY_LAT, KEY_LONG, KEY_LOC,KEY_TELEFONO}, null, null, null, null,null, null);
	} 

	public List<Lugar> getAllLugares() {
		List<Lugar> listaLugares = new ArrayList<Lugar>();

		Cursor cursor = mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID,
				KEY_NOMBRE, KEY_TIPO, KEY_TEL, KEY_LAT, KEY_LONG,KEY_LOC,KEY_TELEFONO}, null , null,
				null, null, KEY_LOC,null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Lugar nuevoLugar = cursorToLugar(cursor);
			listaLugares.add(nuevoLugar);
			cursor.moveToNext();
		}

		cursor.close();
		return listaLugares;
	}

	public List<Lugar> getAllPlaceByCity(String city) {
		List<Lugar> listaLugares = new ArrayList<Lugar>();

		Cursor cursor = mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID,
				KEY_NOMBRE, KEY_TIPO, KEY_TEL, KEY_LAT, KEY_LONG,KEY_LOC,KEY_TELEFONO}, KEY_LOC+"='"+city+"'" , null,
				null, null, KEY_TIPO,null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Lugar nuevoLugar = cursorToLugar(cursor);
			listaLugares.add(nuevoLugar);
			cursor.moveToNext();
		}

		cursor.close();
		return listaLugares;
	}
	
	
	public List<Lugar> getAllPlaceTel(String city) {
		List<Lugar> listaLugares = new ArrayList<Lugar>();

		Cursor cursor = mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID,
				KEY_NOMBRE, KEY_TIPO, KEY_TEL, KEY_LAT, KEY_LONG, KEY_LOC,KEY_TELEFONO},KEY_TELEFONO+"!='0'"+" AND "+ KEY_LOC+"="+"'"+city+"'" , null,
				null, null,KEY_TIPO, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Lugar nuevoLugar = cursorToLugar(cursor);
			listaLugares.add(nuevoLugar);
			cursor.moveToNext();
		}

		cursor.close();
		return listaLugares;
	}
	
	
	public Lugar getLugar(String nom, String city) {
		//Lugar lugar ;

		Cursor cursor = mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID,
				KEY_NOMBRE, KEY_TIPO, KEY_TEL, KEY_LAT, KEY_LONG, KEY_LOC,KEY_TELEFONO},KEY_NOMBRE+"="+"'"+nom+"' AND "+ KEY_LOC+"="+"'"+city+"'" , null,
				null, null,null, null);
		cursor.moveToFirst();
		//while (!cursor.isAfterLast()) {
		Lugar lugar = cursorToLugar(cursor);
		//listaLugares.add(nuevoLugar);
		//cursor.moveToNext();
		//}

		cursor.close();
		return lugar;
	}
	
	public Lugar getLugar(String nom) {
		//Lugar lugar ;

		Cursor cursor = mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID,
				KEY_NOMBRE, KEY_TIPO, KEY_TEL, KEY_LAT, KEY_LONG, KEY_LOC,KEY_TELEFONO},KEY_NOMBRE+"="+"'"+nom+"'" , null,
				null, null,null, null);
		cursor.moveToFirst();
		//while (!cursor.isAfterLast()) {
		Lugar lugar = cursorToLugar(cursor);
		//listaLugares.add(nuevoLugar);
		//cursor.moveToNext();
		//}

		cursor.close();
		return lugar;
	}

	private Lugar cursorToLugar(Cursor cursor) {
		Lugar l = new Lugar(mCtx);
		l.setNombre(cursor.getString(1));
		l.setCategoria(cursor.getString(2));
		l.setTel(cursor.getInt(3));
		l.setLatitud(cursor.getDouble(4));
		l.setLongitud(cursor.getDouble(5));
		l.setLocalidad(cursor.getString(6));
		l.setTelefono(cursor.getString(7));
		return l;
	}

	//Metodo que retorna falso si el cursor es vacio, sino si contiene algo retorna true
	public boolean ExisteLugar(String nombre, String city) {		  
		Cursor c = mDb.rawQuery(" SELECT * FROM "+DATABASE_TABLE+" WHERE "+KEY_NOMBRE+"="+"'"+nombre+"' AND "+KEY_LOC+"="+"'"+city+"'", null);        

		return c.moveToFirst();
	}

	public void actualizaLugar(String nombre, Lugar l,String city){
		//mDb.update(DATABASE_TABLE, values, KEY_NOMBRE+"="+"'"+nombre+"'", null);
		mDb.execSQL("UPDATE "+ DATABASE_TABLE+" SET "+ KEY_NOMBRE+"="+"'"+ l.getNombre() +
				"'"+","+KEY_TIPO+"="+"'"+l.getCategoria()+"'"+
				","+ KEY_TEL+"="+"'"+ l.getTel()+"'"+
				","+ KEY_LAT+"="+"'"+ l.getLatitud()+"'"+
				","+ KEY_LONG+"="+"'"+ l.getLongitud()+"'"+
				","+ KEY_LOC+"="+"'"+ l.getLocalidad()+"'"+
				","+ KEY_TELEFONO+"="+"'"+ l.getTelefono()+"'"+
				" WHERE "+KEY_NOMBRE+"="+"'"+nombre+"' AND "+KEY_LOC+"="+"'"+city+"'");
		mDb.close();
	}

	//Busca un lugar especifico en la bd
	/*public Lugar buscarLugarNom(String nombre){
		Lugar lugar = null;
	    SQLiteDatabase bd = lugarBd.getReadableDatabase();
	    Cursor cursor = bd.rawQuery("SELECT * FROM lugares WHERE nombre = " + nombre, null);
	    if (cursor.moveToNext()){
	        lugar = new Lugar();
	        lugar.setNombre(cursor.getString(1));
	        lugar.setCategoria(cursor.getString(2));
	        lugar.setTel(cursor.getInt(3));
	        lugar.setLatitud(cursor.getDouble(4));
	        lugar.setLongitud(cursor.getDouble(5));
	    }
	    cursor.close();
	    bd.close();
	    return lugar;
	}*/

	//actualiza los datos de un lugar determinado en la bd
	/*public void actualizaLugar(int id, Lugar lugar){
	    SQLiteDatabase bd = lugarBd.getWritableDatabase();
	    bd.execSQL("UPDATE lugares SET nombre = '"+ lugar.getNombre() +
	        "', categoria = '" + lugar.getCategoria() +
	        "', telefono = " + lugar.getTel()  +
	        "', latitud = " + lugar.getLongitud()  +
	        " , longitud = " + lugar.getLatitud()  +
	        " WHERE _id = "+ id);
	        bd.close();
	}*/


	//Metodo que agrega un nuevo lugar, si se produce algun error, retorna id=-1;
	/*public int addLugar(Lugar lugar) {
	    int id = -1;
	    //Lugar lugar = new Lugar();
	    SQLiteDatabase bd = lugarBd.getWritableDatabase();
	    bd.execSQL("INSERT INTO lugares (nombre,categoria,telefono,latitud, longitud) VALUES ( "+
	       lugar.getNombre()+","+lugar.getCategoria()+","+lugar.getTel()+","+lugar.getLatitud()+", "+lugar.getLongitud()+")");
	    Cursor c = bd.rawQuery("SELECT _id FROM lugares WHERE nombre = " +
	                                lugar.getNombre(), null);
	    if (c.moveToNext()){
	        id = c.getInt(0);
	    }
	    c.close();
	    bd.close();
	    return id;
	}

	public boolean addLugar1(Lugar l){
		SQLiteDatabase bd = lugarBd.getWritableDatabase();
		if (bd != null) {
			try {

				bd.execSQL("INSERT INTO lugares(id,nombre,edad) VALUES(null,'german',24) ");
				/*bd.execSQL(
						"INSERT INTO " + "lugares" + " (" + "nombre" + ", " + categoria + ", "
								+"telefono"+","+ latitud + ", " + longitud + ")" + " VALUES (?, ?, ?, ?,?)", new Object[] {"Casa", "Hogar",4680005,1.3596, 1.5896 });
				/*if (!lugaresGroups.containsKey(l.getCategoria())) {
					lugaresGroups.put(l.getCategoria(), new ArrayList<Lugar>());
					if (!l.getNombre().equals("")) {
						addLugar(new Lugar("", l.getCategoria(),0,0,0));
					}
				}
				if (!l.getNombre().equals("")) {
					lugaresGroups.get(l.getCategoria()).add(l);
					//cachedLugares.add(l);
				}
				l.setStored(true);*/
	//backupSilently();
	/*	} finally {
				bd.close();
			}
		}
		return true;
	}*/

	public String getLocalidad() {
		return localidad;
	}

	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}
	
	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	/**
	 * Crea una nueva tarea, si esto va bien retorna la
	 * rowId de la tarea, de lo contrario retorna -1
	 */
	/*public long createTodo(String nombre,String categoria) {
		ContentValues initialValues = createContentValues(nombre,categoria);
		//database.execSQL("INSERT INTO lugares(nombre,edad) VALUES('german',24) ");//ESto no anda
		//database.execSQL("INSERT INTO lugares"+"("+LUGAR_COL_NAME+","+LUGAR_COL_CATEGORY+")"+"VALUES('german',24) ");
		return database.insert(LUGAR_TABLE_NAME, null, initialValues);//esto aparent si
	}

	//Elimina un lugar de la base de datos
	public void eliminarLugar(int id){
		SQLiteDatabase bd = lugarBd.getWritableDatabase();
	    bd.execSQL("DELETE FROM lugares WHERE _id = " + id );
	    bd.close();
	}

	private ContentValues createContentValues(String nombre, String categoria) {
		ContentValues values = new ContentValues();
		values.put(LUGAR_COL_NAME, nombre);
		values.put(LUGAR_COL_CATEGORY, categoria);
		/*values.put(LUGAR_COL_TEL, tel);
		values.put(LUGAR_COL_LAT , latitud);
		values.put(LUGAR_COL_LON, longitud);*/
	/*return values;
	}*/

	/*private static class LugarBd extends SQLiteOpenHelper {

		LugarBd(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
			db.execSQL(SQLUpdateV2);
			db.execSQL(SQLUpdateV3);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			//db.execSQL("delete table if exists " + DATABASE_TABLE );
			if(oldVersion<2){
				//agregar columna y cargar con rio cuarto
				db.execSQL(SQLUpdateV2);
			}
			if(oldVersion<3){
				db.execSQL(SQLUpdateV3);
			}
			
			//onCreate(db);
		}

	}*/

}
package com.tesis.ags_r4.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class datosBd extends SQLiteOpenHelper {

	

	private static final String DATABASE_NAME = "datos";
	private static final String DATABASE_TABLE = "lugares";
	private static final String TABLE_CONTACT = "contactos";
	private static final int DATABASE_VERSION = 4;
	public static final String KEY_NOMBRE = "nombre";
	public static final String KEY_LASTNAME = "lastname";
	public static final String KEY_TIPO = "tipo";
	public static final String KEY_TEL ="tel";
	public static final String KEY_TELEFONO ="telefono";
	public static final String KEY_LAT ="latitud";
	public static final String KEY_LONG ="longitud";
	public static final String KEY_LOC ="localidad";
	public static final String KEY_ROWID ="_id";
	private static final String SQLUpdateV2 = "ALTER TABLE lugares ADD COLUMN localidad TEXT NOT NULL DEFAULT 'río cuarto'";
	private static final String SQLUpdateV3 = "ALTER TABLE lugares ADD COLUMN telefono TEXT";
	
	private static final String SQLUpdateV4 = "create table " + TABLE_CONTACT + " ("
			+ KEY_ROWID + " INTEGER primary key autoincrement, "
			+ KEY_LASTNAME + " TEXT not null, "+KEY_TEL+" TEXT not null, "+KEY_LONG+" REAL );";
	
	private static final String DATABASE_CREATE ="create table " + DATABASE_TABLE + " ("
			+ KEY_ROWID + " INTEGER primary key autoincrement, "
			+ KEY_NOMBRE + " TEXT not null, "
			+ KEY_TIPO + " TEXT not null, "+KEY_TEL+" INTEGER, "+KEY_LAT+" REAL, "+KEY_LONG+" REAL );";//,"+KEY_LOC+" TEXT

	private static final String CREATE_TABLA_CONTACT ="create table " + TABLE_CONTACT + " ("
			+ KEY_ROWID + " INTEGER primary key autoincrement, "
			+ KEY_LASTNAME + " TEXT not null, "+KEY_TEL+" TEXT not null, "+KEY_LONG+" REAL );";
	
	public datosBd(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
		db.execSQL(CREATE_TABLA_CONTACT);
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
		}if(oldVersion<4){
			db.execSQL(SQLUpdateV4);
		}
		
		//onCreate(db);
	}

	
	
}

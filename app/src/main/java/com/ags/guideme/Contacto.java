package com.ags.guideme;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ags.guideme.db.datosBd;


public class Contacto implements Serializable{

	private String lastname;
	private String tel; 
	
	private datosBd mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mCtx;
	
	private static final String TABLE_CONTACT = "contactos";
	public static final String KEY_ROWID ="_id";
	public static final String KEY_TEL ="tel";
	public static final String KEY_LASTNAME = "lastname";

	public Contacto(Context ctx){
		this.mCtx=ctx;
	}
	
	
	public Contacto open() throws android.database.SQLException {
		mDbHelper = new datosBd(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}
	
	public void close() {
		mDbHelper.close();
	}
	
	public long createContact(Contacto c) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_LASTNAME, c.getLastname());
		initialValues.put(KEY_TEL, c.getTel());
		return mDb.insert(TABLE_CONTACT, null, initialValues);
	}

	public boolean deleteContact(String lnam) {
		return	mDb.delete(TABLE_CONTACT, KEY_LASTNAME + "=" +"'"+lnam+"'",null) > 0;

	}
	
	public List<Contacto> getAllContact() {
		List<Contacto> listaContact = new ArrayList<Contacto>();

		Cursor cursor = mDb.query(TABLE_CONTACT, new String[]{KEY_ROWID,
				KEY_LASTNAME,KEY_TEL}, null , null,
				null, null, null,null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Contacto nuevoContact = cursorToContact(cursor);
			listaContact.add(nuevoContact);
			cursor.moveToNext();
		}

		cursor.close();
		return listaContact;
	}
	
	private Contacto cursorToContact(Cursor cursor) {
		Contacto c = new Contacto(mCtx);
		c.setLastname(cursor.getString(1));
		c.setTel(cursor.getString(2));
		return c;
	}

	//Metodo que retorna falso si el cursor es vacio, sino si contiene algo retorna true
	public boolean ExisteContact(String tel) {		  
		Cursor c = mDb.rawQuery(" SELECT * FROM "+TABLE_CONTACT+" WHERE "+KEY_TEL+"="+"'"+tel+"'", null);        

		return c.moveToFirst();
	}

	public void actualizaContact(String lname, Contacto c,String tel){
		mDb.execSQL("UPDATE "+ TABLE_CONTACT+" SET "+ KEY_LASTNAME+"="+"'"+ c.getLastname() +
				"'"+","+ KEY_TEL+"="+"'"+ c.getTel()+"'"+" WHERE "+KEY_LASTNAME+"="+"'"+lname+"' AND "+KEY_TEL+"="+"'"+tel+"'");
		mDb.close();
	}
	


	public String getLastname() {
		return lastname;
	}


	public void setLastname(String lastname) {
		this.lastname = lastname;
	}


	public String getTel() {
		return tel;
	}


	public void setTel(String tel) {
		this.tel = tel;
	}
	
}

package com.tesis.ags_r4.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.os.Environment;

public class MakeFile {

	public MakeFile(){

	}

	public void grabar(String nom,String coord){
		String nomarchivo = nom;
		String contenido = coord;
		try {
			File tarjeta = Environment.getExternalStorageDirectory();
			File file=new File(tarjeta.getAbsolutePath()+"/Android/data/com.tesis.ags_r4/recorridos");
			boolean success = true;
			if (!file.exists()) {
				//Toast.makeText(MainActivity.this, "Directory Does Not Exist, Create It", Toast.LENGTH_SHORT).show();
				success = file.mkdirs();
			}
			file = new File(file+"/", nomarchivo);
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file));
			osw.write(contenido);
			osw.flush();
			osw.close();

		} catch (IOException ioe) {
		}
	}


	public String recuperar(String nom) {
		String nomarchivo = nom;
		String todo = null ;
		File tarjeta = Environment.getExternalStorageDirectory();
		File file = new File(tarjeta.getAbsolutePath()+"/Android/data/com.tesis.ags_r4/recorridos/", nomarchivo);
		try {
			FileInputStream fIn = new FileInputStream(file);
			InputStreamReader archivo = new InputStreamReader(fIn);
			BufferedReader br = new BufferedReader(archivo);
			String linea = br.readLine();
			todo = "";
			while (linea != null) {
				todo = todo + linea + " ";
				linea = br.readLine();
			}
			br.close();
			archivo.close();


		} catch (IOException e) {
		}
		return todo;
	}

	public boolean fileExist(){
		File tarjeta = Environment.getExternalStorageDirectory();
		File file=new File(tarjeta.getAbsolutePath()+"/Android/data/com.tesis.ags_r4/recorridos");
		return file.exists();
	}
	


}

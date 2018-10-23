package com.tesis.ags_r4;

import com.tesis.ags_r4.activity.AbmActivity;
import com.tesis.ags_r4.activity.AdminActivity;
import com.tesis.ags_r4.activity.AdminContactActivity;
import com.tesis.ags_r4.activity.CargarActivity;
import com.tesis.ags_r4.activity.EditarActivity;
import com.tesis.ags_r4.activity.EliminarActivity;
import com.tesis.ags_r4.activity.GuiarPorDireccion;
import com.tesis.ags_r4.activity.InfActivity;
import com.tesis.ags_r4.activity.MakeCallActivity;
import com.tesis.ags_r4.activity.SalirActivity;
import com.tesis.ags_r4.activity.SelecCityActivity;
import com.tesis.ags_r4.activity.SeleccionGuiado;
import com.tesis.ags_r4.activity.SelectCatActivity;


//Clase Intensts que retorna todas las activitys

public class AgsIntents {

	public static Class<AbmActivity> getAbmActivity(){
		return AbmActivity.class;
	}
	
	public static Class<SalirActivity> getFavoritosActivity(){
		return SalirActivity.class;
	}
	
	public static Class<InfActivity> getInfActivity(){
		return InfActivity.class;
	}
	
	public static Class<CargarActivity> getCargarActivity(){
		return CargarActivity.class;
	}
	
	public static Class<EliminarActivity> getEliminarActivity(){
		return EliminarActivity.class;
	}
	
	public static Class<EditarActivity> getEditarActivity(){
		return EditarActivity.class;
	}
	
	public static Class<SelectCatActivity> getSelecCatActivity(){
		return SelectCatActivity.class;
	}
	
	public static Class<SelecCityActivity> getSelecCityActivity(){
		return SelecCityActivity.class;
	}
	
	public static Class<GuiarMapa> getGuiarMapa(){
		return GuiarMapa.class;
	}
	
	public static Class<AdminActivity> getAdminActivity(){
		return AdminActivity.class;
	}
	
	public static Class<AdminContactActivity> getAdminContactActivity(){
		return AdminContactActivity.class;
	}
	
	public static Class<MakeCallActivity> getMakeCallActivity(){
		return MakeCallActivity.class;
	}
	
	public static Class<GuiarPorDireccion> getGuiarPorDireccion(){
		return GuiarPorDireccion.class;
	}
	
	public static Class<SeleccionGuiado> getSeleccionGuiado(){
		return SeleccionGuiado.class;
	}
	
	
}

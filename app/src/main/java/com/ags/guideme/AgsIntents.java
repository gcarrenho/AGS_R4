package com.ags.guideme;

import com.ags.guideme.activity.AbmActivity;
import com.ags.guideme.activity.AdminActivity;
import com.ags.guideme.activity.AdminContactActivity;
import com.ags.guideme.activity.CargarActivity;
import com.ags.guideme.activity.GuiarPorDireccion;
import com.ags.guideme.activity.InfActivity;
import com.ags.guideme.activity.MakeCallActivity;
import com.ags.guideme.activity.SalirActivity;
import com.ags.guideme.activity.SelecCityActivity;
import com.ags.guideme.activity.SeleccionGuiado;
import com.ags.guideme.activity.SelectCatActivity;


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

package com.tyoku.util;

import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommonUtil {
	public static Player[] getOnlinePlayers(){
		try{
			return Bukkit.getOnlinePlayers().toArray(new Player[0]);
		}catch(NoSuchMethodError e){
			try{
				Method getOnlinePlayers = null;
				for(Method m : Bukkit.class.getDeclaredMethods()){
					if(m.getName().equals("getOnlinePlayers")){
						getOnlinePlayers = m;
						break;
					}
				}
				if(getOnlinePlayers == null) return new Player[]{};
				Object OnlinePlayers = getOnlinePlayers.invoke(Bukkit.class, new Object[0]);
				if(OnlinePlayers instanceof Player[]) return (Player[])OnlinePlayers;
			}catch (Exception es){}
			return new Player[]{};
		}
	}
}

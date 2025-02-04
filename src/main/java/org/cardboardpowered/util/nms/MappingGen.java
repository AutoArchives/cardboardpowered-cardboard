package org.cardboardpowered.util.nms;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import net.minecraft.server.world.ServerChunkManager;

public class MappingGen {

	/**
	 * "C:\Program Files\Eclipse Adoptium\jdk-21.0.3+9\bin\java.exe" -jar tiny-remapper-0.9.0-fat.jar paper-1.20.6.jar paper-1.20.6-reobf.jar reobf.tiny yarn+mojang spigot
"C:\Program Files\Eclipse Adoptium\jdk-21.0.3+9\bin\java.exe" -jar SpecialSource-1.11.5-SNAPSHOT-shaded.jar -i paper-1.20.6-reobf.jar -o paper-1.20.6-bukkit.jar -m bukkit.csrg -r
"C:\Program Files\Eclipse Adoptium\jdk-21.0.3+9\bin\java.exe" -jar tiny-remapper-0.9.0-fat.jar paper-1.20.6-bukkit.jar paper-1.20.6-intermed.jar 1.20.6.tiny official intermediary --ignoreConflicts
"C:\Program Files\Eclipse Adoptium\jdk-21.0.3+9\bin\java.exe" -jar tiny-remapper-0.9.0-fat.jar paper-1.20.6-intermed.jar paper-1.20.6-yarn.jar mappings.tiny intermediary named --ignoreConflicts


	reobf.tiny, bukkit.csrg, 1.20.6.tiny

	 */
	
	public static void main(String[] args) {
		Class<?> cll = ServerChunkManager.class;
		
		for (Method m : cll.getDeclaredMethods()) {
			
			String n = m.getName();
			
			if (n.contains("getChunkFuture")) {

				
				System.out.println( m.getGenericParameterTypes()[2].getTypeName() );
			}
		}
		
	}
	
}

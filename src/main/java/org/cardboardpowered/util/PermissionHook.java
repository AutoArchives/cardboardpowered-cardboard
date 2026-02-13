package org.cardboardpowered.util;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class PermissionHook {

	public static boolean hasPermission(ServerPlayer plr, String p) {
		return hasPermission(plr, p);
	}
	
    public static boolean hasPermission(Entity e, String permission) {
    	if (Permissions.check(e, permission)) {
    	    // Woo!
    		return true;
    	}

        return false;
    }

}
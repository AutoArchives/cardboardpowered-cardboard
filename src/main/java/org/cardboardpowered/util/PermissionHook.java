package org.cardboardpowered.util;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.level.ServerPlayer;

public class PermissionHook {

    public static boolean hasPermission(ServerPlayer plr, String permission) {
    	
    	if (Permissions.check(plr, permission)) {
    	    // Woo!
    		return true;
    	}
    	
        /*if (null == perms.get(permission)) {
            org.bukkit.permissions.Permission bu = Bukkit.getPluginManager().getPermission(permission);
            if (null == bu)
                return false;
            PermissionDefaults def = PermissionDefaults.OPERATOR;
            switch (bu.getDefault()) {
                case FALSE:
                    def = PermissionDefaults.FALSE;
                    break;
                case NOT_OP:
                    def = PermissionDefaults.NON_OPERATOR;
                    break;
                case OP:
                    def = PermissionDefaults.OPERATOR;
                    break;
                case TRUE:
                    def = PermissionDefaults.TRUE;
                    break;
                default:
                    break;
                
            }
            Permission cy = new Permission(permission, bu.getDescription(), def);
            perms.put(permission, cy);
        }

        Permission cyber = perms.get(permission);
        Permissible perm = CyberPermissions.getPermissible(plr);

        return perm.hasPermission(cyber);*/return false;
    }

}

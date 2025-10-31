/**
 * The Bukkit for Fabric Project
 * Copyright (C) 2020-2023
 */
package org.cardboardpowered.util.nms;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mohistmc.banner.bukkit.nms.utils.RemapUtils;
import net.fabricmc.loader.api.FabricLoader;

/**
 * @deprecated Replaced
 */
@Deprecated
public class MappingsReader {

    // public static Mappings MAPPINGS;
    public static HashMap<String, String> METHODS;
    public static HashMap<String, String> METHODS2;
    public static HashMap<String, String> METHODS3;

    public static Logger LOGGER = LogManager.getLogger("CardboardRemapper");

    public static String dev(String s) {
        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) return s;
        return FabricLoader.getInstance().getMappingResolver().mapClassName("intermediary", s);
    }

    public static String obf(String s) {
        return dev(FabricLoader.getInstance().getMappingResolver().mapClassName("official", s));
    }

    @Deprecated
    private static void main1(String[] args) throws IOException {
        // File dir = new File("mappings");
        // dir.mkdirs();
        // File f = exportResource("spigot2srg.srg", dir);
        // MAPPINGS = MappingsFormat.COMPACT_SEARGE_FORMAT.parseFile(f);
        METHODS = new HashMap<>();
        METHODS2 = new HashMap<>();
        METHODS3 = new HashMap<>();
    }

    // TODO
    public static String getIntermedClass(String spigot) {
        return RemapUtils.map(spigot);
    }
    
    // TODO
    public static String getIntermedField_2(Class<?> c, String spigot) {
    	return RemapUtils.mapFieldName(c, spigot);
    }

    @Deprecated
    public static String getIntermedField_old1(String c, String spigot) throws NoSuchFieldException, SecurityException, ClassNotFoundException {
        return null;
    }

    @Deprecated
    public static String getIntermedField2_old1(String c, String spigot) throws NoSuchFieldException, SecurityException, ClassNotFoundException {
        return null;
    }

    @Deprecated
    public static File exportResource(String res, File folder) {
    	return null;
    }

    @Deprecated
    public static String getIntermedMethod_old(String name, String spigot, Class<?>[] parms) {
        String sig = "(";
        for (Class<?> clazz : parms)
            sig += clazz.getName().substring(clazz.getName().lastIndexOf(".")+1) + ",";

        sig += ")";
        sig = sig.replace(",)", ")");
 
        if (METHODS3.containsKey((name + "=" + spigot + sig)))
            return METHODS3.getOrDefault((name + "=" + spigot + sig), obf(spigot));
        try {
            String iclazz = ReflectionRemapper.mapClassName(name);
            Class<?> cl = Class.forName(iclazz);
            Class<?> parent = cl.getSuperclass();
            if (null != parent) {
                String pname = parent.getName();
                return obf(METHODS3.getOrDefault((pname + "=" + spigot + sig), spigot));
            } else return obf(spigot);
        } catch (Exception e) { return getIntermedMethod_old(name, spigot); }
    }

    @Deprecated
    public static String getIntermedMethod_old(String name, String spigot) {
        try {
            String iclazz = ReflectionRemapper.mapClassName(name);
            Class<?> cl = Class.forName(iclazz);
            Class<?> parent = cl.getSuperclass();
            if (null != parent) {
                String pname = parent.getName();
                return obf(METHODS.getOrDefault(pname + "=" + spigot, spigot));
            } else return obf(spigot);
        } catch (Exception e) { return obf(spigot); }
    }

}
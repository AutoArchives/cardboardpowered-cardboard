/**
 * The Bukkit for Fabric Project
 * Copyright (C) 2020-2023
 */
package org.cardboardpowered.util.nms;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mohistmc.banner.bukkit.nms.utils.RemapUtils;

import net.fabricmc.loader.api.FabricLoader;
import net.techcable.srglib.FieldData;
import net.techcable.srglib.JavaType;
import net.techcable.srglib.format.MappingsFormat;
import net.techcable.srglib.mappings.Mappings;

/**
 * @deprecated Replaced
 */
@Deprecated
public class MappingsReader {

    public static Mappings MAPPINGS;
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
    private static void main(String[] args) throws IOException {
        File dir = new File("mappings");
        dir.mkdirs();
        // File f = exportResource("spigot2intermediary.csrg", dir);
        File f = exportResource("spigot2srg.srg", dir);
        MAPPINGS = MappingsFormat.COMPACT_SEARGE_FORMAT.parseFile(f);
        METHODS = new HashMap<>();
        METHODS2 = new HashMap<>();
        METHODS3 = new HashMap<>();
        LOGGER.info("Reflection working: " + dev(MAPPINGS.getNewClass("net.minecraft.server.MinecraftKey").getName()).equalsIgnoreCase(dev("net.minecraft.class_2960")));

        MAPPINGS.forEachMethod((spigot, intermed) -> {
            String sN = spigot.getName();
            String iN = intermed.getName();
            String clazz = intermed.getDeclaringType().getName();

            METHODS.put(clazz + "=" + sN, iN);
            boolean put = true;
            if (METHODS2.containsKey(sN + intermed.getSignature().getDescriptor())) {
                METHODS2.remove(sN + intermed.getSignature().getDescriptor());
                put = false;
            }
            if (sN.length() > 0 && iN.length() > 2 && put) METHODS2.put(sN + intermed.getSignature().getDescriptor(), iN);

            boolean put3 = true;
            String sig = intermed.getSignature().toString();
            String key = (clazz + "=" + sN + sig.substring(0, sig.indexOf(")")+1));
            if (METHODS3.containsKey(key)) {
                METHODS3.remove(key);
                put3 = false;
            }
            if (sN.length() > 0 && iN.length() > 2 && put3) METHODS3.put(key, iN);
        });
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
    public static String getIntermedClass_old(String spigot) {
        return dev(MAPPINGS.getNewClass(spigot).getName());
    }

    @Deprecated
    public static String getIntermedField_old(String c, String spigot) throws NoSuchFieldException, SecurityException, ClassNotFoundException {
        JavaType type = JavaType.fromName(getIntermedClass(c));
        if (c.contains("class_")) type = MAPPINGS.inverted().getNewClass(c);
        return obf(MAPPINGS.getNewField(FieldData.create(type, spigot)).getName());
    }

    @Deprecated
    public static String getIntermedField2_old(String c, String spigot) throws NoSuchFieldException, SecurityException, ClassNotFoundException {
        JavaType type = JavaType.fromName(getIntermedClass(c));
        return obf(MAPPINGS.getNewField(FieldData.create(type, spigot)).getName());
    }

    @Deprecated
    public static File exportResource(String res, File folder) {
        try (InputStream stream = MappingsReader.class.getClassLoader().getResourceAsStream("mappings/" + res)) {
            if (stream == null) throw new IOException("Null " + res);

            Path p = Paths.get(folder.getAbsolutePath() + File.separator + res);
            Files.copy(stream, p, StandardCopyOption.REPLACE_EXISTING);
            return p.toFile();
        } catch (IOException e) { e.printStackTrace(); return null;}
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
        if (METHODS.containsKey((name + "=" + spigot)))
            return METHODS.getOrDefault(name + "=" + spigot, obf(spigot));
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
    
	/**
	 * "C:\Program Files\Eclipse Adoptium\jdk-21.0.3+9\bin\java.exe" -jar tiny-remapper-0.9.0-fat.jar paper-1.20.6.jar paper-1.20.6-reobf.jar reobf.tiny yarn+mojang spigot
"C:\Program Files\Eclipse Adoptium\jdk-21.0.3+9\bin\java.exe" -jar SpecialSource-1.11.5-SNAPSHOT-shaded.jar -i paper-1.20.6-reobf.jar -o paper-1.20.6-bukkit.jar -m bukkit.csrg -r
"C:\Program Files\Eclipse Adoptium\jdk-21.0.3+9\bin\java.exe" -jar tiny-remapper-0.9.0-fat.jar paper-1.20.6-bukkit.jar paper-1.20.6-intermed.jar 1.20.6.tiny official intermediary --ignoreConflicts
"C:\Program Files\Eclipse Adoptium\jdk-21.0.3+9\bin\java.exe" -jar tiny-remapper-0.9.0-fat.jar paper-1.20.6-intermed.jar paper-1.20.6-yarn.jar mappings.tiny intermediary named --ignoreConflicts


	reobf.tiny, bukkit.csrg, 1.20.6.tiny

	 */

}
package org.cardboardpowered.library;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarFile;

import org.cardboardpowered.CardboardConfig;

import net.fabricmc.loader.api.FabricLoader;

public class KnotHelper {

    /**
     * Add file to Knot
     */
    public static boolean propose(File file) {
        try {
        	if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {
            	net.fabricmc.loader.impl.launch.FabricLauncherBase.getLauncher().addToClassPath(file.toPath(), readPackagesFromJar(file));
            }

            if (CardboardConfig.DEBUG_VERBOSE_CALLS) {
            	LibraryManager.logger.info("Debug: Loading library " + file.getName());
            }
            return true;
        } catch (Exception e) {
            LibraryManager.logger.error("ERR: \"" + e.getMessage() + "\" while accessing Fabric Loader.");
            return false;
        }
    }

    private static String[] readPackagesFromJar(File jarFile) throws IOException {
        Set<String> packages = new HashSet<>();

        try (JarFile jar = new JarFile(jarFile)) {
            jar.stream().filter(e -> !e.isDirectory()).filter(e -> e.getName().endsWith(".class"))
               .forEach(e -> {
                   String name = e.getName();
                   int slash = name.lastIndexOf('/');
                   if (slash > 0) { packages.add(name.substring(0, slash).replace('/', '.')); }
               });
        }
        Collections.addAll(packages, "org.bukkit.", "me.isaiah.", "org.cardboardpowered.", "com.", "net.", "org.", "me.");
        return packages.toArray(String[]::new);
    }

}
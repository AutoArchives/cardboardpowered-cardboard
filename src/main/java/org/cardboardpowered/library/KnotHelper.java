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

    public static int loaded = 0;
    public static int loaded_adventure = 0;

	public static String paper_version = "";
	public static String vers = "";

    /**
     * Add file to Knot
     */
    public static void propose(File file) {
        try {
            if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {
            	net.fabricmc.loader.impl.launch.FabricLauncherBase.getLauncher().addToClassPath(file.toPath(), readPackagesFromJar(file));
            }

            if (CardboardConfig.DEBUG_VERBOSE_CALLS) {
            	LibraryManager.logger.info("Debug: Loading library " + file.getName());
            	loaded += 1;
            	return;
            }
            String nam = file.getName().split(".jar")[0];

			if (nam.contains("paper-api-")) {
				String[] parts = nam.split("-");
				paper_version =  parts[2] + "-" + parts[parts.length - 1];
            }

            if (nam.contains("bungeecord-chat-") || nam.contains("adventure-api-")) { vers += nam + "; "; }
            if (nam.contains("adventure-")) { loaded_adventure += 1; }

            loaded += 1;
        } catch (Exception e) {
            e.printStackTrace();
            LibraryManager.logger.info("ERROR: Got " + e.getClass().getSimpleName() + " while accessing Fabric Loader.");
        }
    }

    public static String[] readPackagesFromJar(File jarFile) throws IOException {
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
package org.cardboardpowered.mixin;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/*
import org.cardboardpowered.CardboardConfig;
import org.cardboardpowered.library.Libraries;
import org.cardboardpowered.library.Library;
import org.cardboardpowered.library.LibraryManager;
import org.cardboardpowered.util.JarReader;
*/
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import net.fabricmc.loader.api.FabricLoader;

public class CardboardMixinPlugin implements IMixinConfigPlugin {

    private static final String MIXIN_PACKAGE_ROOT = "org.cardboardpowered.mixin.";
    private final Logger logger = LogManager.getLogger("Cardboard");
    public static boolean libload = true;
    private static boolean read_plugins = false;

    @Override
    public void onLoad(String mixinPackage) {
        /*
    	try {
            CardboardConfig.setup();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        File pl = new File("plugins");
        if (!pl.exists()) {
        	pl.mkdirs();
        }

        logger.info("Loading Libraries...");
        Libraries.loadLibs();
        JarReader.readEvents();
        if (pl.exists()) {
        	try {
                JarReader.readPlugins(pl);
                read_plugins = true;
            } catch (Exception e) {
                read_plugins = false;
                e.printStackTrace();
            }
        }*/
    }
    
    /*
    @Deprecated
    public static void loadLibs() {
    	Libraries.loadLibs();
    }
    
    @Deprecated
    public static List<Library> getLibs1() {
        return Libraries.getLibraries();
    }
    */

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }
    
    /*
    public static URLClassLoader getClassLoader() throws MalformedURLException {
    	File papi = LibraryManager.INSTANCE.getJarFile("paper-api");
    	if (null == papi) {
    		return null;
    	}
        URL[] jar = {
                FabricLoader.getInstance().getModContainer("cardboard").get().getRootPath().toUri().toURL(),
                FabricLoader.getInstance().getModContainer("minecraft").get().getRootPath().toUri().toURL(),
                FabricLoader.getInstance().getModContainer("fabricloader").get().getRootPath().toUri().toURL(),
                papi.toURI().toURL()
        };
        return new URLClassLoader(jar);
    }
    
    public static boolean isEventFound(String event) {
        return read_plugins ? JarReader.found.contains(event) : true;
    }

    public boolean doesNotHaveEvent(String mix, String mixin, String event) {
        if (mix.contains(mixin)) {
            boolean dev = FabricLoader.getInstance().isDevelopmentEnvironment();
            boolean found = isEventFound(event);
            if (dev && !found) {logger.info("DEBUG: Status of " + mixin + ": " + found + ". (" + event + ")");}
            return !found;
        }
        return false;
    }

    /**
     * Check for mods that overwrite onGameMessage for chat event.
     *
    public boolean should_force_alternate_chat() {
        FabricLoader loader = FabricLoader.getInstance();
        String[] bad_mods = {"architectury", "dynmap"};

        for (String s : bad_mods) {
            if (loader.getModContainer(s).isPresent())
                return true;
        }
        return false;
    }*/

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String target, ClassNode targetClass, String mixinClass, IMixinInfo info) {
    }

    @Override
    public void postApply(String targetClass, ClassNode target, String mixinClass, IMixinInfo info) {
    }

}
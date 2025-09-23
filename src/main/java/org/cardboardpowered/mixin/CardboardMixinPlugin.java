package org.cardboardpowered.mixin;

import static org.cardboardpowered.library.LibraryManager.HashAlgorithm.SHA1;
import static org.cardboardpowered.library.LibraryManager.HashAlgorithm.MD5;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cardboardpowered.CardboardConfig;
import org.cardboardpowered.library.Library;
import org.cardboardpowered.library.LibraryManager;
import org.cardboardpowered.util.GameVersion;
import org.cardboardpowered.util.JarReader;
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
    	
    	// testing
        /*if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
        	System.out.println("HELLO");
        	MappTest.do_test(null);
        	System.exit(0);
        	return;
        }*/
    	
        try {
            CardboardConfig.setup();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        File pl = new File("plugins");
        if (pl.exists()) {
            try {
                JarReader.read_plugins(pl);
                read_plugins = true;
            } catch (Exception e) {
                read_plugins = false;
                e.printStackTrace();
            }
        }

        logger.info("Loading Libraries...");
        loadLibs();
    }
    
    public static void loadLibs() {
    	List<Library> libraries = getLibs();
    	
    	String repository = "https://repo1.maven.org/maven2/"; 
    	new LibraryManager(repository, "lib", true, 2, libraries).run();
    }

    public static List<Library> getLibs() {
        String mcver = GameVersion.create().getReleaseTarget();

        List<Library> libraries = new ArrayList<>();

        // Paper API

        // libraries.add( new Library("io.papermc", "paper-api", "1.21.4-R0.1-20250118.134032-116", SHA1, "6a1c95efe04acca299d975021cfc9f33ca28d4c0", "paper"));
        // libraries.add( new Library("io.papermc", "paper-api", "1.21.4-R0.1-20250303.170158-184", SHA1, "c4f8188cbd61e52623069cf19054c43086fcbef7", "paper"));

        // libraries.add( new Library("io.papermc", "paper-api", "1.21.4-R0.1-20250313.115134-207", SHA1, "8531d7de9321971d382e2dd1a3e4555e006a7326", "paper"));
        // libraries.add( new Library("io.papermc", "paper-api", "1.21.4-R0.1-20250511.205801-225", SHA1, "664d0e9471c4a71af483734af88d6e57bbbd226c", "paper"));
        
        // libraries.add( new Library("io.papermc", "paper-api", "1.21.6-R0.1-20250630.094942-50", SHA1, "72c1661b05fb67a0e1e31c95b67aef24986ad1c8", "paper"));
        libraries.add( new Library("io.papermc", "paper-api", "1.21.8-R0.1-20250906.215025-55", SHA1, "1afdc21a255229fa86b344c8c324fd8d57c8eb3e", "paper"));

        // Paper API Libraries
        libraries.add( new Library("org.xerial", "sqlite-jdbc", "3.41.0.0", MD5, "0d63ee5b583e9a75ea1717ffce63fed8", null));
        libraries.add( new Library("com.mysql", "mysql-connector-j", "8.0.32", MD5, "25bf3b3cd262065283962078dc82e99c", null));
		libraries.add( new Library("commons-lang", "commons-lang", "2.6", SHA1, "0ce1edb914c94ebc388f086c6827e8bdeec71ac2", null) );
        libraries.add( new Library("org.apache.commons", "commons-collections4", "4.4", SHA1, "62ebe7544cb7164d87e0637a2a6a2bdc981395e8", null) );
        libraries.add( new Library("commons-collections", "commons-collections", "3.2.1", SHA1, "761ea405b9b37ced573d2df0d1e3a4e0f9edc668", null) );


        // net/md-5/bungeecord-chat/1.16-R0.4/
        
        libraries.add( new Library("net.md-5", "bungeecord-chat", "1.16-R0.4", SHA1, "e043e8eed8fdb5c157090a84ac8fd64a6a8d0d88", null) );
        
        // TODO: Check Adventure version
        // String adventureVersion = "4.9.3";
        // String adventureVersion = "4.13.1";
        // String adventureVersion = "4.16.0";
        // String adventureVersion = "4.18.0";
        
        String adventureVersion = "4.23.0";

        libraries.add( new Library("net.kyori", "adventure-api", adventureVersion, SHA1, "f6b5b75465f7112dbaf38dd8cfdc5f6b906c53a0", null) );
        libraries.add( new Library("net.kyori", "adventure-key", adventureVersion, SHA1, "0be07b3e282f9dbc1c0b24a65f0d442ebc504d4d", null) );
        libraries.add( new Library("net.kyori", "adventure-text-serializer-gson", adventureVersion, SHA1, "bf81a8f6f9509e2c826fac29a0e580e8c377ba37", null) );
        libraries.add( new Library("net.kyori", "adventure-text-serializer-legacy", adventureVersion, SHA1, "e9cacd4d98c1ecab249805e8058cf2eece047d5b", null) );
        libraries.add( new Library("net.kyori", "adventure-text-serializer-plain", adventureVersion, SHA1, "2331599c8e892fe3996b6a25f85477152a1b2b3d", null) );
        libraries.add( new Library("net.kyori", "adventure-text-minimessage", adventureVersion, SHA1, "03230c09453a50090ae354bfc0d8dd6b72026d88", null) );

         
        // libraries.add( new Library("net.kyori", "adventure-api", adventureVersion, SHA1, "cb966704b813d30d4ee9f0b97167b4063a249c34", null) );
        // libraries.add( new Library("net.kyori", "adventure-key", adventureVersion, SHA1, "b695c40a7d2fd658246de78ea428e8f8dc7ffd2d", null) );
        // libraries.add( new Library("net.kyori", "adventure-text-serializer-gson", adventureVersion, SHA1, "5650ed18040e070aa05855ebcb890e6e1e36ee0e", null) );
        // libraries.add( new Library("net.kyori", "adventure-text-serializer-legacy", adventureVersion, SHA1, "21b9450c659146ea4ac6ef3555b5ea1008566b69", null) );
        // libraries.add( new Library("net.kyori", "adventure-text-serializer-plain", adventureVersion, SHA1, "4d5e8b73aac1e5e17dd24a75edd8fd077098e18c", null) );
        
        // libraries.add( new Library("net.kyori", "adventure-api", adventureVersion, SHA1, "b0054b3a4d144f09962fe72abc746191e7f931a2", null) );
        // libraries.add( new Library("net.kyori", "adventure-text-serializer-gson", adventureVersion, SHA1, "141df0329e00b791bcc8b2921cd715ac7d506bbe", null) );
        // libraries.add( new Library("net.kyori", "adventure-text-serializer-legacy", adventureVersion, SHA1, "9e7811601e508e0af6d53e35eab3588ee607bff6", null) );
        // libraries.add( new Library("net.kyori", "adventure-text-serializer-plain", adventureVersion, SHA1, "61d39db7e84c11d91be551cb9bc50c1aa7e64983", null) );

        if (mcver.contains("1.17")) {
    		//libraries.add( new Library("org.xerial", "sqlite-jdbc", "3.21.0.1", SHA1, "81a0bcda2f100dc91dc402554f60ed2f696cded5", null) );
            //libraries.add( new Library("mysql", "mysql-connector-java", "5.1.46", SHA1, "9a3e63b387e376364211e96827bc27db8d7a92e9", null) );
        	//libraries.add( new Library("org.cardboardpowered", "intermediary-adapter", "7.3", SHA1, "", null) );
            libraries.add( new Library("org.jline", "jline", "3.19.0", SHA1, "27edf6497c4fac20b63ca4cd8788581ca86cb83e", null) );
        }
        
        // Set WorldEdit adapter class name here
        // as this provides more verbose stacktraces.
        System.setProperty("worldedit.bukkit.adapter", "com.sk89q.worldedit.bukkit.adapter.impl.v1_21_4.PaperweightAdapter");

        return libraries;
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    public static boolean is_event_found(String event) {
        if (!read_plugins) {
            return true;
        }
        return JarReader.found.contains(event);
    }
    
    // private static final TestCl dummy = new TestCl();

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String mixin = mixinClassName.substring(MIXIN_PACKAGE_ROOT.length());
        if (CardboardConfig.disabledMixins.contains(mixinClassName)) {
            logger.info("Disabling mixin '" + mixin + "', was forced disabled in config.");
            return false;
        }

        if (mixin.equals("item.MixinChorusFruitItem")) {
            FabricLoader loader = FabricLoader.getInstance();
            boolean create_mod = loader.isModLoaded("porting_lib");
            if (create_mod) {
                return false;
            }
        }

        if (mixin.equals("network.MixinServerPlayNetworkHandler_ChatEvent") && 
                should_force_alternate_chat()) {
            logger.info("Architectury Mod detected! Disabling async chat from NetworkHandler.");
            return false;
        }

        /*if (mixin.equals("network.MixinPlayerManager_ChatEvent")) {
            if (should_force_alternate_chat()) {
                logger.info("Architectury Mod detected! Using alternative async chat from PlayerManager");
                return true;
            } else return false;
        }
        if (CardboardConfig.ALT_CHAT && (mixin.contains("_ChatEvent"))) {
            logger.info("Alternative ChatEvent Mixin enabled in config. Changing status on: " + mixin);
            if (mixin.equals("network.MixinServerPlayNetworkHandler_ChatEvent")) return false;
            if (mixin.equals("network.MixinPlayerManager_ChatEvent")) return true;
        }*/
        
        String mcver = GameVersion.create().getReleaseTarget();
        if (mcver.contains("1.18") && mixin.endsWith("_1_19")) {
        	return false;
        }
        if (mcver.contains("1.19") && mixin.endsWith("_1_18")) {
        	return false;
        }
        if (mcver.contains("1.20") && mixin.endsWith("_1_18")) {
        	return false;
        }


        // Disable mixin if event is not found in plugins.
        /*if (not_has_event(mixin, "LeashKnotEntity", "PlayerLeashEntityEvent") && not_has_event(mixin, "LeashKnotEntity", "PlayerUnleashEntityEvent")) return false;
        if (not_has_event(mixin, "GoToWorkTask", "VillagerCareerChangeEvent")) return false;
        if (not_has_event(mixin, "LoseJobOnSiteLossTask", "VillagerCareerChangeEvent")) return false;
        if (not_has_event(mixin, "PiglinBrain", "EntityPickupItemEvent")) return false;
        if (not_has_event(mixin, "DyeItem", "SheepDyeWoolEvent")) return false;
        if (not_has_event(mixin, "FrostWalkerEnchantment", "BlockFormEvent")) return false;
        if (not_has_event(mixin, "ExperienceOrbEntity", "PlayerItemMendEvent") || not_has_event(mixin, "ExperienceOrbEntity", "PlayerExpChangeEvent")) return false;
        if (not_has_event(mixin, "Explosion", "EntityExplodeEvent") && not_has_event(mixin, "Explosion", "BlockExplodeEvent")) return false;
        if (not_has_event(mixin, "LeavesBlock", "LeavesDecayEvent")) return false;
        if (not_has_event(mixin, "PlayerAdvancementTracker", "PlayerAdvancementDoneEvent")) return false;
*/
        if (mixinClassName.contains("ServerPlayNetworkHandler")) return true;

        try {
            URL[] jar = {
                    FabricLoader.getInstance().getModContainer("cardboard").get().getRootPath().toUri().toURL(),
                    FabricLoader.getInstance().getModContainer("minecraft").get().getRootPath().toUri().toURL(),
                    FabricLoader.getInstance().getModContainer("fabricloader").get().getRootPath().toUri().toURL(),
                    new File(new File("lib"), "paper-api-1.17-dev.jar").toURI().toURL()
            };

            Class<?> c = Class.forName(mixinClassName, false, new URLClassLoader(jar));

            for (Annotation a : c.getAnnotations()) {
                String e = a.toString().split("events=")[1].substring(1);
                e = e.substring(0, e.lastIndexOf("}")).replace("\"", "");
                String[] events = e.split(", ");
                if (events.length > 0) {
                    if (events[0].length() < 4) {
                        return true; // No events
                    }
                    
                    boolean disable = true;
                    for (String ev : events) {
                        if (!not_has_event(mixin, mixin, ev))
                            disable = false;
                    }
                    //if (disable)
                    //    return false;
                }
            }
        
        } catch (Exception e) {
            logger.info(e.getMessage());
        }

        return true;
    }

    public boolean not_has_event(String mix, String mixin, String event) {
        if (mix.contains(mixin)) {
            boolean dev = FabricLoader.getInstance().isDevelopmentEnvironment();
            if (is_event_found(event)) {
                if (dev) {logger.info("DEBUG: Status of " + mixin + ": true. (" + event + ")");}
                return false;
            } else {
                if (dev) {logger.info("DEBUG: Status of " + mixin + ": false. (" + event + ")");}
                return true;
            }
        }
        return false;
    }

    /**
     * Check for mods that overwrite onGameMessage for chat event.
     */
    public boolean should_force_alternate_chat() {
        FabricLoader loader = FabricLoader.getInstance();
        String[] bad_mods = {"architectury", "dynmap"};

        for (String s : bad_mods) {
            if (loader.getModContainer(s).isPresent())
                return true;
        }
        return false;
    }

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
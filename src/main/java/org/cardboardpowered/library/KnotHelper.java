package org.cardboardpowered.library;

import java.io.File;
import java.net.MalformedURLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cardboardpowered.CardboardConfig;

import net.fabricmc.loader.api.FabricLoader;

public class KnotHelper {

    public static boolean PAPER_API_LOADED = false;
    public static final Logger LOGGER = LogManager.getLogger("KnotHelper");
    public static int loaded = 0;
    
    public static int loaded_adventure = 0;
    public static String ver_adventure = "";
	
	public static String ver_paper = "";
	public static String ver_bungeechat = "";

    /**
     * Modern Loader 0.17 / Quilt Support
     */
    public static void modern_load(File file) {
        try {
            if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {
            	net.fabricmc.loader.impl.launch.FabricLauncherBase.getLauncher().addToClassPath(file.toPath(), getPackages());
            }

            if (CardboardConfig.DEBUG_VERBOSE_CALLS) {
            	LOGGER.info("Debug: Loading library " + file.getName());
            	loaded += 1;
            	return;
            }
            String nam = file.getName();

			if (nam.contains("paper-api-")) {
				String[] parts = nam.replace(".jar", "").split("-");
				String versionNumber = parts[2];
				String finalNumber = parts[parts.length - 1];
				ver_paper = versionNumber + "-" + finalNumber;
            }

            if (nam.contains("bungeecord-chat-")) {
				ver_bungeechat = nam.split("bungeecord-chat-")[1].split(".jar")[0];
            }

            if (nam.contains("adventure-")) {
            	loaded_adventure += 1;
            }

            if (nam.contains("adventure-api-")) {
				ver_adventure = nam.split("adventure-api-")[1].split(".jar")[0];
            }
            loaded += 1;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("ERROR: Got " + e.getClass().getSimpleName() + " while accessing Fabric Loader.");
        }
    }
    
    public static void fabric_modern_load(File file) {
    	modern_load(file);
    }

    public static void propose(File file) throws MalformedURLException {
    	fabric_modern_load(file);

        if (file.getName().contains("paper")) {
            PAPER_API_LOADED = true;
        }
    }

    /**
     * Allowed packages.
     * Prevents loading class errors.
     */
    private static String[] getPackages() {
        String[] args = 
        {"co.aikar.timings",
            "co.aikar.util",
            "com.destroystokyo.paper",
            "com.destroystokyo.paper.block",
            "com.destroystokyo.paper.entity",
            "com.destroystokyo.paper.entity.ai",
            "com.destroystokyo.paper.entity.villager",
            "com.destroystokyo.paper.event.block",
            "com.destroystokyo.paper.event.entity",
            "com.destroystokyo.paper.event.executor",
            "com.destroystokyo.paper.event.executor.asm",
            "com.destroystokyo.paper.event.inventory",
            "com.destroystokyo.paper.event.player",
            "com.destroystokyo.paper.event.profile",
            "com.destroystokyo.paper.event.server",
            "com.destroystokyo.paper.exception",
            "com.destroystokyo.paper.inventory",
            "com.destroystokyo.paper.inventory.meta",
            "com.destroystokyo.paper.loottable",
            "com.destroystokyo.paper.network",
            "com.destroystokyo.paper.profile",
            "com.destroystokyo.paper.util",
            "com.destroystokyo.paper.utils",
            "io.papermc.paper.advancement",
            "io.papermc.paper.chat",
            "io.papermc.paper.command",
            "io.papermc.paper.datapack",
            "io.papermc.paper.enchantments",
            "io.papermc.paper.event.block",
            "io.papermc.paper.event.entity",
            "io.papermc.paper.event.packet",
            "io.papermc.paper.event.player",
            "io.papermc.paper.event.server",
            "io.papermc.paper.event.world",
            "io.papermc.paper.event.world.border",
            "io.papermc.paper.inventory",
            "io.papermc.paper.tag",
            "io.papermc.paper.text",
            "io.papermc.paper.util",
            "io.papermc.paper.world",
            "io.papermc.paper.world.generation",
            "io.papermc.paper.entity",
            "io.papermc.paper",
            "org.bukkit",
            "org.bukkit.advancement",
            "org.bukkit.attribute",
            "org.bukkit.block",
            "org.bukkit.block.banner",
            "org.bukkit.block.data",
            "org.bukkit.block.data.type",
            "org.bukkit.block.structure",
            "org.bukkit.boss",
            "org.bukkit.command",
            "org.bukkit.command.defaults",
            "org.bukkit.configuration",
            "org.bukkit.configuration.file",
            "org.bukkit.configuration.serialization",
            "org.bukkit.conversations",
            "org.bukkit.enchantments",
            "org.bukkit.entity",
            "org.bukkit.entity.memory",
            "org.bukkit.entity.minecart",
            "org.bukkit.event",
            "org.bukkit.event.block",
            "org.bukkit.event.command",
            "org.bukkit.event.enchantment",
            "org.bukkit.event.entity",
            "org.bukkit.event.hanging",
            "org.bukkit.event.inventory",
            "org.bukkit.event.player",
            "org.bukkit.event.raid",
            "org.bukkit.event.server",
            "org.bukkit.event.vehicle",
            "org.bukkit.event.weather",
            "org.bukkit.event.world",
            "org.bukkit.generator",
            "org.bukkit.help",
            "org.bukkit.inventory",
            "org.bukkit.inventory.meta",
            "org.bukkit.inventory.meta.tags",
            "org.bukkit.loot",
            "org.bukkit.map",
            "org.bukkit.material",
            "org.bukkit.material.types",
            "org.bukkit.metadata",
            "org.bukkit.permissions",
            "org.bukkit.persistence",
            "org.bukkit.plugin",
            "org.bukkit.plugin.java",
            "org.bukkit.plugin.messaging",
            "org.bukkit.potion",
            "org.bukkit.projectiles",
            "org.bukkit.scheduler",
            "org.bukkit.scoreboard",
            "org.bukkit.structure",
            "org.bukkit.util",
            "org.bukkit.util.io",
            "org.bukkit.util.noise",
            "org.bukkit.util.permissions",
            "org.spigotmc",
            "org.spigotmc.event.entity",
            "org.spigotmc.event.player",
            "net.md_5",
            "net.kyori",
            "org.bukkit",
            "me.isaiah",
            "org.cardboardpowered",
            "com.",
            "net.",
            "org.",
            "me."
        };
        return args;
    }


}
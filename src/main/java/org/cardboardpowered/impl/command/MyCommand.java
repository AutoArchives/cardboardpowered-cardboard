package org.cardboardpowered.impl.command;

import com.google.common.collect.ImmutableList;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.cardboardpowered.CardboardConfig;

import java.util.Arrays;
import java.util.List;

/**
 * Provides a /fabricmods command
 */
public class MyCommand extends Command {

    public MyCommand() {
        super("cardboardtest");

        this.description = "Testing";
        this.usageMessage = "/cardboardtest";
        
        List<String> aka = Arrays.asList("cardboarddebug", "cardboardebug");
        
        this.setAliases(aka);
        this.setPermission("cardboard.command.admin");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
    	if (!sender.hasPermission("cardboard.command.admin")) {
    		return false;
    	}
    	
    	if (args.length == 0) {
    		sender.sendMessage("Usage: /cardboardtest <arg>: arg = debugverbose; worlds");
    		return true;
    	}
    	
    	if (args[0].contains("debugverbose")) {
    		CardboardConfig.DEBUG_VERBOSE_CALLS = !CardboardConfig.DEBUG_VERBOSE_CALLS;
            sender.sendMessage("DEBUG_VERBOSE_CALLS: " +CardboardConfig.DEBUG_VERBOSE_CALLS);
    	}
    	
    	if (args[0].equalsIgnoreCase("worlds")) {
    		List<World> worlds = Bukkit.getWorlds();
    		sender.sendMessage("Testing output of \"Bukkit.getWorlds()\":");
    		for (World w : worlds) {
    			sender.sendMessage("- WORLD: " + w.getName() + " with player count: " + w.getPlayerCount());
    		}
    	}
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
    	
    	if (args.length == 0) {
    		return ImmutableList.of("debugverbose", "worlds");
    	}
    	
        return ImmutableList.of();
    }

}

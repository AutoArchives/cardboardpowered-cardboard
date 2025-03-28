package org.cardboardpowered.impl.command;

import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.cardboardpowered.CardboardConfig;

import java.util.ArrayList;
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
        if (sender.hasPermission("cardboard.command.admin")) {
            CardboardConfig.DEBUG_VERBOSE_CALLS = !CardboardConfig.DEBUG_VERBOSE_CALLS;
            sender.sendMessage("DEBUG_VERBOSE_CALLS: " +CardboardConfig.DEBUG_VERBOSE_CALLS);
        } else {
            sender.sendMessage("No Permission for command.");
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return ImmutableList.of();
    }

}

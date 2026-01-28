package org.cardboardpowered.mixin;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.command.CommandSender;
import org.cardboardpowered.interfaces.IServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.cardboardpowered.interfaces.IMixinCommandOutput;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CommandSourceStack.class)
public class MixinServerCommandSource implements IServerCommandSource {
    @Shadow
    public CommandSource source;

    // CraftBukkit start
    public org.bukkit.command.CommandSender getBukkitSender() {
        return ((IMixinCommandOutput)this.source).getBukkitSender((CommandSourceStack)(Object)this);
    }
    // CraftBukkit end
}
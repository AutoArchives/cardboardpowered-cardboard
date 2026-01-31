package org.cardboardpowered.mixin;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import org.cardboardpowered.bridge.commands.CommandSourceStackBridge;
import org.spongepowered.asm.mixin.Mixin;
import org.cardboardpowered.bridge.commands.CommandSourceBridge;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CommandSourceStack.class)
public class MixinServerCommandSource implements CommandSourceStackBridge {
    @Shadow
    public CommandSource source;

    // CraftBukkit start
    public org.bukkit.command.CommandSender getBukkitSender() {
        return ((CommandSourceBridge)this.source).getBukkitSender((CommandSourceStack)(Object)this);
    }
    // CraftBukkit end
}
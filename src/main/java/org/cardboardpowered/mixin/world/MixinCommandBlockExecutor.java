package org.cardboardpowered.mixin.world;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.level.BaseCommandBlock;
import org.bukkit.command.CommandSender;
import org.spongepowered.asm.mixin.Mixin;

import org.cardboardpowered.interfaces.IMixinCommandOutput;

@Mixin(BaseCommandBlock.class)
public abstract class MixinCommandBlockExecutor implements IMixinCommandOutput {

    @Override
    public abstract CommandSender getBukkitSender(CommandSourceStack wrapper);

}
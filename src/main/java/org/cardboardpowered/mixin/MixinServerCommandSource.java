package org.cardboardpowered.mixin;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.command.CommandSender;
import org.cardboardpowered.interfaces.IServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.cardboardpowered.interfaces.IMixinCommandOutput;

@Mixin(CommandSourceStack.class)
public class MixinServerCommandSource implements IServerCommandSource {

    @Override
    public CommandSender getBukkitSender() {
        CommandSourceStack s = (CommandSourceStack) (Object) this;

        // 1.21.4: update has moved CommandOutputs away from Player itself,
    	// we continue to implement getBukkitSender for Player so lets
    	// cast from Player here to avoid ud not yet having getBukkitSender
    	// in net.minecraft.class_3222$3 / Player.commandOutput.
        if (s.isPlayer()) {
        	ServerPlayer player = s.getPlayer();

        	if (player instanceof IMixinCommandOutput) {
        		( (IMixinCommandOutput) player ).getBukkitSender(s);
        	}
        }

        // see above message
        if (null != s.entity && s.entity instanceof IMixinCommandOutput) {
        	( (IMixinCommandOutput) s.entity ).getBukkitSender(s);
        }

        return ((IMixinCommandOutput)s.source).getBukkitSender(s);
    }

}
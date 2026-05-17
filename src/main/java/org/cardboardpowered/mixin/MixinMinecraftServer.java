package org.cardboardpowered.mixin;

import org.cardboardpowered.bridge.IMinecraftServer;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.server.MinecraftServer;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer implements IMinecraftServer {

}

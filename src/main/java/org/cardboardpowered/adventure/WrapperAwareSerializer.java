package org.cardboardpowered.adventure;

import java.util.function.Supplier;

import org.bukkit.craftbukkit.CraftServer;

import com.google.common.base.Suppliers;

import me.isaiah.common.ICommonMod;
import me.isaiah.common.cmixin.IMixinMinecraftServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.minecraft.registry.RegistryOps;
import net.minecraft.text.Text;

public class WrapperAwareSerializer implements ComponentSerializer<Component, Component, Text> {

	private final Supplier<RegistryOps<Object>> javaOps;
	
    public WrapperAwareSerializer(Supplier<RegistryOps<Object>> javaOps) {
    	this.javaOps = Suppliers.memoize(javaOps::get);
    }
	
    @Override
    public Component deserialize(final Text input) {
        if (input instanceof CardboardAdventureComponent) {
            return ((CardboardAdventureComponent) input).adventure;
        }

        return CardboardAdventure.GSON.serializer().fromJson(Text.Serialization.toJsonString(input, CraftServer.server.getRegistryManager()), Component.class);
    }
    
    /*public Component deserialize_1(Text input) throws Throwable {
        if (input instanceof CardboardAdventureComponent) {
            return ((CardboardAdventureComponent)input).adventure;
        }
        RegistryOps ops = RegistryOps.of(JavaOps.INSTANCE, CraftRegistry.getMinecraftRegistry());
        Object obj = TextCodecs.CODEC.encodeStart(ops, input).getOrThrow(s -> new RuntimeException("Failed to encode Minecraft Component: " + String.valueOf(input) + "; " + s));
        Pair converted = (Pair)AdventureCodecs.COMPONENT_CODEC.decode(ops, obj).getOrThrow(s -> new RuntimeException("Failed to decode to adventure Component: " + String.valueOf(obj) + "; " + s));
        return (Component)converted.getFirst();
    }*/

    @Override
    public Text serialize(final Component component) {
    	
    	IMixinMinecraftServer mc = (IMixinMinecraftServer) ICommonMod.getIServer().getMinecraft();
    	
        return mc.IC$from_json(CardboardAdventure.GSON.serializer().toJson(component));
    }

}

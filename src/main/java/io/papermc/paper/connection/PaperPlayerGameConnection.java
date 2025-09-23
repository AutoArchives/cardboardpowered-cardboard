package io.papermc.paper.connection;

// import io.papermc.paper.connection.HorriblePlayerLoginEventHack;
import io.papermc.paper.connection.PaperCommonConnection;
import io.papermc.paper.connection.PlayerGameConnection;
import java.util.Set;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.cardboardpowered.interfaces.IMixinServerEntityPlayer;

public class PaperPlayerGameConnection extends PaperCommonConnection<ServerPlayNetworkHandler> implements PlayerGameConnection {

    public PaperPlayerGameConnection(ServerPlayNetworkHandler serverConfigurationPacketListenerImpl) {
        super(serverConfigurationPacketListenerImpl);
    }

    @Override
    public SyncedClientOptions getClientInformation() {
        return ((ServerPlayNetworkHandler)this.handle).player.getClientOptions();
    }
    
    @Override
    public void reenterConfiguration() {
    	System.out.println("WARNING: Attempted to use PlayerGameConnection#reenterConfiguration()");
    }

    /*
    public void reenterConfiguration() {
        if (((ServerPlayNetworkHandler)this.handle).connection.savedPlayerForLoginEventLegacy != null) {
            HorriblePlayerLoginEventHack.warnReenterConfiguration();
            return;
        }
        ((ServerPlayNetworkHandler)this.handle).reconfigure();
    }
    */

    public Player getPlayer() {
        return (Player) ((IMixinServerEntityPlayer) ((ServerPlayNetworkHandler)this.handle).getPlayer() ).getBukkitEntity();
    }

    public void sendPluginMessage(Plugin source, String channel, byte[] message) {
        this.getPlayer().sendPluginMessage(source, channel, message);
    }

    public Set<String> getListeningPluginChannels() {
        return this.getPlayer().getListeningPluginChannels();
    }

}

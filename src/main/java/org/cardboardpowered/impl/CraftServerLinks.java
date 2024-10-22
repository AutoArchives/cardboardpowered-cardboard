package org.cardboardpowered.impl;

import com.google.common.base.Preconditions;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import org.bukkit.ServerLinks;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.cardboardpowered.adventure.CardboardAdventure;

public class CraftServerLinks implements ServerLinks {

    private final MinecraftDedicatedServer server;
    private net.minecraft.server.ServerLinks serverLinks;

    public CraftServerLinks(MinecraftDedicatedServer server) {
        this(server, null);
    }

    public CraftServerLinks(net.minecraft.server.ServerLinks serverLinks) {
        this(null, serverLinks);
    }

    private CraftServerLinks(MinecraftDedicatedServer server, net.minecraft.server.ServerLinks serverLinks) {
        this.server = server;
        this.serverLinks = serverLinks;
    }

    public ServerLinks.ServerLink getLink(ServerLinks.Type type) {
        Preconditions.checkArgument((type != null ? 1 : 0) != 0, (Object)"type cannot be null");
        return this.getServerLinks().getEntryFor(CraftServerLinks.fromBukkit(type)).map(CraftServerLink::new).orElse(null);
    }

    public List<ServerLinks.ServerLink> getLinks() {
        return this.getServerLinks().entries().stream().map(nms -> (ServerLink) new CraftServerLink((net.minecraft.server.ServerLinks.Entry)nms)).toList();
    }

    public ServerLinks.ServerLink setLink(ServerLinks.Type type, URI url) {
        ServerLinks.ServerLink existing = this.getLink(type);
        if (existing != null) {
            this.removeLink(existing);
        }
        return this.addLink(type, url);
    }

    public ServerLinks.ServerLink addLink(ServerLinks.Type type, URI url) {
        CraftServerLink link = new CraftServerLink(net.minecraft.server.ServerLinks.Entry.create(CraftServerLinks.fromBukkit(type), url));
        this.addLink(link);
        return link;
    }

    public ServerLinks.ServerLink addLink(Component displayName, URI url) {
        CraftServerLink link = new CraftServerLink(net.minecraft.server.ServerLinks.Entry.create(CardboardAdventure.asVanilla(displayName), url));
        this.addLink(link);
        return link;
    }

    public ServerLinks.ServerLink addLink(String displayName, URI url) {
        CraftServerLink link = new CraftServerLink(net.minecraft.server.ServerLinks.Entry.create(CraftChatMessage.fromStringOrNull(displayName), url));
        this.addLink(link);
        return link;
    }

    private void addLink(CraftServerLink link) {
        ArrayList<net.minecraft.server.ServerLinks.Entry> lst = new ArrayList<net.minecraft.server.ServerLinks.Entry>(this.getServerLinks().entries());
        lst.add(link.handle);
        this.setLinks(new net.minecraft.server.ServerLinks(lst));
    }

    public boolean removeLink(ServerLinks.ServerLink link) {
        ArrayList<net.minecraft.server.ServerLinks.Entry> lst = new ArrayList<net.minecraft.server.ServerLinks.Entry>(this.getServerLinks().entries());
        boolean result = lst.remove(((CraftServerLink)link).handle);
        this.setLinks(new net.minecraft.server.ServerLinks(lst));
        return result;
    }

    public ServerLinks copy() {
        return new CraftServerLinks(this.getServerLinks());
    }

    public net.minecraft.server.ServerLinks getServerLinks() {
        return this.server != null ? this.server.getServerLinks() : this.serverLinks;
    }

    private void setLinks(net.minecraft.server.ServerLinks links) {
        if (this.server != null) {
            this.server.serverLinks = links;
        } else {
            this.serverLinks = links;
        }
    }

    private static net.minecraft.server.ServerLinks.Known fromBukkit(ServerLinks.Type type) {
        return net.minecraft.server.ServerLinks.Known.values()[type.ordinal()];
    }

    private static ServerLinks.Type fromNMS(net.minecraft.server.ServerLinks.Known nms) {
        return ServerLinks.Type.values()[nms.ordinal()];
    }

    public static class CraftServerLink implements ServerLinks.ServerLink {
        private final net.minecraft.server.ServerLinks.Entry handle;

        public CraftServerLink(net.minecraft.server.ServerLinks.Entry handle) {
            this.handle = handle;
        }

        public ServerLinks.Type getType() {
            return this.handle.type().left().map(CraftServerLinks::fromNMS).orElse(null);
        }

        public String getDisplayName() {
            return CraftChatMessage.fromComponent(this.handle.getText());
        }

        public Component displayName() {
            return CardboardAdventure.asAdventure(this.handle.getText());
        }

        public URI getUrl() {
            return this.handle.link();
        }
    }

}
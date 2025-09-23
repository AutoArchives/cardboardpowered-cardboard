package io.papermc.paper.connection;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.jspecify.annotations.NullMarked;

import com.google.common.base.Preconditions;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.common.CookieResponseC2SPacket;
import net.minecraft.network.packet.s2c.common.CookieRequestS2CPacket;
import net.minecraft.util.Identifier;

@NullMarked
public abstract class ReadablePlayerCookieConnectionImpl implements ReadablePlayerCookieConnection {

    private final Map<Identifier, CookieFuture> requestedCookies = new ConcurrentHashMap<Identifier, CookieFuture>();
    private final ClientConnection connection;

    public ReadablePlayerCookieConnectionImpl(ClientConnection connection) {
        this.connection = connection;
    }

    public CompletableFuture<byte[]> retrieveCookie(NamespacedKey key) {
        Preconditions.checkArgument(key != null, "Cookie key cannot be null");
        CompletableFuture<byte[]> future = new CompletableFuture<byte[]>();
        Identifier resourceLocation = CraftNamespacedKey.toMinecraft(key);
        this.requestedCookies.put(resourceLocation, new CookieFuture(resourceLocation, future));
        this.connection.send(new CookieRequestS2CPacket(resourceLocation));
        return future;
    }

    public boolean canStoreCookie() {
        return true;
    }

    public boolean handleCookieResponse(CookieResponseC2SPacket packet) {
        CookieFuture future = this.requestedCookies.get(packet.key());
        if (future != null) {
            future.future().complete(packet.payload());
            this.requestedCookies.remove(packet.key());
            return true;
        }
        return false;
    }

    public boolean isAwaitingCookies() {
        return !this.requestedCookies.isEmpty();
    }

    public record CookieFuture(Identifier key, CompletableFuture<byte[]> future) {
    }

}
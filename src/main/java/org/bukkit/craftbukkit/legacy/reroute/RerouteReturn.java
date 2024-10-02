package org.bukkit.craftbukkit.legacy.reroute;

import org.objectweb.asm.Type;

@com.github.bsideup.jabel.Desugar
public record RerouteReturn(Type type) {
    public int instruction() {
        return this.type.getOpcode(172);
    }
}


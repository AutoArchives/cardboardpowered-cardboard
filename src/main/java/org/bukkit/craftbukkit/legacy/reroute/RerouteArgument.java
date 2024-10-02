package org.bukkit.craftbukkit.legacy.reroute;

import org.objectweb.asm.Type;

@com.github.bsideup.jabel.Desugar
public record RerouteArgument(Type type, boolean injectPluginName, boolean injectPluginVersion) {
    public int instruction() {
        if (this.injectPluginName() || this.injectPluginVersion()) {
            throw new IllegalStateException(String.format("Cannot get instruction for plugin name / version argument: %s", this));
        }
        return this.type.getOpcode(21);
    }
}


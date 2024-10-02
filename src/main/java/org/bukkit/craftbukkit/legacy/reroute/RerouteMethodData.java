package org.bukkit.craftbukkit.legacy.reroute;

import java.util.List;
import org.bukkit.craftbukkit.legacy.reroute.RerouteArgument;
import org.bukkit.craftbukkit.legacy.reroute.RerouteReturn;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;

import com.github.bsideup.jabel.Desugar;

@Desugar
public record RerouteMethodData(String source, Type sourceDesc, Type sourceOwner, String sourceName, boolean staticReroute, Type targetType, String targetOwner, String targetName, List<RerouteArgument> arguments, RerouteReturn rerouteReturn, boolean isInBukkit, @Nullable String requiredCompatibility) {
}


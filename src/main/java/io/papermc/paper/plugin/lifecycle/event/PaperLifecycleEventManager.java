package io.papermc.paper.plugin.lifecycle.event;

import com.google.common.base.Preconditions;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventOwner;
// import io.papermc.paper.plugin.lifecycle.event.handler.configuration.AbstractLifecycleEventHandlerConfiguration;
import io.papermc.paper.plugin.lifecycle.event.handler.configuration.LifecycleEventHandlerConfiguration;
import java.util.function.BooleanSupplier;

import org.bukkit.plugin.Plugin;

public final class PaperLifecycleEventManager<O extends LifecycleEventOwner>
implements LifecycleEventManager<Plugin> {
    private final O owner;
    public final BooleanSupplier registrationCheck;

    public PaperLifecycleEventManager(O owner, BooleanSupplier registrationCheck) {
        this.owner = owner;
        this.registrationCheck = registrationCheck;
    }

    public void registerEventHandler(LifecycleEventHandlerConfiguration<? super Plugin> handlerConfiguration) {
        Preconditions.checkState((boolean)this.registrationCheck.getAsBoolean(), (Object)"Cannot register lifecycle event handlers");
        // ((AbstractLifecycleEventHandlerConfiguration)handlerConfiguration).registerFrom(this.owner);
    }
}


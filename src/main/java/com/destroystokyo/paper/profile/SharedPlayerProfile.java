package com.destroystokyo.paper.profile;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.util.UUID;
import net.minecraft.component.type.ProfileComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SharedPlayerProfile {

   @Nullable
   UUID getUniqueId();

   @Nullable
   String getName();

   boolean removeProperty(@NotNull String var1);

   @Nullable
   Property getProperty(@NotNull String var1);

   @Nullable
   void setProperty(@NotNull String var1, @Nullable Property var2);

   @NotNull
   GameProfile buildGameProfile();

   @NotNull
   ProfileComponent buildResolvableProfile();

}
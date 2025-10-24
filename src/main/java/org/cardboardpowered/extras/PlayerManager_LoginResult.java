package org.cardboardpowered.extras;

import javax.annotation.Nullable;
import net.minecraft.text.Text;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.jetbrains.annotations.NotNull;

public record PlayerManager_LoginResult(@Nullable Text message, @NotNull Result result) {

   public static PlayerManager_LoginResult ALLOW = new PlayerManager_LoginResult(null, Result.ALLOWED);

   public boolean isAllowed() {
      return this == ALLOW;
   }

}
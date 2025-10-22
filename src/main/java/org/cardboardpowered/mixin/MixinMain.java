package org.cardboardpowered.mixin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.SharedConstants;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.Main;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.level.storage.LevelStorage;

/**
 * Mixin of {@link net.minecraft.server.Main}
 * 
 * @implSpec https://github.com/PaperMC/Paper/blob/main/paper-server/patches/sources/net/minecraft/server/Main.java.patch
 */
@Mixin(value = Main.class)
public class MixinMain {

	@Inject(method = "main", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/VanillaDataPackProvider;createManager(Lnet/minecraft/world/level/storage/LevelStorage$Session;)Lnet/minecraft/resource/ResourcePackManager;"))
    private static void cardboard$create_bukkit_datapack(String[] strings, CallbackInfo ci, @Local LevelStorage.Session levelStorageAccess) {

		// Paper start - Create Bukkit Datapack
		
		File bukkitDataPackFolder = new File(levelStorageAccess.getDirectory(WorldSavePath.DATAPACKS).toFile(), "bukkit");
        if (!bukkitDataPackFolder.exists()) {
           bukkitDataPackFolder.mkdirs();
        }
		
        File mcMeta = new File(bukkitDataPackFolder, "pack.mcmeta");

        try {
           int major = SharedConstants.getGameVersion().packVersion(ResourceType.SERVER_DATA).major();
           int minor = SharedConstants.getGameVersion().packVersion(ResourceType.SERVER_DATA).minor();
           Files.asCharSink(mcMeta, StandardCharsets.UTF_8, new FileWriteMode[0])
              .write(
                 "{\n    \"pack\": {\n        \"description\": \"Data pack for resources provided by Bukkit plugins\",\n        \"min_format\": [%d, %d],\n        \"max_format\": [%d, %d]\n    }\n}\n"
                    .formatted(major, minor, major, minor)
              );
        } catch (IOException err) {
           throw new RuntimeException("Could not initialize Bukkit datapack", err);
        }
        // Paper end - Create Bukkit Datapack
    }
	
}

package org.cardboardpowered.mixin.world;

import java.io.IOException;
import java.nio.file.Path;

import org.cardboardpowered.interfaces.ILevelStorage;
import org.cardboardpowered.interfaces.ILevelStorageSession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.path.SymlinkValidationException;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelStorage.Session;

@Mixin(LevelStorage.class)
public class MixinLevelStorage implements ILevelStorage {

	@Override
	public Path getStorageFolder(Path path, RegistryKey<DimensionOptions> dimensionType) {
		if (dimensionType == DimensionOptions.OVERWORLD) {
			return path;
		} else if (dimensionType == DimensionOptions.NETHER) {
			return path.resolve("DIM-1");
		} else {
			return dimensionType == DimensionOptions.END
					? path.resolve("DIM1")
							: path.resolve("dimensions").resolve(dimensionType.getValue().getNamespace()).resolve(dimensionType.getValue().getPath());
		}
	}
	
	@Override
	public LevelStorage.Session validateAndCreateAccess(String saveName, RegistryKey<DimensionOptions> dimensionType) throws IOException, SymlinkValidationException {
		LevelStorage.Session vanilla = this.createSession(saveName);
		((ILevelStorageSession) vanilla).cardboard$set_dimensionType(dimensionType); // Paper-ize
		return vanilla;
	}
	
	@Shadow
	public Session createSession( String directoryName) {
		return null; // Shadowed
	}
	
}

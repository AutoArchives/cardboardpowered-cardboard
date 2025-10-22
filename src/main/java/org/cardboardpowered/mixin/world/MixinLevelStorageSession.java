package org.cardboardpowered.mixin.world;

import java.nio.file.Path;

import org.cardboardpowered.interfaces.ILevelStorageSession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelStorage;

@Mixin(LevelStorage.Session.class)
public class MixinLevelStorageSession implements ILevelStorageSession {
	
	@Shadow
	public LevelStorage.LevelSave directory;

	// Paper - Add dimensionType
	public RegistryKey<DimensionOptions> dimensionType;
	
	@Override
	public void cardboard$set_dimensionType(RegistryKey<DimensionOptions> value) {
		this.dimensionType = value;
	}
	
	@Override
	public RegistryKey<DimensionOptions> cardboard$get_dimensionType() {
		return this.dimensionType;
	}

	@Overwrite
	public Path getWorldDirectory(RegistryKey<World> key) {
		if (null == this.dimensionType) {
			// Non-Bukkit
			return DimensionType.getSaveDirectory(key, this.directory.path());
		}
		
		return LevelStorage_getStorageFolder(this.directory.path(), this.dimensionType);
	}

	private Path LevelStorage_getStorageFolder(Path path, RegistryKey<DimensionOptions> dimensionType) {
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
	
}

package org.cardboardpowered.interfaces;

import java.io.IOException;
import java.nio.file.Path;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.path.SymlinkValidationException;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.level.storage.LevelStorage.Session;

public interface ILevelStorage {

	Path getStorageFolder(Path path, RegistryKey<DimensionOptions> dimensionType);

	Session validateAndCreateAccess(String saveName, RegistryKey<DimensionOptions> dimensionType)
			throws IOException, SymlinkValidationException;

}

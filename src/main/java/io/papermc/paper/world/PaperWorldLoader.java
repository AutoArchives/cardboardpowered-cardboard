/**
 * PaperWorldLoader
 */
package io.papermc.paper.world;

import com.google.common.io.Files;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.NbtCrashException;
import net.minecraft.nbt.NbtException;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.Main;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.path.SymlinkValidationException;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.io.FileUtils;
import org.bukkit.World.Environment;
import org.bukkit.craftbukkit.CraftServer;
import org.cardboardpowered.interfaces.ILevelProperties;
import org.cardboardpowered.interfaces.ILevelStorage;
import org.cardboardpowered.interfaces.IMixinMinecraftServer;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public record PaperWorldLoader(MinecraftServer server, String levelId) {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger("Cardboard|PaperWorldLoader");// LogUtils.getClassLogger();

	public static PaperWorldLoader create(MinecraftServer server, String levelId) {
		return new PaperWorldLoader(server, levelId);
	}

	private PaperWorldLoader.WorldLoadingInfo getWorldInfo(String levelId, DimensionOptions stem) {
		RegistryKey<DimensionOptions> stemKey = this.server.getRegistryManager().getOrThrow(RegistryKeys.DIMENSION).getKey(stem).orElseThrow();
		int dimension = 0;
		boolean enabled = true;

		// Cardboard: server.server -> CraftServer.INSTANCE
		if (stemKey == DimensionOptions.NETHER) {
			dimension = -1;
			enabled = CraftServer.INSTANCE.getAllowNether();
		} else if (stemKey == DimensionOptions.END) {
			dimension = 1;
			enabled = CraftServer.INSTANCE.getAllowEnd();
		} else if (stemKey != DimensionOptions.OVERWORLD) {
			dimension = -999;
		}

		String worldType = dimension == -999
				? stemKey.getValue().getNamespace() + "_" + stemKey.getValue().getPath()
						: Environment.getEnvironment(dimension).toString().toLowerCase(Locale.ROOT);
		String name = stemKey == DimensionOptions.OVERWORLD ? levelId : levelId + "_" + worldType;
		return new PaperWorldLoader.WorldLoadingInfo(dimension, name, worldType, stemKey, enabled);
	}
	
	public static Path LevelStorage_getStorageFolder(Path path, RegistryKey<DimensionOptions> dimensionType) {
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

	private void migrateWorldFolder(PaperWorldLoader.WorldLoadingInfo info) {
		if (info.dimension() != 0) {
			File newWorld = LevelStorage_getStorageFolder(new File(info.name()).toPath(), info.stemKey()).toFile();
			File oldWorld = LevelStorage_getStorageFolder(new File(this.levelId).toPath(), info.stemKey()).toFile();
			File oldLevelDat = new File(new File(this.levelId), "level.dat");
			if (!newWorld.isDirectory() && oldWorld.isDirectory() && oldLevelDat.isFile()) {
				LOGGER.info("---- Migration of old " + info.worldType() + " folder required ----");
				LOGGER.info(
						"Unfortunately due to the way that Minecraft implemented multiworld support in 1.6, Bukkit requires that you move your "
								+ info.worldType()
								+ " folder to a new location in order to operate correctly."
						);
				LOGGER.info("We will move this folder for you, but it will mean that you need to move it back should you wish to stop using Bukkit in the future.");
				LOGGER.info("Attempting to move " + oldWorld + " to " + newWorld + "...");
				if (newWorld.exists()) {
					LOGGER.warn("A file or folder already exists at " + newWorld + "!");
					LOGGER.info("---- Migration of old " + info.worldType() + " folder failed ----");
				} else if (newWorld.getParentFile().mkdirs()) {
					if (oldWorld.renameTo(newWorld)) {
						LOGGER.info("Success! To restore " + info.worldType() + " in the future, simply move " + newWorld + " to " + oldWorld);

						try {
							Files.copy(oldLevelDat, new File(new File(info.name()), "level.dat"));
							FileUtils.copyDirectory(new File(new File(this.levelId), "data"), new File(new File(info.name()), "data"));
						} catch (IOException var6) {
							LOGGER.warn("Unable to migrate world data.");
						}

						LOGGER.info("---- Migration of old " + info.worldType() + " folder complete ----");
					} else {
						LOGGER.warn("Could not move folder " + oldWorld + " to " + newWorld + "!");
						LOGGER.info("---- Migration of old " + info.worldType() + " folder failed ----");
					}
				} else {
					LOGGER.warn("Could not create path for " + newWorld + "!");
					LOGGER.info("---- Migration of old " + info.worldType() + " folder failed ----");
				}
			}
		}
	}

	public void loadInitialWorlds() {
		IMixinMinecraftServer mc = (IMixinMinecraftServer) this.server; // Cardboard
		
		for (DimensionOptions stem : this.server.getRegistryManager().getOrThrow(RegistryKeys.DIMENSION)) {
			PaperWorldLoader.WorldLoadingInfo info = this.getWorldInfo(this.levelId, stem);
			this.migrateWorldFolder(info);
			if (info.enabled()) {
				LevelStorage.Session levelStorageAccess = ((IMixinMinecraftServer) this.server).getSessionBF();
				if (info.dimension() != 0) {
					try {
						levelStorageAccess = ((ILevelStorage) LevelStorage.create(CraftServer.INSTANCE.getWorldContainer().toPath()))
								.validateAndCreateAccess(info.name(), info.stemKey());
					} catch (SymlinkValidationException | IOException var7) {
						throw new RuntimeException(var7);
					}
				}

				PaperWorldLoader.LevelDataResult levelData = getLevelData(levelStorageAccess);
				if (levelData.fatalError) {
					return;
				}

				LevelProperties primaryLevelData;
				if (levelData.dataTag == null) {
					primaryLevelData = (LevelProperties)Main.createWorld(
							((MinecraftDedicatedServer)this.server).propertiesLoader,
							mc.cardboard$worldLoaderContext(),
							mc.cardboard$worldLoaderContext().dimensionsRegistryManager().getOrThrow(RegistryKeys.DIMENSION),
							this.server.isDemo(),
							true // TODO: this.server.options.has("bonusChest")
							)
							.extraData();
				} else {
					primaryLevelData = (LevelProperties)LevelStorage.parseSaveProperties(
							levelData.dataTag,
							mc.cardboard$worldLoaderContext().dataConfiguration(),
							mc.cardboard$worldLoaderContext().dimensionsRegistryManager().getOrThrow(RegistryKeys.DIMENSION),
							mc.cardboard$worldLoaderContext().worldGenRegistryManager()
						)
							.properties();
				}

				((ILevelProperties) primaryLevelData).checkName(info.name());
				primaryLevelData.addServerBrand(this.server.getServerModName(), this.server.getModStatus().isModded());

				/*
			if (this.server.options.has("forceUpgrade")) {
				Main.forceUpgradeWorld(
				  levelStorageAccess,
				  primaryLevelData,
				  Schemas.getFixer(),
				  this.server.options.has("eraseCache"),
				  () -> true,
				  this.server.getRegistryManager(),
				  this.server.options.has("recreateRegionFiles")
				);
			}
				 */

				((IMixinMinecraftServer) this.server).createLevel(stem, info, levelStorageAccess, primaryLevelData);
			}
		}

		((MinecraftDedicatedServer)this.server).updateDifficulty();

		for (ServerWorld serverLevel : this.server.getWorlds()) {
			mc.cardboard$prepareLevel(serverLevel);
		}
	}

	public static PaperWorldLoader.LevelDataResult getLevelData(LevelStorage.Session levelStorageAccess) {
		if (levelStorageAccess.levelDatExists()) {
			Dynamic<?> dataTag;
			LevelSummary summary;
			try {
				dataTag = levelStorageAccess.readLevelProperties();
				summary = levelStorageAccess.getLevelSummary(dataTag);
			} catch (NbtCrashException | IOException | NbtException var7) {
				LevelStorage.LevelSave levelDirectory = levelStorageAccess.getDirectory();
				LOGGER.warn("Failed to load world data from {}", levelDirectory.getLevelDatPath(), var7);
				LOGGER.info("Attempting to use fallback");

				try {
					dataTag = levelStorageAccess.readOldLevelProperties();
					summary = levelStorageAccess.getLevelSummary(dataTag);
				} catch (NbtCrashException | IOException | NbtException var6) {
					LOGGER.error("Failed to load world data from {}", levelDirectory.getLevelDatOldPath(), var6);
					LOGGER.error(
							"Failed to load world data from {} and {}. World files may be corrupted. Shutting down.",
							levelDirectory.getLevelDatPath(),
							levelDirectory.getLevelDatOldPath()
							);
					return new PaperWorldLoader.LevelDataResult(null, true);
				}

				levelStorageAccess.tryRestoreBackup();
			}

			if (summary.requiresConversion()) {
				LOGGER.info("This world must be opened in an older version (like 1.6.4) to be safely converted");
				return new PaperWorldLoader.LevelDataResult(null, true);
			} else if (!summary.isVersionAvailable()) {
				LOGGER.info("This world was created by an incompatible version.");
				return new PaperWorldLoader.LevelDataResult(null, true);
			} else {
				return new PaperWorldLoader.LevelDataResult(dataTag, false);
			}
		} else {
			return new PaperWorldLoader.LevelDataResult(null, false);
		}
	}

	public record LevelDataResult(@Nullable Dynamic<?> dataTag, boolean fatalError) {
	}

	public record WorldLoadingInfo(int dimension, String name, String worldType, RegistryKey<DimensionOptions> stemKey, boolean enabled) {
	}

}
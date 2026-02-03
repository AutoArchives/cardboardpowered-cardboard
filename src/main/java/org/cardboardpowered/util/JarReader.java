package org.cardboardpowered.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @implNote This is old code, target was to disable Mixins that are unneeded, to prevent any conflicts with other mods.
 */
public class JarReader {

    public static List<String> found = new ArrayList<>();
    // public static long timeTook;

    public static int read_plugins(File folder) throws Exception {
        // logger.info("Please wait, Scanning plugins for events...");
        // long start = System.currentTimeMillis();
    	for (File f : folder.listFiles()) {
    		if (!f.getName().endsWith(".jar")) {
    			continue;
    		}
    		ZipFile zipFile = new ZipFile(f.getAbsolutePath());

    		Enumeration<? extends ZipEntry> entries = zipFile.entries();

    		while(entries.hasMoreElements()){
    			ZipEntry entry = entries.nextElement();
    			InputStream stream = zipFile.getInputStream(entry);
    			if (!entry.getName().endsWith(".class")) continue;

    			// convert stream into a reader
    			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    			String contents = reader.lines().collect(Collectors.joining("\n"));

    			for (String event : EVENTS) {
    				if (contents.contains(event) && !found.contains(event) ) {
    					found.add(event);
    				}
    			}

    			stream.close();
    		}
    		zipFile.close();
    	}

        // timeTook = System.currentTimeMillis() - start;
        // for (String s : found) System.out.println(s);
        //logger.info("Found: " + found.size() + " (Took: " + timeTook + "ms)");
        return found.size();
    }
    
    /**
     * @implNote TODO: Grab from API jar?
     */
    public static String[] EVENTS = {
            "ThrownEggHatchEvent", // from Paper
            "BlockBreakEvent",
            "BlockBurnEvent",
            "BlockCanBuildEvent",
            "BlockCookEvent",
            "BlockDamageEvent",
            "BlockDispenseArmorEvent",
            "BlockDispenseEvent",
            "BlockDropItemEvent",
            "BlockExpEvent",
            "BlockExplodeEvent",
            "BlockFadeEvent",
            "BlockFertilizeEvent",
            "BlockFormEvent",
            "BlockFromToEvent",
            "BlockGrowEvent",
            "BlockIgniteEvent",
            "BlockMultiPlaceEvent",
            "BlockPhysicsEvent",
            "BlockPistonEvent",
            "BlockPistonExtendEvent",
            "BlockPistonRetractEvent",
            "BlockPlaceEvent",
            "BlockReceiveGameEvent",
            "BlockRedstoneEvent",
            "BlockShearEntityEvent",
            "BlockSpreadEvent",
            "CauldronLevelChangeEvent",
            "EntityBlockFormEvent",
            "FluidLevelChangeEvent",
            "LeavesDecayEvent",
            "MoistureChangeEvent",
            "NotePlayEvent",
            "SignChangeEvent",
            "SpongeAbsorbEvent",
            "UnknownCommandEvent",
            "EnchantItemEvent",
            "PrepareItemEnchantEvent",
            "AreaEffectCloudApplyEvent",
            "ArrowBodyCountChangeEvent",
            "BatToggleSleepEvent",
            "CreatureSpawnEvent",
            "CreeperPowerEvent",
            "EnderDragonChangePhaseEvent",
            "EntityAirChangeEvent",
            "EntityBreakDoorEvent",
            "EntityBreedEvent",
            "EntityChangeBlockEvent",
            "EntityCombustByBlockEvent",
            "EntityCombustByEntityEvent",
            "EntityCombustEvent",
            "EntityCreatePortalEvent",
            "EntityDamageByBlockEvent",
            "EntityDamageByEntityEvent",
            "EntityDamageEvent",
            "EntityDeathEvent",
            "EntityDropItemEvent",
            "EntityEnterBlockEvent",
            "EntityEnterLoveModeEvent",
            "EntityExhaustionEvent",
            "EntityExplodeEvent",
            "EntityInteractEvent",
            "EntityPickupItemEvent",
            "EntityPlaceEvent",
            "EntityPortalEnterEvent",
            "EntityPortalEvent",
            "EntityPortalExitEvent",
            "EntityPoseChangeEvent",
            "EntityPotionEffectEvent",
            "EntityRegainHealthEvent",
            "EntityResurrectEvent",
            "EntityShootBowEvent",
            "EntitySpawnEvent",
            "EntitySpellCastEvent",
            "EntityTameEvent",
            "EntityTargetEvent",
            "EntityTargetLivingEntityEvent",
            "EntityTeleportEvent",
            "EntityToggleGlideEvent",
            "EntityToggleSwimEvent",
            "EntityTransformEvent",
            "EntityUnleashEvent",
            "ExpBottleEvent",
            "ExplosionPrimeEvent",
            "FireworkExplodeEvent",
            "FoodLevelChangeEvent",
            "HorseJumpEvent",
            "ItemDespawnEvent",
            "ItemMergeEvent",
            "ItemSpawnEvent",
            "LingeringPotionSplashEvent",
            "PiglinBarterEvent",
            "PigZapEvent",
            "PigZombieAngerEvent",
            "PlayerDeathEvent",
            "PlayerLeashEntityEvent",
            "PotionSplashEvent",
            "ProjectileHitEvent",
            "ProjectileLaunchEvent",
            "SheepDyeWoolEvent",
            "SheepRegrowWoolEvent",
            "SlimeSplitEvent",
            "SpawnerSpawnEvent",
            "StriderTemperatureChangeEvent",
            "VillagerAcquireTradeEvent",
            "VillagerCareerChangeEvent",
            "VillagerReplenishTradeEvent",
            "HangingBreakByEntityEvent",
            "HangingBreakEvent",
            "HangingPlaceEvent",
            "BrewEvent",
            "BrewingStandFuelEvent",
            "CraftItemEvent",
            "FurnaceBurnEvent",
            "FurnaceExtractEvent",
            "FurnaceSmeltEvent",
            "FurnaceStartSmeltEvent",
            "InventoryAction",
            "InventoryClickEvent",
            "InventoryCloseEvent",
            "InventoryCreativeEvent",
            "InventoryDragEvent",
            "InventoryEvent",
            "InventoryInteractEvent",
            "InventoryMoveItemEvent",
            "InventoryOpenEvent",
            "InventoryPickupItemEvent",
            "PrepareAnvilEvent",
            "PrepareItemCraftEvent",
            "PrepareSmithingEvent",
            "SmithItemEvent",
            "TradeSelectEvent",
            "AsyncPlayerChatEvent",
            "AsyncPlayerPreLoginEvent",
            "PlayerAdvancementDoneEvent",
            "PlayerAnimationEvent",
            "PlayerArmorStandManipulateEvent",
            "PlayerAttemptPickupItemEvent",
            "PlayerBedEnterEvent",
            "PlayerBedLeaveEvent",
            "PlayerBucketEmptyEvent",
            "PlayerBucketEntityEvent",
            "PlayerBucketEvent",
            "PlayerBucketFillEvent",
            "PlayerBucketFishEvent",
            "PlayerChangedMainHandEvent",
            "PlayerChangedWorldEvent",
            "PlayerChannelEvent",
            "PlayerChatEvent",
            "PlayerChatTabCompleteEvent",
            "PlayerCommandPreprocessEvent",
            "PlayerCommandSendEvent",
            "PlayerDropItemEvent",
            "PlayerEditBookEvent",
            "PlayerEggThrowEvent",
            "PlayerExpChangeEvent",
            "PlayerFishEvent",
            "PlayerGameModeChangeEvent",
            "PlayerHarvestBlockEvent",
            "PlayerInteractAtEntityEvent",
            "PlayerInteractEntityEvent",
            "PlayerInteractEvent",
            "PlayerItemBreakEvent",
            "PlayerItemConsumeEvent",
            "PlayerItemDamageEvent",
            "PlayerItemHeldEvent",
            "PlayerItemMendEvent",
            "PlayerJoinEvent",
            "PlayerKickEvent",
            "PlayerLevelChangeEvent",
            "PlayerMoveEvent",
            "PlayerPickupArrowEvent",
            "PlayerPickupItemEvent",
            "PlayerPortalEvent",
            "PlayerPreLoginEvent",
            "PlayerQuitEvent",
            "PlayerRecipeDiscoverEvent",
            "PlayerRegisterChannelEvent",
            "PlayerResourcePackStatusEvent",
            "PlayerRespawnEvent",
            "PlayerRiptideEvent",
            "PlayerShearEntityEvent",
            "PlayerStatisticIncrementEvent",
            "PlayerSwapHandItemsEvent",
            "PlayerTakeLecternBookEvent",
            "PlayerTeleportEvent",
            "PlayerToggleFlightEvent",
            "PlayerToggleSneakEvent",
            "PlayerToggleSprintEvent",
            "PlayerUnleashEntityEvent",
            "PlayerUnregisterChannelEvent",
            "PlayerVelocityEvent",
            "RaidFinishEvent",
            "RaidSpawnWaveEvent",
            "RaidStopEvent",
            "RaidTriggerEvent",
            "BroadcastMessageEvent",
            "MapInitializeEvent",
            "ServerCommandEvent",
            "ServerListPingEvent",
            "TabCompleteEvent",
            "VehicleBlockCollisionEvent",
            "VehicleCollisionEvent",
            "VehicleCreateEvent",
            "VehicleDamageEvent",
            "VehicleDestroyEvent",
            "VehicleEnterEvent",
            "VehicleEntityCollisionEvent",
            "VehicleExitEvent",
            "VehicleMoveEvent",
            "VehicleUpdateEvent",
            "LightningStrikeEvent",
            "ThunderChangeEvent",
            "WeatherChangeEvent",
            "WeatherEvent",
            "ChunkLoadEvent",
            "ChunkPopulateEvent",
            "ChunkUnloadEvent",
            "EntitiesLoadEvent",
            "EntitiesUnloadEvent",
            "GenericGameEvent",
            "LootGenerateEvent",
            "PortalCreateEvent",
            "SpawnChangeEvent",
            "StructureGrowEvent",
            "TimeSkipEvent",
            "WorldInitEvent",
            "WorldLoadEvent",
            "WorldSaveEvent",
            "WorldUnloadEvent",
    };

}

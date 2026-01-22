package io.papermc.paper.datacomponent;

import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.datacomponent.DataComponentAdapter;
import io.papermc.paper.datacomponent.item.PaperBannerPatternLayers;
import io.papermc.paper.datacomponent.item.PaperBlockItemDataProperties;
import io.papermc.paper.datacomponent.item.PaperBundleContents;
import io.papermc.paper.datacomponent.item.PaperChargedProjectiles;
import io.papermc.paper.datacomponent.item.PaperConsumable;
import io.papermc.paper.datacomponent.item.PaperCustomModelData;
import io.papermc.paper.datacomponent.item.PaperDamageResistant;
import io.papermc.paper.datacomponent.item.PaperDeathProtection;
import io.papermc.paper.datacomponent.item.PaperDyedItemColor;
import io.papermc.paper.datacomponent.item.PaperEnchantable;
import io.papermc.paper.datacomponent.item.PaperEquippable;
import io.papermc.paper.datacomponent.item.PaperFireworks;
import io.papermc.paper.datacomponent.item.PaperFoodProperties;
import io.papermc.paper.datacomponent.item.PaperItemAdventurePredicate;
import io.papermc.paper.datacomponent.item.PaperItemArmorTrim;
import io.papermc.paper.datacomponent.item.PaperItemAttributeModifiers;
import io.papermc.paper.datacomponent.item.PaperItemContainerContents;
import io.papermc.paper.datacomponent.item.PaperItemEnchantments;
import io.papermc.paper.datacomponent.item.PaperItemLore;
import io.papermc.paper.datacomponent.item.PaperItemTool;
import io.papermc.paper.datacomponent.item.PaperJukeboxPlayable;
import io.papermc.paper.datacomponent.item.PaperLodestoneTracker;
import io.papermc.paper.datacomponent.item.PaperMapDecorations;
import io.papermc.paper.datacomponent.item.PaperMapId;
import io.papermc.paper.datacomponent.item.PaperMapItemColor;
import io.papermc.paper.datacomponent.item.PaperOminousBottleAmplifier;
import io.papermc.paper.datacomponent.item.PaperPotDecorations;
import io.papermc.paper.datacomponent.item.PaperPotionContents;
import io.papermc.paper.datacomponent.item.PaperRepairable;
import io.papermc.paper.datacomponent.item.PaperResolvableProfile;
import io.papermc.paper.datacomponent.item.PaperSeededContainerLoot;
import io.papermc.paper.datacomponent.item.PaperSuspiciousStewEffects;
import io.papermc.paper.datacomponent.item.PaperUnbreakable;
import io.papermc.paper.datacomponent.item.PaperUseCooldown;
import io.papermc.paper.datacomponent.item.PaperUseRemainder;
import io.papermc.paper.datacomponent.item.PaperWritableBookContent;
import io.papermc.paper.datacomponent.item.PaperWrittenBookContent;
import io.papermc.paper.item.MapPostProcessing;
import io.papermc.paper.util.MCUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.InstrumentComponent;
import org.bukkit.craftbukkit.CraftMusicInstrument;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.inventory.CraftMetaFirework;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.inventory.ItemRarity;
import org.cardboardpowered.adventure.CardboardAdventure;

public final class DataComponentAdapters {
    static final Function<Unit, Void> UNIT_TO_API_CONVERTER = $ -> {
        throw new UnsupportedOperationException("Cannot convert the Unit type to an API value");
    };
    static final Map<ResourceKey<DataComponentType<?>>, DataComponentAdapter<?, ?>> ADAPTERS = new HashMap();

    public static void bootstrap() {
        DataComponentAdapters.registerIdentity(DataComponents.MAX_STACK_SIZE);
        DataComponentAdapters.registerIdentity(DataComponents.MAX_DAMAGE);
        DataComponentAdapters.registerIdentity(DataComponents.DAMAGE);
        DataComponentAdapters.registerUntyped(DataComponents.UNBREAKABLE);
        //DataComponentAdapters.register(DataComponentTypes.UNBREAKABLE, PaperUnbreakable::new);
        DataComponentAdapters.register(DataComponents.CUSTOM_NAME, CardboardAdventure::asAdventure, CardboardAdventure::asVanilla);
        DataComponentAdapters.register(DataComponents.ITEM_NAME, CardboardAdventure::asAdventure, CardboardAdventure::asVanilla);
        DataComponentAdapters.register(DataComponents.ITEM_MODEL, CardboardAdventure::asAdventure, CardboardAdventure::asVanilla);
        DataComponentAdapters.register(DataComponents.LORE, PaperItemLore::new);
        DataComponentAdapters.register(DataComponents.RARITY, nms -> ItemRarity.valueOf((String)nms.name()), api -> Rarity.valueOf(api.name()));
        DataComponentAdapters.register(DataComponents.ENCHANTMENTS, PaperItemEnchantments::new);
        DataComponentAdapters.register(DataComponents.CAN_PLACE_ON, PaperItemAdventurePredicate::new);
        DataComponentAdapters.register(DataComponents.CAN_BREAK, PaperItemAdventurePredicate::new);
        DataComponentAdapters.register(DataComponents.ATTRIBUTE_MODIFIERS, PaperItemAttributeModifiers::new);
        DataComponentAdapters.register(DataComponents.CUSTOM_MODEL_DATA, PaperCustomModelData::new);
        // DataComponentAdapters.registerUntyped(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP);
        // DataComponentAdapters.registerUntyped(DataComponentTypes.HIDE_TOOLTIP);
        
        // TODO
        // DataComponentAdapters.register(DataComponentTypes.TOOLTIP_DISPLAY, PaperTooltipDisplay::new);
        
        DataComponentAdapters.registerIdentity(DataComponents.REPAIR_COST);
        DataComponentAdapters.registerIdentity(DataComponents.ENCHANTMENT_GLINT_OVERRIDE);
        DataComponentAdapters.registerUntyped(DataComponents.INTANGIBLE_PROJECTILE);
        DataComponentAdapters.register(DataComponents.FOOD, PaperFoodProperties::new);
        DataComponentAdapters.register(DataComponents.CONSUMABLE, PaperConsumable::new);
        DataComponentAdapters.register(DataComponents.USE_REMAINDER, PaperUseRemainder::new);
        DataComponentAdapters.register(DataComponents.USE_COOLDOWN, PaperUseCooldown::new);
        DataComponentAdapters.register(DataComponents.DAMAGE_RESISTANT, PaperDamageResistant::new);
        DataComponentAdapters.register(DataComponents.TOOL, PaperItemTool::new);
        DataComponentAdapters.register(DataComponents.ENCHANTABLE, PaperEnchantable::new);
        DataComponentAdapters.register(DataComponents.EQUIPPABLE, PaperEquippable::new);
        DataComponentAdapters.register(DataComponents.REPAIRABLE, PaperRepairable::new);
        DataComponentAdapters.registerUntyped(DataComponents.GLIDER);
        DataComponentAdapters.register(DataComponents.TOOLTIP_STYLE, CardboardAdventure::asAdventure, CardboardAdventure::asVanilla);
        DataComponentAdapters.register(DataComponents.DEATH_PROTECTION, PaperDeathProtection::new);
        DataComponentAdapters.register(DataComponents.STORED_ENCHANTMENTS, PaperItemEnchantments::new);
        DataComponentAdapters.register(DataComponents.DYED_COLOR, PaperDyedItemColor::new);
        DataComponentAdapters.register(DataComponents.MAP_COLOR, PaperMapItemColor::new);
        DataComponentAdapters.register(DataComponents.MAP_ID, PaperMapId::new);
        DataComponentAdapters.register(DataComponents.MAP_DECORATIONS, PaperMapDecorations::new);
        DataComponentAdapters.register(DataComponents.MAP_POST_PROCESSING, nms -> MapPostProcessing.valueOf((String)nms.name()), api -> net.minecraft.world.item.component.MapPostProcessing.valueOf(api.name()));
        DataComponentAdapters.register(DataComponents.CHARGED_PROJECTILES, PaperChargedProjectiles::new);
        DataComponentAdapters.register(DataComponents.BUNDLE_CONTENTS, PaperBundleContents::new);
        DataComponentAdapters.register(DataComponents.POTION_CONTENTS, PaperPotionContents::new);
        DataComponentAdapters.register(DataComponents.SUSPICIOUS_STEW_EFFECTS, PaperSuspiciousStewEffects::new);
        DataComponentAdapters.register(DataComponents.WRITTEN_BOOK_CONTENT, PaperWrittenBookContent::new);
        DataComponentAdapters.register(DataComponents.WRITABLE_BOOK_CONTENT, PaperWritableBookContent::new);
        DataComponentAdapters.register(DataComponents.TRIM, PaperItemArmorTrim::new);
        //DataComponentAdapters.register(DataComponentTypes.INSTRUMENT, CraftMusicInstrument::minecraftHolderToBukkit, CraftMusicInstrument::bukkitToMinecraftHolder);
        DataComponentAdapters.register(DataComponents.INSTRUMENT, nms -> CraftMusicInstrument.minecraftHolderToBukkit(nms.instrument().unwrap(CraftRegistry.getMinecraftRegistry()).orElseThrow()), api -> new InstrumentComponent(CraftMusicInstrument.bukkitToMinecraftHolder(api)));
        DataComponentAdapters.register(DataComponents.OMINOUS_BOTTLE_AMPLIFIER, PaperOminousBottleAmplifier::new);
        DataComponentAdapters.register(DataComponents.JUKEBOX_PLAYABLE, PaperJukeboxPlayable::new);
        DataComponentAdapters.register(DataComponents.RECIPES, nms -> MCUtil.transformUnmodifiable(nms, CardboardAdventure::asAdventureKey), api -> MCUtil.transformUnmodifiable(api, key -> PaperAdventure.asVanilla(Registries.RECIPE, key)));
        DataComponentAdapters.register(DataComponents.LODESTONE_TRACKER, PaperLodestoneTracker::new);
        DataComponentAdapters.register(DataComponents.FIREWORK_EXPLOSION, CraftMetaFirework::getEffect, CraftMetaFirework::getExplosion);
        DataComponentAdapters.register(DataComponents.FIREWORKS, PaperFireworks::new);
        DataComponentAdapters.register(DataComponents.PROFILE, PaperResolvableProfile::new);
        DataComponentAdapters.register(DataComponents.NOTE_BLOCK_SOUND, CardboardAdventure::asAdventure, CardboardAdventure::asVanilla);
        DataComponentAdapters.register(DataComponents.BANNER_PATTERNS, PaperBannerPatternLayers::new);
        DataComponentAdapters.register(DataComponents.BASE_COLOR, nms -> org.bukkit.DyeColor.getByWoolData((byte)((byte)nms.getId())), api -> DyeColor.byId(api.getWoolData()));
        DataComponentAdapters.register(DataComponents.POT_DECORATIONS, PaperPotDecorations::new);
        DataComponentAdapters.register(DataComponents.CONTAINER, PaperItemContainerContents::new);
        DataComponentAdapters.register(DataComponents.BLOCK_STATE, PaperBlockItemDataProperties::new);
        DataComponentAdapters.register(DataComponents.CONTAINER_LOOT, PaperSeededContainerLoot::new);

        for (final Map.Entry<ResourceKey<DataComponentType<?>>, DataComponentType<?>> componentType : BuiltInRegistries.DATA_COMPONENT_TYPE.entrySet()) {
            if (!ADAPTERS.containsKey(componentType.getKey())) {
                registerUntyped((DataComponentType<Unit>) componentType.getValue());
            }
        }
    }

    public static void registerUntyped(DataComponentType<Unit> type) {
        DataComponentAdapters.registerInternal(type, UNIT_TO_API_CONVERTER, DataComponentAdapter.API_TO_UNIT_CONVERTER, false);
    }

    private static <COMMON> void registerIdentity(DataComponentType<COMMON> type) {
        DataComponentAdapters.registerInternal(type, Function.identity(), Function.identity(), true);
    }

    private static <NMS, API extends Handleable<NMS>> void register(DataComponentType<NMS> type, Function<NMS, API> vanillaToApi) {
        DataComponentAdapters.registerInternal(type, vanillaToApi, Handleable::getHandle, false);
    }

    private static <NMS, API> void register(DataComponentType<NMS> type, Function<NMS, API> vanillaToApi, Function<API, NMS> apiToVanilla) {
        DataComponentAdapters.registerInternal(type, vanillaToApi, apiToVanilla, false);
    }

    private static <NMS, API> void registerInternal(final DataComponentType<NMS> type, final Function<NMS, API> vanillaToApi, final Function<API, NMS> apiToVanilla, final boolean codecValidation) {
        final ResourceKey<DataComponentType<?>> key = BuiltInRegistries.DATA_COMPONENT_TYPE.getResourceKey(type).orElseThrow();
        if (ADAPTERS.containsKey(key)) {
            throw new IllegalStateException("Duplicate adapter registration for " + key);
        }
        ADAPTERS.put(key, new DataComponentAdapter<>(type, apiToVanilla, vanillaToApi, codecValidation && !type.isTransient()));
    }

}
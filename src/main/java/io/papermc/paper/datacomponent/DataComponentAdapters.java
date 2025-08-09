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
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapPostProcessingComponent;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Rarity;
import net.minecraft.util.Unit;
import org.bukkit.craftbukkit.CraftMusicInstrument;
import org.bukkit.craftbukkit.inventory.CraftMetaFirework;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.inventory.ItemRarity;
import org.cardboardpowered.adventure.CardboardAdventure;

public final class DataComponentAdapters {
    static final Function<Unit, Void> UNIT_TO_API_CONVERTER = $ -> {
        throw new UnsupportedOperationException("Cannot convert the Unit type to an API value");
    };
    static final Map<RegistryKey<ComponentType<?>>, DataComponentAdapter<?, ?>> ADAPTERS = new HashMap();

    public static void bootstrap() {
        DataComponentAdapters.registerIdentity(DataComponentTypes.MAX_STACK_SIZE);
        DataComponentAdapters.registerIdentity(DataComponentTypes.MAX_DAMAGE);
        DataComponentAdapters.registerIdentity(DataComponentTypes.DAMAGE);
        DataComponentAdapters.register(DataComponentTypes.UNBREAKABLE, PaperUnbreakable::new);
        DataComponentAdapters.register(DataComponentTypes.CUSTOM_NAME, CardboardAdventure::asAdventure, CardboardAdventure::asVanilla);
        DataComponentAdapters.register(DataComponentTypes.ITEM_NAME, CardboardAdventure::asAdventure, CardboardAdventure::asVanilla);
        DataComponentAdapters.register(DataComponentTypes.ITEM_MODEL, CardboardAdventure::asAdventure, CardboardAdventure::asVanilla);
        DataComponentAdapters.register(DataComponentTypes.LORE, PaperItemLore::new);
        DataComponentAdapters.register(DataComponentTypes.RARITY, nms -> ItemRarity.valueOf((String)nms.name()), api -> Rarity.valueOf(api.name()));
        DataComponentAdapters.register(DataComponentTypes.ENCHANTMENTS, PaperItemEnchantments::new);
        DataComponentAdapters.register(DataComponentTypes.CAN_PLACE_ON, PaperItemAdventurePredicate::new);
        DataComponentAdapters.register(DataComponentTypes.CAN_BREAK, PaperItemAdventurePredicate::new);
        DataComponentAdapters.register(DataComponentTypes.ATTRIBUTE_MODIFIERS, PaperItemAttributeModifiers::new);
        DataComponentAdapters.register(DataComponentTypes.CUSTOM_MODEL_DATA, PaperCustomModelData::new);
        DataComponentAdapters.registerUntyped(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP);
        DataComponentAdapters.registerUntyped(DataComponentTypes.HIDE_TOOLTIP);
        DataComponentAdapters.registerIdentity(DataComponentTypes.REPAIR_COST);
        DataComponentAdapters.registerIdentity(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE);
        DataComponentAdapters.registerUntyped(DataComponentTypes.INTANGIBLE_PROJECTILE);
        DataComponentAdapters.register(DataComponentTypes.FOOD, PaperFoodProperties::new);
        DataComponentAdapters.register(DataComponentTypes.CONSUMABLE, PaperConsumable::new);
        DataComponentAdapters.register(DataComponentTypes.USE_REMAINDER, PaperUseRemainder::new);
        DataComponentAdapters.register(DataComponentTypes.USE_COOLDOWN, PaperUseCooldown::new);
        DataComponentAdapters.register(DataComponentTypes.DAMAGE_RESISTANT, PaperDamageResistant::new);
        DataComponentAdapters.register(DataComponentTypes.TOOL, PaperItemTool::new);
        DataComponentAdapters.register(DataComponentTypes.ENCHANTABLE, PaperEnchantable::new);
        DataComponentAdapters.register(DataComponentTypes.EQUIPPABLE, PaperEquippable::new);
        DataComponentAdapters.register(DataComponentTypes.REPAIRABLE, PaperRepairable::new);
        DataComponentAdapters.registerUntyped(DataComponentTypes.GLIDER);
        DataComponentAdapters.register(DataComponentTypes.TOOLTIP_STYLE, CardboardAdventure::asAdventure, CardboardAdventure::asVanilla);
        DataComponentAdapters.register(DataComponentTypes.DEATH_PROTECTION, PaperDeathProtection::new);
        DataComponentAdapters.register(DataComponentTypes.STORED_ENCHANTMENTS, PaperItemEnchantments::new);
        DataComponentAdapters.register(DataComponentTypes.DYED_COLOR, PaperDyedItemColor::new);
        DataComponentAdapters.register(DataComponentTypes.MAP_COLOR, PaperMapItemColor::new);
        DataComponentAdapters.register(DataComponentTypes.MAP_ID, PaperMapId::new);
        DataComponentAdapters.register(DataComponentTypes.MAP_DECORATIONS, PaperMapDecorations::new);
        DataComponentAdapters.register(DataComponentTypes.MAP_POST_PROCESSING, nms -> MapPostProcessing.valueOf((String)nms.name()), api -> MapPostProcessingComponent.valueOf(api.name()));
        DataComponentAdapters.register(DataComponentTypes.CHARGED_PROJECTILES, PaperChargedProjectiles::new);
        DataComponentAdapters.register(DataComponentTypes.BUNDLE_CONTENTS, PaperBundleContents::new);
        DataComponentAdapters.register(DataComponentTypes.POTION_CONTENTS, PaperPotionContents::new);
        DataComponentAdapters.register(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS, PaperSuspiciousStewEffects::new);
        DataComponentAdapters.register(DataComponentTypes.WRITTEN_BOOK_CONTENT, PaperWrittenBookContent::new);
        DataComponentAdapters.register(DataComponentTypes.WRITABLE_BOOK_CONTENT, PaperWritableBookContent::new);
        DataComponentAdapters.register(DataComponentTypes.TRIM, PaperItemArmorTrim::new);
        DataComponentAdapters.register(DataComponentTypes.INSTRUMENT, CraftMusicInstrument::minecraftHolderToBukkit, CraftMusicInstrument::bukkitToMinecraftHolder);
        DataComponentAdapters.register(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER, PaperOminousBottleAmplifier::new);
        DataComponentAdapters.register(DataComponentTypes.JUKEBOX_PLAYABLE, PaperJukeboxPlayable::new);
        DataComponentAdapters.register(DataComponentTypes.RECIPES, nms -> MCUtil.transformUnmodifiable(nms, CardboardAdventure::asAdventureKey), api -> MCUtil.transformUnmodifiable(api, key -> PaperAdventure.asVanilla(RegistryKeys.RECIPE, key)));
        DataComponentAdapters.register(DataComponentTypes.LODESTONE_TRACKER, PaperLodestoneTracker::new);
        DataComponentAdapters.register(DataComponentTypes.FIREWORK_EXPLOSION, CraftMetaFirework::getEffect, CraftMetaFirework::getExplosion);
        DataComponentAdapters.register(DataComponentTypes.FIREWORKS, PaperFireworks::new);
        DataComponentAdapters.register(DataComponentTypes.PROFILE, PaperResolvableProfile::new);
        DataComponentAdapters.register(DataComponentTypes.NOTE_BLOCK_SOUND, CardboardAdventure::asAdventure, CardboardAdventure::asVanilla);
        DataComponentAdapters.register(DataComponentTypes.BANNER_PATTERNS, PaperBannerPatternLayers::new);
        DataComponentAdapters.register(DataComponentTypes.BASE_COLOR, nms -> org.bukkit.DyeColor.getByWoolData((byte)((byte)nms.getIndex())), api -> DyeColor.byId(api.getWoolData()));
        DataComponentAdapters.register(DataComponentTypes.POT_DECORATIONS, PaperPotDecorations::new);
        DataComponentAdapters.register(DataComponentTypes.CONTAINER, PaperItemContainerContents::new);
        DataComponentAdapters.register(DataComponentTypes.BLOCK_STATE, PaperBlockItemDataProperties::new);
        DataComponentAdapters.register(DataComponentTypes.CONTAINER_LOOT, PaperSeededContainerLoot::new);

        for (final Map.Entry<RegistryKey<ComponentType<?>>, ComponentType<?>> componentType : Registries.DATA_COMPONENT_TYPE.getEntrySet()) {
            if (!ADAPTERS.containsKey(componentType.getKey())) {
                registerUntyped((ComponentType<Unit>) componentType.getValue());
            }
        }
    }

    public static void registerUntyped(ComponentType<Unit> type) {
        DataComponentAdapters.registerInternal(type, UNIT_TO_API_CONVERTER, DataComponentAdapter.API_TO_UNIT_CONVERTER, false);
    }

    private static <COMMON> void registerIdentity(ComponentType<COMMON> type) {
        DataComponentAdapters.registerInternal(type, Function.identity(), Function.identity(), true);
    }

    private static <NMS, API extends Handleable<NMS>> void register(ComponentType<NMS> type, Function<NMS, API> vanillaToApi) {
        DataComponentAdapters.registerInternal(type, vanillaToApi, Handleable::getHandle, false);
    }

    private static <NMS, API> void register(ComponentType<NMS> type, Function<NMS, API> vanillaToApi, Function<API, NMS> apiToVanilla) {
        DataComponentAdapters.registerInternal(type, vanillaToApi, apiToVanilla, false);
    }

    private static <NMS, API> void registerInternal(final ComponentType<NMS> type, final Function<NMS, API> vanillaToApi, final Function<API, NMS> apiToVanilla, final boolean codecValidation) {
        final RegistryKey<ComponentType<?>> key = Registries.DATA_COMPONENT_TYPE.getKey(type).orElseThrow();
        if (ADAPTERS.containsKey(key)) {
            throw new IllegalStateException("Duplicate adapter registration for " + key);
        }
        ADAPTERS.put(key, new DataComponentAdapter<>(type, apiToVanilla, vanillaToApi, codecValidation && !type.shouldSkipSerialization()));
    }

}
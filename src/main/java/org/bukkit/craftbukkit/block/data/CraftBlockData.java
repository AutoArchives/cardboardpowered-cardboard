package org.bukkit.craftbukkit.block.data;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundGroup;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockSupport;
import org.bukkit.block.BlockType;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftSoundGroup;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.block.CraftBlockStates;
import org.bukkit.craftbukkit.block.CraftBlockSupport;
import org.bukkit.craftbukkit.block.CraftBlockType;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.util.CraftVoxelShape;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.VoxelShape;
import org.cardboardpowered.BlockImplUtil;
import org.cardboardpowered.impl.world.CraftWorld;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class CraftBlockData implements BlockData {

    private BlockState state;
    private Map<Property<?>, Comparable<?>> parsedStates;
    private static Map<String, CraftBlockData> stringDataCache;

    protected CraftBlockData() {
        throw new AssertionError("Template Constructor");
    }

    protected CraftBlockData(BlockState state) {
        this.state = state;
    }

    @Override
    public Material getMaterial() {
        return BlockImplUtil.MN.IgetMaterial(state.getBlock());
    }

    public BlockState getState() {
        return state;
    }

    /**
     * Get a given EnumProperty's value as its Bukkit counterpart.
     *
     * @param nms the NMS state to convert
     * @param bukkit the Bukkit class
     * @param <B> the type
     * @return the matching Bukkit type
     */
    protected <B extends Enum<B>> B get(EnumProperty<?> nms, Class<B> bukkit) {
        return toBukkit(state.getValue(nms), bukkit);
    }

    /**
     * Convert all values from the given EnumProperty to their appropriate
     * Bukkit counterpart.
     *
     * @param nms the NMS state to get values from
     * @param bukkit the bukkit class to convert the values to
     * @param <B> the bukkit class type
     * @return an immutable Set of values in their appropriate Bukkit type
     */
    protected <B extends Enum<B>> Set<B> getValues(EnumProperty<?> nms, Class<B> bukkit) {
        ImmutableSet.Builder<B> values = ImmutableSet.builder();

        for (Enum<?> e : nms.getPossibleValues())
            values.add(toBukkit(e, bukkit));

        return values.build();
    }

    /**
     * Set a given {@link EnumProperty} with the matching enum from Bukkit.
     *
     * @param nms the NMS EnumProperty to set
     * @param bukkit the matching Bukkit Enum
     * @param <B> the Bukkit type
     * @param <N> the NMS type
     */
    protected <B extends Enum<B>, N extends Enum<N> & StringRepresentable> void set(EnumProperty<N> nms, Enum<B> bukkit) {
        this.parsedStates = null;
        this.state = this.state.setValue(nms, toNMS(bukkit, nms.getValueClass()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public BlockData merge(BlockData data) {
        CraftBlockData craft = (CraftBlockData) data;
        Preconditions.checkArgument(craft.parsedStates != null, "Data not created via string parsing");
        Preconditions.checkArgument(this.state.getBlock() == craft.state.getBlock(), "States have different types (got %s, expected %s)", data, this);

        CraftBlockData clone = (CraftBlockData) this.clone();
        clone.parsedStates = null;

        for (Property parsed : craft.parsedStates.keySet()) clone.state = clone.state.setValue(parsed, craft.state.getValue(parsed));

        return clone;
    }

    @Override
    public boolean matches(BlockData data) {
        if (data == null) return false;
        if (!(data instanceof CraftBlockData)) return false;

        CraftBlockData craft = (CraftBlockData) data;
        if (this.state.getBlock() != craft.state.getBlock()) return false;

        // Fastpath an exact match
        boolean exactMatch = this.equals(data);

        // If that failed, do a merge and check
        if (!exactMatch && craft.parsedStates != null)
            return this.merge(data).equals(this);

        return exactMatch;
    }

    private static final Map<Class, BiMap<Enum<?>, Enum<?>>> classMappings = new HashMap<>();

    /**
     * Convert an NMS Enum (usually a EnumProperty) to its appropriate Bukkit
     * enum from the given class.
     *
     * @throws IllegalStateException if the Enum could not be converted
     */
    @SuppressWarnings("unchecked")
    private static <B extends Enum<B>> B toBukkit(Enum<?> nms, Class<B> bukkit) {
        Enum<?> converted;
        BiMap<Enum<?>, Enum<?>> nmsToBukkit = classMappings.get(nms.getClass());

        if (nmsToBukkit != null) {
            converted = nmsToBukkit.get(nms);
            if (converted != null)
                return (B) converted;
        }

        converted = (nms instanceof Direction) ? BlockImplUtil.notchToBlockFace((Direction) nms) : bukkit.getEnumConstants()[nms.ordinal()];

        Preconditions.checkState(converted != null, "Could not convert enum %s->%s", nms, bukkit);

        if (nmsToBukkit == null) {
            nmsToBukkit = HashBiMap.create();
            classMappings.put(nms.getClass(), nmsToBukkit);
        }

        nmsToBukkit.put(nms, converted);

        return (B) converted;
    }

    /**
     * Convert a given Bukkit enum to its matching NMS enum type.
     *
     * @param bukkit the Bukkit enum to convert
     * @param nms the NMS class
     * @return the matching NMS type
     * @throws IllegalStateException if the Enum could not be converted
     */
    @SuppressWarnings("unchecked")
    private static <N extends Enum<N> & StringRepresentable> N toNMS(Enum<?> bukkit, Class<N> nms) {
        Enum<?> converted;
        BiMap<Enum<?>, Enum<?>> nmsToBukkit = classMappings.get(nms);

        if (nmsToBukkit != null) {
            converted = nmsToBukkit.inverse().get(bukkit);
            if (converted != null)
                return (N) converted;
        }

        converted = (bukkit instanceof BlockFace) ? BlockImplUtil.blockFaceToNotch((BlockFace) bukkit) : nms.getEnumConstants()[bukkit.ordinal()];;

        Preconditions.checkState(converted != null, "Could not convert enum %s->%s", nms, bukkit);

        if (nmsToBukkit == null) {
            nmsToBukkit = HashBiMap.create();
            classMappings.put(nms, nmsToBukkit);
        }

        nmsToBukkit.put(converted, bukkit);

        return (N) converted;
    }

    /**
     * Get the current value of a given state.
     *
     * @param ibs the state to check
     * @param <T> the type
     * @return the current value of the given state
     */
    protected <T extends Comparable<T>> T get(Property<T> ibs) {
        // Straight integer or boolean getter
        return this.state.getValue(ibs);
    }

    /**
     * Set the specified state's value.
     *
     * @param ibs the state to set
     * @param v the new value
     * @param <T> the state's type
     * @param <V> the value's type. Must match the state's type.
     */
    public <T extends Comparable<T>, V extends T> void set(Property<T> ibs, V v) {
        // Straight integer or boolean setter
        this.parsedStates = null;
        this.state = this.state.setValue(ibs, v);
    }

    @Override
    public String getAsString() {
        return toString(state.getValues());
    }

    @Override
    public String getAsString(boolean hideUnspecified) {
        return (hideUnspecified && parsedStates != null) ? toString(parsedStates) : getAsString();
    }

    @Override
    public BlockData clone() {
        try {
            return (BlockData) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError("Clone not supported", ex);
        }
    }

    @Override
    public String toString() {
        return "CardboardBlockData{" + getAsString() + "}";
    }

    // Mimicked from BlockDataAbstract#toString()
    public String toString(Map<Property<?>, Comparable<?>> states) {
        StringBuilder stateString = new StringBuilder(BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString());

        if (!states.isEmpty()) {
            stateString.append('[');
            stateString.append(states.entrySet().stream().map(BlockStateBase.PROPERTY_ENTRY_TO_STRING_FUNCTION).collect(Collectors.joining(",")));
            stateString.append(']');
        }

        return stateString.toString();
    }

    @SuppressWarnings("rawtypes")
    @Deprecated // TODO: 1.20.6
    public CompoundTag toStates_old() {
        CompoundTag compound = new CompoundTag();

        for (Map.Entry<Property<?>, Comparable<?>> entry : state.getValues().entrySet()) {
            Property Property = (Property) entry.getKey();
            compound.putString(Property.getName(), Property.getName(entry.getValue()));
        }
        return compound;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, String> toStates() {
        HashMap<String, String> compound = new HashMap<String, String>();
        for (Map.Entry<Property<?>, Comparable<?>> entry : this.state.getValues().entrySet()) {
            Property iblockstate = (Property) entry.getKey();

            compound.put(iblockstate.getName(), iblockstate.getName(entry.getValue()));
        }
        return compound;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CraftBlockData && state.equals(((CraftBlockData) obj).state);
    }

    @Override
    public int hashCode() {
        return state.hashCode();
    }

    protected static BooleanProperty getBoolean(String name) {
        throw new AssertionError("Template Method");
    }

    protected static BooleanProperty getBoolean(String name, boolean optional) {
        throw new AssertionError("Template Method");
    }

    protected static EnumProperty<?> getEnum(String name) {
        throw new AssertionError("Template Method");
    }

    protected static IntegerProperty getInteger(String name) {
        throw new AssertionError("Template Method");
    }

    protected static BooleanProperty getBoolean(Class<? extends Block> block, String name) {
        return (BooleanProperty) getState(block, name, false);
    }

    protected static BooleanProperty getBoolean(Class<? extends Block> block, String name, boolean optional) {
        return (BooleanProperty) getState(block, name, optional);
    }

    protected static EnumProperty<?> getEnum(Class<? extends Block> block, String name) {
        return (EnumProperty<?>) getState(block, name, false);
    }

    protected static IntegerProperty getInteger(Class<? extends Block> block, String name) {
        return (IntegerProperty) getState(block, name, false);
    }

    /**
     * Get a specified {@link Property} from a given block's class with a
     * given name
     *
     * @param block the class to retrieve the state from
     * @param name the name of the state to retrieve
     * @param optional if the state can be null
     * @return the specified state or null
     * @throws IllegalStateException if the state is null and {@code optional}
     * is false.
     */
    private static Property<?> getState(Class<? extends Block> block, String name, boolean optional) {
        Property<?> state = null;

        for (Block instance : BuiltInRegistries.BLOCK) {
            if (instance.getClass() == block) {
                if (state == null) {
                    state = instance.getStateDefinition().getProperty(name);
                } else {
                    Property<?> newState = instance.getStateDefinition().getProperty(name);
                    Preconditions.checkState(state == newState, "State mistmatch %s,%s", state, newState);
                }
            }
        }

        Preconditions.checkState(optional || state != null, "Null state for %s,%s", block, name);

        return state;
    }

    protected static int getMin(IntegerProperty state) {
        return 0; // TODO auto-generated method stub
    }

    protected static int getMax(IntegerProperty state) {
        return Integer.MAX_VALUE; // TODO auto-generated method stub
    }

    private static final Map<Class<? extends Block>, Function<BlockState, CraftBlockData>> MAP = new HashMap<>();

    static {
        // Keep synchronized with Spigot
        register(net.minecraft.world.level.block.AnvilBlock.class, org.bukkit.craftbukkit.block.impl.CraftAnvil::new);
        register(net.minecraft.world.level.block.BambooStalkBlock.class, org.bukkit.craftbukkit.block.impl.CraftBamboo::new);
        register(net.minecraft.world.level.block.BannerBlock.class, org.bukkit.craftbukkit.block.impl.CraftBanner::new);
        register(net.minecraft.world.level.block.WallBannerBlock.class, org.bukkit.craftbukkit.block.impl.CraftBannerWall::new);
        register(net.minecraft.world.level.block.BarrelBlock.class, org.bukkit.craftbukkit.block.impl.CraftBarrel::new);
        register(net.minecraft.world.level.block.BedBlock.class, org.bukkit.craftbukkit.block.impl.CraftBed::new);
        register(net.minecraft.world.level.block.BeehiveBlock.class, org.bukkit.craftbukkit.block.impl.CraftBeehive::new);
        register(net.minecraft.world.level.block.BeetrootBlock.class, org.bukkit.craftbukkit.block.impl.CraftBeetroot::new);
        register(net.minecraft.world.level.block.BellBlock.class, org.bukkit.craftbukkit.block.impl.CraftBell::new);
        register(net.minecraft.world.level.block.BlastFurnaceBlock.class, org.bukkit.craftbukkit.block.impl.CraftBlastFurnace::new);
        register(net.minecraft.world.level.block.BrewingStandBlock.class, org.bukkit.craftbukkit.block.impl.CraftBrewingStand::new);
        register(net.minecraft.world.level.block.BubbleColumnBlock.class, org.bukkit.craftbukkit.block.impl.CraftBubbleColumn::new);
        register(net.minecraft.world.level.block.CactusBlock.class, org.bukkit.craftbukkit.block.impl.CraftCactus::new);
        register(net.minecraft.world.level.block.CakeBlock.class, org.bukkit.craftbukkit.block.impl.CraftCake::new);
        register(net.minecraft.world.level.block.CampfireBlock.class, org.bukkit.craftbukkit.block.impl.CraftCampfire::new);
        register(net.minecraft.world.level.block.CarrotBlock.class, org.bukkit.craftbukkit.block.impl.CraftCarrots::new);
        register(net.minecraft.world.level.block.AbstractCauldronBlock.class, org.bukkit.craftbukkit.block.impl.CraftCauldron::new);
        register(net.minecraft.world.level.block.ChainBlock.class, org.bukkit.craftbukkit.block.impl.CraftChain::new);
        register(net.minecraft.world.level.block.ChestBlock.class, org.bukkit.craftbukkit.block.impl.CraftChest::new);
        register(net.minecraft.world.level.block.TrappedChestBlock.class, org.bukkit.craftbukkit.block.impl.CraftChestTrapped::new);
        register(net.minecraft.world.level.block.ChorusFlowerBlock.class, org.bukkit.craftbukkit.block.impl.CraftChorusFlower::new);
        register(net.minecraft.world.level.block.ChorusPlantBlock.class, org.bukkit.craftbukkit.block.impl.CraftChorusFruit::new);
        register(net.minecraft.world.level.block.WallBlock.class, org.bukkit.craftbukkit.block.impl.CraftCobbleWall::new);
        register(net.minecraft.world.level.block.CocoaBlock.class, org.bukkit.craftbukkit.block.impl.CraftCocoa::new);
        register(net.minecraft.world.level.block.CommandBlock.class, org.bukkit.craftbukkit.block.impl.CraftCommand::new);
        register(net.minecraft.world.level.block.ComposterBlock.class, org.bukkit.craftbukkit.block.impl.CraftComposter::new);
        register(net.minecraft.world.level.block.ConduitBlock.class, org.bukkit.craftbukkit.block.impl.CraftConduit::new);
        register(net.minecraft.world.level.block.BaseCoralPlantBlock.class, org.bukkit.craftbukkit.block.impl.CraftCoralDead::new);
        register(net.minecraft.world.level.block.CoralFanBlock.class, org.bukkit.craftbukkit.block.impl.CraftCoralFan::new);
        register(net.minecraft.world.level.block.BaseCoralFanBlock.class, org.bukkit.craftbukkit.block.impl.CraftCoralFanAbstract::new);
        register(net.minecraft.world.level.block.CoralWallFanBlock.class, org.bukkit.craftbukkit.block.impl.CraftCoralFanWall::new);
        register(net.minecraft.world.level.block.BaseCoralWallFanBlock.class, org.bukkit.craftbukkit.block.impl.CraftCoralFanWallAbstract::new);
        register(net.minecraft.world.level.block.CoralPlantBlock.class, org.bukkit.craftbukkit.block.impl.CraftCoralPlant::new);
        register(net.minecraft.world.level.block.CropBlock.class, org.bukkit.craftbukkit.block.impl.CraftCrops::new);
        register(net.minecraft.world.level.block.DaylightDetectorBlock.class, org.bukkit.craftbukkit.block.impl.CraftDaylightDetector::new);
        register(net.minecraft.world.level.block.SnowyDirtBlock.class, org.bukkit.craftbukkit.block.impl.CraftDirtSnow::new);
        register(net.minecraft.world.level.block.DispenserBlock.class, org.bukkit.craftbukkit.block.impl.CraftDispenser::new);
        register(net.minecraft.world.level.block.DoorBlock.class, org.bukkit.craftbukkit.block.impl.CraftDoor::new);
        register(net.minecraft.world.level.block.DropperBlock.class, org.bukkit.craftbukkit.block.impl.CraftDropper::new);
        register(net.minecraft.world.level.block.RodBlock.class, org.bukkit.craftbukkit.block.impl.CraftEndRod::new);
        register(net.minecraft.world.level.block.EnderChestBlock.class, org.bukkit.craftbukkit.block.impl.CraftEnderChest::new);
        register(net.minecraft.world.level.block.EndPortalFrameBlock.class, org.bukkit.craftbukkit.block.impl.CraftEnderPortalFrame::new);
        register(net.minecraft.world.level.block.FenceBlock.class, org.bukkit.craftbukkit.block.impl.CraftFence::new);
        register(net.minecraft.world.level.block.FenceGateBlock.class, org.bukkit.craftbukkit.block.impl.CraftFenceGate::new);
        register(net.minecraft.world.level.block.FireBlock.class, org.bukkit.craftbukkit.block.impl.CraftFire::new);
        register(net.minecraft.world.level.block.StandingSignBlock.class, org.bukkit.craftbukkit.block.impl.CraftFloorSign::new);
        register(net.minecraft.world.level.block.LiquidBlock.class, org.bukkit.craftbukkit.block.impl.CraftFluids::new);
        register(net.minecraft.world.level.block.FurnaceBlock.class, org.bukkit.craftbukkit.block.impl.CraftFurnaceFurace::new);
        register(net.minecraft.world.level.block.GlazedTerracottaBlock.class, org.bukkit.craftbukkit.block.impl.CraftGlazedTerracotta::new);
        register(net.minecraft.world.level.block.GrassBlock.class, org.bukkit.craftbukkit.block.impl.CraftGrass::new);
        register(net.minecraft.world.level.block.GrindstoneBlock.class, org.bukkit.craftbukkit.block.impl.CraftGrindstone::new);
        register(net.minecraft.world.level.block.HayBlock.class, org.bukkit.craftbukkit.block.impl.CraftHay::new);
        register(net.minecraft.world.level.block.HopperBlock.class, org.bukkit.craftbukkit.block.impl.CraftHopper::new);
        register(net.minecraft.world.level.block.HugeMushroomBlock.class, org.bukkit.craftbukkit.block.impl.CraftHugeMushroom::new);
        register(net.minecraft.world.level.block.FrostedIceBlock.class, org.bukkit.craftbukkit.block.impl.CraftIceFrost::new);
        register(net.minecraft.world.level.block.IronBarsBlock.class, org.bukkit.craftbukkit.block.impl.CraftIronBars::new);
        register(net.minecraft.world.level.block.JigsawBlock.class, org.bukkit.craftbukkit.block.impl.CraftJigsaw::new);
        register(net.minecraft.world.level.block.JukeboxBlock.class, org.bukkit.craftbukkit.block.impl.CraftJukeBox::new);
        register(net.minecraft.world.level.block.KelpBlock.class, org.bukkit.craftbukkit.block.impl.CraftKelp::new);
        register(net.minecraft.world.level.block.LadderBlock.class, org.bukkit.craftbukkit.block.impl.CraftLadder::new);
        register(net.minecraft.world.level.block.LanternBlock.class, org.bukkit.craftbukkit.block.impl.CraftLantern::new);
        register(net.minecraft.world.level.block.LeavesBlock.class, org.bukkit.craftbukkit.block.impl.CraftLeaves::new);
        register(net.minecraft.world.level.block.LecternBlock.class, org.bukkit.craftbukkit.block.impl.CraftLectern::new);
        register(net.minecraft.world.level.block.LeverBlock.class, org.bukkit.craftbukkit.block.impl.CraftLever::new);
        register(net.minecraft.world.level.block.LoomBlock.class, org.bukkit.craftbukkit.block.impl.CraftLoom::new);
        register(net.minecraft.world.level.block.DetectorRailBlock.class, org.bukkit.craftbukkit.block.impl.CraftMinecartDetector::new);
        register(net.minecraft.world.level.block.RailBlock.class, org.bukkit.craftbukkit.block.impl.CraftMinecartTrack::new);
        register(net.minecraft.world.level.block.MyceliumBlock.class, org.bukkit.craftbukkit.block.impl.CraftMycel::new);
        register(net.minecraft.world.level.block.NetherWartBlock.class, org.bukkit.craftbukkit.block.impl.CraftNetherWart::new);
        register(net.minecraft.world.level.block.NoteBlock.class, org.bukkit.craftbukkit.block.impl.CraftNote::new);
        register(net.minecraft.world.level.block.ObserverBlock.class, org.bukkit.craftbukkit.block.impl.CraftObserver::new);
        register(net.minecraft.world.level.block.piston.PistonBaseBlock.class, org.bukkit.craftbukkit.block.impl.CraftPiston::new);
        register(net.minecraft.world.level.block.piston.PistonHeadBlock.class, org.bukkit.craftbukkit.block.impl.CraftPistonExtension::new);
        register(net.minecraft.world.level.block.piston.MovingPistonBlock.class, org.bukkit.craftbukkit.block.impl.CraftPistonMoving::new);
        register(net.minecraft.world.level.block.NetherPortalBlock.class, org.bukkit.craftbukkit.block.impl.CraftPortal::new);
        register(net.minecraft.world.level.block.PotatoBlock.class, org.bukkit.craftbukkit.block.impl.CraftPotatoes::new);
        register(net.minecraft.world.level.block.PoweredRailBlock.class, org.bukkit.craftbukkit.block.impl.CraftPoweredRail::new);
        register(net.minecraft.world.level.block.PressurePlateBlock.class, org.bukkit.craftbukkit.block.impl.CraftPressurePlateBinary::new);
        register(net.minecraft.world.level.block.WeightedPressurePlateBlock.class, org.bukkit.craftbukkit.block.impl.CraftPressurePlateWeighted::new);
        register(net.minecraft.world.level.block.CarvedPumpkinBlock.class, org.bukkit.craftbukkit.block.impl.CraftPumpkinCarved::new);
        register(net.minecraft.world.level.block.ComparatorBlock.class, org.bukkit.craftbukkit.block.impl.CraftRedstoneComparator::new);
        register(net.minecraft.world.level.block.RedstoneLampBlock.class, org.bukkit.craftbukkit.block.impl.CraftRedstoneLamp::new);
        register(net.minecraft.world.level.block.RedStoneOreBlock.class, org.bukkit.craftbukkit.block.impl.CraftRedstoneOre::new);
        register(net.minecraft.world.level.block.RedstoneTorchBlock.class, org.bukkit.craftbukkit.block.impl.CraftRedstoneTorch::new);
        register(net.minecraft.world.level.block.RedstoneWallTorchBlock.class, org.bukkit.craftbukkit.block.impl.CraftRedstoneTorchWall::new);
        register(net.minecraft.world.level.block.RedStoneWireBlock.class, org.bukkit.craftbukkit.block.impl.CraftRedstoneWire::new);
        register(net.minecraft.world.level.block.SugarCaneBlock.class, org.bukkit.craftbukkit.block.impl.CraftReed::new);
        register(net.minecraft.world.level.block.RepeaterBlock.class, org.bukkit.craftbukkit.block.impl.CraftRepeater::new);
        register(net.minecraft.world.level.block.RespawnAnchorBlock.class, org.bukkit.craftbukkit.block.impl.CraftRespawnAnchor::new);
        register(net.minecraft.world.level.block.RotatedPillarBlock.class, org.bukkit.craftbukkit.block.impl.CraftRotatable::new);
        register(net.minecraft.world.level.block.SaplingBlock.class, org.bukkit.craftbukkit.block.impl.CraftSapling::new);
        register(net.minecraft.world.level.block.ScaffoldingBlock.class, org.bukkit.craftbukkit.block.impl.CraftScaffolding::new);
        register(net.minecraft.world.level.block.SeaPickleBlock.class, org.bukkit.craftbukkit.block.impl.CraftSeaPickle::new);
        register(net.minecraft.world.level.block.ShulkerBoxBlock.class, org.bukkit.craftbukkit.block.impl.CraftShulkerBox::new);
        register(net.minecraft.world.level.block.SkullBlock.class, org.bukkit.craftbukkit.block.impl.CraftSkull::new);
        register(net.minecraft.world.level.block.PlayerHeadBlock.class, org.bukkit.craftbukkit.block.impl.CraftSkullPlayer::new);
        register(net.minecraft.world.level.block.PlayerWallHeadBlock.class, org.bukkit.craftbukkit.block.impl.CraftSkullPlayerWall::new);
        register(net.minecraft.world.level.block.WallSkullBlock.class, org.bukkit.craftbukkit.block.impl.CraftSkullWall::new);
        register(net.minecraft.world.level.block.SmokerBlock.class, org.bukkit.craftbukkit.block.impl.CraftSmoker::new);
        register(net.minecraft.world.level.block.SnowLayerBlock.class, org.bukkit.craftbukkit.block.impl.CraftSnow::new);
        register(net.minecraft.world.level.block.FarmBlock.class, org.bukkit.craftbukkit.block.impl.CraftSoil::new);
        register(net.minecraft.world.level.block.StainedGlassPaneBlock.class, org.bukkit.craftbukkit.block.impl.CraftStainedGlassPane::new);
        register(net.minecraft.world.level.block.StairBlock.class, org.bukkit.craftbukkit.block.impl.CraftStairs::new);
        register(net.minecraft.world.level.block.StemBlock.class, org.bukkit.craftbukkit.block.impl.CraftStem::new);
        register(net.minecraft.world.level.block.AttachedStemBlock.class, org.bukkit.craftbukkit.block.impl.CraftStemAttached::new);
        register(net.minecraft.world.level.block.SlabBlock.class, org.bukkit.craftbukkit.block.impl.CraftStepAbstract::new);
        // TODO: 1.19.4: register(net.minecraft.block.StoneButtonBlock.class, org.bukkit.craftbukkit.block.impl.CraftStoneButton::new);
        register(net.minecraft.world.level.block.StonecutterBlock.class, org.bukkit.craftbukkit.block.impl.CraftStonecutter::new);
        register(net.minecraft.world.level.block.StructureBlock.class, org.bukkit.craftbukkit.block.impl.CraftStructure::new);
        register(net.minecraft.world.level.block.SweetBerryBushBlock.class, org.bukkit.craftbukkit.block.impl.CraftSweetBerryBush::new);
        register(net.minecraft.world.level.block.TntBlock.class, org.bukkit.craftbukkit.block.impl.CraftTNT::new);
        register(net.minecraft.world.level.block.DoublePlantBlock.class, org.bukkit.craftbukkit.block.impl.CraftTallPlant::new);
        register(net.minecraft.world.level.block.TallFlowerBlock.class, org.bukkit.craftbukkit.block.impl.CraftTallPlantFlower::new);
        register(net.minecraft.world.level.block.TallSeagrassBlock.class, org.bukkit.craftbukkit.block.impl.CraftTallSeaGrass::new);
        register(net.minecraft.world.level.block.TargetBlock.class, org.bukkit.craftbukkit.block.impl.CraftTarget::new);
        register(net.minecraft.world.level.block.WallTorchBlock.class, org.bukkit.craftbukkit.block.impl.CraftTorchWall::new);
        register(net.minecraft.world.level.block.TrapDoorBlock.class, org.bukkit.craftbukkit.block.impl.CraftTrapdoor::new);
        register(net.minecraft.world.level.block.TripWireBlock.class, org.bukkit.craftbukkit.block.impl.CraftTripwire::new);
        register(net.minecraft.world.level.block.TripWireHookBlock.class, org.bukkit.craftbukkit.block.impl.CraftTripwireHook::new);
        register(net.minecraft.world.level.block.TurtleEggBlock.class, org.bukkit.craftbukkit.block.impl.CraftTurtleEgg::new);
        register(net.minecraft.world.level.block.TwistingVinesBlock.class, org.bukkit.craftbukkit.block.impl.CraftTwistingVines::new);
        register(net.minecraft.world.level.block.VineBlock.class, org.bukkit.craftbukkit.block.impl.CraftVine::new);
        register(net.minecraft.world.level.block.WallSignBlock.class, org.bukkit.craftbukkit.block.impl.CraftWallSign::new);
        register(net.minecraft.world.level.block.WeepingVinesBlock.class, org.bukkit.craftbukkit.block.impl.CraftWeepingVines::new);
        register(net.minecraft.world.level.block.WitherSkullBlock.class, org.bukkit.craftbukkit.block.impl.CraftWitherSkull::new);
        register(net.minecraft.world.level.block.WitherWallSkullBlock.class, org.bukkit.craftbukkit.block.impl.CraftWitherSkullWall::new);
        // TODO: 1.19.4: register(net.minecraft.block.WoodenButtonBlock.class, org.bukkit.craftbukkit.block.impl.CraftWoodButton::new);

        stringDataCache = new ConcurrentHashMap<String, CraftBlockData>();
        CraftBlockData.reloadCache();
        // TOODO 1.19: net.minecraft.block.Block.STATE_IDS.iterator().forEachRemaining(AbstractBlock.AbstractBlockState::createCraftBlockData);
    }

    public static void reloadCache() {
        stringDataCache.clear();
        // TODO: 1.19 net.minecraft.block.Block.STATE_IDS.forEach(blockData -> stringDataCache.put(blockData.toString(), blockData.createCraftBlockData()));
    }

    private static void register(Class<? extends Block> nms, Function<BlockState, CraftBlockData> bukkit) {
        Preconditions.checkState(MAP.put(nms, bukkit) == null, "Duplicate mapping %s->%s", nms, bukkit);
    }



    public static CraftBlockData newData(Material material, String data) {
        Preconditions.checkArgument(material == null || material.isBlock(), "Cannot get data for not block %s", material);

        BlockState blockData;
        Block block = CraftBlockType.bukkitToMinecraft(material);

        if (null == block) {
        	
        	/*if (material == Material.GRASS) {
        		// Old material
        		
        	}*/

        	System.out.println("BLOCK NULL! for " + material + " / " + data);
        }

        Map<net.minecraft.world.level.block.state.properties.Property<?>, Comparable<?>> parsed = null;

        // Data provided, use it
        if (data != null) {
            try {
                // Material provided, force that material in
                if (block != null) {
                    data = BuiltInRegistries.BLOCK.getKey(block) + data;
                }

                StringReader reader = new StringReader(data);
                BlockStateParser.BlockResult arg = BlockStateParser.parseForBlock(CraftRegistry.getMinecraftRegistry(Registries.BLOCK), reader, false);
                Preconditions.checkArgument(!reader.canRead(), "Spurious trailing data: " + data);

                blockData = arg.blockState();
                parsed = arg.properties();
            } catch (CommandSyntaxException ex) {
                throw new IllegalArgumentException("Could not parse data: " + data, ex);
            }
        } else {
            blockData = block.defaultBlockState();
        }

        CraftBlockData craft = fromData(blockData);
        craft.parsedStates = parsed;
        return craft;
    }

    @Deprecated
    public static CraftBlockData newData_old(Material material, String data) {
        net.minecraft.world.level.block.Block block;
        Preconditions.checkArgument((material == null || material.isBlock() ? 1 : 0) != 0, (String)"Cannot get data for not block %s", (Object)material);
        if (material != null && (block = CraftMagicNumbers.getBlock(material)) != null) {
            Identifier key = BuiltInRegistries.BLOCK.getKey(block);
            data = data == null ? key.toString() : key + (String)data;
        }
        CraftBlockData cached = stringDataCache.computeIfAbsent((String)data, s2 -> CraftBlockData.createNewData_old(null, s2));
        return (CraftBlockData)cached.clone();
    }

    private static CraftBlockData createNewData_old(Material material, String data) {
        BlockState blockData;
        net.minecraft.world.level.block.Block block = CraftMagicNumbers.getBlock(material);
        Map<Property<?>, Comparable<?>> parsed = null;
        if (data != null) {
            try {
                if (block != null) {
                    data = BuiltInRegistries.BLOCK.getKey(block) + (String)data;
                }
                StringReader reader = new StringReader((String)data);
                BlockStateParser.BlockResult arg = BlockStateParser.parseForBlock(CraftRegistry.getMinecraftRegistry(Registries.BLOCK), reader, false);
                Preconditions.checkArgument((!reader.canRead() ? 1 : 0) != 0, (Object)("Spurious trailing data: " + (String)data));
                blockData = arg.blockState();
                parsed = arg.properties();
            }
            catch (CommandSyntaxException ex) {
                throw new IllegalArgumentException("Could not parse data: " + (String)data, ex);
            }
        } else {
            blockData = block.defaultBlockState();
        }
        CraftBlockData craft = CraftBlockData.fromData(blockData);
        craft.parsedStates = parsed;
        return craft;
    }

    /*public static CraftBlockData newData_old(Material material, String data) {
        //Preconditions.checkArgument(material == null || material.isBlock(), "Cannot get data for not block %s", material);

        BlockState blockData;
        Block block = BlockImplUtil.MN.IgetBlock(material);

        Map<Property<?>, Comparable<?>> parsed = null;

        // Data provided, use it
        if (data != null) {
            try {
                // Material provided, force that material in
                if (block != null) data = Registry.BLOCK.getId(block) + data;

                StringReader reader = new StringReader(data);
                BlockArgumentParser arg = new BlockArgumentParser(reader, false).parse(false);
                Preconditions.checkArgument(!reader.canRead(), "Spurious trailing data: " + data);

                blockData = arg.getBlockState();
                parsed = arg.getBlockProperties();
            } catch (CommandSyntaxException ex) {
                throw new IllegalArgumentException("Could not parse data: " + data, ex);
            }
        } else blockData = block.getDefaultState();

        CraftBlockData craft = fromData(blockData);
        craft.parsedStates = parsed;
        return craft;
    }*/

    public static CraftBlockData fromData(BlockState data) {
        return MAP.getOrDefault(data.getBlock().getClass(), CraftBlockData::new).apply(data);
    }

    public static CraftBlockData createData(BlockState data) {
        return fromData(data);
    }

    @Override
    public SoundGroup getSoundGroup() {
        return CraftSoundGroup.getSoundGroup(state.getSoundType());
    }

	@Override
	public boolean isFaceSturdy(@NotNull BlockFace face, @NotNull BlockSupport support) {
		return state.isFaceSturdy(EmptyBlockGetter.INSTANCE, BlockPos.ZERO,
                CraftBlock.blockFaceToNotch(face),
                CraftBlockSupport.toNMS(support));
	}

    @Override
    public boolean isPreferredTool(ItemStack tool) {
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(tool);
        return isPreferredTool(state, nms);
    }

    public static boolean isPreferredTool(BlockState state, net.minecraft.world.item.ItemStack nmsItem) {
        return !state.requiresCorrectToolForDrops() || nmsItem.isCorrectToolForDrops(state);
    }

    @Override
    public boolean isRandomlyTicked() {
    	return state.isRandomlyTicking();
    }

    public boolean isReplaceable() {
    	return this.state.canBeReplaced();
    }

    @Override
    public boolean isSupported(org.bukkit.block.Block block) {
    	Preconditions.checkArgument(block != null, "block must not be null");

    	CraftBlock craftBlock = (CraftBlock) block;
    	return state.canSurvive(craftBlock.getCraftWorld().getHandle(), craftBlock.getPosition());
    }

    @Override
    public boolean isSupported(Location location) {
    	Preconditions.checkArgument(location != null, "location must not be null");

    	CraftWorld world = (CraftWorld) location.getWorld();
    	Preconditions.checkArgument(world != null, "location must not have a null world");



    	BlockPos position = CraftLocation.toBlockPosition(location);
    	return state.canSurvive(world.getHandle(), position);
    }

    @Override
    public int getLightEmission() {
    	return this.state.getLightEmission();
    }

    @Override
    public Material getPlacementMaterial() {
    	return CraftMagicNumbers.getMaterial(this.state.getBlock().asItem());
    }

    @Override
    public boolean isOccluding() {
    	return this.state.canOcclude();
    }

    @Override
    public void mirror(Mirror mirror) {
    	this.state = this.state.mirror(net.minecraft.world.level.block.Mirror.valueOf(mirror.name()));
    }

    @Override
    public boolean requiresCorrectToolForDrops() {
    	return this.state.requiresCorrectToolForDrops();
    }

    @Override
    public void rotate(StructureRotation rotation) {
    	this.state = this.state.rotate(Rotation.valueOf(rotation.name()));
    }

    @Override
    public PistonMoveReaction getPistonMoveReaction() {
    	return PistonMoveReaction.getById((int)this.state.getPistonPushReaction().ordinal());
    }

    @Override
    public org.bukkit.block.@NotNull BlockState createBlockState() {
    	return CraftBlockStates.getBlockState(this.state, null);
    }

    @Override
    public float getDestroySpeed(ItemStack itemStack, boolean considerEnchants) {
    	net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.unwrap(itemStack);
    	float speed = nmsItemStack.getDestroySpeed(this.state);
    	if (speed > 1.0f && considerEnchants) {
    		Holder<Attribute> attribute = Attributes.MINING_EFFICIENCY;
    		double initialBaseValue = attribute.value().getDefaultValue();
    		MutableDouble modifiedBaseValue = new MutableDouble(initialBaseValue);
    		MutableDouble baseValMul = new MutableDouble(1.0);
    		MutableDouble totalValMul = new MutableDouble(1.0);
    		EnchantmentHelper.forEachModifier(nmsItemStack, EquipmentSlot.MAINHAND, (attributeHolder, attributeModifier) -> {
    			switch (attributeModifier.operation()) {
    			case ADD_VALUE: {
    				modifiedBaseValue.add(attributeModifier.amount());
    				break;
    			}
    			case ADD_MULTIPLIED_BASE: {
    				baseValMul.add(attributeModifier.amount());
    				break;
    			}
    			case ADD_MULTIPLIED_TOTAL: {
    				totalValMul.setValue(totalValMul.doubleValue() * (1.0 + attributeModifier.amount()));
    			}
    			}
    		});
    		double actualModifier = modifiedBaseValue.doubleValue() * baseValMul.doubleValue() * totalValMul.doubleValue();
    		speed += (float)attribute.value().sanitizeValue(actualModifier);
    	}
    	return speed;
    }

    // 1.20.2 API
    @Override
    public org.bukkit.util.VoxelShape getCollisionShape(Location location) {
    	Preconditions.checkArgument((location != null ? 1 : 0) != 0, "location must not be null");
    	CraftWorld world = (CraftWorld)location.getWorld();
    	Preconditions.checkArgument((world != null ? 1 : 0) != 0, "location must not have a null world");
    	BlockPos position = CraftLocation.toBlockPosition(location);
    	net.minecraft.world.phys.shapes.VoxelShape shape = this.state.getCollisionShape(world.getHandle(), position);
    	return new CraftVoxelShape(shape);
    }

    // 1.20.4 API

    @Override
    public @NotNull Color getMapColor() {
    	return Color.fromRGB((int)this.state.getMapColor(null, null).col);
    }

    @Override
    public void copyTo(@NotNull BlockData blockData) {
    	CraftBlockData other = (CraftBlockData)blockData;
    	net.minecraft.world.level.block.state.BlockState nms = other.state;
    	for (Property<?> property : this.state.getBlock().getStateDefinition().getProperties()) {
    		if (!nms.hasProperty(property)) continue;
    		nms = this.copyProperty(this.state, nms, property);
    	}
    	other.state = nms;
    }

    private <T extends Comparable<T>> net.minecraft.world.level.block.state.BlockState copyProperty(net.minecraft.world.level.block.state.BlockState source, net.minecraft.world.level.block.state.BlockState target, Property<T> property) {
    	return (net.minecraft.world.level.block.state.BlockState)target.setValue(property, source.getValue(property));
    }

    public static CraftBlockData newData(BlockType blockType, String data) {
    	net.minecraft.world.level.block.Block block;
    	if (blockType != null && (block = CraftBlockType.bukkitToMinecraftNew(blockType)) != null) {
    		Identifier key = BuiltInRegistries.BLOCK.getKey(block);
    		data = data == null ? key.toString() : String.valueOf(key) + data;
    	}
    	CraftBlockData cached = stringDataCache.computeIfAbsent(data, s -> CraftBlockData.createNewData(null, s));
    	return (CraftBlockData) cached.clone();
    }

    private static CraftBlockData createNewData(BlockType blockType, String data) {
    	net.minecraft.world.level.block.state.BlockState blockData;
    	net.minecraft.world.level.block.Block block = blockType == null ? null : ((CraftBlockType)blockType).getHandle();
    	Map<Property<?>, Comparable<?>> parsed = null;
    	if (data != null) {
    		try {
    			if (block != null) {
    				data = String.valueOf(BuiltInRegistries.BLOCK.getKey(block)) + (String)data;
    			}
    			StringReader reader = new StringReader((String)data);
    			BlockStateParser.BlockResult arg = BlockStateParser.parseForBlock(CraftRegistry.getMinecraftRegistry(Registries.BLOCK), reader, false);
    			Preconditions.checkArgument((!reader.canRead() ? 1 : 0) != 0, (Object)("Spurious trailing data: " + (String)data));
    			blockData = arg.blockState();
    			parsed = arg.properties();
    		}
    		catch (CommandSyntaxException ex) {
    			throw new IllegalArgumentException("Could not parse data: " + (String)data, ex);
    		}
    	} else {
    		blockData = block.defaultBlockState();
    	}
    	CraftBlockData craft = CraftBlockData.fromData(blockData);
    	craft.parsedStates = parsed;
    	return craft;
    }

}

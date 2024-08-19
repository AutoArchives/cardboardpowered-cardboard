package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapColorComponent;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.component.type.MapPostProcessingComponent;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.inventory.CraftMetaItem;
import org.bukkit.craftbukkit.inventory.SerializableMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

@DelegateDeserialization(value=SerializableMeta.class)
class CraftMetaMap
extends CraftMetaItem
implements MapMeta {
    static final CraftMetaItem.ItemMetaKeyType<MapPostProcessingComponent> MAP_POST_PROCESSING = new CraftMetaItem.ItemMetaKeyType<MapPostProcessingComponent>(DataComponentTypes.MAP_POST_PROCESSING);
    static final CraftMetaItem.ItemMetaKey MAP_SCALING = new CraftMetaItem.ItemMetaKey("scaling");
    @Deprecated
    static final CraftMetaItem.ItemMetaKey MAP_LOC_NAME = new CraftMetaItem.ItemMetaKey("display-loc-name");
    static final CraftMetaItem.ItemMetaKeyType<MapColorComponent> MAP_COLOR = new CraftMetaItem.ItemMetaKeyType<MapColorComponent>(DataComponentTypes.MAP_COLOR, "display-map-color");
    static final CraftMetaItem.ItemMetaKeyType<MapIdComponent> MAP_ID = new CraftMetaItem.ItemMetaKeyType<MapIdComponent>(DataComponentTypes.MAP_ID, "map-id");
    static final byte SCALING_EMPTY = 0;
    static final byte SCALING_TRUE = 1;
    static final byte SCALING_FALSE = 2;
    private Integer mapId;
    private byte scaling = 0;
    private Color color;

    CraftMetaMap(CraftMetaItem meta) {
        super(meta);
        if (!(meta instanceof CraftMetaMap)) {
            return;
        }
        CraftMetaMap map = (CraftMetaMap)meta;
        this.mapId = map.mapId;
        this.scaling = map.scaling;
        this.color = map.color;
    }

    CraftMetaMap(ComponentChanges tag, Set<DataComponentType<?>> extraHandledDcts) {
        super(tag, extraHandledDcts);
        CraftMetaMap.getOrEmpty(tag, MAP_ID).ifPresent(mapId -> {
            this.mapId = mapId.id();
        });
        CraftMetaMap.getOrEmpty(tag, MAP_POST_PROCESSING).ifPresent(mapPostProcessing -> {
            this.scaling = (byte)(mapPostProcessing == MapPostProcessingComponent.SCALE ? 1 : 2);
        });
        CraftMetaMap.getOrEmpty(tag, MAP_COLOR).ifPresent(mapColor -> {
            try {
                this.color = Color.fromRGB((int)mapColor.rgb());
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        });
    }

    CraftMetaMap(Map<String, Object> map) {
        super(map);
        Color color;
        String locName;
        Boolean scaling;
        Integer id = SerializableMeta.getObject(Integer.class, map, CraftMetaMap.MAP_ID.BUKKIT, true);
        if (id != null) {
            this.setMapId(id);
        }
        if ((scaling = SerializableMeta.getObject(Boolean.class, map, CraftMetaMap.MAP_SCALING.BUKKIT, true)) != null) {
            this.setScaling(scaling);
        }
        if ((locName = SerializableMeta.getString(map, CraftMetaMap.MAP_LOC_NAME.BUKKIT, true)) != null) {
            this.setLocationName(locName);
        }
        if ((color = SerializableMeta.getObject(Color.class, map, CraftMetaMap.MAP_COLOR.BUKKIT, true)) != null) {
            this.setColor(color);
        }
    }

    @Override
    void applyToItem(CraftMetaItem.Applicator tag) {
        super.applyToItem(tag);
        if (this.hasMapId()) {
            tag.put(MAP_ID, new MapIdComponent(this.getMapId()));
        }
        if (this.hasScaling()) {
            tag.put(MAP_POST_PROCESSING, this.isScaling() ? MapPostProcessingComponent.SCALE : MapPostProcessingComponent.LOCK);
        }
        if (this.hasColor()) {
            tag.put(MAP_COLOR, new MapColorComponent(this.color.asRGB()));
        }
    }

    @Override
    boolean applicableTo(Material type) {
        return type == Material.FILLED_MAP;
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && this.isMapEmpty();
    }

    boolean isMapEmpty() {
        return !this.hasMapId() && !(this.hasScaling() | this.hasLocationName()) && !this.hasColor();
    }

    public boolean hasMapId() {
        return this.mapId != null;
    }

    public int getMapId() {
        Preconditions.checkState((boolean)this.hasMapView(), "Item does not have map associated - check hasMapView() first!");
        return this.mapId;
    }

    public void setMapId(int id) {
        this.mapId = id;
    }

    public boolean hasMapView() {
        return this.mapId != null;
    }

    public MapView getMapView() {
        Preconditions.checkState((boolean)this.hasMapView(), "Item does not have map associated - check hasMapView() first!");
        return Bukkit.getMap((int)this.mapId);
    }

    public void setMapView(MapView map) {
        this.mapId = map != null ? Integer.valueOf(map.getId()) : null;
    }

    boolean hasScaling() {
        return this.scaling != 0;
    }

    public boolean isScaling() {
        return this.scaling == 1;
    }

    public void setScaling(boolean scaling) {
        this.scaling = (byte)(scaling ? 1 : 2);
    }

    public boolean hasLocationName() {
        return this.hasLocalizedName();
    }

    public String getLocationName() {
        return this.getLocalizedName();
    }

    public void setLocationName(String name) {
        this.setLocalizedName(name);
    }

    public boolean hasColor() {
        return this.color != null;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    boolean equalsCommon(CraftMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof CraftMetaMap) {
            CraftMetaMap that = (CraftMetaMap)meta;
            return this.scaling == that.scaling && (this.hasMapId() ? that.hasMapId() && this.mapId.equals(that.mapId) : !that.hasMapId()) && (this.hasColor() ? that.hasColor() && this.color.equals(that.color) : !that.hasColor());
        }
        return true;
    }

    @Override
    boolean notUncommon(CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaMap || this.isMapEmpty());
    }

    @Override
    int applyHash() {
        int original;
        int hash = original = super.applyHash();
        if (this.hasMapId()) {
            hash = 61 * hash + this.mapId.hashCode();
        }
        if (this.hasScaling()) {
            hash ^= 0x22222222 << (this.isScaling() ? 1 : -1);
        }
        if (this.hasColor()) {
            hash = 61 * hash + this.color.hashCode();
        }
        return original != hash ? CraftMetaMap.class.hashCode() ^ hash : hash;
    }

    @Override
    public CraftMetaMap clone() {
        return (CraftMetaMap)super.clone();
    }

    @Override
    ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
        super.serialize(builder);
        if (this.hasMapId()) {
            builder.put(CraftMetaMap.MAP_ID.BUKKIT, this.getMapId());
        }
        if (this.hasScaling()) {
            builder.put(CraftMetaMap.MAP_SCALING.BUKKIT, this.isScaling());
        }
        if (this.hasColor()) {
            builder.put(CraftMetaMap.MAP_COLOR.BUKKIT, this.getColor());
        }
        return builder;
    }
}


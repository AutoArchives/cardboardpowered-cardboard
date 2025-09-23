package org.bukkit.craftbukkit;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import net.minecraft.entity.Entity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.TintedParticleEffect;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.SculkChargeParticleEffect;
import net.minecraft.particle.ShriekParticleEffect;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.particle.TrailParticleEffect;
import net.minecraft.particle.VibrationParticleEffect;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.EntityPositionSource;
import net.minecraft.world.event.PositionSource;
import org.bukkit.Color;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.Vibration;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.legacy.FieldRename;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public abstract class CraftParticle<D> implements Keyed {

	/*
	 public static ParticleEffect toNMS(Particle bukkit) {
	        return toNMS(bukkit, null);
	    }

	    @SuppressWarnings({ "unchecked", "rawtypes" })
	    public static <T> ParticleEffect toNMS(Particle particle, T obj) {
	        Particle canonical = particle;
	        if (aliases.containsKey(particle))
	            canonical = aliases.get(particle);

	        net.minecraft.particle.ParticleType nms = Registries.PARTICLE_TYPE.get(particles.get(canonical));
	        Preconditions.checkArgument(nms != null, "No NMS particle %s", particle);

	        if (particle.getDataType().equals(Void.class)) {
	        	
	        	// <= 1.20.4 = DefaultParticleType
	        	// >= 1.20.5 = SimpleParticleType
	        	
	            return (SimpleParticleType) nms;
	        }

	        Preconditions.checkArgument(obj != null, "Particle %s requires data, null provided", particle);
	        if (particle.getDataType().equals(ItemStack.class)) {
	            ItemStack itemStack = (ItemStack) obj;
	            return new ItemStackParticleEffect((net.minecraft.particle.ParticleType<ItemStackParticleEffect>) nms, CraftItemStack.asNMSCopy(itemStack));
	        }
	        if (particle.getDataType() == MaterialData.class) {
	            MaterialData data = (MaterialData) obj;
	            return new BlockStateParticleEffect((net.minecraft.particle.ParticleType<BlockStateParticleEffect>) nms, CraftMagicNumbers.getBlock(data));
	        }
	        if (particle.getDataType() == BlockData.class) {
	            BlockData data = (BlockData) obj;
	            return new BlockStateParticleEffect((net.minecraft.particle.ParticleType<BlockStateParticleEffect>) nms, ((CraftBlockData) data).getState());
	        }
	        if (particle.getDataType() == Particle.DustOptions.class) {
	            Particle.DustOptions data = (Particle.DustOptions) obj;
	            Color color = data.getColor();
	            
	            // TODO 1.17ify: return new DustParticleEffect(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, data.getSize());
	        }
	        throw new IllegalArgumentException(particle.getDataType().toString());
	    }

	    public static Particle toBukkit(net.minecraft.particle.ParticleEffect nms) {
	        return toBukkit(nms.getType());
	    }

	    @SuppressWarnings("rawtypes")
	    public static Particle toBukkit(net.minecraft.particle.ParticleType nms) {
	        return particles.inverse().get(Registries.PARTICLE_TYPE.getId(nms));
	    }
*/
	
    private static final Registry<CraftParticle<?>> CRAFT_PARTICLE_REGISTRY = new CraftParticleRegistry(CraftRegistry.getMinecraftRegistry(RegistryKeys.PARTICLE_TYPE));

    public static Particle minecraftToBukkit(net.minecraft.particle.ParticleType<?> minecraft) {
        Preconditions.checkArgument(minecraft != null);

        net.minecraft.registry.Registry<net.minecraft.particle.ParticleType<?>> registry = CraftRegistry.getMinecraftRegistry(RegistryKeys.PARTICLE_TYPE);
        Particle bukkit = Registry.PARTICLE_TYPE.get(CraftNamespacedKey.fromMinecraft(registry.getKey(minecraft).orElseThrow().getValue()));

        Preconditions.checkArgument(bukkit != null);

        return bukkit;
    }

    public static net.minecraft.particle.ParticleType<?> bukkitToMinecraft(Particle bukkit) {
        Preconditions.checkArgument(bukkit != null);

        return CraftRegistry.getMinecraftRegistry(RegistryKeys.PARTICLE_TYPE)
                .getOptionalValue(CraftNamespacedKey.toMinecraft(bukkit.getKey())).orElseThrow();
    }

    public static <D> ParticleEffect createParticleParam(Particle particle, D data) {
        Preconditions.checkArgument(particle != null, "particle cannot be null");

        data = CraftParticle.convertLegacy(data);
        if (particle.getDataType() != Void.class) {
            Preconditions.checkArgument(data != null, "missing required data %s", particle.getDataType());
        }
        if (data != null) {
            Preconditions.checkArgument(particle.getDataType().isInstance(data), "data (%s) should be %s", data.getClass(), particle.getDataType());
        }

        CraftParticle<D> craftParticle = (CraftParticle<D>) CraftParticle.CRAFT_PARTICLE_REGISTRY.get(particle.getKey());

        Preconditions.checkArgument(craftParticle != null);

        return craftParticle.createParticleParam(data);
    }

    public static <T> T convertLegacy(T object) {
        if (object instanceof MaterialData mat) {
            return (T) CraftBlockData.fromData(CraftMagicNumbers.getBlock(mat));
        }

        return object;
    }

    private final NamespacedKey key;
    private final net.minecraft.particle.ParticleType<?> particle;
    private final Class<D> clazz;

    public CraftParticle(NamespacedKey key, net.minecraft.particle.ParticleType<?> particle, Class<D> clazz) {
        this.key = key;
        this.particle = particle;
        this.clazz = clazz;
    }

    public net.minecraft.particle.ParticleType<?> getHandle() {
        return this.particle;
    }

    public abstract ParticleEffect createParticleParam(D data);

    @Override
    public NamespacedKey getKey() {
        return this.key;
    }

    public static class CraftParticleRegistry extends CraftRegistry<CraftParticle<?>, net.minecraft.particle.ParticleType<?>> {

        private static final Map<NamespacedKey, BiFunction<NamespacedKey, net.minecraft.particle.ParticleType<?>, CraftParticle<?>>> PARTICLE_MAP = new HashMap<>();

        private static final BiFunction<NamespacedKey, net.minecraft.particle.ParticleType<?>, CraftParticle<?>> VOID_FUNCTION = (name, particle) -> new CraftParticle<>(name, particle, Void.class) {
            @Override
            public ParticleEffect createParticleParam(Void data) {
                return (SimpleParticleType) this.getHandle();
            }
        };

        static {
            BiFunction<NamespacedKey, net.minecraft.particle.ParticleType<?>, CraftParticle<?>> dustOptionsFunction = (name, particle) -> new CraftParticle<>(name, particle, Particle.DustOptions.class) {
                @Override
                public ParticleEffect createParticleParam(Particle.DustOptions data) {
                    Color color = data.getColor();
                    return new DustParticleEffect(color.asRGB(), data.getSize());
                }
            };

            BiFunction<NamespacedKey, net.minecraft.particle.ParticleType<?>, CraftParticle<?>> itemStackFunction = (name, particle) -> new CraftParticle<>(name, particle, ItemStack.class) {
                @Override
                public ParticleEffect createParticleParam(ItemStack data) {
                    return new ItemStackParticleEffect((net.minecraft.particle.ParticleType<ItemStackParticleEffect>) this.getHandle(), CraftItemStack.asNMSCopy(data));
                }
            };

            BiFunction<NamespacedKey, net.minecraft.particle.ParticleType<?>, CraftParticle<?>> blockDataFunction = (name, particle) -> new CraftParticle<>(name, particle, BlockData.class) {
                @Override
                public ParticleEffect createParticleParam(BlockData data) {
                    return new BlockStateParticleEffect((net.minecraft.particle.ParticleType<BlockStateParticleEffect>) this.getHandle(), ((CraftBlockData) data).getState());
                }
            };

            BiFunction<NamespacedKey, net.minecraft.particle.ParticleType<?>, CraftParticle<?>> dustTransitionFunction = (name, particle) -> new CraftParticle<>(name, particle, Particle.DustTransition.class) {
                @Override
                public ParticleEffect createParticleParam(Particle.DustTransition data) {
                    Color from = data.getColor();
                    Color to = data.getToColor();
                    return new DustColorTransitionParticleEffect(from.asRGB(), to.asRGB(), data.getSize());
                }
            };

            BiFunction<NamespacedKey, net.minecraft.particle.ParticleType<?>, CraftParticle<?>> vibrationFunction = (name, particle) -> new CraftParticle<>(name, particle, Vibration.class) {
                @Override
                public ParticleEffect createParticleParam(Vibration data) {
                    PositionSource source;
                    if (data.getDestination() instanceof Vibration.Destination.BlockDestination) {
                        Location destination = ((Vibration.Destination.BlockDestination) data.getDestination()).getLocation();
                        source = new BlockPositionSource(CraftLocation.toBlockPosition(destination));
                    } else if (data.getDestination() instanceof Vibration.Destination.EntityDestination) {
                        Entity destination = ((CraftEntity) ((Vibration.Destination.EntityDestination) data.getDestination()).getEntity()).getHandle();
                        source = new EntityPositionSource(destination, destination.getStandingEyeHeight());
                    } else {
                        throw new IllegalArgumentException("Unknown vibration destination " + data.getDestination());
                    }

                    return new VibrationParticleEffect(source, data.getArrivalTime());
                }
            };

            BiFunction<NamespacedKey, net.minecraft.particle.ParticleType<?>, CraftParticle<?>> floatFunction = (name, particle) -> new CraftParticle<>(name, particle, Float.class) {
                @Override
                public ParticleEffect createParticleParam(Float data) {
                    return new SculkChargeParticleEffect(data);
                }
            };

            BiFunction<NamespacedKey, net.minecraft.particle.ParticleType<?>, CraftParticle<?>> integerFunction = (name, particle) -> new CraftParticle<>(name, particle, Integer.class) {
                @Override
                public ParticleEffect createParticleParam(Integer data) {
                    return new ShriekParticleEffect(data);
                }
            };

            BiFunction<NamespacedKey, net.minecraft.particle.ParticleType<?>, CraftParticle<?>> colorFunction = (name, particle) -> new CraftParticle<>(name, particle, Color.class) {
                @Override
                public ParticleEffect createParticleParam(Color color) {
                    return TintedParticleEffect.create((net.minecraft.particle.ParticleType<TintedParticleEffect>) particle, color.asARGB());
                }
            };

            /*
            BiFunction<NamespacedKey, net.minecraft.particle.ParticleType<?>, CraftParticle<?>> trailFunction = (name, particle) -> new CraftParticle<>(name, particle, Particle.Trail.class) {
                @Override
                public ParticleEffect createParticleParam(Particle.Trail data) {
                    return new TrailParticleEffect(CraftLocation.toVec3D(data.getTarget()), data.getColor().asRGB(), data.getDuration());
                }
            };
            */

            add("dust", dustOptionsFunction);
            add("item", itemStackFunction);
            add("block", blockDataFunction);
            add("falling_dust", blockDataFunction);
            add("dust_color_transition", dustTransitionFunction);
            add("vibration", vibrationFunction);
            add("sculk_charge", floatFunction);
            add("shriek", integerFunction);
            add("block_marker", blockDataFunction);
            add("entity_effect", colorFunction);
            add("dust_pillar", blockDataFunction);
            add("block_crumble", blockDataFunction);
            // add("trail", trailFunction);
        }

        private static void add(String name, BiFunction<NamespacedKey, net.minecraft.particle.ParticleType<?>, CraftParticle<?>> function) {
            CraftParticleRegistry.PARTICLE_MAP.put(NamespacedKey.fromString(name), function);
        }

        public CraftParticleRegistry(net.minecraft.registry.Registry<net.minecraft.particle.ParticleType<?>> minecraftRegistry) {
            super(CraftParticle.class, minecraftRegistry, CraftParticleRegistry::createBukkit, FieldRename.PARTICLE_TYPE_RENAME); // Paper - switch to Holder
        }

        public static CraftParticle<?> createBukkit(NamespacedKey namespacedKey, net.minecraft.particle.ParticleType<?> particle) { // Paper - idk why this is a separate implementation, just wrap the function
            if (particle == null) {
                return null;
            }

            BiFunction<NamespacedKey, net.minecraft.particle.ParticleType<?>, CraftParticle<?>> function = CraftParticleRegistry.PARTICLE_MAP.getOrDefault(namespacedKey, CraftParticleRegistry.VOID_FUNCTION);

            return function.apply(namespacedKey, particle);
        }
    }
}

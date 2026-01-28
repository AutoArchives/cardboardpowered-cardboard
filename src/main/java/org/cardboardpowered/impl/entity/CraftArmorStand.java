package org.cardboardpowered.impl.entity;

import java.util.Set;

import com.google.common.base.Preconditions;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftServer;
import org.jetbrains.annotations.NotNull;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import com.destroystokyo.paper.block.TargetBlockInfo;
import com.destroystokyo.paper.block.TargetBlockInfo.FluidMode;
import com.destroystokyo.paper.entity.TargetEntityInfo;
import org.cardboardpowered.bridge.world.entity.decoration.ArmorStandBridge;

import io.papermc.paper.math.Rotations;

@SuppressWarnings("deprecation")
public class CraftArmorStand extends LivingEntityImpl implements ArmorStand {

    public CraftArmorStand(CraftServer server, net.minecraft.world.entity.decoration.ArmorStand entity) {
        super(server, entity);
    }

    @Override
    public String toString() {
        return "ArmorStand";
    }

    @Override
    public EntityType getType() {
        return EntityType.ARMOR_STAND;
    }

    @Override
    public net.minecraft.world.entity.decoration.ArmorStand getHandle() {
        return (net.minecraft.world.entity.decoration.ArmorStand) super.getHandle();
    }

    @Override
    public ItemStack getItemInHand() {
        return getEquipment().getItemInHand();
    }

    @Override
    public void setItemInHand(ItemStack item) {
        getEquipment().setItemInHand(item);
    }

    @Override
    public ItemStack getBoots() {
        return getEquipment().getBoots();
    }

    @Override
    public void setBoots(ItemStack item) {
        getEquipment().setBoots(item);
    }

    @Override
    public ItemStack getLeggings() {
        return getEquipment().getLeggings();
    }

    @Override
    public void setLeggings(ItemStack item) {
        getEquipment().setLeggings(item);
    }

    @Override
    public ItemStack getChestplate() {
        return getEquipment().getChestplate();
    }

    @Override
    public void setChestplate(ItemStack item) {
        getEquipment().setChestplate(item);
    }

    @Override
    public ItemStack getHelmet() {
        return getEquipment().getHelmet();
    }

    @Override
    public void setHelmet(ItemStack item) {
        getEquipment().setHelmet(item);
    }

    @Override
    public EulerAngle getBodyPose() {
        return fromNMS(getHandle().getBodyPose());
    }

    @Override
    public void setBodyPose(EulerAngle pose) {
        getHandle().setBodyPose(toNMS(pose));
    }

    @Override
    public EulerAngle getLeftArmPose() {
        return fromNMS(getHandle().getLeftArmPose());
    }

    @Override
    public void setLeftArmPose(EulerAngle pose) {
        getHandle().setLeftArmPose(toNMS(pose));
    }

    @Override
    public EulerAngle getRightArmPose() {
        return fromNMS(getHandle().getRightArmPose());
    }

    @Override
    public void setRightArmPose(EulerAngle pose) {
        getHandle().setRightArmPose(toNMS(pose));
    }

    @Override
    public EulerAngle getLeftLegPose() {
        return fromNMS(getHandle().getLeftLegPose());
    }

    @Override
    public void setLeftLegPose(EulerAngle pose) {
        getHandle().setLeftLegPose(toNMS(pose));
    }

    @Override
    public EulerAngle getRightLegPose() {
        return fromNMS(getHandle().getRightLegPose());
    }

    @Override
    public void setRightLegPose(EulerAngle pose) {
        getHandle().setRightLegPose(toNMS(pose));
    }

    @Override
    public EulerAngle getHeadPose() {
        return fromNMS(getHandle().getHeadPose());
    }

    @Override
    public void setHeadPose(EulerAngle pose) {
        getHandle().setHeadPose(toNMS(pose));
    }

    @Override
    public boolean hasBasePlate() {
        return getHandle().showBasePlate();
    }

    @Override
    public void setBasePlate(boolean basePlate) {
        ((ArmorStandBridge)getHandle()).setHideBasePlateBF(!basePlate);
    }

    @Override
    public void setGravity(boolean gravity) {
        super.setGravity(gravity);
        // Armor stands are special
        getHandle().noPhysics = !gravity;
    }

    @Override
    public boolean isVisible() {
        return !getHandle().isInvisible();
    }

    @Override
    public void setVisible(boolean visible) {
        getHandle().setInvisible(!visible);
    }

    @Override
    public boolean hasArms() {
        return getHandle().showArms();
    }

    @Override
    public void setArms(boolean arms) {
        ((ArmorStandBridge)getHandle()).setShowArmsBF(arms);
    }

    @Override
    public boolean isSmall() {
        return getHandle().isSmall();
    }

    @Override
    public void setSmall(boolean small) {
        ((ArmorStandBridge)getHandle()).setSmallBF(small);
    }

    private static EulerAngle fromNMS(net.minecraft.core.Rotations old) {
        return new EulerAngle(
            Math.toRadians(old.x()),
            Math.toRadians(old.y()),
            Math.toRadians(old.z())
        );
    }

    private static net.minecraft.core.Rotations toNMS(EulerAngle old) {
        return new net.minecraft.core.Rotations(
            (float) Math.toDegrees(old.getX()),
            (float) Math.toDegrees(old.getY()),
            (float) Math.toDegrees(old.getZ())
        );
    }

    @Override
    public boolean isMarker() {
        return getHandle().isMarker();
    }

    @Override
    public void setMarker(boolean marker) {
        ((ArmorStandBridge)getHandle()).setMarkerBF(marker);
    }

    @Override
    public void addEquipmentLock(EquipmentSlot slot, LockType lockType) {
        // TODO Auto-generated method stub
    }

    @Override
    public void removeEquipmentLock(EquipmentSlot slot, LockType lockType) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean hasEquipmentLock(EquipmentSlot slot, LockType lockType) {
        // TODO Auto-generated method stub
        return false;
    }

    // Paper - START
    public boolean canMove() {
        return ((ArmorStandBridge)getHandle()).canMoveBF();
    }

    public void setCanMove(boolean move) {
        ((ArmorStandBridge)getHandle()).setCanMoveBF(move);
    }
    // Paper - END

    @Override
    public void clearActiveItem() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public ItemStack getActiveItem() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getArrowsStuck() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getHandRaisedTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public float getHurtDirection() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getItemUseRemainingTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getShieldBlockingDelay() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Block getTargetBlock(int arg0, FluidMode arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BlockFace getTargetBlockFace(int arg0, FluidMode arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TargetBlockInfo getTargetBlockInfo(int arg0, FluidMode arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Entity getTargetEntity(int arg0, boolean arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TargetEntityInfo getTargetEntityInfo(int arg0, boolean arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isHandRaised() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void playPickupItemAnimation(Item arg0, int arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setArrowsStuck(int arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setHurtDirection(float arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setKiller(Player arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setShieldBlockingDelay(int arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean fromMobSpawner() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Chunk getChunk() {
        // TODO Auto-generated method stub
        return super.getChunk();
    }

    @Override
    public SpawnReason getEntitySpawnReason() {
        // TODO Auto-generated method stub
        return SpawnReason.DEFAULT;
    }

    @Override
    public boolean isInBubbleColumn() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isInWaterOrBubbleColumn() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isInWaterOrRain() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isInWaterOrRainOrBubbleColumn() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void addDisabledSlots(EquipmentSlot... arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean canTick() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Set<EquipmentSlot> getDisabledSlots() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ItemStack getItem(org.bukkit.inventory.EquipmentSlot slot) {
        Preconditions.checkArgument(slot != null, "slot cannot be null");
        Preconditions.checkArgument(slot != EquipmentSlot.BODY, "Cannot get body item");
        return this.getEquipment().getItem(slot);
    }

    @Override
    public boolean isSlotDisabled(EquipmentSlot arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void removeDisabledSlots(EquipmentSlot... arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setCanTick(boolean arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setDisabledSlots(EquipmentSlot... arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setItem(EquipmentSlot slot, ItemStack stack) {
        // TODO Auto-generated method stub
    }
    
    // 1.19.2

	@Override
	public @NotNull Rotations getBodyRotations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull Rotations getHeadRotations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull Rotations getLeftArmRotations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull Rotations getLeftLegRotations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull Rotations getRightArmRotations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull Rotations getRightLegRotations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setBodyRotations(@NotNull Rotations arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setHeadRotations(@NotNull Rotations arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLeftArmRotations(@NotNull Rotations arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLeftLegRotations(@NotNull Rotations arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRightArmRotations(@NotNull Rotations arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRightLegRotations(@NotNull Rotations arg0) {
		// TODO Auto-generated method stub
		
	}

}
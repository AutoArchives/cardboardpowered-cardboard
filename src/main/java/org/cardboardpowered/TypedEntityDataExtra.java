package org.cardboardpowered;

import com.mojang.serialization.Codec;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.TypedEntityData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registries;

// TODO: mixin this into TypedEntityData
public class TypedEntityDataExtra {

	public static <IdType> TypedEntityData<IdType> decode(Codec<IdType> idTypeCodec, NbtCompound tag) {
		return (TypedEntityData<IdType>)(TypedEntityData.createCodec(idTypeCodec).decode(NbtOps.INSTANCE, tag).result().orElseThrow()).getFirst();
	}

	public static TypedEntityData<EntityType<?>> decodeEntity(NbtCompound tag) {
		return decode(EntityType.CODEC, tag);
	}

	public static TypedEntityData<BlockEntityType<?>> decodeBlockEntity(NbtCompound tag) {
		return decode(Registries.BLOCK_ENTITY_TYPE.getCodec(), tag);
	}
	
	public static NbtCompound copyTagWithEntityId(TypedEntityData<?> data) {
		NbtCompound tag = data.copyNbtWithoutId();
		tag.putString("id", EntityType.getId((EntityType<?>)data.getType()).toString());
		return tag;
	}

	public static NbtCompound copyTagWithBlockEntityId(TypedEntityData<?> data) {
		NbtCompound tag = data.copyNbtWithoutId();
		tag.putString("id", BlockEntityType.getId((BlockEntityType<?>)data.getType()).toString());
		return tag;
	}
	
}

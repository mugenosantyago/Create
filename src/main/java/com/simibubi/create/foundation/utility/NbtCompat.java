package com.simibubi.create.foundation.utility;
import com.simibubi.create.foundation.utility.NbtCompat;

import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

/**
 * Compatibility utilities for MC 1.21.8 NBT API changes.
 * 
 * In MC 1.21.8:
 * - CompoundTag.getUUID(), putUUID(), hasUUID() were removed
 * - NbtCompat.createUUID(), loadUUID(), writeBlockPos(), readBlockPos() were removed
 * - UUIDUtil provides uuid<->intArray conversions
 */
public class NbtCompat {

	/** Equivalent of the removed NbtCompat.createUUID(UUID) */
	public static IntArrayTag createUUID(UUID uuid) {
		return new IntArrayTag(UUIDUtil.uuidToIntArray(uuid));
	}

	/** Equivalent of the removed NbtCompat.loadUUID(Tag) */
	public static UUID loadUUID(Tag tag) {
		if (tag instanceof IntArrayTag intArrayTag) {
			return UUIDUtil.uuidFromIntArray(intArrayTag.getAsIntArray());
		}
		throw new IllegalArgumentException("Not an IntArrayTag: " + tag);
	}

	/** Equivalent of the removed CompoundTag.putUUID(String, UUID) */
	public static void putUUID(CompoundTag nbt, String key, UUID uuid) {
		nbt.put(key, createUUID(uuid));
	}

	/** Equivalent of the removed CompoundTag.getUUID(String) */
	public static UUID getUUID(CompoundTag nbt, String key) {
		Tag tag = nbt.get(key);
		if (tag == null) throw new IllegalArgumentException("No UUID at key: " + key);
		return loadUUID(tag);
	}

	/** Equivalent of the removed CompoundTag.hasUUID(String) */
	public static boolean hasUUID(CompoundTag nbt, String key) {
		Tag tag = nbt.get(key);
		return tag instanceof IntArrayTag && ((IntArrayTag) tag).size() == 4;
	}

	/** Equivalent of the removed NbtUtils.writeBlockPos(BlockPos) */
	public static Tag writeBlockPos(BlockPos pos) {
		if (pos == null) return new CompoundTag();
		return BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, pos).result().orElse(new CompoundTag());
	}

	/** Equivalent of the removed NbtUtils.readBlockPos(Tag) */
	public static BlockPos readBlockPos(Tag tag) {
		return BlockPos.CODEC.parse(NbtOps.INSTANCE, tag).result().orElse(BlockPos.ZERO);
	}

	/** Equivalent of the removed ItemStack.saveOptional(HolderLookup.Provider) */
	public static net.minecraft.nbt.Tag saveItemStack(net.minecraft.world.item.ItemStack stack, net.minecraft.core.HolderLookup.Provider registries) {
		return net.minecraft.world.item.ItemStack.OPTIONAL_CODEC
			.encodeStart(NbtOps.INSTANCE, stack)
			.result()
			.orElse(new CompoundTag());
	}

	/** Equivalent of the removed ItemStack.parseOptional(HolderLookup.Provider, CompoundTag) */
	public static net.minecraft.world.item.ItemStack parseOptionalItemStack(net.minecraft.core.HolderLookup.Provider registries, net.minecraft.nbt.Tag tag) {
		return net.minecraft.world.item.ItemStack.OPTIONAL_CODEC
			.parse(NbtOps.INSTANCE, tag)
			.result()
			.orElse(net.minecraft.world.item.ItemStack.EMPTY);
	}

	/** Save an entity as passenger using the new ValueOutput API, returns CompoundTag */
	public static CompoundTag saveEntityAsPassenger(net.minecraft.world.entity.Entity entity, net.minecraft.core.HolderLookup.Provider registries) {
		net.minecraft.util.ProblemReporter.Collector reporter = new net.minecraft.util.ProblemReporter.Collector();
		net.minecraft.world.level.storage.TagValueOutput output = net.minecraft.world.level.storage.TagValueOutput.createWithContext(reporter, registries);
		entity.saveAsPassenger(output);
		return output.buildResult();
	}

	/** Save an entity using the new ValueOutput API, returns CompoundTag */
	public static CompoundTag saveEntity(net.minecraft.world.entity.Entity entity, net.minecraft.core.HolderLookup.Provider registries) {
		net.minecraft.util.ProblemReporter.Collector reporter = new net.minecraft.util.ProblemReporter.Collector();
		net.minecraft.world.level.storage.TagValueOutput output = net.minecraft.world.level.storage.TagValueOutput.createWithContext(reporter, registries);
		entity.save(output);
		return output.buildResult();
	}

	/** Load an entity from a CompoundTag using the new ValueInput API */
	public static void loadEntity(net.minecraft.world.entity.Entity entity, CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
		net.minecraft.util.ProblemReporter.Collector reporter = new net.minecraft.util.ProblemReporter.Collector();
		net.minecraft.world.level.storage.TagValueInput input = net.minecraft.world.level.storage.TagValueInput.create(reporter, registries, tag);
		entity.load(input);
	}

	/** Compatibility replacement for removed Ingredient.of(TagKey<Item>) */
	public static net.minecraft.world.item.crafting.Ingredient ingredientFromTag(net.minecraft.tags.TagKey<net.minecraft.world.item.Item> tag) {
		net.minecraft.core.HolderSet.Named<net.minecraft.world.item.Item> namedSet =
			net.minecraft.core.registries.BuiltInRegistries.ITEM.getValue(tag)
				.orElseGet(() -> net.minecraft.core.HolderSet.emptyNamed(net.minecraft.core.registries.BuiltInRegistries.ITEM, tag));
		return net.minecraft.world.item.crafting.Ingredient.of(namedSet);
	}

	/** Compatibility replacement for ItemStackHandler.serializeNBT(Provider) */
	public static net.minecraft.nbt.CompoundTag serializeItemStackHandler(net.neoforged.neoforge.items.ItemStackHandler handler, net.minecraft.core.HolderLookup.Provider registries) {
		net.minecraft.util.ProblemReporter.Collector reporter = new net.minecraft.util.ProblemReporter.Collector();
		net.minecraft.world.level.storage.TagValueOutput output = net.minecraft.world.level.storage.TagValueOutput.createWithContext(reporter, registries);
		handler.serialize(output);
		return output.buildResult();
	}

	/** Compatibility replacement for ItemStackHandler.deserializeNBT(Provider, CompoundTag) */
	public static void deserializeItemStackHandler(net.neoforged.neoforge.items.ItemStackHandler handler, net.minecraft.core.HolderLookup.Provider registries, net.minecraft.nbt.CompoundTag tag) {
		net.minecraft.util.ProblemReporter.Collector reporter = new net.minecraft.util.ProblemReporter.Collector();
		net.minecraft.world.level.storage.TagValueInput input = net.minecraft.world.level.storage.TagValueInput.create(reporter, registries, tag);
		handler.deserialize(input);
	}

	/** Compatibility replacement for Entity.saveAsPassenger(CompoundTag) */
	public static boolean saveEntityToTag(net.minecraft.world.entity.Entity entity, net.minecraft.nbt.CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
		net.minecraft.util.ProblemReporter.Collector reporter = new net.minecraft.util.ProblemReporter.Collector();
		net.minecraft.world.level.storage.TagValueOutput output = net.minecraft.world.level.storage.TagValueOutput.createWithContext(reporter, registries);
		boolean result = entity.saveAsPassenger(output);
		if (result) tag.merge(output.buildResult());
		return result;
	}

	/** Compatibility: Entity.save() now takes ValueOutput; returns CompoundTag or null if not saved */
	public static net.minecraft.nbt.CompoundTag saveEntity(net.minecraft.world.entity.Entity entity) {
		net.minecraft.util.ProblemReporter.Collector reporter = new net.minecraft.util.ProblemReporter.Collector();
		net.minecraft.world.level.storage.TagValueOutput output = net.minecraft.world.level.storage.TagValueOutput.createWithContext(reporter, entity.registryAccess());
		if (entity.save(output)) {
			return output.buildResult();
		}
		return null;
	}

	/** Compatibility: Entity.save() now takes ValueOutput; merges result into tag, returns whether successful */
	public static boolean saveEntityIntoTag(net.minecraft.world.entity.Entity entity, net.minecraft.nbt.CompoundTag tag) {
		net.minecraft.nbt.CompoundTag result = saveEntity(entity);
		if (result != null) { tag.merge(result); return true; }
		return false;
	}

	/** Compatibility: Entity.saveWithoutId() now takes ValueOutput; returns CompoundTag */
	public static net.minecraft.nbt.CompoundTag saveEntityWithoutId(net.minecraft.world.entity.Entity entity) {
		net.minecraft.util.ProblemReporter.Collector reporter = new net.minecraft.util.ProblemReporter.Collector();
		net.minecraft.world.level.storage.TagValueOutput output = net.minecraft.world.level.storage.TagValueOutput.createWithContext(reporter, entity.registryAccess());
		entity.saveWithoutId(output);
		return output.buildResult();
	}

	/** Compatibility: Entity.load() now takes ValueInput */
	public static void loadEntity(net.minecraft.world.entity.Entity entity, net.minecraft.nbt.CompoundTag tag) {
		net.minecraft.util.ProblemReporter.Collector reporter = new net.minecraft.util.ProblemReporter.Collector();
		net.minecraft.world.level.storage.TagValueInput input = net.minecraft.world.level.storage.TagValueInput.create(reporter, entity.registryAccess(), tag);
		entity.load(input);
	}

	/** Compatibility: BlockEntity.addEntityType() now takes ValueOutput; stores type id directly in tag */
	public static void addEntityType(net.minecraft.nbt.CompoundTag tag, net.minecraft.world.level.block.entity.BlockEntityType<?> type) {
		net.minecraft.util.ProblemReporter.Collector reporter = new net.minecraft.util.ProblemReporter.Collector();
		// Use a temporary ValueOutput to get the serialized form, then merge into tag
		// Or simply store the id directly since TYPE_CODEC is byNameCodec()
		net.minecraft.resources.ResourceLocation key = net.minecraft.core.registries.BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type);
		if (key != null) tag.putString("id", key.toString());
	}

	/** Compatibility: EntityType.by() now takes ValueInput; use entity id string from tag */
	public static java.util.Optional<net.minecraft.world.entity.EntityType<?>> entityTypeByTag(net.minecraft.nbt.CompoundTag tag) {
		String id = tag.getStringOr("id", "");
		if (id.isEmpty()) return java.util.Optional.empty();
		return net.minecraft.world.entity.EntityType.byString(id);
	}

}

package com.simibubi.create.foundation.blockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public abstract class CachedRenderBBBlockEntity extends SyncedBlockEntity {

	private AABB renderBoundingBox;

	public CachedRenderBBBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// Bridge from MC 1.21.8 ValueInput to old CompoundTag-based API
	@Override
	protected void loadAdditional(ValueInput input) {
		HolderLookup.Provider registries = input.lookup();
		CompoundTag tag = input.read(com.mojang.serialization.MapCodec.assumeMapUnsafe(CompoundTag.CODEC)).orElse(new CompoundTag());
		loadAdditional(tag, registries);
	}

	// Bridge from MC 1.21.8 ValueOutput to old CompoundTag-based API
	@Override
	protected void saveAdditional(ValueOutput output) {
		HolderLookup.Provider registries = getLevel() != null ? getLevel().registryAccess()
			: HolderLookup.Provider.create(java.util.stream.Stream.empty());
		CompoundTag tag = new CompoundTag();
		saveAdditional(tag, registries);
		output.store(tag);
	}

	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {}

	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {}

	@OnlyIn(Dist.CLIENT)
	public AABB getRenderBoundingBox() {
		if (renderBoundingBox == null) {
			renderBoundingBox = createRenderBoundingBox();
		}
		return renderBoundingBox;
	}

	protected void invalidateRenderBoundingBox() {
		renderBoundingBox = null;
	}

	protected AABB createRenderBoundingBox() {
		return new AABB(getBlockPos());
	}

}

package com.simibubi.create.foundation.model;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.model.data.ModelData;

/**
 * Extends {@link BakedModelWrapper} and wires {@link #gatherModelData} into {@link #getModelData}.
 */
public abstract class BakedModelWrapperWithData extends BakedModelWrapper<BakedModel> {

	public BakedModelWrapperWithData(BakedModel template) {
		super(template);
	}

	@Override
	public ModelData getModelData(BlockAndTintGetter world, BlockPos pos, BlockState state, ModelData existing) {
		ModelData.Builder builder =
			existing != null && existing != ModelData.EMPTY ? existing.derive() : ModelData.builder();
		return gatherModelData(builder, world, pos, state, existing).build();
	}

	protected abstract ModelData.Builder gatherModelData(ModelData.Builder builder, BlockAndTintGetter world, BlockPos pos, BlockState state, ModelData existing);
}

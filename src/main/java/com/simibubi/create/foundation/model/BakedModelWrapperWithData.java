package com.simibubi.create.foundation.model;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Stub for BakedModelWrapperWithData.
 * In MC 1.21.8, BakedModel, BakedModelWrapper, and client.model.data were removed.
 */
public abstract class BakedModelWrapperWithData {

	protected final Object originalModel;

	public BakedModelWrapperWithData(Object originalModel) {
		this.originalModel = originalModel;
	}

	public Object getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, Object modelData) {
		return modelData;
	}

}

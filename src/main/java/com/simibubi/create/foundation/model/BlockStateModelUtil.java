package com.simibubi.create.foundation.model;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Helpers for {@link BlockStateModel} now that block models no longer expose {@code BakedModel#getQuads} directly.
 */
public final class BlockStateModelUtil {
	private BlockStateModelUtil() {}

	public static void collectQuads(BlockStateModel model, BlockState state, Direction side, RandomSource rand, List<BakedQuad> out) {
		List<BlockModelPart> parts = new ArrayList<>();
		model.collectParts(rand, parts);
		for (BlockModelPart part : parts) {
			out.addAll(part.getQuads(side));
		}
	}

	public static List<BakedQuad> collectQuads(BlockStateModel model, BlockState state, Direction side, RandomSource rand) {
		List<BakedQuad> out = new ArrayList<>();
		collectQuads(model, state, side, rand, out);
		return out;
	}
}

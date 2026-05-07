package com.simibubi.create.content.fluids.tank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.foundation.block.connected.CTModel;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;

import net.createmod.catnip.data.Iterate;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 * MC 1.21.8: Rewritten to use BlockStateModel.collectParts() instead of BakedModel.getQuads().
 */
public class FluidTankModel extends CTModel {

	public static FluidTankModel standard(BlockStateModel originalModel) {
		return new FluidTankModel(originalModel, AllSpriteShifts.FLUID_TANK, AllSpriteShifts.FLUID_TANK_TOP,
			AllSpriteShifts.FLUID_TANK_INNER);
	}

	public static FluidTankModel creative(BlockStateModel originalModel) {
		return new FluidTankModel(originalModel, AllSpriteShifts.CREATIVE_FLUID_TANK, AllSpriteShifts.CREATIVE_CASING,
			AllSpriteShifts.CREATIVE_CASING);
	}

	private FluidTankModel(BlockStateModel originalModel, CTSpriteShiftEntry side, CTSpriteShiftEntry top,
		CTSpriteShiftEntry inner) {
		super(originalModel, new FluidTankCTBehaviour(side, top, inner));
	}

	@Override
	public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random,
			List<BlockModelPart> out) {
		// Get cull data
		CullData cullData = new CullData();
		for (Direction d : Iterate.horizontalDirections)
			cullData.setCulled(d, ConnectivityHandler.isConnected(level, pos, pos.relative(d)));

		// Get base parts from parent CTModel
		List<BlockModelPart> baseParts = new ArrayList<>();
		super.collectParts(level, pos, state, random, baseParts);

		// Filter quads based on cull data
		for (BlockModelPart part : baseParts) {
			out.add(new CullBlockModelPart(part, cullData));
		}
	}

	private static class CullData {
		boolean[] culledFaces;

		public CullData() {
			culledFaces = new boolean[4];
			Arrays.fill(culledFaces, false);
		}

		void setCulled(Direction face, boolean cull) {
			if (face.getAxis()
				.isVertical())
				return;
			culledFaces[face.get2DDataValue()] = cull;
		}

		boolean isCulled(Direction face) {
			if (face.getAxis()
				.isVertical())
				return false;
			return culledFaces[face.get2DDataValue()];
		}
	}

	/**
	 * BlockModelPart wrapper that filters out quads for culled faces.
	 */
	private static class CullBlockModelPart implements BlockModelPart {
		private final BlockModelPart wrapped;
		private final CullData cullData;

		CullBlockModelPart(BlockModelPart wrapped, CullData cullData) {
			this.wrapped = wrapped;
			this.cullData = cullData;
		}

		@Override
		public List<BakedQuad> getQuads(Direction side) {
			// Filter out quads for culled horizontal faces
			if (side != null && cullData.isCulled(side)) {
				return new ArrayList<>();
			}
			return wrapped.getQuads(side);
		}

		@Override
		public TextureAtlasSprite particleIcon() {
			return wrapped.particleIcon();
		}

		@Override
		public net.minecraft.client.renderer.chunk.ChunkSectionLayer getRenderType(BlockState state) {
			return wrapped.getRenderType(state);
		}

		@Override
		public boolean useAmbientOcclusion() {
			return wrapped.useAmbientOcclusion();
		}
	}

}

package com.simibubi.create.content.logistics.tableCloth;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.model.BlockStateModelUtil;
import com.simibubi.create.foundation.model.BakedQuadHelper;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.catnip.data.Iterate;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class TableClothModel implements BlockStateModel {

	private static final Map<TableClothBlock, List<List<BakedQuad>>> CORNERS = new HashMap<>();

	private final BlockStateModel wrapped;

	public TableClothModel(BlockStateModel wrapped) {
		this.wrapped = wrapped;
	}

	public static void reload() {
		CORNERS.clear();
	}

	@Override
	public void collectParts(RandomSource random, List<BlockModelPart> out) {
		wrapped.collectParts(random, out);
	}

	@Override
	public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random,
			List<BlockModelPart> out) {
		List<BlockModelPart> baseParts = new ArrayList<>();
		wrapped.collectParts(level, pos, state, random, baseParts);

		if (!(state.getBlock() instanceof TableClothBlock dcb)) {
			out.addAll(baseParts);
			return;
		}

		EnumSet<Direction> culledSides = null;
		List<Direction> culled = new ArrayList<>();
		for (Direction side : Iterate.horizontalDirections)
			if (!Block.shouldRenderFace(level, pos, state, level.getBlockState(pos.relative(side)), side))
				culled.add(side);
		if (!culled.isEmpty())
			culledSides = EnumSet.copyOf(culled);

		for (BlockModelPart part : baseParts)
			out.add(new TableClothBlockModelPart(part, dcb, culledSides, random));
	}

	@Override
	public TextureAtlasSprite particleIcon() {
		return wrapped.particleIcon();
	}

	private List<BakedQuad> getCorner(TableClothBlock block, int corner, RandomSource rand) {
		if (!CORNERS.containsKey(block)) {
			TextureAtlasSprite targetSprite = wrapped.particleIcon();
			List<List<BakedQuad>> list = new ArrayList<>();
			for (PartialModel pm : List.of(AllPartialModels.TABLE_CLOTH_SW, AllPartialModels.TABLE_CLOTH_NW,
				AllPartialModels.TABLE_CLOTH_NE, AllPartialModels.TABLE_CLOTH_SE))
				list.add(buildCornerQuads(rand, targetSprite, pm));
			CORNERS.put(block, list);
		}
		return CORNERS.get(block).get(corner);
	}

	private static List<BakedQuad> buildCornerQuads(RandomSource rand, TextureAtlasSprite targetSprite,
			PartialModel pm) {
		List<BakedQuad> quads = new ArrayList<>();
		for (BakedQuad quad : BlockStateModelUtil.collectQuads(pm.get(), Blocks.AIR.defaultBlockState(), null, rand)) {
			TextureAtlasSprite original = quad.sprite();
			BakedQuad newQuad = BakedQuadHelper.clone(quad);
			int[] vertexData = newQuad.vertices();
			for (int vertex = 0; vertex < 4; vertex++) {
				BakedQuadHelper.setU(vertexData, vertex, targetSprite
					.getU(SpriteShiftEntry.getUnInterpolatedU(original, BakedQuadHelper.getU(vertexData, vertex))));
				BakedQuadHelper.setV(vertexData, vertex, targetSprite
					.getV(SpriteShiftEntry.getUnInterpolatedV(original, BakedQuadHelper.getV(vertexData, vertex))));
			}
			quads.add(newQuad);
		}
		return quads;
	}

	private class TableClothBlockModelPart implements BlockModelPart {
		private final BlockModelPart wrapped;
		private final TableClothBlock block;
		private final EnumSet<Direction> culledSides;
		private final RandomSource random;

		TableClothBlockModelPart(BlockModelPart wrapped, TableClothBlock block,
				EnumSet<Direction> culledSides, RandomSource random) {
			this.wrapped = wrapped;
			this.block = block;
			this.culledSides = culledSides;
			this.random = random;
		}

		@Override
		public List<BakedQuad> getQuads(Direction side) {
			List<BakedQuad> mainQuads = wrapped.getQuads(side);
			if (side == null || side.getAxis() == Axis.Y)
				return mainQuads;
			if (culledSides != null && culledSides.contains(side.getClockWise()))
				return mainQuads;
			List<BakedQuad> result = new ArrayList<>(mainQuads);
			result.addAll(TableClothModel.this.getCorner(block, side.get2DDataValue(), random));
			return result;
		}

		@Override
		public TextureAtlasSprite particleIcon() {
			return wrapped.particleIcon();
		}

		@Override
		public ChunkSectionLayer getRenderType(BlockState state) {
			return wrapped.getRenderType(state);
		}

		@Override
		public boolean useAmbientOcclusion() {
			return false;
		}
	}

}

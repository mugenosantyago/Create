package com.simibubi.create.content.kinetics.belt;

import java.util.ArrayList;
import java.util.List;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity.CasingType;
import com.simibubi.create.foundation.model.BakedQuadHelper;

import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.ponder.api.level.PonderLevel;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelProperty;

public class BeltModel implements BlockStateModel {

	public static final ModelProperty<CasingType> CASING_PROPERTY = new ModelProperty<>();
	public static final ModelProperty<Boolean> COVER_PROPERTY = new ModelProperty<>();

	private static final SpriteShiftEntry SPRITE_SHIFT = AllSpriteShifts.ANDESIDE_BELT_CASING;

	private final BlockStateModel wrapped;

	public BeltModel(BlockStateModel wrapped) {
		this.wrapped = wrapped;
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

		if (level instanceof PonderLevel) {
			out.addAll(baseParts);
			return;
		}

		BlockEntity be = level.getBlockEntity(pos);
		if (!(be instanceof BeltBlockEntity beltBE)) {
			out.addAll(baseParts);
			return;
		}

		CasingType type = beltBE.casing;
		boolean cover = beltBE.covered;
		boolean brassCasing = type == CasingType.BRASS;

		if (type == CasingType.NONE || brassCasing && !cover) {
			out.addAll(baseParts);
			return;
		}

		if (cover) {
			boolean alongX = state.getValue(BeltBlock.HORIZONTAL_FACING).getAxis() == Axis.X;
			BlockStateModel coverModel =
				(brassCasing ? alongX ? AllPartialModels.BRASS_BELT_COVER_X : AllPartialModels.BRASS_BELT_COVER_Z
					: alongX ? AllPartialModels.ANDESITE_BELT_COVER_X : AllPartialModels.ANDESITE_BELT_COVER_Z).get();
			coverModel.collectParts(level, pos, state, random, out);
		}

		if (brassCasing) {
			out.addAll(baseParts);
			return;
		}

		for (BlockModelPart part : baseParts) {
			out.add(new SpriteShiftingBlockModelPart(part, SPRITE_SHIFT));
		}
	}

	@Override
	public TextureAtlasSprite particleIcon() {
		return wrapped.particleIcon();
	}

	private static class SpriteShiftingBlockModelPart implements BlockModelPart {
		private final BlockModelPart wrapped;
		private final SpriteShiftEntry shift;

		SpriteShiftingBlockModelPart(BlockModelPart wrapped, SpriteShiftEntry shift) {
			this.wrapped = wrapped;
			this.shift = shift;
		}

		@Override
		public List<BakedQuad> getQuads(Direction side) {
			List<BakedQuad> quads = new ArrayList<>(wrapped.getQuads(side));
			for (int i = 0; i < quads.size(); i++) {
				BakedQuad quad = quads.get(i);
				if (quad.sprite() != shift.getOriginal())
					continue;
				BakedQuad newQuad = BakedQuadHelper.clone(quad);
				int[] vertexData = newQuad.vertices();
				for (int vertex = 0; vertex < 4; vertex++) {
					float u = BakedQuadHelper.getU(vertexData, vertex);
					float v = BakedQuadHelper.getV(vertexData, vertex);
					BakedQuadHelper.setU(vertexData, vertex, shift.getTargetU(u));
					BakedQuadHelper.setV(vertexData, vertex, shift.getTargetV(v));
				}
				quads.set(i, newQuad);
			}
			return quads;
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
			return wrapped.useAmbientOcclusion();
		}
	}

}

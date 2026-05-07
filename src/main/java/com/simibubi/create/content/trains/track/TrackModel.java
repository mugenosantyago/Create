package com.simibubi.create.content.trains.track;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import com.simibubi.create.foundation.model.BakedQuadHelper;

import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class TrackModel implements BlockStateModel {

	private final BlockStateModel wrapped;

	public TrackModel(BlockStateModel wrapped) {
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

		if (!state.hasProperty(TrackBlock.SHAPE)) {
			out.addAll(baseParts);
			return;
		}

		BlockEntity be = level.getBlockEntity(pos);
		if (!(be instanceof TrackBlockEntity trackBE) || trackBE.tilt.smoothingAngle.isEmpty()) {
			out.addAll(baseParts);
			return;
		}

		double angleIn = trackBE.tilt.smoothingAngle.get();
		double angle = Math.abs(angleIn);
		boolean flip = angleIn < 0;

		TrackShape trackShape = state.getValue(TrackBlock.SHAPE);
		double hAngle = switch (trackShape) {
			case XO -> 0;
			case PD -> 45;
			case ZO -> 90;
			case ND -> 135;
			default -> 0;
		};

		Vec3 verticalOffset = new Vec3(0, -0.25, 0);
		Vec3 diagonalRotationPoint =
			(trackShape == TrackShape.ND || trackShape == TrackShape.PD)
				? new Vec3((Mth.SQRT_OF_TWO - 1) / 2, 0, 0) : Vec3.ZERO;

		UnaryOperator<Vec3> transform = v -> {
			v = v.add(verticalOffset);
			v = VecHelper.rotateCentered(v, hAngle, Axis.Y);
			v = v.add(diagonalRotationPoint);
			v = VecHelper.rotate(v, angle, Axis.Z);
			v = v.subtract(diagonalRotationPoint);
			v = VecHelper.rotateCentered(v, -hAngle + (flip ? 180 : 0), Axis.Y);
			v = v.subtract(verticalOffset);
			return v;
		};

		for (BlockModelPart part : baseParts) {
			out.add(new TransformedBlockModelPart(part, transform));
		}
	}

	@Override
	public TextureAtlasSprite particleIcon() {
		return wrapped.particleIcon();
	}

	private static class TransformedBlockModelPart implements BlockModelPart {
		private final BlockModelPart wrapped;
		private final UnaryOperator<Vec3> transform;

		TransformedBlockModelPart(BlockModelPart wrapped, UnaryOperator<Vec3> transform) {
			this.wrapped = wrapped;
			this.transform = transform;
		}

		@Override
		public List<BakedQuad> getQuads(Direction side) {
			List<BakedQuad> quads = wrapped.getQuads(side);
			if (quads.isEmpty())
				return quads;
			List<BakedQuad> result = new ArrayList<>(quads.size());
			for (BakedQuad quad : quads) {
				BakedQuad newQuad = BakedQuadHelper.clone(quad);
				int[] vertexData = newQuad.vertices();
				for (int j = 0; j < 4; j++)
					BakedQuadHelper.setXYZ(vertexData, j, transform.apply(BakedQuadHelper.getXYZ(vertexData, j)));
				result.add(newQuad);
			}
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
			return wrapped.useAmbientOcclusion();
		}
	}

}

package com.simibubi.create.foundation.block.connected;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.simibubi.create.content.decoration.copycat.CopycatBlock;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour.CTContext;
import com.simibubi.create.foundation.model.BakedQuadHelper;

import net.createmod.catnip.data.Iterate;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * MC 1.21.8: Rewritten to use BlockStateModel.collectParts() instead of BakedModel.getQuads().
 * Connected textures are applied by wrapping BlockModelPart instances and modifying their quads.
 */
public class CTModel implements BlockStateModel {

	private final BlockStateModel wrapped;
	private final ConnectedTextureBehaviour behaviour;

	public CTModel(BlockStateModel wrapped, ConnectedTextureBehaviour behaviour) {
		this.wrapped = wrapped;
		this.behaviour = behaviour;
	}

	@Override
	public void collectParts(RandomSource random, List<BlockModelPart> out) {
		// Delegate to wrapped model - this path is used for item rendering
		wrapped.collectParts(random, out);
	}

	@Override
	public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random,
			List<BlockModelPart> out) {
		// Get parts from wrapped model
		List<BlockModelPart> wrappedParts = new ArrayList<>();
		wrapped.collectParts(level, pos, state, random, wrappedParts);

		// Gather connected texture data
		CTData data = createCTData(level, pos, state);

		// Wrap each part to apply sprite shifting
		for (BlockModelPart part : wrappedParts) {
			out.add(new CTBlockModelPart(part, state, random, data, behaviour));
		}
	}

	protected CTData createCTData(BlockAndTintGetter world, BlockPos pos, BlockState state) {
		CTData data = new CTData();
		MutableBlockPos mutablePos = new MutableBlockPos();
		for (Direction face : Iterate.directions) {
			BlockState actualState = world.getBlockState(pos);
			if (!behaviour.buildContextForOccludedDirections()
					&& !Block.shouldRenderFace(world, pos, state, world.getBlockState(mutablePos.setWithOffset(pos, face)), face)
					&& !(actualState.getBlock() instanceof CopycatBlock ufb
							&& !ufb.canFaceBeOccluded(actualState, face)))
				continue;
			CTType dataType = behaviour.getDataType(world, pos, state, face);
			if (dataType == null)
				continue;
			CTContext context = behaviour.buildContext(world, pos, state, face, dataType.getContextRequirement());
			data.put(face, dataType.getTextureIndex(context));
		}
		return data;
	}

	@Override
	public TextureAtlasSprite particleIcon() {
		return wrapped.particleIcon();
	}

	/**
	 * BlockModelPart wrapper that applies connected texture sprite shifting.
	 */
	private static class CTBlockModelPart implements BlockModelPart {
		private final BlockModelPart wrapped;
		private final BlockState state;
		private final RandomSource random;
		private final CTData data;
		private final ConnectedTextureBehaviour behaviour;

		CTBlockModelPart(BlockModelPart wrapped, BlockState state, RandomSource random, CTData data,
				ConnectedTextureBehaviour behaviour) {
			this.wrapped = wrapped;
			this.state = state;
			this.random = random;
			this.data = data;
			this.behaviour = behaviour;
		}

		@Override
		public List<BakedQuad> getQuads(Direction side) {
			List<BakedQuad> quads = new ArrayList<>(wrapped.getQuads(side));
			applySpriteShifts(quads, side);
			return quads;
		}

		@Override
		public TextureAtlasSprite particleIcon() {
			return wrapped.particleIcon();
		}

		private void applySpriteShifts(List<BakedQuad> quads, Direction side) {
			for (int i = 0; i < quads.size(); i++) {
				BakedQuad quad = quads.get(i);

				Direction face = side != null ? side : quad.direction();
				if (face == null)
					continue;

				int index = data.get(face);
				if (index == -1)
					continue;

				CTSpriteShiftEntry spriteShift = behaviour.getShift(state, random, face, quad.sprite());
				if (spriteShift == null)
					continue;
				if (quad.sprite() != spriteShift.getOriginal())
					continue;

				BakedQuad newQuad = BakedQuadHelper.clone(quad);
				int[] vertexData = newQuad.vertices();

				for (int vertex = 0; vertex < 4; vertex++) {
					float u = BakedQuadHelper.getU(vertexData, vertex);
					float v = BakedQuadHelper.getV(vertexData, vertex);
					BakedQuadHelper.setU(vertexData, vertex, spriteShift.getTargetU(u, index));
					BakedQuadHelper.setV(vertexData, vertex, spriteShift.getTargetV(v, index));
				}

				quads.set(i, newQuad);
			}
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

	private static class CTData {
		private final int[] indices;

		public CTData() {
			indices = new int[6];
			Arrays.fill(indices, -1);
		}

		public void put(Direction face, int texture) {
			indices[face.get3DDataValue()] = texture;
		}

		public int get(Direction face) {
			return face != null ? indices[face.get3DDataValue()] : -1;
		}
	}

}

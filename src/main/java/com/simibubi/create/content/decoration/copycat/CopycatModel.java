package com.simibubi.create.content.decoration.copycat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.simibubi.create.AllBlocks;

import net.createmod.catnip.data.Iterate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.neoforge.client.model.QuadTransformers;
import net.neoforged.neoforge.model.data.ModelData;

/**
 * MC 1.21.8: Rewritten to use BlockStateModel.collectParts() instead of BakedModel.getQuads().
 */
public abstract class CopycatModel implements BlockStateModel {

	// Kept for compatibility with code that uses ModelData
	public static final net.neoforged.neoforge.model.data.ModelProperty<BlockState> MATERIAL_PROPERTY =
		new net.neoforged.neoforge.model.data.ModelProperty<>();

	protected final BlockStateModel wrapped;

	public CopycatModel(BlockStateModel originalModel) {
		this.wrapped = originalModel;
	}

	@Override
	public void collectParts(RandomSource random, List<BlockModelPart> out) {
		// Default implementation delegates to wrapped model
		wrapped.collectParts(random, out);
	}

	@Override
	public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random,
			List<BlockModelPart> out) {
		if (!(state.getBlock() instanceof CopycatBlock copycatBlock))
			return;

		// Get material from block entity
		BlockState material = getMaterial(level, pos);

		// Gather occlusion data
		OcclusionData occlusionData = new OcclusionData();
		gatherOcclusionData(level, pos, state, material, occlusionData, copycatBlock);

		// Check emissivity
		boolean isEmissive = material.emissiveRendering(level, pos);

		// Get material model and its parts
		BlockStateModel materialModel = getModelOf(material);
		List<BlockModelPart> materialParts = new ArrayList<>();
		materialModel.collectParts(level, pos, material, random, materialParts);

		// Create filtered getter for texture connection
		FilteredBlockAndTintGetter filteredWorld = new FilteredBlockAndTintGetter(level,
			targetPos -> copycatBlock.canConnectTexturesToward(level, pos, targetPos, state));

		// Wrap each part to apply copycat transformations
		for (BlockModelPart part : materialParts) {
			out.add(new CopycatBlockModelPart(part, state, material, occlusionData, copycatBlock,
				isEmissive, filteredWorld, pos, random));
		}
	}

	private void gatherOcclusionData(BlockAndTintGetter level, BlockPos pos, BlockState state, BlockState material,
		OcclusionData occlusionData, CopycatBlock copycatBlock) {
		MutableBlockPos mutablePos = new MutableBlockPos();
		for (Direction face : Iterate.directions) {

			// Rubidium: Run an additional IForgeBlock.hidesNeighborFace check because it
			// seems to be missing in Block.shouldRenderFace
			MutableBlockPos neighbourPos = mutablePos.setWithOffset(pos, face);
			BlockState neighbourState = level.getBlockState(neighbourPos);
			if (state.supportsExternalFaceHiding()
				&& neighbourState.hidesNeighborFace(level, neighbourPos, state, face.getOpposite())) {
				occlusionData.occlude(face);
				continue;
			}

			if (!copycatBlock.canFaceBeOccluded(state, face))
				continue;
			if (!Block.shouldRenderFace(level, pos, material, level.getBlockState(neighbourPos), face))
				occlusionData.occlude(face);
		}
	}

	@Override
	public TextureAtlasSprite particleIcon() {
		return wrapped.particleIcon();
	}

	/**
	 * Get material from block entity at position.
	 */
	public static BlockState getMaterial(BlockAndTintGetter level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof CopycatBlockEntity be) {
			BlockState material = be.getMaterial();
			if (material != null)
				return material;
		}
		return AllBlocks.COPYCAT_BASE.getDefaultState();
	}

	/**
	 * Get material from ModelData (for compatibility with code that still uses ModelData).
	 * @deprecated Use getMaterial(BlockAndTintGetter, BlockPos) instead.
	 */
	@Deprecated
	public static BlockState getMaterial(ModelData data) {
		return AllBlocks.COPYCAT_BASE.getDefaultState();
	}

	/**
	 * Get the BlockStateModel for a given BlockState.
	 */
	public static BlockStateModel getModelOf(BlockState state) {
		return Minecraft.getInstance()
			.getBlockRenderer()
			.getBlockModel(state);
	}

	/**
	 * Process quads for a specific face. Subclasses implement this to apply their specific transformations.
	 */
	protected abstract List<BakedQuad> processQuadsForFace(List<BakedQuad> quads, Direction face,
		BlockState copycatState, BlockState materialState);

	/**
	 * BlockModelPart wrapper that applies copycat transformations.
	 */
	private class CopycatBlockModelPart implements BlockModelPart {
		private final BlockModelPart wrappedPart;
		private final BlockState copycatState;
		private final BlockState materialState;
		private final OcclusionData occlusionData;
		private final CopycatBlock copycatBlock;
		private final boolean isEmissive;
		private final FilteredBlockAndTintGetter filteredWorld;
		private final BlockPos pos;
		private final RandomSource random;

		CopycatBlockModelPart(BlockModelPart wrappedPart, BlockState copycatState, BlockState materialState,
				OcclusionData occlusionData, CopycatBlock copycatBlock, boolean isEmissive,
				FilteredBlockAndTintGetter filteredWorld, BlockPos pos, RandomSource random) {
			this.wrappedPart = wrappedPart;
			this.copycatState = copycatState;
			this.materialState = materialState;
			this.occlusionData = occlusionData;
			this.copycatBlock = copycatBlock;
			this.isEmissive = isEmissive;
			this.filteredWorld = filteredWorld;
			this.pos = pos;
			this.random = random;
		}

		@Override
		public List<BakedQuad> getQuads(Direction side) {
			// Rubidium: Return empty for faces that should always render
			if (side != null && copycatBlock.shouldFaceAlwaysRender(copycatState, side))
				return Collections.emptyList();

			// Check occlusion - return wrapped model quads for occluded faces
			if (occlusionData.isOccluded(side))
				return collectPartsAsQuads(filteredWorld, pos, copycatState, random, side);

			// Get quads from wrapped part
			List<BakedQuad> quads = new ArrayList<>(wrappedPart.getQuads(side));

			// Apply subclass-specific processing
			quads = processQuadsForFace(quads, side, copycatState, materialState);

			// Apply emissivity
			if (isEmissive)
				QuadTransformers.settingMaxEmissivity().processInPlace(quads);

			return quads;
		}

		@Override
		public TextureAtlasSprite particleIcon() {
			return wrappedPart.particleIcon();
		}

		@Override
		public net.minecraft.client.renderer.chunk.ChunkSectionLayer getRenderType(BlockState state) {
			return wrappedPart.getRenderType(state);
		}

		@Override
		public boolean useAmbientOcclusion() {
			return wrappedPart.useAmbientOcclusion();
		}
	}

	/**
	 * Helper to get quads from the wrapped model for occluded faces.
	 */
	private List<BakedQuad> collectPartsAsQuads(BlockAndTintGetter level, BlockPos pos, BlockState state,
			RandomSource random, Direction side) {
		List<BlockModelPart> parts = new ArrayList<>();
		wrapped.collectParts(level, pos, state, random, parts);
		List<BakedQuad> quads = new ArrayList<>();
		for (BlockModelPart part : parts) {
			quads.addAll(part.getQuads(side));
		}
		return quads;
	}

	private static class OcclusionData {
		private final boolean[] occluded;

		public OcclusionData() {
			occluded = new boolean[6];
		}

		public void occlude(Direction face) {
			occluded[face.get3DDataValue()] = true;
		}

		public boolean isOccluded(Direction face) {
			return face != null && occluded[face.get3DDataValue()];
		}
	}

}

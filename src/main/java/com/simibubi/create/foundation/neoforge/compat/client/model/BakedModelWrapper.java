package com.simibubi.create.foundation.neoforge.compat.client.model;

import java.util.Collections;
import java.util.List;

import com.simibubi.create.foundation.neoforge.compat.client.ChunkRenderTypeSet;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import com.simibubi.create.foundation.client.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Compatibility stub for BakedModelWrapper which was removed in NeoForge for MC 1.21.8.
 * Packaged under Create to avoid JPMS conflicts with the NeoForge module.
 */
@SuppressWarnings("all")
public class BakedModelWrapper<T extends BakedModel> implements BakedModel {

	protected final T wrapped;

	public BakedModelWrapper(T wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand) {
		return wrapped != null ? wrapped.getQuads(state, side, rand) : Collections.emptyList();
	}

	@Override
	public boolean useAmbientOcclusion() {
		return wrapped != null && wrapped.useAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return wrapped == null || wrapped.isGui3d();
	}

	@Override
	public boolean usesBlockLight() {
		return wrapped == null || wrapped.usesBlockLight();
	}

	@Override
	public boolean isCustomRenderer() {
		return wrapped != null && wrapped.isCustomRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return wrapped != null ? wrapped.getParticleIcon() : null;
	}

	@Override
	public ItemTransforms getTransforms() {
		return wrapped != null ? wrapped.getTransforms() : ItemTransforms.NO_TRANSFORMS;
	}

	@Override
	public BakedModel applyTransform(net.minecraft.world.item.ItemDisplayContext transformType, com.mojang.blaze3d.vertex.PoseStack poseStack, boolean applyLeftHandTransform) {
		return this;
	}

	// NeoForge 21.1.x extension methods (stubbed for MC 1.21.8 port)
	public TextureAtlasSprite getParticleIcon(net.neoforged.neoforge.model.data.ModelData data) {
		return getParticleIcon();
	}

	@Override
	public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, net.neoforged.neoforge.model.data.ModelData data) {
		return wrapped != null ? wrapped.getRenderTypes(state, rand, data) : ChunkRenderTypeSet.ALL;
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, net.neoforged.neoforge.model.data.ModelData data, net.minecraft.client.renderer.RenderType renderType) {
		return wrapped != null ? wrapped.getQuads(state, side, rand, data, renderType) : getQuads(state, side, rand);
	}

	@Override
	public net.neoforged.neoforge.model.data.ModelData getModelData(net.minecraft.world.level.BlockAndTintGetter world, net.minecraft.core.BlockPos pos, BlockState state, net.neoforged.neoforge.model.data.ModelData data) {
		return data != null ? data : net.neoforged.neoforge.model.data.ModelData.EMPTY;
	}

	@Override
	public net.minecraft.util.TriState useAmbientOcclusion(BlockState state, net.neoforged.neoforge.model.data.ModelData data, net.minecraft.client.renderer.RenderType renderType) {
		return wrapped != null ? wrapped.useAmbientOcclusion(state, data, renderType)
			: (useAmbientOcclusion() ? net.minecraft.util.TriState.TRUE : net.minecraft.util.TriState.FALSE);
	}

}

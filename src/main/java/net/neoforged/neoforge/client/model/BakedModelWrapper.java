package net.neoforged.neoforge.client.model;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Compatibility stub for BakedModelWrapper which was removed in NeoForge for MC 1.21.8.
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

}

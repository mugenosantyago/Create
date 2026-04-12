package net.minecraft.client.resources.model;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Compatibility stub for BakedModel which was removed in MC 1.21.8.
 * In 1.21.8, the model system was completely revamped to use BlockStateModel and BlockModelPart.
 */
public interface BakedModel {

	default List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand) {
		return Collections.emptyList();
	}

	default boolean useAmbientOcclusion() {
		return true;
	}

	default boolean isGui3d() {
		return true;
	}

	default boolean usesBlockLight() {
		return true;
	}

	default boolean isCustomRenderer() {
		return false;
	}

	default TextureAtlasSprite getParticleIcon() {
		return null;
	}

	default ItemTransforms getTransforms() {
		return ItemTransforms.NO_TRANSFORMS;
	}

	default BakedModel applyTransform(net.minecraft.world.item.ItemDisplayContext transformType, com.mojang.blaze3d.vertex.PoseStack poseStack, boolean applyLeftHandTransform) {
		return this;
	}

	// NeoForge extended methods
	default net.neoforged.neoforge.client.ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, net.neoforged.neoforge.model.data.ModelData data) {
		return net.neoforged.neoforge.client.ChunkRenderTypeSet.ALL;
	}

	default List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, net.neoforged.neoforge.model.data.ModelData data, net.minecraft.client.renderer.RenderType renderType) {
		return getQuads(state, side, rand);
	}

	default net.neoforged.neoforge.model.data.ModelData getModelData(net.minecraft.world.level.BlockAndTintGetter level, net.minecraft.core.BlockPos pos, BlockState state, net.neoforged.neoforge.model.data.ModelData existing) {
		return existing;
	}

	default net.minecraft.util.TriState useAmbientOcclusion(BlockState state, net.neoforged.neoforge.model.data.ModelData data, net.minecraft.client.renderer.RenderType renderType) {
		return useAmbientOcclusion() ? net.minecraft.util.TriState.TRUE : net.minecraft.util.TriState.FALSE;
	}

	default List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous) {
		return Collections.singletonList(this);
	}

}

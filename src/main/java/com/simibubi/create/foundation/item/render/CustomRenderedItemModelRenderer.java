package com.simibubi.create.foundation.item.render;

import java.util.Set;

import com.mojang.blaze3d.vertex.PoseStack;

import org.joml.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Base class for custom rendered item model renderers.
 * In MC 1.21.8, BlockEntityWithoutLevelRenderer was replaced by SpecialModelRenderer.
 */
public abstract class CustomRenderedItemModelRenderer implements SpecialModelRenderer<ItemStack> {

	@Override
	public void render(ItemStack stack, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer,
		int light, int overlay, boolean hasFoilApplied) {
		if (stack.isEmpty())
			return;
		PartialItemModelRenderer renderer = PartialItemModelRenderer.of(stack, transformType, ms, buffer, overlay);

		ms.pushPose();
		ms.translate(0.5F, 0.5F, 0.5F);
		render(stack, null, renderer, transformType, ms, buffer, light, overlay);
		ms.popPose();
	}

	@Override
	public void getExtents(Set<Vector3f> extents) {
	}

	@Override
	public ItemStack extractArgument(ItemStack stack) {
		return stack;
	}

	protected abstract void render(ItemStack stack, CustomRenderedItemModel model,
		PartialItemModelRenderer renderer, ItemDisplayContext transformType,
		PoseStack ms, MultiBufferSource buffer, int light, int overlay);

}

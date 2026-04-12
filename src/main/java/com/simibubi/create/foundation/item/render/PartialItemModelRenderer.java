package com.simibubi.create.foundation.item.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
/**
 * Partial item model renderer.
 * Note: In MC 1.21.8, BakedModel was removed and item rendering uses ItemStackRenderState.
 * These methods are stubs that fall back to standard item rendering.
 */
public class PartialItemModelRenderer {

	private static final PartialItemModelRenderer INSTANCE = new PartialItemModelRenderer();

	private ItemStack stack;
	private ItemDisplayContext transformType;
	private PoseStack ms;
	private MultiBufferSource buffer;
	private int overlay;
	private int light;

	public static PartialItemModelRenderer of(ItemStack stack, ItemDisplayContext transformType,
		PoseStack ms, MultiBufferSource buffer, int overlay) {
		PartialItemModelRenderer instance = INSTANCE;
		instance.stack = stack;
		instance.transformType = transformType;
		instance.ms = ms;
		instance.buffer = buffer;
		instance.overlay = overlay;
		return instance;
	}

	public void render(Object model, int light) {
		renderFallback(light);
	}

	public void renderSolid(Object model, int light) {
		renderFallback(light);
	}

	public void renderGlowing(Object model, int light) {
		renderFallback(light);
	}

	public void renderSolidGlowing(Object model, int light) {
		renderFallback(light);
	}

	public void render(Object model, RenderType type, int light) {
		renderFallback(light);
	}

	private void renderFallback(int light) {
		if (stack.isEmpty())
			return;
		Minecraft.getInstance().getItemRenderer().renderStatic(stack, transformType, light, overlay, ms, buffer, null, 0);
	}

}

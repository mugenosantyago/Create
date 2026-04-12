package com.simibubi.create.content.equipment.zapper;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ZapperItemRenderer extends CustomRenderedItemModelRenderer {

	@Override
	protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType,
		PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		// Block indicator
		if (transformType == ItemDisplayContext.GUI && stack.has(AllDataComponents.SHAPER_BLOCK_USED))
			renderBlockUsed(stack, ms, buffer, light, overlay);
	}

	@SuppressWarnings("deprecation")
	private void renderBlockUsed(ItemStack stack, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		BlockState state = stack.get(AllDataComponents.SHAPER_BLOCK_USED);

		ms.pushPose();
		ms.translate(-0.3F, -0.45F, -0.0F);
		ms.scale(0.25F, 0.25F, 0.25F);
		ItemStack blockStack = new ItemStack(state.getBlock());
		ItemStackRenderState blockState = new ItemStackRenderState();
		Minecraft.getInstance()
			.getItemModelResolver()
			.updateForTopItem(blockState, blockStack, ItemDisplayContext.NONE, null, null, 0);
		blockState.render(ms, buffer, light, overlay);
		ms.popPose();
	}

	protected float getAnimationProgress(float pt, boolean leftHanded, boolean mainHand) {
		float animation = CreateClient.ZAPPER_RENDER_HANDLER.getAnimation(mainHand ^ leftHanded, pt);
		return Mth.clamp(animation * 5, 0, 1);
	}

}

package com.simibubi.create.content.equipment.armor;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

/**
 * Renders the Backtank 3D model on the player's (and other living entities') back.
 *
 * <p>In 1.21.2+, {@link RenderLayer} was refactored to use an {@code EntityRenderState} generic
 * instead of the live entity. The render method no longer receives the entity directly.
 */
public class BacktankArmorLayer<S extends LivingEntityRenderState, M extends EntityModel<? super S>>
		extends RenderLayer<S, M> {

	public BacktankArmorLayer(RenderLayerParent<S, M> renderer) {
		super(renderer);
	}

	@Override
	public void render(PoseStack ms, MultiBufferSource buffer, int light, S state, float netHeadYaw, float headPitch) {
		// TODO: retrieve backtank info from render state; body disabled pending full migration.
	}

	public static void registerOnAll(EntityRenderDispatcher renderManager) {
		// TODO: re-enable when render-state integration is complete.
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static void registerOn(EntityRenderer<?, ?> entityRenderer) {
		if (!(entityRenderer instanceof LivingEntityRenderer livingRenderer))
			return;
		if (!(livingRenderer.getModel() instanceof HumanoidModel))
			return;
		BacktankArmorLayer layer = new BacktankArmorLayer(livingRenderer);
		livingRenderer.addLayer(layer);
	}
}

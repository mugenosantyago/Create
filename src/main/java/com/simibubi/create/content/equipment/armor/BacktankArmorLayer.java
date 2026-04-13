package com.simibubi.create.content.equipment.armor;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.entity.state.SheepRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import com.simibubi.create.foundation.mixin.accessor.EntityRenderDispatcherAccessor;

/**
 * Renders equipped backtank items on the entity's torso (third-person).
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class BacktankArmorLayer<S extends LivingEntityRenderState, M extends EntityModel<? super S>>
		extends RenderLayer<S, M> {

	public BacktankArmorLayer(RenderLayerParent<S, M> renderer) {
		super(renderer);
	}

	@Override
	public void render(PoseStack ms, MultiBufferSource buffer, int light, S state, float netHeadYaw, float headPitch) {
		LivingEntity entity = resolveEntity(state);
		if (entity == null)
			return;
		List<ItemStack> tanks = BacktankUtil.getAllWithAir(entity);
		if (tanks.isEmpty())
			return;
		ItemStack stack = tanks.get(0);
		M model = getParentModel();
		if (!(model instanceof HumanoidModel<?> humanoid))
			return;

		ms.pushPose();
		humanoid.body.translateAndRotate(ms);
		ms.translate(0.0, -0.25, 0.18);
		ms.scale(0.65f, 0.65f, 0.65f);
		Minecraft.getInstance()
			.getItemRenderer()
			.renderStatic(stack, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, light, OverlayTexture.NO_OVERLAY, ms, buffer,
				entity.level(), 0);
		ms.popPose();
	}

	@Nullable
	private static LivingEntity resolveEntity(LivingEntityRenderState state) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null)
			return null;
		if (state instanceof PlayerRenderState ps) {
			Entity e = mc.level.getEntity(ps.id);
			return e instanceof LivingEntity le ? le : null;
		}
		if (state instanceof SheepRenderState ss) {
			Entity e = mc.level.getEntity(ss.id);
			return e instanceof LivingEntity le ? le : null;
		}
		Vec3 pos = new Vec3(state.x, state.y, state.z);
		LivingEntity closest = null;
		double bestD = 0.35 * 0.35;
		for (LivingEntity le : mc.level.getEntitiesOfClass(LivingEntity.class,
			new AABB(pos.x - 0.5, pos.y - 0.25, pos.z - 0.5, pos.x + 0.5, pos.y + Math.max(state.boundingBoxHeight, 2.0),
				pos.z + 0.5))) {
			double d = le.position().distanceToSqr(pos);
			if (d < bestD) {
				bestD = d;
				closest = le;
			}
		}
		return closest;
	}

	public static void registerOnAll(EntityRenderDispatcher renderManager) {
		for (EntityRenderer<? extends Player, ?> renderer : renderManager.getSkinMap().values())
			registerOn(renderer);
		for (EntityRenderer<?, ?> renderer : ((EntityRenderDispatcherAccessor) renderManager).create$getRenderers().values())
			registerOn(renderer);
	}

	public static void registerOn(EntityRenderer<?, ?> entityRenderer) {
		if (!(entityRenderer instanceof LivingEntityRenderer<?, ?, ?> livingRenderer))
			return;
		if (!(livingRenderer.getModel() instanceof HumanoidModel))
			return;
		BacktankArmorLayer<?, ?> layer = new BacktankArmorLayer<>(livingRenderer);
		livingRenderer.addLayer((BacktankArmorLayer) layer);
	}
}

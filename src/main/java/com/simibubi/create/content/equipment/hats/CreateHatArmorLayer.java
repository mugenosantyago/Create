package com.simibubi.create.content.equipment.hats;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.trains.schedule.hat.TrainHatInfo;
import com.simibubi.create.content.trains.schedule.hat.TrainHatInfoReloadListener;
import com.simibubi.create.foundation.mixin.accessor.EntityRenderDispatcherAccessor;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.entity.state.SheepRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings({"rawtypes", "unchecked"})
public class CreateHatArmorLayer<S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends RenderLayer<S, M> {

	public CreateHatArmorLayer(RenderLayerParent<S, M> renderer) {
		super(renderer);
	}

	@Override
	public void render(PoseStack ms, MultiBufferSource buffer, int light, S state, float limbSwing, float limbSwingAmount) {
		LivingEntity entity = resolveEntity(state);
		if (entity == null)
			return;

		PartialModel hat = EntityHats.getHatFor(entity);
		if (hat == null)
			return;

		M entityModel = getParentModel();
		ms.pushPose();

		var msr = TransformStack.of(ms);
		TrainHatInfo info = TrainHatInfoReloadListener.getHatInfoFor(entity);
		List<ModelPart> partsToHead = new ArrayList<>();

		// AgeableListModel and HierarchicalModel were removed in MC 1.21.8
		// Hat rendering for entities that used these models is temporarily disabled

		if (!partsToHead.isEmpty()) {
			partsToHead.forEach(part -> part.translateAndRotate(ms));

			ModelPart lastChild = partsToHead.get(partsToHead.size() - 1);
			if (!lastChild.isEmpty()) {
				Cube cube = lastChild.cubes.get(Mth.clamp(info.cubeIndex(), 0, lastChild.cubes.size() - 1));
				ms.translate(info.offset().x() / 16.0F, (cube.minY - cube.maxY + info.offset().y()) / 16.0F, info.offset().z() / 16.0F);
				float max = Math.max(cube.maxX - cube.minX, cube.maxZ - cube.minZ) / 8.0F * info.scale();
				ms.scale(max, max, max);
			}

			ms.scale(1, -1, -1);
			ms.translate(0, -2.25F / 16.0F, 0);
			msr.rotateXDegrees(-8.5F);
			BlockState air = Blocks.AIR.defaultBlockState();
			CachedBuffers.partial(hat, air)
				.disableDiffuse()
				.light(light)
				.renderInto(ms, buffer.getBuffer(Sheets.cutoutBlockSheet()));
		}

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
		for (LivingEntity le : mc.level.getEntitiesOfClass(LivingEntity.class, new AABB(pos.x - 0.5, pos.y - 0.25, pos.z - 0.5, pos.x + 0.5, pos.y + Math.max(state.boundingBoxHeight, 2.0), pos.z + 0.5))) {
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

		EntityModel<?> model = livingRenderer.getModel();

		CreateHatArmorLayer<?, ?> layer = new CreateHatArmorLayer<>(livingRenderer);
		livingRenderer.addLayer((CreateHatArmorLayer) layer);
	}
}

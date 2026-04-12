package com.simibubi.create.content.equipment.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Renders the Backtank 3D model on the player's (and other living entities') back.
 *
 * <p>In 1.21.2+, {@link RenderLayer} was refactored to use an {@code EntityRenderState} generic
 * instead of the live entity. The render method no longer receives the entity directly.
 *
 * <p>TODO: Create a custom {@code LivingEntityRenderState} subclass (or extend
 * {@code PlayerRenderState}) that carries the backtank item information extracted from the
 * live entity in {@code extractRenderState}. For now this class does a lookup using the
 * entity stored on the parent model, which is a temporary workaround.
 */
public class BacktankArmorLayer<S extends LivingEntityRenderState, M extends EntityModel<S>>
        extends RenderLayer<S, M> {

    public BacktankArmorLayer(RenderLayerParent<S, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack ms, MultiBufferSource buffer, int light, S state,
                       float netHeadYaw, float headPitch) {
        // TODO: In 1.21.2+, retrieve backtank info from render state rather than live entity.
        // The following is a placeholder that will need proper render-state integration.
        // The backtank rendering body is disabled until the EntityRenderState migration is
        // complete and Flywheel 1.21.8 is available.
    }

    public static void registerOnAll(EntityRenderDispatcher renderManager) {
        // TODO: In 1.21.2+, EntityRenderDispatcher API changed. The getSkinMap()/getRenderers()
        // approach still broadly applies, but the generics changed.
        // Re-enable once EntityRenderState migration is complete.
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void registerOn(EntityRenderer<?, ?> entityRenderer) {
        if (!(entityRenderer instanceof LivingEntityRenderer<?, ?, ?> livingRenderer))
            return;
        if (!(livingRenderer.getModel() instanceof HumanoidModel))
            return;
        BacktankArmorLayer<?, ?> layer = new BacktankArmorLayer<>(livingRenderer);
        livingRenderer.addLayer((BacktankArmorLayer) layer);
    }
}

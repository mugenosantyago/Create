package com.simibubi.create.content.equipment.potatoCannon;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileRenderMode;
import net.minecraft.world.phys.AABB;

/**
 * Renders the potato cannon's projectile in the world.
 *
 * <p>In 1.21.2+, EntityRenderer uses render states; in 1.21.4+ item rendering uses
 * {@link net.minecraft.client.renderer.item.ItemModelResolver} and {@link ItemStackRenderState}.
 */
public class PotatoProjectileRenderer
        extends EntityRenderer<PotatoProjectileEntity, PotatoProjectileRenderer.PotatoProjectileRenderState> {

    public PotatoProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public PotatoProjectileRenderState createRenderState() {
        return new PotatoProjectileRenderState();
    }

    @Override
    public void extractRenderState(PotatoProjectileEntity entity, PotatoProjectileRenderState state, float pt) {
        super.extractRenderState(entity, state, pt);
        state.item        = entity.getItem();
        state.bbYSize     = (float) entity.getBoundingBox().getYsize();
        state.renderMode  = entity.getRenderMode();
        state.partialTick = pt;
        state.entity      = entity;
    }

    @Override
    public void render(PotatoProjectileRenderState state, PoseStack ms, MultiBufferSource buffer, int light) {
        ItemStack item = state.item;
        if (item == null || item.isEmpty())
            return;

        ms.pushPose();
        ms.translate(0, state.bbYSize / 2 - 1 / 8f, 0);
        if (state.renderMode != null && state.entity != null)
            state.renderMode.transform(ms, state.entity, state.partialTick);

        // In 1.21.4+, items are rendered via ItemModelResolver + ItemStackRenderState.
        ItemStackRenderState itemState = new ItemStackRenderState();
        Minecraft.getInstance()
            .getItemModelResolver()
            .updateForNonLiving(itemState, item, ItemDisplayContext.GROUND, state.entity);
        itemState.render(ms, buffer, light, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);

        ms.popPose();
    }

    public static class PotatoProjectileRenderState extends EntityRenderState {
        public ItemStack item = ItemStack.EMPTY;
        public float bbYSize;
        public PotatoProjectileRenderMode renderMode;
        public float partialTick;
        /** Used by {@link com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileRenderMode} transforms. */
        public PotatoProjectileEntity entity;
    }
}

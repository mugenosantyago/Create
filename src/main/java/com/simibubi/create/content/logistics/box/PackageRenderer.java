package com.simibubi.create.content.logistics.box;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;

import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

/**
 * Renders the PackageEntity in the world.
 *
 * <p>In 1.21.2+, {@link EntityRenderer} was refactored to use a render-state pattern:
 * entity data is extracted once in {@link #extractRenderState} and stored in a
 * {@link PackageRenderState}, then consumed in {@link #render}.
 */
public class PackageRenderer extends EntityRenderer<PackageEntity, PackageRenderer.PackageRenderState> {

    public PackageRenderer(Context pContext) {
        super(pContext);
        shadowRadius = 0.5f;
    }

    @Override
    public PackageRenderState createRenderState() {
        return new PackageRenderState();
    }

    @Override
    public void extractRenderState(PackageEntity entity, PackageRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.box = entity.box;
        state.yRot = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        // Store entity id for nudge()
        state.entityId = entity.getId();
    }

    @Override
    public void render(PackageRenderState state, PoseStack ms, MultiBufferSource buffer, int light) {
        // TODO: VisualizationManager lookup needs entity, not render state.
        // Using state.box directly - visualization check skipped until entity lookup is resolved.
        ItemStack box = state.box;
        if (box == null || box.isEmpty() || !PackageItem.isPackage(box))
            box = AllBlocks.CARDBOARD_BLOCK.asStack();
        PartialModel model = AllPartialModels.PACKAGES.get(BuiltInRegistries.ITEM.getKey(box.getItem()));
        renderBox(state, ms, buffer, light, model);
        super.render(state, ms, buffer, light);
    }

    public static void renderBox(PackageRenderState state, PoseStack ms, MultiBufferSource buffer, int light,
        PartialModel model) {
        if (model == null)
            return;
        SuperByteBuffer sbb = CachedBuffers.partial(model, Blocks.AIR.defaultBlockState());
        sbb.translate(-.5, 0, -.5)
            .rotateCentered(-AngleHelper.rad(state.yRot + 90), Direction.UP)
            .light(light)
            .nudge(state.entityId);
        sbb.renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }

    public static class PackageRenderState extends EntityRenderState {
        public ItemStack box = ItemStack.EMPTY;
        public int entityId;
        public float yRot;
    }
}

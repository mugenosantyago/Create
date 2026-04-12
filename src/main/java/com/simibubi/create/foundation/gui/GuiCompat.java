package com.simibubi.create.foundation.gui;

import java.util.Collections;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * Compatibility shim for MC 1.21.8 GUI API changes.
 */
@SuppressWarnings({"unchecked", "all"})
public class GuiCompat {

    /**
     * Returns a PoseStack from GuiGraphics for backward compatibility.
     */
    @SuppressWarnings("unchecked")
    public static PoseStack poseStack(GuiGraphics graphics) {
        return (PoseStack)(Object) graphics.pose();
    }

    /**
     * Renders a component tooltip. In MC 1.21.8, renderComponentTooltip was removed.
     * Uses setTooltipForNextFrame as replacement.
     */
    public static void renderComponentTooltip(GuiGraphics graphics, Font font, List<? extends Component> components, int x, int y) {
        graphics.setComponentTooltipForNextFrame(font, (List) components, x, y, ItemStack.EMPTY);
    }

    /**
     * Compatibility blit methods for ResourceLocation-based blitting.
     * In MC 1.21.8, blit(ResourceLocation, ...) was changed to require RenderPipeline as first arg.
     */
    public static void blit(GuiGraphics graphics, net.minecraft.resources.ResourceLocation loc, int x, int y, int z, int uOffset, int vOffset, int uWidth, int vHeight, int texW, int texH) {
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, loc, x, y, (float)((float)uOffset), (float)((float)vOffset), uWidth, vHeight, texW, texH, 256, 256);
    }

    public static void blit(GuiGraphics graphics, net.minecraft.resources.ResourceLocation loc, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight, int texW, int texH) {
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, loc, x, y, (float)((float)uOffset), (float)((float)vOffset), uWidth, vHeight, texW, texH, 256, 256);
    }

    public static void blit(GuiGraphics graphics, net.minecraft.resources.ResourceLocation loc, int x, int y, int uOffset, int vOffset, int width, int height) {
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, loc, x, y, (float)((float)uOffset), (float)((float)vOffset), width, height, 256, 256, 256, 256);
    }
}

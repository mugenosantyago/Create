package com.simibubi.create.foundation.gui;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * Compatibility shim for MC 1.21.8 GUI API changes.
 */
@SuppressWarnings({"unchecked", "all"})
public class GuiCompat {

    private static final ResourceLocation TOOLTIP_BACKGROUND_SPRITE =
        ResourceLocation.withDefaultNamespace("textures/gui/sprites/tooltip/background");

    /**
     * Returns a PoseStack from GuiGraphics for backward compatibility.
     */
    @SuppressWarnings("unchecked")
    public static PoseStack poseStack(GuiGraphics graphics) {
        return (PoseStack)(Object) graphics.pose();
    }

    /**
     * Renders a component tooltip immediately (MC 1.21.8 {@link GuiGraphics#renderTooltip} API).
     */
    public static void renderComponentTooltip(GuiGraphics graphics, Font font, List<? extends Component> components, int x, int y) {
        List<ClientTooltipComponent> list = new ArrayList<>(components.size());
        for (Component c : components) {
            list.add(ClientTooltipComponent.create(c.getVisualOrderText()));
        }
        graphics.renderTooltip(font, list, x, y, DefaultTooltipPositioner.INSTANCE, TOOLTIP_BACKGROUND_SPRITE);
    }

    /**
     * Renders the vanilla item tooltip at the given mouse position.
     */
    public static void renderTooltipForItem(GuiGraphics graphics, Font font, ItemStack stack, int mouseX, int mouseY) {
        List<Component> lines = Screen.getTooltipFromItem(Minecraft.getInstance(), stack);
        List<ClientTooltipComponent> list = new ArrayList<>(lines.size());
        for (Component c : lines) {
            list.add(ClientTooltipComponent.create(c.getVisualOrderText()));
        }
        graphics.renderTooltip(font, list, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, TOOLTIP_BACKGROUND_SPRITE, stack);
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

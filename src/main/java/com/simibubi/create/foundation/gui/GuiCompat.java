package com.simibubi.create.foundation.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector4f;

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

    /**
     * Fills a convex triangle in GUI space (MC 1.21.8+ replacement for immediate-mode POSITION_COLOR draws).
     */
    public static void fillTriangle(GuiGraphics graphics, Matrix4f pose, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, int color) {
        Vector4f a = new Vector4f(x1, y1, z1, 1f);
        Vector4f b = new Vector4f(x2, y2, z2, 1f);
        Vector4f c = new Vector4f(x3, y3, z3, 1f);
        pose.transform(a);
        pose.transform(b);
        pose.transform(c);
        fillTriangleScreen(graphics, a.x, a.y, b.x, b.y, c.x, c.y, color);
    }

    private static void fillTriangleScreen(GuiGraphics graphics, float x1, float y1, float x2, float y2, float x3, float y3, int color) {
        float minY = Math.min(y1, Math.min(y2, y3));
        float maxY = Math.max(y1, Math.max(y2, y3));
        int iy0 = (int) Math.floor(minY);
        int iy1 = (int) Math.ceil(maxY);
        List<Float> xs = new ArrayList<>(3);
        for (int y = iy0; y < iy1; y++) {
            float cy = y + 0.5f;
            xs.clear();
            addEdgeXAtY(x1, y1, x2, y2, cy, xs);
            addEdgeXAtY(x2, y2, x3, y3, cy, xs);
            addEdgeXAtY(x3, y3, x1, y1, cy, xs);
            if (xs.size() < 2)
                continue;
            Collections.sort(xs);
            for (int i = 0; i + 1 < xs.size(); i += 2) {
                int xl = (int) Math.floor(xs.get(i));
                int xr = (int) Math.ceil(xs.get(i + 1));
                if (xr > xl)
                    graphics.fill(xl, y, xr, y + 1, color);
            }
        }
    }

    private static void addEdgeXAtY(float x1, float y1, float x2, float y2, float cy, List<Float> xs) {
        float minY = Math.min(y1, y2);
        float maxY = Math.max(y1, y2);
        if (cy < minY || cy > maxY || Math.abs(maxY - minY) < 1e-6f)
            return;
        float t = (cy - y1) / (y2 - y1);
        if (t >= 0f && t <= 1f)
            xs.add(x1 + t * (x2 - x1));
    }
}

package com.simibubi.create.compat.jei;

import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;

public class ScreenResourceWrapper implements IDrawable {

    private AllGuiTextures resource;

    public ScreenResourceWrapper(AllGuiTextures resource) {
        this.resource = resource;
    }

    @Override
    public int getWidth() {
        return resource.getWidth();
    }

    @Override
    public int getHeight() {
        return resource.getHeight();
    }

    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        // In 1.21.6+, blit requires RenderPipeline; blitOffset is removed; PNG size required.
        graphics.blit(RenderPipelines.GUI_TEXTURED, resource.location, xOffset, yOffset,
            (float) resource.getStartX(), (float) resource.getStartY(),
            resource.getWidth(), resource.getHeight(), 256, 256);
    }
}

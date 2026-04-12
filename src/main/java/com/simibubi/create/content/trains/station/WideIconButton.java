package com.simibubi.create.content.trains.station;

import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.widget.IconButton;

import net.createmod.catnip.gui.element.ScreenElement;
import net.minecraft.client.gui.GuiGraphics;

public class WideIconButton extends IconButton {

	public WideIconButton(int x, int y, ScreenElement icon) {
		super(x, y, 26, 18, icon);
	}

	@Override
	protected void drawBg(GuiGraphics graphics, AllGuiTextures button) {
		super.drawBg(graphics, button);
		graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, button.location, getX() + 9, getY(), (float)(button.getStartX() + 1), (float)(button.getStartY()), button.getWidth() - 1, button.getHeight(), 256, 256);
	}

}

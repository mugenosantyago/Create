package com.simibubi.create.foundation.gui.widget;

import net.minecraft.client.gui.GuiGraphics;

/**
 * A set of widgets that are offset on the Z axis, allowing them to render above/below other "layers".
 */
public class ScreenOverlay extends CompositeWidget {
	public final int zOffset;

	public ScreenOverlay(int zOffset) {
		this.zOffset = zOffset;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		com.simibubi.create.foundation.gui.GuiCompat.poseStack(graphics).pushPose();
		com.simibubi.create.foundation.gui.GuiCompat.poseStack(graphics).translate(0, 0, this.zOffset);
		super.render(graphics, mouseX, mouseY, partialTicks);
		com.simibubi.create.foundation.gui.GuiCompat.poseStack(graphics).popPose();
	}
}

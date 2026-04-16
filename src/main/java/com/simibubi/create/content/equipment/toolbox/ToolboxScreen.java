package com.simibubi.create.content.equipment.toolbox;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import net.createmod.catnip.platform.CatnipServices;
import com.simibubi.create.foundation.utility.CreateLang;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.data.Iterate;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public class ToolboxScreen extends AbstractSimiContainerScreen<ToolboxMenu> {

	protected static final AllGuiTextures BG = AllGuiTextures.TOOLBOX;
	protected static final AllGuiTextures PLAYER = AllGuiTextures.PLAYER_INVENTORY;

	protected Slot hoveredToolboxSlot;
	private IconButton confirmButton;
	private IconButton disposeButton;
	private DyeColor color;

	private List<Rect2i> extraAreas = Collections.emptyList();

	public ToolboxScreen(ToolboxMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
		init();
	}

	@Override
	protected void init() {
		setWindowSize(30 + BG.getWidth(), BG.getHeight() + PLAYER.getHeight() - 24);
		setWindowOffset(-11, 0);
		super.init();
		clearWidgets();

		color = menu.contentHolder.getColor();

		confirmButton = new IconButton(leftPos + 30 + BG.getWidth() - 33, topPos + BG.getHeight() - 24, AllIcons.I_CONFIRM);
		confirmButton.withCallback(() -> {
			minecraft.player.closeContainer();
		});
		addRenderableWidget(confirmButton);

		disposeButton = new IconButton(leftPos + 30 + 81, topPos + 69, AllIcons.I_TOOLBOX);
		disposeButton.withCallback(() -> {
			CatnipServices.NETWORK.sendToServer(new ToolboxDisposeAllPacket(menu.contentHolder.getBlockPos()));
		});
		disposeButton.setToolTip(CreateLang.translateDirect("toolbox.depositBox"));
		addRenderableWidget(disposeButton);

		extraAreas = ImmutableList.of(
			new Rect2i(leftPos + 30 + BG.getWidth(), topPos + BG.getHeight() - 15 - 34 - 6, 72, 68)
		);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		menu.renderPass = true;
		super.render(graphics, mouseX, mouseY, partialTicks);
		menu.renderPass = false;
		// Last pass so slots, 3D preview, widgets, and tooltips cannot paint over the strip.
		renderDepositLabel(graphics);
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
		int x = leftPos + imageWidth - BG.getWidth();
		int y = topPos;

		BG.render(graphics, x, y);
		// 1.21.6+ requires full ARGB; bare 0xRRGGBB is treated as alpha=0 and text disappears.
		graphics.drawString(font, title, x + 15, y + 4, 0xFF592424, false);

		int invX = leftPos;
		int invY = topPos + imageHeight - PLAYER.getHeight();
		renderPlayerInventory(graphics, invX, invY);

		renderToolbox(graphics, x + BG.getWidth() + 50, y + BG.getHeight() + 12, partialTicks);

		PoseStack ms = com.simibubi.create.foundation.gui.GuiCompat.poseStack(graphics);

		hoveredToolboxSlot = null;
		for (int compartment = 0; compartment < 8; compartment++) {
			int baseIndex = compartment * ToolboxInventory.STACKS_PER_COMPARTMENT;
			Slot slot = menu.slots.get(baseIndex);
			ItemStack itemstack = slot.getItem();
			int i = slot.x + leftPos;
			int j = slot.y + topPos;

			if (itemstack.isEmpty())
				itemstack = menu.getFilter(compartment);

			if (!itemstack.isEmpty()) {
				int count = menu.totalCountInCompartment(compartment);
				String s = String.valueOf(count);
				ms.pushPose();
				ms.translate(0, 0, 100);
				graphics.renderItem(minecraft.player, itemstack, i, j, 0);
				graphics.renderItemDecorations(font, itemstack, i, j, s);
				ms.popPose();
			}

			if (isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY)) {
				hoveredToolboxSlot = slot;
				int slotColor = this.getSlotColor(baseIndex);
				graphics.fillGradient(i, j, i + 16, j + 16, slotColor, slotColor);
			}
		}
	}

	private void renderDepositLabel(GuiGraphics graphics) {
		Component depositLabel = CreateLang.translateDirect("toolbox.depositBox");
		int depositCenterX = leftPos + 30 + 81 + 9;
		int depositTextY = topPos + 69 + 18 + 3;
		int textHalf = font.width(depositLabel) / 2;
		int pad = 3;
		graphics.fill(depositCenterX - textHalf - pad, depositTextY - 2, depositCenterX + textHalf + pad,
			depositTextY + font.lineHeight + 1, 0xFF2A2A2E);
		graphics.drawString(font, depositLabel, depositCenterX - textHalf, depositTextY, AllGuiTextures.FONT_COLOR,
			false);
	}

	private void renderToolbox(GuiGraphics graphics, int x, int y, float partialTicks) {
        PoseStack ms = com.simibubi.create.foundation.gui.GuiCompat.poseStack(graphics);
		TransformStack.of(ms)
			.pushPose()
			.translate(x, y, 100)
			.scale(50)
			.rotateXDegrees(-22)
			.rotateYDegrees(-202);

		GuiGameElement.of(AllBlocks.TOOLBOXES.get(color)
			.getDefaultState())
			.render(graphics);

        TransformStack.of(ms)
			.pushPose()
			.translate(0, -6 / 16f, 12 / 16f)
			.rotateXDegrees(-105 * menu.contentHolder.lid.getValue(partialTicks))
			.translate(0, 6 / 16f, -12 / 16f);
		GuiGameElement.of(AllPartialModels.TOOLBOX_LIDS.get(color))
			.render(graphics);
		ms.popPose();

		for (int offset : Iterate.zeroAndOne) {
			ms.pushPose();
			ms.translate(0, -offset * 1 / 8f,
				menu.contentHolder.drawers.getValue(partialTicks) * -.175f * (2 - offset));
			GuiGameElement.of(AllPartialModels.TOOLBOX_DRAWER)
				.render(graphics);
			ms.popPose();
		}
		ms.popPose();
	}

	@Override
	protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		if (hoveredToolboxSlot != null)
			hoveredSlot = hoveredToolboxSlot;
		super.renderForeground(graphics, mouseX, mouseY, partialTicks);
	}

	@Override
	public List<Rect2i> getExtraAreas() {
		return extraAreas;
	}

}

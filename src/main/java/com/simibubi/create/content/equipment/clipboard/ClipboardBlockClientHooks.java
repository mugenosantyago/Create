package com.simibubi.create.content.equipment.clipboard;

import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.entity.player.Player;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ClipboardBlockClientHooks {

	private ClipboardBlockClientHooks() {
	}

	public static void openScreen(Player player, DataComponentMap components, BlockPos pos) {
		if (Minecraft.getInstance().player == player)
			ScreenOpener.open(new ClipboardScreen(player.getInventory().getSelectedSlot(), components, pos));
	}
}

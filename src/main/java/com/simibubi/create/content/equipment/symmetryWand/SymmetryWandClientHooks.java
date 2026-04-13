package com.simibubi.create.content.equipment.symmetryWand;

import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

/**
 * Client-only GUI entry for {@link SymmetryWandItem}. Loaded via reflection from the item so common code does not
 * reference {@link net.minecraft.client.gui.screens.Screen}.
 */
public final class SymmetryWandClientHooks {

	private SymmetryWandClientHooks() {}

	public static void openWandGui(ItemStack wand, InteractionHand hand) {
		ScreenOpener.open(new SymmetryWandScreen(wand, hand));
	}
}

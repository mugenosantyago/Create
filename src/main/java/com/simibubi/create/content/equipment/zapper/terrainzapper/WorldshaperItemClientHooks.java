package com.simibubi.create.content.equipment.zapper.terrainzapper;

import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

/**
 * Client-only GUI entry for {@link WorldshaperItem}. Loaded via reflection so the item class does not reference
 * {@link net.minecraft.client.gui.screens.Screen}.
 */
public final class WorldshaperItemClientHooks {

	private WorldshaperItemClientHooks() {}

	public static void openHandgunGui(ItemStack item, InteractionHand hand) {
		ScreenOpener.open(new WorldshaperScreen(item, hand));
	}
}

package com.simibubi.create.content.schematics;

import com.simibubi.create.content.schematics.client.SchematicEditScreen;

import net.createmod.catnip.gui.ScreenOpener;

/**
 * Client-only; invoked via reflection from {@link SchematicItem} so that class does not reference
 * {@link net.minecraft.client.gui.screens.Screen}.
 */
public final class SchematicItemClientHooks {

	private SchematicItemClientHooks() {}

	public static void openEditScreen() {
		ScreenOpener.open(new SchematicEditScreen());
	}
}

package com.simibubi.create.content.logistics;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.simibubi.create.content.equipment.clipboard.ClipboardBlockEntity;
import com.simibubi.create.content.trains.schedule.DestinationSuggestions;

import net.createmod.catnip.data.IntAttached;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

/**
 * Client-only helpers that reference {@link Screen}. Kept separate so {@link AddressEditBoxHelper} can load on dedicated servers.
 */
public final class AddressEditBoxHelperClient {

	private AddressEditBoxHelperClient() {
	}

	public static DestinationSuggestions createSuggestions(Screen screen, EditBox pInput, boolean anchorToBottom,
		String localAddress) {
		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		List<IntAttached<String>> options = new ArrayList<>();
		Set<String> alreadyAdded = new HashSet<>();

		DestinationSuggestions destinationSuggestions = new DestinationSuggestions(mc, screen, pInput, mc.font, options,
			anchorToBottom, -72 + pInput.getY() + (anchorToBottom ? 0 : pInput.getHeight()));

		if (player == null)
			return destinationSuggestions;

		if (localAddress != null) {
			options.add(IntAttached.with(-1, localAddress));
			alreadyAdded.add(localAddress);
		}

		for (int i = 0; i < Inventory.INVENTORY_SIZE; i++)
			AddressEditBoxHelper.appendAddresses(options, alreadyAdded, player.getInventory().getItem(i));

		for (WeakReference<ClipboardBlockEntity> wr : AddressEditBoxHelper.NEARBY_CLIPBOARDS.asMap()
			.values()) {
			ClipboardBlockEntity cbe = wr.get();
			if (cbe != null)
				AddressEditBoxHelper.appendAddresses(options, alreadyAdded, cbe.components());
		}

		return destinationSuggestions;
	}
}

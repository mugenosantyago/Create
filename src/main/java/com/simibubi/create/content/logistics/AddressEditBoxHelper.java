package com.simibubi.create.content.logistics;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;

import com.google.common.cache.Cache;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.clipboard.ClipboardBlockEntity;
import com.simibubi.create.content.equipment.clipboard.ClipboardEntry;
import com.simibubi.create.foundation.utility.TickBasedCache;

import net.createmod.catnip.data.IntAttached;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class AddressEditBoxHelper {

	static final Cache<BlockPos, WeakReference<ClipboardBlockEntity>> NEARBY_CLIPBOARDS = new TickBasedCache<>(20, false);

	public static void advertiseClipboard(ClipboardBlockEntity blockEntity) {
		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		if (player == null)
			return;
		BlockPos blockPos = blockEntity.getBlockPos();
		if (player.distanceToSqr(Vec3.atCenterOf(blockPos)) > 32 * 32)
			return;
		NEARBY_CLIPBOARDS.put(blockPos, new WeakReference<>(blockEntity));
	}

	static void appendAddresses(List<IntAttached<String>> options, Set<String> alreadyAdded, ItemStack item) {
		if (item == null || !AllBlocks.CLIPBOARD.isIn(item))
			return;

		appendAddresses(options, alreadyAdded, item.getComponents());
	}

	static void appendAddresses(List<IntAttached<String>> options, Set<String> alreadyAdded, DataComponentMap components) {
		List<List<ClipboardEntry>> pages = ClipboardEntry.readAll(components);
		pages.forEach(page -> page.forEach(entry -> {
			String string = entry.text.getString();
			if (entry.checked)
				return;
			if (!string.startsWith("#") || string.length() == 1)
				return;
			String address = string.substring(1);
			if (address.isBlank())
				return;
			String trim = address.trim();
			if (!alreadyAdded.add(trim))
				return;
			options.add(IntAttached.withZero(trim));
		}));
	}

}

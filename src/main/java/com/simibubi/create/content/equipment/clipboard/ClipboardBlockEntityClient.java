package com.simibubi.create.content.equipment.clipboard;

import java.lang.reflect.Method;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.logistics.AddressEditBoxHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Client-only logic for {@link ClipboardBlockEntity} so the common class does not reference {@link Minecraft}.
 */
@OnlyIn(Dist.CLIENT)
public final class ClipboardBlockEntityClient {

	private ClipboardBlockEntityClient() {}

	public static void readClientSide(ClipboardBlockEntity be, CompoundTag tag) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.screen == null
			|| !"com.simibubi.create.content.equipment.clipboard.ClipboardScreen".equals(mc.screen.getClass().getName()))
			return;
		Object screen = mc.screen;
		if (tag.contains("LastEdit") && tag.getIntArray("LastEdit").map(net.minecraft.core.UUIDUtil::uuidFromIntArray).orElse(null)
			.equals(mc.player.getUUID()))
			return;
		try {
			BlockPos targeted = (BlockPos) screen.getClass().getField("targetedBlock").get(screen);
			if (!be.getBlockPos().equals(targeted))
				return;
			Method reopenWith = screen.getClass().getMethod("reopenWith", ClipboardContent.class);
			reopenWith.invoke(screen, be.components().getOrDefault(AllDataComponents.CLIPBOARD_CONTENT, ClipboardContent.EMPTY));
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	public static void advertiseToAddressHelper(ClipboardBlockEntity be) {
		AddressEditBoxHelper.advertiseClipboard(be);
	}
}

package com.simibubi.create.content.redstone.displayLink;

import com.simibubi.create.foundation.utility.CreateLang;

import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class DisplayLinkBlockClientHooks {

	private DisplayLinkBlockClientHooks() {
	}

	public static void openScreen(Level level, BlockPos pos, Player player) {
		if (!(player instanceof LocalPlayer))
			return;
		BlockState state = level.getBlockState(pos);
		if (!(state.getBlock() instanceof DisplayLinkBlock block))
			return;
		block.withBlockEntityDo(level, pos, be -> {
			if (be.targetOffset.equals(BlockPos.ZERO)) {
				player.displayClientMessage(CreateLang.translateDirect("display_link.invalid"), true);
				return;
			}
			ScreenOpener.open(new DisplayLinkScreen(be));
		});
	}
}

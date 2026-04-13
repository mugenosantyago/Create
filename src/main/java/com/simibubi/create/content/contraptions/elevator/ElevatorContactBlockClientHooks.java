package com.simibubi.create.content.contraptions.elevator;

import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ElevatorContactBlockClientHooks {

	private ElevatorContactBlockClientHooks() {
	}

	public static void openScreen(Level level, BlockPos pos, Player player) {
		if (!(player instanceof LocalPlayer))
			return;
		BlockState state = level.getBlockState(pos);
		if (!(state.getBlock() instanceof ElevatorContactBlock block))
			return;
		block.withBlockEntityDo(level, pos, be -> ScreenOpener.open(
			new ElevatorContactScreen(be.getBlockPos(), be.shortName, be.longName, be.doorControls.mode)));
	}
}

package com.simibubi.create.content.trains.station;

import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class StationBlockClientHooks {

	private StationBlockClientHooks() {
	}

	public static void openScreen(Level level, BlockPos pos, Player player) {
		if (!(player instanceof LocalPlayer))
			return;
		BlockState placedState = level.getBlockState(pos);
		if (!(placedState.getBlock() instanceof StationBlock stationBlock))
			return;
		stationBlock.withBlockEntityDo(level, pos, be -> {
			GlobalStation station = be.getStation();
			BlockState blockState = be.getBlockState();
			if (station == null || blockState == null)
				return;
			boolean assembling = blockState.getBlock() == stationBlock && blockState.getValue(StationBlock.ASSEMBLING);
			ScreenOpener.open(assembling ? new AssemblyScreen(be, station) : new StationScreen(be, station));
		});
	}
}

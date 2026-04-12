package com.simibubi.create.compat.farmersdelight;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import net.minecraft.world.level.block.state.BlockState;

public class FarmersDelightCompat {
	public static boolean shouldHarvestMushroom(Level world, BlockPos pos, BlockState state) {
		// Farmers Delight not available for 1.21.8 yet
		return true;
	}
}

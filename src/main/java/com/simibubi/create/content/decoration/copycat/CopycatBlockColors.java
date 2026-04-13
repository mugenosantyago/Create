package com.simibubi.create.content.decoration.copycat;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Block tint for copycat blocks — separated from {@link CopycatBlock} so the base class does not
 * reference {@link BlockColor} (dedicated servers cannot load client-only types when copycat blocks register).
 */
public final class CopycatBlockColors {

	private CopycatBlockColors() {
	}

	@OnlyIn(Dist.CLIENT)
	public static BlockColor wrappedColor() {
		return new WrappedBlockColor();
	}

	@OnlyIn(Dist.CLIENT)
	public static class WrappedBlockColor implements BlockColor {

		@Override
		public int getColor(BlockState pState, @Nullable BlockAndTintGetter pLevel, @Nullable BlockPos pPos,
			int pTintIndex) {
			if (pLevel == null || pPos == null)
				return GrassColor.get(0.5D, 1.0D);
			return Minecraft.getInstance()
				.getBlockColors()
				.getColor(CopycatBlock.getMaterial(pLevel, pPos), pLevel, pPos, pTintIndex);
		}
	}
}

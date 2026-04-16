package com.simibubi.create.content.redstone.displayLink;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.api.behaviour.display.DisplayTarget;

import net.createmod.catnip.outliner.Outliner;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ClickToLinkBlockItemClientHooks {

	private static BlockPos lastShownPos;
	private static AABB lastShownAABB;

	private ClickToLinkBlockItemClientHooks() {
	}

	public static void clientTick() {
		Player player = Minecraft.getInstance().player;
		if (player == null)
			return;
		ItemStack heldItemMainhand = player.getMainHandItem();
		if (!(heldItemMainhand.getItem() instanceof ClickToLinkBlockItem blockItem))
			return;
		if (!heldItemMainhand.has(AllDataComponents.CLICK_TO_LINK_DATA))
			return;

		BlockPos selectedPos = heldItemMainhand.get(AllDataComponents.CLICK_TO_LINK_DATA).selectedPos();

		if (!selectedPos.equals(lastShownPos)) {
			lastShownAABB = getSelectionBounds(blockItem, selectedPos);
			lastShownPos = selectedPos;
		}

		Outliner.getInstance().showAABB("target", lastShownAABB)
			.colored(0xFFffcb74)
			.lineWidth(1 / 16f);
	}

	private static AABB getSelectionBounds(ClickToLinkBlockItem item, BlockPos pos) {
		if (item instanceof DisplayLinkBlockItem)
			return getDisplayLinkSelectionBounds(pos);
		return getDefaultSelectionBounds(pos);
	}

	private static AABB getDisplayLinkSelectionBounds(BlockPos pos) {
		Level world = Minecraft.getInstance().level;
		DisplayTarget target = DisplayTarget.get(world, pos);
		if (target != null)
			return target.getMultiblockBounds(world, pos);
		return getDefaultSelectionBounds(pos);
	}

	private static AABB getDefaultSelectionBounds(BlockPos pos) {
		Level world = Minecraft.getInstance().level;
		BlockState state = world.getBlockState(pos);
		VoxelShape shape = state.getShape(world, pos);
		return shape.isEmpty() ? new AABB(BlockPos.ZERO)
			: shape.bounds()
				.move(pos);
	}
}

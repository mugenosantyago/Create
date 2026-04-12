package com.simibubi.create.content.logistics.redstoneRequester;

import java.util.List;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBlockItem;
import com.simibubi.create.foundation.utility.CreateLang;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

public class RedstoneRequesterBlockItem extends LogisticallyLinkedBlockItem {

	public RedstoneRequesterBlockItem(Block pBlock, Properties pProperties) {
		super(pBlock, pProperties);
	}

	@Override
	public void appendHoverText(ItemStack pStack, net.minecraft.world.item.Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay tooltipDisplay, java.util.function.Consumer<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
		java.util.List<net.minecraft.network.chat.Component> tooltipList = new java.util.ArrayList<>();
		appendHoverText_compat(pStack, context, tooltipList, flag);
		toolipList.forEach(tooltip);
	}

	@Override
	public void appendHoverText_compat(ItemStack stack, net.minecraft.world.item.Item.TooltipContext tooltipContext, java.util.List<net.minecraft.network.chat.Component> tooltipComponents, TooltipFlag tooltipFlag) {
		if (!isTuned(stack))
			return;

		if (!stack.has(AllDataComponents.AUTO_REQUEST_DATA)) {
			// super.appendHoverText(stack, tooltipContext, tooltipComponents, tooltipFlag);;
			return;
		}

		CreateLang.translate("logistically_linked.tooltip")
			.style(ChatFormatting.GOLD)
			.addTo(tooltipComponents);
		RedstoneRequesterBlock.appendRequesterTooltip(stack, tooltipComponents);
	}

}

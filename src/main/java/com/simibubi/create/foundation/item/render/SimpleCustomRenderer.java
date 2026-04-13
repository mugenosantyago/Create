package com.simibubi.create.foundation.item.render;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.foundation.item.CustomArmPoseItem;

import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class SimpleCustomRenderer implements IClientItemExtensions {

	protected CustomRenderedItemModelRenderer renderer;

	protected SimpleCustomRenderer(CustomRenderedItemModelRenderer renderer) {
		this.renderer = renderer;
	}

	public static SimpleCustomRenderer create(Item item, CustomRenderedItemModelRenderer renderer) {
		CustomRenderedItems.register(item);
		return new SimpleCustomRenderer(renderer);
	}

	public CustomRenderedItemModelRenderer getRenderer() {
		return renderer;
	}

	@Override
	@Nullable
	public ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
		if (itemStack.getItem() instanceof CustomArmPoseItem armPoseItem
			&& entityLiving instanceof AbstractClientPlayer clientPlayer) {
			return armPoseItem.getArmPose(itemStack, clientPlayer, hand);
		}
		return IClientItemExtensions.super.getArmPose(entityLiving, hand, itemStack);
	}

}

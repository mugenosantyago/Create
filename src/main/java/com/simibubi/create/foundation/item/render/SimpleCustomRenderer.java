package com.simibubi.create.foundation.item.render;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.content.equipment.potatoCannon.PotatoCannonItem;
import com.simibubi.create.content.equipment.zapper.ZapperItem;
import com.simibubi.create.foundation.item.CustomArmPoseItemMarker;

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
		if (!(entityLiving instanceof AbstractClientPlayer clientPlayer))
			return IClientItemExtensions.super.getArmPose(entityLiving, hand, itemStack);
		Item item = itemStack.getItem();
		if (!(item instanceof CustomArmPoseItemMarker))
			return IClientItemExtensions.super.getArmPose(entityLiving, hand, itemStack);
		if (item instanceof PotatoCannonItem || item instanceof ZapperItem) {
			if (!clientPlayer.swinging)
				return ArmPose.CROSSBOW_HOLD;
			return null;
		}
		return IClientItemExtensions.super.getArmPose(entityLiving, hand, itemStack);
	}

}

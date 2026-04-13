package com.simibubi.create.foundation.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.foundation.item.CustomUseEffectsItem;

import net.createmod.catnip.data.TriState;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(LivingEntity.class)
public abstract class CustomItemUseEffectsMixin extends Entity {
	private CustomItemUseEffectsMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@Shadow
	protected RandomSource random;

	/**
	 * MC 1.21.8 removed {@code shouldTriggerItemUseEffects} / {@code triggerItemUseEffects}; drive custom
	 * use feedback from {@link LivingEntity#updateUsingItem(ItemStack)} instead.
	 */
	@Inject(method = "updateUsingItem", at = @At("TAIL"))
	private void create$customUseEffectsAfterUpdate(ItemStack usingItem, CallbackInfo ci) {
		if (usingItem.isEmpty())
			return;
		Item item = usingItem.getItem();
		if (!(item instanceof CustomUseEffectsItem handler))
			return;
		LivingEntity self = (LivingEntity) (Object) this;
		TriState should = handler.shouldTriggerUseEffects(usingItem, self);
		if (should == TriState.FALSE)
			return;
		if (should == TriState.DEFAULT && !create$vanillaShouldTriggerItemUseEffects(usingItem))
			return;
		handler.triggerUseEffects(usingItem, self, 1, random);
	}

	/** Same tick rhythm vanilla used before 1.21.8 for eat/drink-style use animations. */
	private boolean create$vanillaShouldTriggerItemUseEffects(ItemStack usingItem) {
		LivingEntity self = (LivingEntity) (Object) this;
		int remaining = self.getUseItemRemainingTicks();
		int duration = usingItem.getUseDuration(self);
		int j = duration - remaining * 2;
		return j % 4 == 0;
	}
}

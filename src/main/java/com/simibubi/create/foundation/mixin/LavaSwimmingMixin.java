package com.simibubi.create.foundation.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.armor.DivingBootsItem;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

@Mixin(LivingEntity.class)
public abstract class LavaSwimmingMixin extends Entity {
	private LavaSwimmingMixin(EntityType<?> type, Level level) {
		super(type, level);
	}

	@Inject(method = "travelInFluid(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/level/material/FluidState;)V", at = @At("TAIL"))
	private void create$onLavaTravel(Vec3 travelVector, FluidState fluidState, CallbackInfo ci) {
		LivingEntity self = (LivingEntity) (Object) this;
		if (!self.isInLava())
			return;
		ItemStack bootsStack = DivingBootsItem.getWornItem(self);
		if (AllItems.NETHERITE_DIVING_BOOTS.isIn(bootsStack))
			self.setDeltaMovement(self.getDeltaMovement().multiply(DivingBootsItem.getMovementMultiplier(self)));
	}
}

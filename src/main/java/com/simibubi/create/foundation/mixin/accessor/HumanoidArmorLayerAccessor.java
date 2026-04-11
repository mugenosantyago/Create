package com.simibubi.create.foundation.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;

/**
 * Accessor mixin for HumanoidArmorLayer.
 *
 * <p>In 1.21.2+, HumanoidArmorLayer was refactored for the EntityRenderState system.
 * The old {@code ARMOR_LOCATION_CACHE}, {@code innerModel}, and {@code outerModel}
 * fields no longer exist in the same form. Armor textures are now data-driven via
 * {@code EquipmentModel} JSON (introduced in 1.21.4).
 *
 * <p>TODO: Re-evaluate what custom armor layer hooks are still needed under the new API
 * and re-implement this accessor targeting the appropriate 1.21.8 fields.
 */
@Mixin(HumanoidArmorLayer.class)
public interface HumanoidArmorLayerAccessor {
    // TODO: Re-add @Accessor / @Invoker targets once the 1.21.8 HumanoidArmorLayer
    // field names and render-state generic types are confirmed.
}

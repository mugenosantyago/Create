package com.simibubi.create.foundation.item;

/**
 * In 1.21.4+, the old armor-texture layer system (HumanoidArmorLayer cache, innerModel/outerModel)
 * was replaced by the data-driven {@code EquipmentModel} JSON at
 * {@code assets/<ns>/models/equipment/<id>.json}.
 *
 * <p>Multi-layer textures are now declared in the EquipmentModel JSON, so this interface is no
 * longer needed for texture layering purposes.  Custom 3D overlays (e.g. the Backtank renderer)
 * continue to use their own {@link net.minecraft.client.renderer.entity.layers.RenderLayer} subclass.
 *
 * <p>TODO: Remove all remaining usages of this interface and migrate any leftover custom
 * layer rendering to NeoForge's updated {@code HumanoidArmorLayer} API.
 *
 * @deprecated Replaced by EquipmentModel JSON in 1.21.4.
 */
@Deprecated(forRemoval = true)
public interface LayeredArmorItem {
    // Interface kept as stub to allow gradual migration.
    // The renderArmorPiece hook no longer has a stable signature to expose here.
}

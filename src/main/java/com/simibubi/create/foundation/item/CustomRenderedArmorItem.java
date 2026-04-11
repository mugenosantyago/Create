package com.simibubi.create.foundation.item;

/**
 * Marker interface for items that supply a custom armor rendering layer.
 *
 * <p>In 1.21.2+, entity rendering moved to an {@code EntityRenderState}-based system; the
 * old {@code HumanoidArmorLayer} signature no longer passes raw entity/model arguments directly.
 * Custom rendering should be done via a {@link net.minecraft.client.renderer.entity.layers.RenderLayer}
 * subclass registered on the relevant entity renderer.
 *
 * <p>TODO: Update all usages of this interface to the new EntityRenderState render layer API.
 *
 * @deprecated Superseded by the 1.21.2 EntityRenderState rendering system.
 */
@Deprecated(forRemoval = true)
public interface CustomRenderedArmorItem {
    // Interface kept as stub for gradual migration.
}

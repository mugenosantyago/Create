package com.simibubi.create.foundation.render;

import java.util.function.BiFunction;

import com.simibubi.create.Create;

import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * Custom render types for Create.
 * Note: In MC 1.21.8, RenderType.create(), ShaderStateShard and ShaderProgram were removed.
 * These are stubs using standard render types as fallbacks.
 */
@EventBusSubscriber(Dist.CLIENT)
public class RenderTypes {

    public static RenderType entitySolidBlockMipped() {
        return RenderType.ENTITY_SOLID.apply(TextureAtlas.LOCATION_BLOCKS);
    }

    public static RenderType entityCutoutBlockMipped() {
        return RenderType.ENTITY_CUTOUT_NO_CULL.apply(TextureAtlas.LOCATION_BLOCKS, false);
    }

    public static RenderType entityTranslucentBlockMipped() {
        return RenderType.ENTITY_TRANSLUCENT.apply(TextureAtlas.LOCATION_BLOCKS, false);
    }

    public static RenderType additive() {
        return RenderType.TRANSLUCENT_MOVING_BLOCK;
    }

    public static BiFunction<ResourceLocation, Boolean, RenderType> TRAIN_MAP = Util.memoize(RenderTypes::getTrainMap);

    private static RenderType getTrainMap(ResourceLocation locationIn, boolean linearFiltering) {
        return RenderType.TEXT.apply(locationIn);
    }

    public static RenderType itemGlowingSolid() {
        // Fallback to standard entity solid in 1.21.8 (glowing shader not yet ported)
        return RenderType.ENTITY_SOLID.apply(TextureAtlas.LOCATION_BLOCKS);
    }

    public static RenderType itemGlowingTranslucent() {
        // Fallback to standard entity translucent in 1.21.8
        return RenderType.ENTITY_TRANSLUCENT.apply(TextureAtlas.LOCATION_BLOCKS, false);
    }

    public static RenderType chain(ResourceLocation pLocation) {
        return RenderType.ENTITY_CUTOUT_NO_CULL.apply(pLocation, false);
    }

}

package com.simibubi.create.foundation.render;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.simibubi.create.Create;

import net.minecraft.Util;
import net.minecraft.client.renderer.CompiledShaderProgram;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.client.renderer.ShaderProgram;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

/**
 * Custom render types for Create.
 *
 * <p>In 1.21.2+:
 * <ul>
 *   <li>{@code ShaderInstance} was renamed to {@code CompiledShaderProgram}.</li>
 *   <li>A new {@code ShaderProgram} class serves as the registered identifier.</li>
 *   <li>{@code ShaderStateShard} now takes a {@code ShaderProgram} directly.</li>
 *   <li>{@code RegisterShadersEvent#registerShader} now uses the updated types.</li>
 * </ul>
 */
public class RenderTypes extends RenderStateShard {

    /** ShaderProgram definition for the glowing shader. */
    static final ShaderProgram GLOWING_SHADER_PROGRAM = new ShaderProgram(
            Create.asResource("glowing_shader"),
            DefaultVertexFormat.NEW_ENTITY,
            ShaderDefines.EMPTY
    );

    public static final ShaderStateShard GLOWING_SHADER = new ShaderStateShard(GLOWING_SHADER_PROGRAM);

    private static final RenderType ENTITY_SOLID_BLOCK_MIPPED = RenderType.create(createLayerName("entity_solid_block_mipped"),
            DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false,
            RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_ENTITY_SOLID_SHADER)
                .setTextureState(BLOCK_SHEET_MIPPED)
                .setTransparencyState(NO_TRANSPARENCY)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(true));

    private static final RenderType ENTITY_CUTOUT_BLOCK_MIPPED = RenderType.create(createLayerName("entity_cutout_block_mipped"),
            DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false,
            RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_ENTITY_CUTOUT_SHADER)
                .setTextureState(BLOCK_SHEET_MIPPED)
                .setTransparencyState(NO_TRANSPARENCY)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(true));

    private static final RenderType ENTITY_TRANSLUCENT_BLOCK_MIPPED = RenderType.create(createLayerName("entity_translucent_block_mipped"),
            DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true,
            RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER)
                .setTextureState(BLOCK_SHEET_MIPPED)
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(true));

    private static final RenderType ADDITIVE = RenderType.create(createLayerName("additive"), DefaultVertexFormat.BLOCK,
        VertexFormat.Mode.QUADS, 256, true, true, RenderType.CompositeState.builder()
            .setShaderState(RENDERTYPE_SOLID_SHADER)
            .setTextureState(BLOCK_SHEET)
            .setTransparencyState(ADDITIVE_TRANSPARENCY)
            .setCullState(NO_CULL)
            .setLightmapState(LIGHTMAP)
            .setOverlayState(OVERLAY)
            .createCompositeState(true));

    private static final RenderType ITEM_GLOWING_SOLID = RenderType.create(createLayerName("item_glowing_solid"),
        DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, RenderType.CompositeState.builder()
            .setShaderState(GLOWING_SHADER)
            .setTextureState(BLOCK_SHEET)
            .setLightmapState(LIGHTMAP)
            .setOverlayState(OVERLAY)
            .createCompositeState(true));

    private static final RenderType ITEM_GLOWING_TRANSLUCENT = RenderType.create(createLayerName("item_glowing_translucent"),
        DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, RenderType.CompositeState.builder()
            .setShaderState(GLOWING_SHADER)
            .setTextureState(BLOCK_SHEET)
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setLightmapState(LIGHTMAP)
            .setOverlayState(OVERLAY)
            .createCompositeState(true));

    private static final Function<ResourceLocation, RenderType> CHAIN = Util.memoize((p_234330_) -> {
        return RenderType.create("chain_conveyor_chain", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 256, false,
            true, RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_CUTOUT_MIPPED_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(p_234330_, false, true))
                .setTransparencyState(NO_TRANSPARENCY)
                .setWriteMaskState(COLOR_DEPTH_WRITE)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(false));
    });

    public static RenderType entitySolidBlockMipped() {
        return ENTITY_SOLID_BLOCK_MIPPED;
    }

    public static RenderType entityCutoutBlockMipped() {
        return ENTITY_CUTOUT_BLOCK_MIPPED;
    }

    public static RenderType entityTranslucentBlockMipped() {
        return ENTITY_TRANSLUCENT_BLOCK_MIPPED;
    }

    public static RenderType additive() {
        return ADDITIVE;
    }

    public static BiFunction<ResourceLocation, Boolean, RenderType> TRAIN_MAP = Util.memoize(RenderTypes::getTrainMap);

    private static RenderType getTrainMap(ResourceLocation locationIn, boolean linearFiltering) {
        RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder()
            .setShaderState(RENDERTYPE_TEXT_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(locationIn, linearFiltering, false))
            .setTransparencyState(NO_TRANSPARENCY)
            .setLightmapState(LIGHTMAP)
            .createCompositeState(false);
        return RenderType.create("create_train_map", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
            VertexFormat.Mode.QUADS, 256, false, true, rendertype$state);
    }

    public static RenderType itemGlowingSolid() {
        return ITEM_GLOWING_SOLID;
    }

    public static RenderType itemGlowingTranslucent() {
        return ITEM_GLOWING_TRANSLUCENT;
    }

    public static RenderType chain(ResourceLocation pLocation) {
        return CHAIN.apply(pLocation);
    }

    private static String createLayerName(String name) {
        return Create.ID + ":" + name;
    }

    // Mmm gimme those protected fields
    private RenderTypes() {
        super(null, null, null);
    }

    @EventBusSubscriber(Dist.CLIENT)
    private static class Shaders {
        /**
         * In 1.21.2+, RegisterShadersEvent#registerShader accepts a {@link ShaderProgram} and a
         * {@code Consumer<CompiledShaderProgram>} callback.
         * The glowing shader JSON lives at assets/create/shaders/glowing_shader.json.
         */
        @SubscribeEvent
        public static void onRegisterShaders(RegisterShadersEvent event) {
            event.registerShader(GLOWING_SHADER_PROGRAM, compiled -> {
                // compiled is a CompiledShaderProgram; no instance storage required
                // since ShaderStateShard already holds a reference to GLOWING_SHADER_PROGRAM
                // and the GPU looks it up at draw time.
            });
        }
    }
}

package net.neoforged.neoforge.client.model.generators;

import net.minecraft.resources.ResourceLocation;

/**
 * Stub for the removed NeoForge BlockModelBuilder (MC 1.21.8 port).
 * This class is a no-op stub to allow compilation only.
 */
@SuppressWarnings("all")
public class BlockModelBuilder extends ModelFile {

    public BlockModelBuilder(ResourceLocation location) {
        super(location);
    }

    public BlockModelBuilder texture(String key, ResourceLocation loc) { return this; }
    public BlockModelBuilder texture(String key, String loc) { return this; }
    public BlockModelBuilder renderType(String renderType) { return this; }
    public BlockModelBuilder renderType(ResourceLocation renderType) { return this; }
    public BlockModelBuilder transforms() { return this; }
    public BlockModelBuilder ao(boolean ao) { return this; }
    public BlockModelBuilder guiLight(String light) { return this; }

    public <L extends CustomLoaderBuilder<BlockModelBuilder>> L customLoader(java.util.function.BiFunction<BlockModelBuilder, net.neoforged.neoforge.client.model.generators.loaders.CompositeModelBuilder<BlockModelBuilder>, L> loader) {
        return null;
    }
}

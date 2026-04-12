package com.tterrag.registrate.providers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;

import com.tterrag.registrate.providers.generators.RegistrateBlockModelGenerator;

import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.BiConsumer;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Compatibility stub for old RegistrateBlockstateProvider API.
 * Now extends RegistrateBlockModelGenerator for MC 1.21.8 compatibility.
 * Data gen methods are stubbed.
 */
@SuppressWarnings("all")
public class RegistrateBlockstateProvider extends RegistrateBlockModelGenerator {

    private final String modId;
    private final BlockModelProvider models;

    @SuppressWarnings("unchecked")
    public RegistrateBlockstateProvider(String modId) {
        super(null, definitionGenerator -> {}, loc -> {}, (location, model) -> {});
        this.modId = modId;
        this.models = new BlockModelProvider(ResourceLocation.parse(modId + ":"));
    }

    public BlockModelProvider models() { return models; }

    public ResourceLocation modLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(modId, path);
    }

    public ResourceLocation mcLoc(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }

    public ResourceLocation blockTexture(Block block) {
        return ResourceLocation.fromNamespaceAndPath(modId, "block/" + block);
    }

    public void simpleBlock(Block block, ModelFile model) {}
    public void simpleBlock(Block block, ConfiguredModel... models) {}
    public void simpleBlock(Block block) {}

    public void directionalBlock(Block block, Function<BlockState, ModelFile> modelFunc) {}
    public void directionalBlock(Block block, ModelFile model) {}

    public void horizontalBlock(Block block, Function<BlockState, ModelFile> modelFunc) {}
    public void horizontalBlock(Block block, ModelFile model) {}

    public void horizontalFaceBlock(Block block, Function<BlockState, ModelFile> modelFunc) {}
    public void horizontalFaceBlock(Block block, ModelFile model) {}

    public void axisBlock(Block block, Function<BlockState, ModelFile> modelFunc) {}
    public void axisBlock(Block block, ModelFile model) {}

    public ModelFile getExistingFile(ResourceLocation loc) {
        return new ModelFile.ExistingModelFile(loc, null);
    }

    public VariantBlockStateBuilder getVariantBuilder(Block block) {
        return new VariantBlockStateBuilder();
    }

    public MultiPartBlockStateBuilder getMultipartBuilder(Block block) {
        return new MultiPartBlockStateBuilder();
    }

    public ModelBuilder withExistingParent(String name, ResourceLocation parent) {
        return new ModelBuilder();
    }

    public ModelBuilder withExistingParent(String name, String parent) {
        return new ModelBuilder();
    }

    public static class ModelBuilder {
        public ModelBuilder texture(String key, ResourceLocation loc) { return this; }
        public ModelBuilder texture(String key, String loc) { return this; }
        public ModelBuilder renderType(String renderType) { return this; }
        public ModelFile build() { return new ModelFile(ResourceLocation.parse("create:stub")); }
    }

    public static class VariantBlockStateBuilder {
        public PartialVariantBuilder partialState() { return new PartialVariantBuilder(this); }
        public ForAllVariantBuilder forAllStates(Function<BlockState, ConfiguredModel[]> func) { return new ForAllVariantBuilder(this); }
        public ForAllVariantBuilder forAllStatesExcept(Function<BlockState, ConfiguredModel[]> func, net.minecraft.world.level.block.state.properties.Property<?>... ignored) { return new ForAllVariantBuilder(this); }
    }

    public static class PartialVariantBuilder {
        private final VariantBlockStateBuilder parent;
        public PartialVariantBuilder(VariantBlockStateBuilder parent) { this.parent = parent; }
        public PartialVariantBuilder with(net.minecraft.world.level.block.state.properties.Property<?> prop, Comparable<?> value) { return this; }
        public ConfiguredModel.Builder<?> modelForState() { return ConfiguredModel.builder(); }
        public VariantBlockStateBuilder addModels(ConfiguredModel... models) { return parent; }
    }

    public static class ForAllVariantBuilder {
        private final VariantBlockStateBuilder parent;
        public ForAllVariantBuilder(VariantBlockStateBuilder parent) { this.parent = parent; }
    }
}

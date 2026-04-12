package com.tterrag.registrate.providers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import net.neoforged.neoforge.client.model.generators.ModelFile;

/**
 * Stub for removed RegistrateItemModelProvider (MC 1.21.8 port).
 */
@SuppressWarnings("all")
public class RegistrateItemModelProvider {

    private final String modId;

    public RegistrateItemModelProvider(String modId) {
        this.modId = modId;
    }

    public ResourceLocation modLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(modId, path);
    }

    public ResourceLocation mcLoc(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }

    public ModelFile getExistingFile(ResourceLocation loc) {
        return new ModelFile.ExistingModelFile(loc, null);
    }

    public ModelBuilder generated(java.util.function.Supplier<?> item) {
        return new ModelBuilder();
    }

    public ModelBuilder withExistingParent(String name, ResourceLocation parent) {
        return new ModelBuilder();
    }

    public ModelBuilder withExistingParent(String name, String parent) {
        return new ModelBuilder();
    }

    public ModelBuilder getBuilder(String name) {
        return new ModelBuilder();
    }

    public static class ModelBuilder {
        public ModelBuilder texture(String key, ResourceLocation loc) { return this; }
        public ModelBuilder texture(String key, String loc) { return this; }
        public ModelBuilder renderType(String renderType) { return this; }
        public OverrideBuilder override() { return new OverrideBuilder(this); }
        public ModelFile build() { return new ModelFile(ResourceLocation.parse("create:stub")); }
        public ModelBuilder parent(ModelFile parent) { return this; }
    }

    public static class OverrideBuilder {
        private final ModelBuilder parent;
        public OverrideBuilder(ModelBuilder parent) { this.parent = parent; }
        public OverrideBuilder predicate(ResourceLocation id, float value) { return this; }
        public OverrideBuilder model(ModelBuilder model) { return this; }
        public ModelBuilder end() { return parent; }
    }
}

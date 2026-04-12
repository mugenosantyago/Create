package net.neoforged.neoforge.client.model.generators;

import net.minecraft.resources.ResourceLocation;

/**
 * Stub for the removed NeoForge model generators API (MC 1.21.8 port).
 */
@SuppressWarnings("all")
public class ConfiguredModel {
    public final ModelFile model;
    public final int rotationX;
    public final int rotationY;
    public final boolean uvLock;
    public final int weight;

    public ConfiguredModel(ModelFile model) {
        this.model = model;
        this.rotationX = 0;
        this.rotationY = 0;
        this.uvLock = false;
        this.weight = 1;
    }

    public ConfiguredModel(ModelFile model, int x, int y, boolean uvlock, int weight) {
        this.model = model;
        this.rotationX = x;
        this.rotationY = y;
        this.uvLock = uvlock;
        this.weight = weight;
    }

    public static Builder<?> builder() {
        return new Builder<>();
    }

    public static ConfiguredModel[] allWithStates(ModelFile model) {
        return new ConfiguredModel[] { new ConfiguredModel(model) };
    }

    public static class Builder<T> {
        private ModelFile model;
        private int rotationX = 0;
        private int rotationY = 0;
        private boolean uvLock = false;
        private int weight = 1;

        public Builder<T> modelFile(ModelFile model) { this.model = model; return this; }
        public Builder<T> rotationX(int x) { this.rotationX = x; return this; }
        public Builder<T> rotationY(int y) { this.rotationY = y; return this; }
        public Builder<T> uvLock(boolean uvLock) { this.uvLock = uvLock; return this; }
        public Builder<T> weight(int weight) { this.weight = weight; return this; }

        public ConfiguredModel[] build() {
            return new ConfiguredModel[]{ new ConfiguredModel(model, rotationX, rotationY, uvLock, weight) };
        }

        public ConfiguredModel buildLast() {
            return new ConfiguredModel(model, rotationX, rotationY, uvLock, weight);
        }
    }
}

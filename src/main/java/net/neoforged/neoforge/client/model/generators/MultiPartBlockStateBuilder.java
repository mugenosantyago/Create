package net.neoforged.neoforge.client.model.generators;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.function.Function;

/**
 * Stub for the removed NeoForge MultiPartBlockStateBuilder (MC 1.21.8 port).
 */
@SuppressWarnings("all")
public class MultiPartBlockStateBuilder {
    public PartBuilder part() { return new PartBuilder(this); }

    public class PartBuilder {
        private final MultiPartBlockStateBuilder parent;

        public PartBuilder(MultiPartBlockStateBuilder parent) {
            this.parent = parent;
        }

        // Model configuration methods
        public PartBuilder modelFile(ModelFile model) { return this; }
        public PartBuilder rotationX(int x) { return this; }
        public PartBuilder rotationY(int y) { return this; }
        public PartBuilder uvLock(boolean uvLock) { return this; }
        public PartBuilder weight(int weight) { return this; }

        // After model config, addModel() transitions to condition setting
        public PartBuilder addModel() { return this; }
        public PartBuilder addModel(ConfiguredModel... models) { return this; }

        // Condition methods
        public <T extends Comparable<T>> PartBuilder condition(Property<T> prop, T... values) { return this; }
        public <T extends Comparable<T>> PartBuilder condition(Property<T> prop, Comparable<?>... values) { return this; }
        public PartBuilder useOr() { return this; }

        public MultiPartBlockStateBuilder end() { return parent; }
    }
}

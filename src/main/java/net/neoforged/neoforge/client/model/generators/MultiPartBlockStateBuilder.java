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
    public PartBuilder part() { return new PartBuilder(); }

    public class PartBuilder {
        public PartBuilder condition(Property<?> prop, Comparable<?>... values) { return this; }
        public PartBuilder useOr() { return this; }
        public PartBuilder end() { return this; }
        public PartBuilder addModel(ConfiguredModel... models) { return this; }

        public class ModelSelectorBuilder {
            public ModelSelectorBuilder addModel(ConfiguredModel... models) { return this; }
            public PartBuilder add() { return PartBuilder.this; }
        }
    }
}

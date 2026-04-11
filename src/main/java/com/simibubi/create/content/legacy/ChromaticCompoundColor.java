package com.simibubi.create.content.legacy;

import com.mojang.serialization.MapCodec;

import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

/**
 * Animated item tint sources for the Chromatic Compound item.
 *
 * <p>In 1.21.4+, the old {@code ItemColor} interface (single class per item handling all tint
 * layers via an index) was replaced by per-layer {@link ItemTintSource} instances.
 * Each tint layer in the item model now references a registered {@code ItemTintSource}.
 */
public final class ChromaticCompoundColor {

    // Not instantiated – use the singleton enum entries below.
    private ChromaticCompoundColor() {}

    /** Tint source for layer 0 (purple base). */
    public enum Layer0 implements ItemTintSource {
        INSTANCE;

        public static final MapCodec<Layer0> CODEC = MapCodec.unit(INSTANCE);

        @Override
        public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
            float progress = viewProgress(level);
            return Color.mixColors(ARGB32.color(110, 87, 115), ARGB32.color(107, 48, 116), (Mth.sin(progress) + 1) / 2);
        }

        @Override
        public MapCodec<Layer0> type() { return CODEC; }
    }

    /** Tint source for layer 1 (pink mid). */
    public enum Layer1 implements ItemTintSource {
        INSTANCE;

        public static final MapCodec<Layer1> CODEC = MapCodec.unit(INSTANCE);

        @Override
        public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
            float progress = viewProgress(level);
            return Color.mixColors(ARGB32.color(212, 93, 121), ARGB32.color(110, 87, 115), (Mth.sin((float) (progress + Math.PI)) + 1) / 2);
        }

        @Override
        public MapCodec<Layer1> type() { return CODEC; }
    }

    /** Tint source for layer 2 (salmon highlight). */
    public enum Layer2 implements ItemTintSource {
        INSTANCE;

        public static final MapCodec<Layer2> CODEC = MapCodec.unit(INSTANCE);

        @Override
        public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
            float progress = viewProgress(level);
            return Color.mixColors(ARGB32.color(234, 144, 133), ARGB32.color(212, 93, 121), (Mth.sin((float) (progress * 1.5f + Math.PI)) + 1) / 2);
        }

        @Override
        public MapCodec<Layer2> type() { return CODEC; }
    }

    private static float viewProgress(@Nullable ClientLevel level) {
        Minecraft mc = Minecraft.getInstance();
        float pt = AnimationTickHolder.getPartialTicks();
        return (mc.player != null ? (float) (mc.player.getViewYRot(pt) / 180 * Math.PI) : 0f)
            + AnimationTickHolder.getRenderTime() / 10f;
    }
}

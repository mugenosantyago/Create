package com.simibubi.create.foundation.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Compatibility shim for MC 1.21.8 GUI API changes.
 * In 1.21.8, GuiGraphics.pose() returns Matrix3x2fStack instead of PoseStack.
 * This class provides bridge methods for compilation compatibility.
 */
@SuppressWarnings({"unchecked", "all"})
public class GuiCompat {

    /**
     * Returns a PoseStack from GuiGraphics for backward compatibility.
     * NOTE: In 1.21.8 this is a stub - GUI rendering now uses Matrix3x2fStack.
     */
    @SuppressWarnings("unchecked")
    public static PoseStack poseStack(GuiGraphics graphics) {
        // Unsafe cast for compilation compatibility only
        // In MC 1.21.8, GUI graphics uses Matrix3x2fStack - this will need proper migration
        return (PoseStack)(Object) graphics.pose();
    }
}

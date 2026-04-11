package com.simibubi.create.foundation.mixin;

import java.util.function.BiFunction;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.armor.AllArmorMaterials;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
// ArmorMaterial moved to net.minecraft.world.item.equipment in 1.21.4
import net.minecraft.world.item.equipment.ArmorMaterial;
// Armor trim classes moved to net.minecraft.world.item.equipment.trim in 1.21.8
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;

/**
 * Injects custom cardboard trim textures into the armor trim system.
 *
 * <p>In 1.21.4+, ArmorMaterial is no longer a registry object so the method signatures
 * changed from {@code Holder<ArmorMaterial>} to {@code ArmorMaterial} directly.
 * Also, trim classes moved to the {@code net.minecraft.world.item.equipment.trim} package.
 *
 * <p>TODO: Verify that {@code innerTexture} and {@code outerTexture} method names and
 * signatures are still correct in 1.21.8, and update the mixin targets as needed.
 */
@Mixin(ArmorTrim.class)
public abstract class ArmorTrimMixin {
    @Shadow
    @Final
    private Holder<TrimMaterial> material;

    @Shadow
    @Final
    private Holder<TrimPattern> pattern;

    /**
     * In 1.21.4+, ArmorMaterial is no longer a Holder; the method signature is
     * {@code getColorPaletteSuffix(Holder<TrimMaterial>, ArmorMaterial)}.
     */
    @Shadow
    private static String getColorPaletteSuffix(Holder<TrimMaterial> trimMaterial, ArmorMaterial armorMaterial) {
        throw new AssertionError();
    }

    @Unique
    private final BiFunction<Boolean, ArmorMaterial, ResourceLocation> create$textureCardboard = Util.memoize((inner, armorMaterial) -> {
        String assetPath = pattern.value().assetId().getPath();
        String colorSuffix = getColorPaletteSuffix(material, armorMaterial);
        return Create.asResource("trims/models/armor/card_" + assetPath + (inner ? "_leggings_" : "_") + colorSuffix);
    });

    @Inject(method = "innerTexture", at = @At("HEAD"), cancellable = true)
    private void create$swapTexturesForCardboardTrimsInner(ArmorMaterial armorMaterial, CallbackInfoReturnable<ResourceLocation> cir) {
        if (armorMaterial == AllArmorMaterials.CARDBOARD) {
            cir.setReturnValue(create$textureCardboard.apply(true, armorMaterial));
        }
    }

    @Inject(method = "outerTexture", at = @At("HEAD"), cancellable = true)
    private void create$swapTexturesForCardboardTrimsOuter(ArmorMaterial armorMaterial, CallbackInfoReturnable<ResourceLocation> cir) {
        if (armorMaterial == AllArmorMaterials.CARDBOARD) {
            cir.setReturnValue(create$textureCardboard.apply(false, armorMaterial));
        }
    }
}

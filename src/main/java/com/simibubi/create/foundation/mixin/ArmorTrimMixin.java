package com.simibubi.create.foundation.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.armor.AllArmorMaterials;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;

/**
 * Injects custom cardboard trim textures into the armor trim system.
 *
 * <p>1.21.8: {@code ArmorTrim} is a record; {@code innerTexture}/{@code outerTexture} and
 * {@code getColorPaletteSuffix} were removed in favor of {@link ArmorTrim#layerAssetId}.
 */
@Mixin(ArmorTrim.class)
public abstract class ArmorTrimMixin {
	@Shadow
	@Final
	private Holder<TrimMaterial> material;

	@Shadow
	@Final
	private Holder<TrimPattern> pattern;

	@Inject(method = "layerAssetId", at = @At("HEAD"), cancellable = true)
	private void create$swapTexturesForCardboardTrims(
		String layerName,
		ResourceKey<EquipmentAsset> equipmentAsset,
		CallbackInfoReturnable<ResourceLocation> cir
	) {
		if (!equipmentAsset.equals(AllArmorMaterials.CARDBOARD_ASSET))
			return;

		String assetPath = pattern.value().assetId().getPath();
		String colorSuffix = material.value().assets().assetId(equipmentAsset).suffix();
		boolean leggings = layerName.contains("leggings");
		cir.setReturnValue(Create.asResource(
			"trims/models/armor/card_" + assetPath + (leggings ? "_leggings_" : "_") + colorSuffix
		));
	}
}

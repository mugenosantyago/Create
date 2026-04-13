package com.simibubi.create.content.equipment.armor;

import java.util.EnumMap;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.AllTags;
import com.simibubi.create.Create;

import net.minecraft.Util;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

/**
 * Armor materials for Create equipment. In 1.21.4+, ArmorMaterial is a plain record and is
 * NOT a registry object. Textures are defined by EquipmentAsset JSONs under
 * {@code assets/create/equipment/}.
 *
 * <p>Items use {@code Item.Properties.humanoidArmor(material, type)} to apply components.
 */
public class AllArmorMaterials {

    /**
     * Equipment asset key for copper diving armor.
     * Resolves to {@code assets/create/textures/entity/equipment/humanoid/copper_diving.png}
     * and {@code assets/create/equipment/copper_diving.json}.
     */
    public static final ResourceKey<EquipmentAsset> COPPER_DIVING_ASSET =
            ResourceKey.create(EquipmentAssets.ROOT_ID, Create.asResource("copper_diving"));

    /**
     * Equipment asset key for cardboard armor.
     * Resolves to {@code assets/create/textures/entity/equipment/humanoid/cardboard.png}
     * and {@code assets/create/equipment/cardboard.json}.
     */
    public static final ResourceKey<EquipmentAsset> CARDBOARD_ASSET =
            ResourceKey.create(EquipmentAssets.ROOT_ID, Create.asResource("cardboard"));

    /**
     * Copper armor material.
     * Defense values: helmet=2, chestplate=4, leggings=3, boots=1.
     * Base durability scalar: 15.
     */
    public static final ArmorMaterial COPPER = new ArmorMaterial(
            15,
            Util.make(new EnumMap<>(ArmorType.class), map -> {
                map.put(ArmorType.HELMET,     2);
                map.put(ArmorType.CHESTPLATE, 4);
                map.put(ArmorType.LEGGINGS,   3);
                map.put(ArmorType.BOOTS,      1);
                map.put(ArmorType.BODY,       4);
            }),
            7,
            AllSoundEvents.COPPER_ARMOR_EQUIP.getMainEventHolder(),
            0.0F,
            0.0F,
            AllTags.commonItemTag("ingots/copper"),
            COPPER_DIVING_ASSET
    );

    /**
     * Cardboard armor material.
     * Defense values: all 1 (basically no protection – it's cardboard!).
     * Base durability scalar: 2.
     */
    public static final ArmorMaterial CARDBOARD = new ArmorMaterial(
            2,
            Util.make(new EnumMap<>(ArmorType.class), map -> {
                map.put(ArmorType.HELMET,     1);
                map.put(ArmorType.CHESTPLATE, 1);
                map.put(ArmorType.LEGGINGS,   1);
                map.put(ArmorType.BOOTS,      1);
                map.put(ArmorType.BODY,       2);
            }),
            4,
            net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_LEATHER,
            0.0F,
            0.0F,
            net.minecraft.tags.ItemTags.REPAIRS_LEATHER_ARMOR,
            CARDBOARD_ASSET
    );
}

package com.simibubi.create.content.equipment.armor;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

/**
 * Base class for Create armor pieces. In 1.21.5+, ArmorItem was removed; armor is now an ordinary
 * Item. Data components (EQUIPPABLE, ATTRIBUTE_MODIFIERS, MAX_DAMAGE, etc.) are set by calling
 * {@link Item.Properties#humanoidArmor(ArmorMaterial, ArmorType)}.
 *
 * <p>When registering through Registrate, use the factory {@code p} and call
 * {@code p.humanoidArmor(material, type)} so {@link Item.Properties#setId} is preserved.
 * {@link #propertiesFor(ArmorMaterial, ArmorType)} is only for callers that set an id themselves.
 */
public class BaseArmorItem extends Item {

    protected final ArmorMaterial material;
    protected final ArmorType     armorType;

    public BaseArmorItem(ArmorMaterial material, ArmorType type, Properties properties) {
        super(properties);
        this.material  = material;
        this.armorType = type;
    }

    /**
     * Creates a fresh {@link Item.Properties} with all armor data components applied for the
     * given material and slot type, including correct durability.
     *
     * @param material  the armor material to use
     * @param type      the armor slot
     * @return a new {@link Item.Properties} without a registry id — not suitable for Registrate
     *         item factories unless you also call {@link Item.Properties#setId}
     */
    public static Properties propertiesFor(ArmorMaterial material, ArmorType type) {
        // humanoidArmor() sets all required data components including MAX_DAMAGE, EQUIPPABLE,
        // ATTRIBUTE_MODIFIERS, ENCHANTABLE, REPAIRABLE, and MAX_STACK_SIZE(1).
        return new Item.Properties().humanoidArmor(material, type);
    }
}

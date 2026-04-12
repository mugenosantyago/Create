package com.simibubi.create.content.equipment.tool;

import com.simibubi.create.Create;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ToolMaterial;

/**
 * Tool materials for Create equipment.
 * In 1.21.4+, Tier was replaced by ToolMaterial. In 1.21.5+, SwordItem/DiggerItem etc. are removed;
 * tool behaviour is applied as data components via ToolMaterial#apply*Properties on Item.Properties.
 */
public final class AllToolMaterials {

    /**
     * Cardboard "tool material" – used to build the Cardboard Sword's data-component stack.
     * Durability 0 (nearly unbreakable for a gag weapon), mining speed 1, attack bonus 0.
     * Repair items: tag create:cardboard_sword_repair_items.
     */
    public static final ToolMaterial CARDBOARD = new ToolMaterial(
            BlockTags.INCORRECT_FOR_WOODEN_TOOL,
            1,   // uses (durability)
            1f,  // speed
            0f,  // attack damage bonus
            1,   // enchantment value
            ItemTags.create(Create.asResource("cardboard_repair_materials")) // item tag for repairs
    );

    private AllToolMaterials() {}
}

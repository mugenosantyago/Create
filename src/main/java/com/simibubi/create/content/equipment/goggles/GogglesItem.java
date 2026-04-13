package com.simibubi.create.content.equipment.goggles;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.simibubi.create.AllItems;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

// In MC 1.21.8, Equippable is a data component (Record), not an interface.
// Goggles are equipped via the EQUIPPABLE data component instead.
public class GogglesItem extends Item {
	private static final List<Predicate<Player>> IS_WEARING_PREDICATES = new ArrayList<>();

	static {
		addIsWearingPredicate(player -> AllItems.GOGGLES.isIn(player.getItemBySlot(EquipmentSlot.HEAD)));
	}

	public GogglesItem(Properties properties) {
		super(properties);
		DispenserBlock.registerBehavior(this, new net.minecraft.core.dispenser.EquipmentDispenseItemBehavior());
	}

	// getEquipmentSlot() removed in MC 1.21.8 - use EQUIPPABLE data component instead
	public EquipmentSlot getEquipmentSlot() {
		return EquipmentSlot.HEAD;
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack inHand = player.getItemInHand(hand);
		if (!inHand.is(this))
			return InteractionResult.PASS;
		if (level.isClientSide)
			return InteractionResult.SUCCESS;
		ItemStack onHead = player.getItemBySlot(EquipmentSlot.HEAD);
		ItemStack toEquip = inHand.split(1);
		player.setItemSlot(EquipmentSlot.HEAD, toEquip);
		if (!onHead.isEmpty()) {
			if (inHand.isEmpty())
				player.setItemInHand(hand, onHead);
			else if (!player.getInventory().add(onHead))
				player.drop(onHead, false);
		}
		return InteractionResult.SUCCESS;
	}

	public static boolean isWearingGoggles(Player player) {
		for (Predicate<Player> predicate : IS_WEARING_PREDICATES) {
			if (predicate.test(player)) {
				return true;
			}
		}
		return false;
	}

	public static synchronized void addIsWearingPredicate(Predicate<Player> predicate) {
		IS_WEARING_PREDICATES.add(predicate);
	}
}

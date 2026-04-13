package net.createmod.catnip.net.base;

import net.minecraft.world.entity.player.Player;

/**
 * Dedicated servers do not have {@code LocalPlayer} on the classpath; packet types referenced from
 * shared mod initialization must not encode that type in bytecode.
 */
public non-sealed interface ClientboundPacketPayload extends BasePacketPayload {
	void handle(Player player);

	default void handleInternal(Player player) {
		handle(player);
	}
}

package com.simibubi.create.foundation.utility;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Reflective calls into {@link com.simibubi.create.CreateClient} and Minecraft so common item classes do not reference
 * client-only types in their constant pool (dedicated server compatibility).
 */
public final class CreateClientAccess {

	private CreateClientAccess() {}

	public static void potatoCannonDontAnimateItem(InteractionHand hand) {
		try {
			Class<?> cc = Class.forName("com.simibubi.create.CreateClient");
			Object handler = cc.getField("POTATO_CANNON_RENDER_HANDLER").get(null);
			handler.getClass().getMethod("dontAnimateItem", InteractionHand.class).invoke(handler, hand);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	public static void zapperDontAnimateItem(InteractionHand hand) {
		try {
			Class<?> cc = Class.forName("com.simibubi.create.CreateClient");
			Object handler = cc.getField("ZAPPER_RENDER_HANDLER").get(null);
			handler.getClass().getMethod("dontAnimateItem", InteractionHand.class).invoke(handler, hand);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	@Nullable
	public static Player getClientSidePlayer() {
		try {
			Class<?> mcClass = Class.forName("net.minecraft.client.Minecraft");
			Object mc = mcClass.getMethod("getInstance").invoke(null);
			Object playerObj = mcClass.getField("player").get(mc);
			return playerObj instanceof Player p ? p : null;
		} catch (ReflectiveOperationException e) {
			return null;
		}
	}

	@Nullable
	public static Level getClientLevel() {
		try {
			Class<?> mcClass = Class.forName("net.minecraft.client.Minecraft");
			Object mc = mcClass.getMethod("getInstance").invoke(null);
			Object levelObj = mcClass.getField("level").get(mc);
			return levelObj instanceof Level l ? l : null;
		} catch (ReflectiveOperationException e) {
			return null;
		}
	}
}

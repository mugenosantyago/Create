package com.simibubi.create.foundation.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.foundation.render.PlayerSkyhookRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

@Mixin(HumanoidModel.class)
public class HumanoidModelMixin {
	@Shadow
	@Final
	public ModelPart body;

	@Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V", at = @At("RETURN"))
	private void create$afterSetupAnim(HumanoidRenderState state, CallbackInfo callbackInfo) {
		Player player = create$resolvePlayer(state);
		if (player == null)
			return;

		PlayerSkyhookRenderer.afterSetupAnim(player, (HumanoidModel<?>) (Object) this);
	}

	@Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V", at = @At("HEAD"))
	private void create$beforeSetupAnim(HumanoidRenderState state, CallbackInfo callbackInfo) {
		Player player = create$resolvePlayer(state);
		if (player == null)
			return;

		PlayerSkyhookRenderer.beforeSetupAnim(player, (HumanoidModel<?>) (Object) this);
	}

	private static Player create$resolvePlayer(HumanoidRenderState state) {
		if (!(state instanceof PlayerRenderState prs))
			return null;
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null)
			return null;
		Entity entity = mc.level.getEntity(prs.id);
		return entity instanceof Player player ? player : null;
	}
}

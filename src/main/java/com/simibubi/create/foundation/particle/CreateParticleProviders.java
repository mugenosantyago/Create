package com.simibubi.create.foundation.particle;

import com.simibubi.create.AllParticleTypes;
import com.simibubi.create.content.equipment.bell.SoulBaseParticle;
import com.simibubi.create.content.equipment.bell.SoulParticle;
import com.simibubi.create.content.fluids.particle.FluidParticleData;
import com.simibubi.create.content.fluids.particle.FluidStackParticle;
import com.simibubi.create.content.kinetics.base.RotationIndicatorParticle;
import com.simibubi.create.content.kinetics.fan.AirFlowParticle;
import com.simibubi.create.content.kinetics.steamEngine.SteamJetParticle;
import com.simibubi.create.content.logistics.packagerLink.WiFiParticle;
import com.simibubi.create.content.trains.CubeParticle;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleType;

import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

/**
 * Client-only particle factories; kept separate from {@link AllParticleTypes} so the dedicated server never loads
 * {@link ParticleProvider} or other client particle classes during mod construction.
 */
public final class CreateParticleProviders {

	private CreateParticleProviders() {}

	public static void register(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(AllParticleTypes.ROTATION_INDICATOR.get(), RotationIndicatorParticle.Factory::new);
		event.registerSpriteSet(AllParticleTypes.AIR_FLOW.get(), AirFlowParticle.Factory::new);
		event.registerSpriteSet(AllParticleTypes.AIR.get(), AirParticle.Factory::new);
		event.registerSpriteSet(AllParticleTypes.STEAM_JET.get(), SteamJetParticle.Factory::new);
		event.registerSpecial(AllParticleTypes.CUBE.get(), new CubeParticle.Factory());

		ParticleProvider<FluidParticleData> fluidProvider = (data, world, x, y, z, vx, vy, vz) -> FluidStackParticle
			.create((ParticleType<FluidParticleData>) data.getType(), world, data.getFluid(), x, y, z, vx, vy, vz);
		event.registerSpecial(AllParticleTypes.FLUID_PARTICLE.get(), fluidProvider);
		event.registerSpecial(AllParticleTypes.BASIN_FLUID.get(), fluidProvider);
		event.registerSpecial(AllParticleTypes.FLUID_DRIP.get(), fluidProvider);

		event.registerSpriteSet(AllParticleTypes.WIFI.get(),
			sprites -> (data, w, x, y, z, vx, vy, vz) -> new WiFiParticle(w, x, y, z, vx, vy, vz, sprites));
		event.registerSpriteSet(AllParticleTypes.SOUL.get(),
			sprites -> (data, w, x, y, z, vx, vy, vz) -> new SoulParticle(w, x, y, z, vx, vy, vz, sprites, data));
		event.registerSpriteSet(AllParticleTypes.SOUL_PERIMETER.get(),
			sprites -> (data, w, x, y, z, vx, vy, vz) -> new SoulParticle(w, x, y, z, vx, vy, vz, sprites, data));
		event.registerSpriteSet(AllParticleTypes.SOUL_EXPANDING_PERIMETER.get(),
			sprites -> (data, w, x, y, z, vx, vy, vz) -> new SoulParticle(w, x, y, z, vx, vy, vz, sprites, data));
		event.registerSpriteSet(AllParticleTypes.SOUL_BASE.get(),
			sprites -> (data, w, x, y, z, vx, vy, vz) -> new SoulBaseParticle(w, x, y, z, vx, vy, vz, sprites));
	}
}

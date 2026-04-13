package com.simibubi.create.foundation.particle;

import com.simibubi.create.AllParticleTypes;
import com.simibubi.create.content.equipment.bell.SoulBaseParticle;
import com.simibubi.create.content.equipment.bell.SoulBaseParticleData;
import com.simibubi.create.content.equipment.bell.SoulParticle;
import com.simibubi.create.content.equipment.bell.SoulParticleData;
import com.simibubi.create.content.equipment.bell.SoulParticleExpandingPerimeterData;
import com.simibubi.create.content.equipment.bell.SoulParticlePerimeterData;
import com.simibubi.create.content.fluids.particle.FluidParticleData;
import com.simibubi.create.content.fluids.particle.FluidStackParticle;
import com.simibubi.create.content.kinetics.base.RotationIndicatorParticle;
import com.simibubi.create.content.kinetics.base.RotationIndicatorParticleData;
import com.simibubi.create.content.kinetics.fan.AirFlowParticle;
import com.simibubi.create.content.kinetics.fan.AirFlowParticleData;
import com.simibubi.create.content.kinetics.steamEngine.SteamJetParticle;
import com.simibubi.create.content.kinetics.steamEngine.SteamJetParticleData;
import com.simibubi.create.content.logistics.packagerLink.WiFiParticle;
import com.simibubi.create.content.logistics.packagerLink.WiFiParticleData;
import com.simibubi.create.content.trains.CubeParticle;
import com.simibubi.create.content.trains.CubeParticleData;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleType;

import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

/**
 * Client-only particle factories; kept separate from {@link AllParticleTypes} so the dedicated server never loads
 * {@link ParticleProvider} or other client particle classes during mod construction.
 */
public final class CreateParticleProviders {

	private CreateParticleProviders() {}

	@SuppressWarnings("unchecked")
	public static void register(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet((ParticleType<RotationIndicatorParticleData>) AllParticleTypes.ROTATION_INDICATOR.get(),
			RotationIndicatorParticle.Factory::new);
		event.registerSpriteSet((ParticleType<AirFlowParticleData>) AllParticleTypes.AIR_FLOW.get(),
			AirFlowParticle.Factory::new);
		event.registerSpriteSet((ParticleType<AirParticleData>) AllParticleTypes.AIR.get(), AirParticle.Factory::new);
		event.registerSpriteSet((ParticleType<SteamJetParticleData>) AllParticleTypes.STEAM_JET.get(),
			SteamJetParticle.Factory::new);
		event.registerSpecial((ParticleType<CubeParticleData>) AllParticleTypes.CUBE.get(), new CubeParticle.Factory());

		ParticleProvider<FluidParticleData> fluidProvider = (data, world, x, y, z, vx, vy, vz) -> FluidStackParticle
			.create((ParticleType<FluidParticleData>) data.getType(), world, data.getFluid(), x, y, z, vx, vy, vz);
		event.registerSpecial((ParticleType<FluidParticleData>) AllParticleTypes.FLUID_PARTICLE.get(), fluidProvider);
		event.registerSpecial((ParticleType<FluidParticleData>) AllParticleTypes.BASIN_FLUID.get(), fluidProvider);
		event.registerSpecial((ParticleType<FluidParticleData>) AllParticleTypes.FLUID_DRIP.get(), fluidProvider);

		event.registerSpriteSet((ParticleType<WiFiParticleData>) AllParticleTypes.WIFI.get(),
			sprites -> (data, w, x, y, z, vx, vy, vz) -> new WiFiParticle(w, x, y, z, vx, vy, vz, sprites));
		event.registerSpriteSet((ParticleType<SoulParticleData>) AllParticleTypes.SOUL.get(),
			sprites -> (data, w, x, y, z, vx, vy, vz) -> new SoulParticle(w, x, y, z, vx, vy, vz, sprites, data));
		event.registerSpriteSet((ParticleType<SoulParticlePerimeterData>) AllParticleTypes.SOUL_PERIMETER.get(),
			sprites -> (data, w, x, y, z, vx, vy, vz) -> new SoulParticle(w, x, y, z, vx, vy, vz, sprites, data));
		event.registerSpriteSet((ParticleType<SoulParticleExpandingPerimeterData>) AllParticleTypes.SOUL_EXPANDING_PERIMETER.get(),
			sprites -> (data, w, x, y, z, vx, vy, vz) -> new SoulParticle(w, x, y, z, vx, vy, vz, sprites, data));
		event.registerSpriteSet((ParticleType<SoulBaseParticleData>) AllParticleTypes.SOUL_BASE.get(),
			sprites -> (data, w, x, y, z, vx, vy, vz) -> new SoulBaseParticle(w, x, y, z, vx, vy, vz, sprites));
	}
}

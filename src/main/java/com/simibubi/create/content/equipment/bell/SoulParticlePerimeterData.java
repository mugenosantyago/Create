package com.simibubi.create.content.equipment.bell;

import com.simibubi.create.AllParticleTypes;

import net.minecraft.core.particles.ParticleType;

public class SoulParticlePerimeterData extends BasicParticleData {

	@Override
	public ParticleType<?> getType() {
		return AllParticleTypes.SOUL_PERIMETER.get();
	}
}

package com.simibubi.create.content.equipment.bell;

import com.simibubi.create.AllParticleTypes;

import net.minecraft.core.particles.ParticleType;

public class SoulParticleExpandingPerimeterData extends SoulParticlePerimeterData {

	@Override
	public ParticleType<?> getType() {
		return AllParticleTypes.SOUL_EXPANDING_PERIMETER.get();
	}
}

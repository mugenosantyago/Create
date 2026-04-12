package com.simibubi.create.foundation.damageTypes;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.AllDamageTypes;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

public class CreateDamageSources {
	public static DamageSource crush(Level level) {
		return source(AllDamageTypes.CRUSH, level);
	}

	public static DamageSource cuckooSurprise(Level level) {
		return source(AllDamageTypes.CUCKOO_SURPRISE, level);
	}

	public static DamageSource fanFire(Level level) {
		return source(AllDamageTypes.FAN_FIRE, level);
	}

	public static DamageSource fanLava(Level level) {
		return source(AllDamageTypes.FAN_LAVA, level);
	}

	public static DamageSource drill(Level level) {
		return source(AllDamageTypes.DRILL, level);
	}

	public static DamageSource roller(Level level) {
		return source(AllDamageTypes.ROLLER, level);
	}

	public static DamageSource saw(Level level) {
		return source(AllDamageTypes.SAW, level);
	}

	public static DamageSource potatoCannon(Level level, Entity causingEntity, Entity directEntity) {
		return source(AllDamageTypes.POTATO_CANNON, level, causingEntity, directEntity);
	}

	public static DamageSource runOver(Level level, Entity entity) {
		return source(AllDamageTypes.RUN_OVER, level, entity);
	}

	private static DamageSource source(ResourceKey<DamageType> key, LevelReader level) {
		Holder<DamageType> holder = level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(key);
		return new DamageSource(holder);
	}

	private static DamageSource source(ResourceKey<DamageType> key, LevelReader level, @Nullable Entity entity) {
		Holder<DamageType> holder = level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(key);
		return new DamageSource(holder, entity);
	}

	private static DamageSource source(ResourceKey<DamageType> key, LevelReader level, @Nullable Entity causingEntity, @Nullable Entity directEntity) {
		Holder<DamageType> holder = level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(key);
		return new DamageSource(holder, causingEntity, directEntity);
	}
}

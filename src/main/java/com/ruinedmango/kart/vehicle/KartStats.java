package com.ruinedmango.kart.vehicle;

import com.ruinedmango.kart.component.ComponentDefinition;
import com.ruinedmango.kart.component.ComponentSlot;
import com.ruinedmango.kart.component.ComponentStats;
import com.ruinedmango.kart.component.KartLoadout;

import net.minecraft.util.Mth;

public record KartStats(
		float mass,
		float engineAcceleration,
		float reverseAcceleration,
		float maxForwardSpeed,
		float maxReverseSpeed,
		float brakeForce,
		float rollingResistance,
		float lateralGrip,
		float steeringRate,
		float wheelRadius,
		float boostForce,
		float boostHeatPerTick,
		float boostCoolingPerTick,
		int boostOverheatTicks) {

	public static final KartStats DEFAULT = new KartStats(
			120.0F,
			0.055F,
			0.030F,
			0.65F,
			0.25F,
			0.10F,
			0.015F,
			0.82F,
			4.0F,
			0.35F,
			0.0F,
			0.0F,
			0.0F,
			0);

	public static KartStats derive(KartLoadout loadout) {
		float mass = 0.0F;
		float engineForce = 0.0F;
		float reverseForce = 0.0F;
		float maxForwardSpeed = 0.0F;
		float maxReverseSpeed = 0.0F;
		float brakeForce = 0.0F;
		float rollingResistance = 0.0F;
		float lateralGrip = 0.0F;
		float steeringRate = 0.0F;
		float wheelRadius = 0.0F;
		float gripMultiplier = 1.0F;
		float steeringMultiplier = 1.0F;
		float boostForce = 0.0F;
		float boostHeatPerTick = 0.0F;
		float boostCoolingPerTick = 0.0F;
		int boostOverheatTicks = 0;

		for (ComponentSlot slot : ComponentSlot.values()) {
			ComponentDefinition definition = loadout.definition(slot).orElse(null);
			if (definition == null) {
				continue;
			}

			ComponentStats stats = definition.stats();
			mass += stats.mass();
			engineForce += stats.engineForce();
			reverseForce += stats.reverseForce();
			maxForwardSpeed = Math.max(maxForwardSpeed, stats.maxForwardSpeed());
			maxReverseSpeed = Math.max(maxReverseSpeed, stats.maxReverseSpeed());
			brakeForce = Math.max(brakeForce, stats.brakeForce());
			rollingResistance = Math.max(rollingResistance, stats.rollingResistance());
			lateralGrip = Math.max(lateralGrip, stats.lateralGrip());
			steeringRate = Math.max(steeringRate, stats.steeringRate());
			wheelRadius = Math.max(wheelRadius, stats.wheelRadius());
			gripMultiplier *= stats.gripMultiplier();
			steeringMultiplier *= stats.steeringMultiplier();
			boostForce += stats.boostForce();
			boostHeatPerTick = Math.max(boostHeatPerTick, stats.boostHeatPerTick());
			boostCoolingPerTick = Math.max(boostCoolingPerTick, stats.boostCoolingPerTick());
			boostOverheatTicks = Math.max(boostOverheatTicks, stats.boostOverheatTicks());
		}

		mass = Math.max(1.0F, mass);
		float acceleration = engineForce > 0.0F ? Mth.clamp(engineForce / mass, 0.005F, 0.20F) : 0.0F;
		float reverseAcceleration = reverseForce > 0.0F ? Mth.clamp(reverseForce / mass, 0.005F, 0.10F) : 0.0F;

		return new KartStats(
				mass,
				acceleration,
				reverseAcceleration,
				maxForwardSpeed > 0.0F ? maxForwardSpeed : 0.40F,
				maxReverseSpeed > 0.0F ? maxReverseSpeed : 0.20F,
				brakeForce > 0.0F ? brakeForce : 0.10F,
				rollingResistance > 0.0F ? rollingResistance : 0.015F,
				Mth.clamp(lateralGrip * gripMultiplier, 0.10F, 1.20F),
				Mth.clamp(steeringRate * steeringMultiplier, 1.0F, 10.0F),
				wheelRadius > 0.0F ? wheelRadius : 0.35F,
				boostForce,
				boostHeatPerTick,
				boostCoolingPerTick,
				boostOverheatTicks);
	}
}

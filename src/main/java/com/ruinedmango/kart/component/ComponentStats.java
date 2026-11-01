package com.ruinedmango.kart.component;

public record ComponentStats(
		float mass,
		float engineForce,
		float reverseForce,
		float maxForwardSpeed,
		float maxReverseSpeed,
		float brakeForce,
		float rollingResistance,
		float lateralGrip,
		float steeringRate,
		float wheelRadius,
		float gripMultiplier,
		float steeringMultiplier,
		float boostForce,
		float boostHeatPerTick,
		float boostCoolingPerTick,
		int boostOverheatTicks) {

	public static ComponentStats chassis(float mass, float gripMultiplier, float steeringMultiplier) {
		return new ComponentStats(mass, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, gripMultiplier, steeringMultiplier, 0.0F, 0.0F, 0.0F, 0);
	}

	public static ComponentStats engine(float mass, float engineForce, float reverseForce, float maxForwardSpeed, float maxReverseSpeed) {
		return new ComponentStats(mass, engineForce, reverseForce, maxForwardSpeed, maxReverseSpeed, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0);
	}

	public static ComponentStats wheels(float mass, float brakeForce, float rollingResistance, float lateralGrip, float steeringRate, float wheelRadius) {
		return new ComponentStats(mass, 0.0F, 0.0F, 0.0F, 0.0F, brakeForce, rollingResistance, lateralGrip, steeringRate, wheelRadius, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0);
	}

	public static ComponentStats booster(float mass, float boostForce, float boostHeatPerTick, float boostCoolingPerTick, int boostOverheatTicks) {
		return new ComponentStats(mass, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, boostForce, boostHeatPerTick, boostCoolingPerTick, boostOverheatTicks);
	}

	public static ComponentStats accessory(float mass, float gripMultiplier, float steeringMultiplier) {
		return new ComponentStats(mass, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, gripMultiplier, steeringMultiplier, 0.0F, 0.0F, 0.0F, 0);
	}
}

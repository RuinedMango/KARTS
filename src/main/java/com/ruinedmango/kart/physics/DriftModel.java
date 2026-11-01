package com.ruinedmango.kart.physics;

import com.ruinedmango.kart.vehicle.KartInput;

import net.minecraft.util.Mth;

public final class DriftModel {
	private static final float MIN_DRIFT_SPEED = 0.15F;
	private static final float SLIP_START_DEGREES = 5.0F;
	private static final float SLIP_FULL_DEGREES = 30.0F;

	private DriftModel() {
	}

	public static State evaluate(KartInput input, double forwardSpeed, double lateralSpeed, boolean onGround, boolean wasDrifting) {
		float speed = (float) Math.abs(forwardSpeed);
		float slipAngle = (float) Math.toDegrees(Math.atan2(lateralSpeed, Math.max(0.05D, Math.abs(forwardSpeed))));

		if (!onGround || speed < 0.10F) {
			return new State(false, 0.0F, slipAngle, false);
		}

		boolean steering = Math.abs(input.steering()) > 0.10F;
		boolean countersteering = steering
				&& Math.abs(slipAngle) > SLIP_START_DEGREES
				&& Math.signum(input.steering()) == Math.signum(lateralSpeed);

		float slipStrength = Mth.clamp(
				(Math.abs(slipAngle) - SLIP_START_DEGREES) / (SLIP_FULL_DEGREES - SLIP_START_DEGREES),
				0.0F,
				1.0F);

		float slipThreshold = wasDrifting ? 0.20F : 0.35F;
		boolean drifting = (input.handbrake() && steering && speed > MIN_DRIFT_SPEED)
				|| slipStrength > slipThreshold;

		float strength = drifting ? Mth.clamp(0.35F + slipStrength, 0.0F, 1.0F) : 0.0F;
		return new State(drifting, strength, slipAngle, countersteering);
	}

	public record State(boolean drifting, float strength, float slipAngleDegrees, boolean countersteering) {
	}
}

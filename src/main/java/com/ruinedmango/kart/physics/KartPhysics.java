package com.ruinedmango.kart.physics;

import com.ruinedmango.kart.vehicle.KartEntity;
import com.ruinedmango.kart.vehicle.KartInput;
import com.ruinedmango.kart.vehicle.KartStats;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;

public final class KartPhysics {
	private static final double GRAVITY = 0.08D;
	private static final double AIR_DRAG = 0.995D;
	private static final float AIR_STEERING = 0.15F;

	private KartPhysics() {
	}

	public static Result step(KartEntity kart, KartStats stats, KartInput input, boolean boosting) {
		Vec3 velocity = kart.getDeltaMovement();
		boolean onGround = kart.onGround();
		float yaw = kart.getYRot();

		Vec3 forward = forward(yaw);
		Vec3 right = right(yaw);
		double forwardSpeed = velocity.dot(forward);
		double lateralSpeed = velocity.dot(right);

		DriftModel.State drift = DriftModel.evaluate(input, forwardSpeed, lateralSpeed, onGround, kart.isDrifting());

		float speedFactor = Mth.clamp((float) Math.abs(forwardSpeed) * 4.0F, 0.0F, 1.0F);
		boolean reversing = forwardSpeed < -0.02D;
		float yawDelta = input.steering() * stats.steeringRate() * speedFactor
				* TractionModel.steeringMultiplier(drift);
		if (reversing) {
			yawDelta = -yawDelta;
		}
		if (!onGround) {
			yawDelta *= AIR_STEERING;
		}
		yaw = Mth.wrapDegrees(yaw + yawDelta);
		kart.setYRot(yaw);

		forward = forward(yaw);
		right = right(yaw);

		if (onGround) {
			float throttle = input.throttle();
			if (input.handbrake()) {
				throttle *= drift.drifting() ? 0.55F : 0.25F;
			}

			if (throttle > 0.0F) {
				forwardSpeed += stats.engineAcceleration() * throttle;
			} else if (throttle < 0.0F) {
				if (forwardSpeed > 0.02D) {
					forwardSpeed = Math.max(0.0D, forwardSpeed - stats.brakeForce() * -throttle);
				} else {
					forwardSpeed += stats.reverseAcceleration() * throttle;
				}
			}

			if (input.handbrake()) {
				forwardSpeed *= drift.drifting() ? 0.97D : 0.90D;
				if (Math.abs(forwardSpeed) < 0.02D) {
					forwardSpeed = 0.0D;
				}
			}

			if (boosting) {
				forwardSpeed += stats.boostForce();
			}

			lateralSpeed *= TractionModel.lateralRetention(stats, drift);
			forwardSpeed *= 1.0D - stats.rollingResistance();
		} else {
			forwardSpeed *= AIR_DRAG;
			lateralSpeed *= AIR_DRAG;
		}

		float maxForwardSpeed = stats.maxForwardSpeed() * (boosting ? 1.25F : 1.0F);
		forwardSpeed = Mth.clamp(forwardSpeed, -stats.maxReverseSpeed(), maxForwardSpeed);

		Vec3 horizontal = forward.scale(forwardSpeed).add(right.scale(lateralSpeed));
		double vertical = velocity.y;
		if (!onGround) {
			vertical -= GRAVITY;
		} else if (vertical < 0.0D) {
			vertical = 0.0D;
		}

		kart.setDeltaMovement(horizontal.x, vertical, horizontal.z);
		kart.move(MoverType.SELF, kart.getDeltaMovement());

		if (kart.horizontalCollision) {
			kart.setDeltaMovement(kart.getDeltaMovement().multiply(0.5D, 1.0D, 0.5D));
			forwardSpeed *= 0.5D;
			lateralSpeed *= 0.5D;
		}
		if (kart.verticalCollisionBelow) {
			kart.setDeltaMovement(kart.getDeltaMovement().x, 0.0D, kart.getDeltaMovement().z);
		}

		return new Result(
				drift.drifting(),
				drift.strength(),
				drift.slipAngleDegrees(),
				forwardSpeed,
				lateralSpeed,
				forwardSpeed / stats.wheelRadius());
	}

	public static Vec3 forward(float yaw) {
		float radians = yaw * Mth.DEG_TO_RAD;
		return new Vec3(-Mth.sin(radians), 0.0D, Mth.cos(radians));
	}

	public static Vec3 right(float yaw) {
		float radians = yaw * Mth.DEG_TO_RAD;
		return new Vec3(-Mth.cos(radians), 0.0D, -Mth.sin(radians));
	}

	public record Result(
			boolean drifting,
			float driftStrength,
			float slipAngleDegrees,
			double forwardSpeed,
			double lateralSpeed,
			double wheelRotationDelta) {
	}
}

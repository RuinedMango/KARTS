package com.ruinedmango.kart.physics;

import com.ruinedmango.kart.vehicle.KartStats;

public final class TractionModel {
	private static final double DRIFT_GRIP = 0.18D;
	private static final double COUNTERSTEER_GRIP = 0.45D;

	private TractionModel() {
	}

	public static double lateralRetention(KartStats stats, DriftModel.State drift) {
		double grip = stats.lateralGrip();
		if (drift.drifting()) {
			grip *= drift.countersteering() ? COUNTERSTEER_GRIP : DRIFT_GRIP;
		}
		return Math.exp(-grip);
	}

	public static float steeringMultiplier(DriftModel.State drift) {
		if (!drift.drifting()) {
			return 1.0F;
		}
		return drift.countersteering() ? 0.75F : 1.25F;
	}
}

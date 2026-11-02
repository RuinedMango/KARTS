package com.ruinedmango.kart.client;

import com.ruinedmango.kart.vehicle.KartEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.ViewportEvent;

import org.jspecify.annotations.Nullable;

public final class KartCameraController {
	private static @Nullable KartEntity lastKart;
	private static float yawOffset;
	private static float lastPlayerYaw;

	private KartCameraController() {
	}

	public static void apply(ViewportEvent.ComputeCameraAngles event) {
		Player player = Minecraft.getInstance().player;
		if (player == null || !(player.getRootVehicle() instanceof KartEntity kart)) {
			lastKart = null;
			return;
		}

		if (kart != lastKart) {
			lastKart = kart;
			lastPlayerYaw = player.getYRot();
			yawOffset = Mth.wrapDegrees(player.getYRot() - kart.getYRot());
		} else {
			float playerYawDelta = Mth.wrapDegrees(player.getYRot() - lastPlayerYaw);
			yawOffset = Mth.wrapDegrees(yawOffset + playerYawDelta);
			lastPlayerYaw = player.getYRot();
		}

		float partialTick = (float) event.getPartialTick();
		event.setYaw(Mth.wrapDegrees(kart.getYRot(partialTick) + yawOffset));
		event.setPitch(Mth.clamp(event.getPitch() - kart.getPitch(partialTick), -90.0F, 90.0F));
		event.setRoll(event.getRoll() - kart.getRoll(partialTick));
		player.setYHeadRot(event.getYaw());
	}
}

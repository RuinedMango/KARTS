package com.ruinedmango.kart.client;

import com.ruinedmango.kart.KART;
import com.ruinedmango.kart.networking.KartInputPayload;
import com.ruinedmango.kart.vehicle.KartEntity;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

@EventBusSubscriber(modid = KART.MODID, value = Dist.CLIENT)
public final class KartInputHandler {
	private static int sequence;

	private KartInputHandler() {
	}

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent.Post event) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player == null || minecraft.gui.screen() != null) {
			return;
		}

		if (!(minecraft.player.getVehicle() instanceof KartEntity kart)) {
			return;
		}

		float throttle = (minecraft.options.keyUp.isDown() ? 1.0F : 0.0F)
				- (minecraft.options.keyDown.isDown() ? 1.0F : 0.0F);
		float steering = (minecraft.options.keyRight.isDown() ? 1.0F : 0.0F)
				- (minecraft.options.keyLeft.isDown() ? 1.0F : 0.0F);

		ClientPacketDistributor.sendToServer(new KartInputPayload(
				kart.getId(),
				sequence++,
				throttle,
				steering,
				KartKeyMappings.HANDBRAKE.isDown(),
				KartKeyMappings.BOOST.isDown()));
	}
}

package com.ruinedmango.kart.client;

import com.ruinedmango.kart.KART;
import com.ruinedmango.kart.vehicle.KartEntity;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import java.util.Locale;

@EventBusSubscriber(modid = KART.MODID, value = Dist.CLIENT)
public final class KartHud {
	private static final Identifier LAYER_ID = Identifier.fromNamespaceAndPath(KART.MODID, "hud");

	private KartHud() {
	}

	@SubscribeEvent
	public static void register(RegisterGuiLayersEvent event) {
		event.registerAbove(VanillaGuiLayers.HOTBAR, LAYER_ID, KartHud::render);
	}

	private static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player == null || !(minecraft.player.getVehicle() instanceof KartEntity kart)) {
			return;
		}

		int width = graphics.guiWidth();
		int height = graphics.guiHeight();
		int x = width / 2 - 55;
		int y = height - 62;

		float speed = (float) kart.getDeltaMovement().horizontalDistance() * 20.0F;
		graphics.text(minecraft.font, Component.literal(String.format(Locale.ROOT, "%.0f km/h", speed * 3.6F)), x, y - 14, 0xFFFFFFFF);

		graphics.fill(x, y, x + 110, y + 7, 0x90000000);
		float heatRatio = Mth.clamp(kart.getHeat() / 100.0F, 0.0F, 1.0F);
		int heatColor = kart.isOverheated()
				? 0xFFFF3B30
				: (heatRatio > 0.70F ? 0xFFFFA500 : 0xFF4CAF50);
		graphics.fill(x + 1, y + 1, x + 1 + (int) (108 * heatRatio), y + 6, heatColor);

		String status;
		int statusColor;
		if (!kart.hasBooster()) {
			status = "NO BOOSTER";
			statusColor = 0xFF757575;
		} else if (kart.isOverheated()) {
			status = "OVERHEATED";
			statusColor = 0xFFFF3B30;
		} else if (kart.isBoosting()) {
			status = "BOOST";
			statusColor = 0xFFFFD54F;
		} else {
			status = "BOOST READY";
			statusColor = 0xFFBDBDBD;
		}
		graphics.text(minecraft.font, Component.literal(status), x, y + 11, statusColor);

		if (kart.getDriftStrength() > 0.25F) {
			graphics.text(minecraft.font, Component.literal("DRIFT"), x + 82, y + 11, 0xFFFFD54F);
		}
	}
}

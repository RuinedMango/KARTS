package com.ruinedmango.kart.client;

import com.ruinedmango.kart.KART;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(modid = KART.MODID, value = Dist.CLIENT)
public final class KartKeyMappings {
	public static final KeyMapping.Category CATEGORY =
			new KeyMapping.Category(Identifier.fromNamespaceAndPath(KART.MODID, "controls"));

	public static final KeyMapping HANDBRAKE = new KeyMapping(
			"key.kart.handbrake",
			InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_SPACE,
			CATEGORY);

	public static final KeyMapping BOOST = new KeyMapping(
			"key.kart.boost",
			InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_LEFT_CONTROL,
			CATEGORY);

	private KartKeyMappings() {
	}

	@SubscribeEvent
	public static void register(RegisterKeyMappingsEvent event) {
		event.registerCategory(CATEGORY);
		event.register(HANDBRAKE);
		event.register(BOOST);
	}
}

package com.ruinedmango.kart.events;

import com.ruinedmango.kart.KART;
import com.ruinedmango.kart.command.KartCommands;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@EventBusSubscriber(modid = KART.MODID)
public final class KartCommonEvents {
	private KartCommonEvents() {
	}

	@SubscribeEvent
	public static void onRegisterCommands(RegisterCommandsEvent event) {
		KartCommands.register(event.getDispatcher());
	}

	@SubscribeEvent
	public static void onServerStarting(ServerStartingEvent event) {
		KART.LOGGER.info("KART server starting");
	}
}

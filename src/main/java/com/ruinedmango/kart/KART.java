package com.ruinedmango.kart;

import com.mojang.logging.LogUtils;
import com.ruinedmango.kart.component.KartComponentItems;
import com.ruinedmango.kart.networking.KartNetwork;
import com.ruinedmango.kart.registries.CreativeTabRegistry;
import com.ruinedmango.kart.registries.EntityRegistry;
import com.ruinedmango.kart.registries.ItemRegistry;
import com.ruinedmango.kart.registries.MenuRegistry;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

import org.slf4j.Logger;

@Mod(KART.MODID)
public final class KART {
	public static final String MODID = "kart";
	public static final Logger LOGGER = LogUtils.getLogger();

	public KART(IEventBus modEventBus) {
		KartComponentItems.bootstrap();
		ItemRegistry.register(modEventBus);
		EntityRegistry.register(modEventBus);
		MenuRegistry.register(modEventBus);
		CreativeTabRegistry.register(modEventBus);
		modEventBus.addListener(KartNetwork::registerPayloads);
	}
}

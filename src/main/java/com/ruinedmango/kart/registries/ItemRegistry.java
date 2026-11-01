package com.ruinedmango.kart.registries;

import com.ruinedmango.kart.KART;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ItemRegistry {
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(KART.MODID);

	private ItemRegistry() {
	}

	public static void register(IEventBus modBus) {
		ITEMS.register(modBus);
	}
}

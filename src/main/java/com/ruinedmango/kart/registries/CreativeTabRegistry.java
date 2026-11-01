package com.ruinedmango.kart.registries;

import com.ruinedmango.kart.KART;
import com.ruinedmango.kart.component.KartComponentIds;
import com.ruinedmango.kart.component.KartComponentItems;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CreativeTabRegistry {
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
			DeferredRegister.create(Registries.CREATIVE_MODE_TAB, KART.MODID);

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> KART_TAB = CREATIVE_MODE_TABS.register(
			"main",
			() -> CreativeModeTab.builder()
					.title(Component.translatable("itemGroup.kart"))
					.withTabsBefore(CreativeModeTabs.COMBAT)
					.icon(() -> KartComponentItems.stackFor(KartComponentIds.SHOPPING_CART_FRAME))
					.displayItems((parameters, output) -> KartComponentItems.allStacks().forEach(output::accept))
					.build());

	private CreativeTabRegistry() {
	}

	public static void register(IEventBus modBus) {
		CREATIVE_MODE_TABS.register(modBus);
	}
}

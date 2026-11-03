package com.ruinedmango.kart.registries;

import com.ruinedmango.kart.KART;
import com.ruinedmango.kart.menu.KartMenu;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MenuRegistry {
	public static final DeferredRegister<MenuType<?>> MENUS =
			DeferredRegister.create(Registries.MENU, KART.MODID);

	public static final DeferredHolder<MenuType<?>, MenuType<KartMenu>> KART_MENU =
			MENUS.register("kart", () -> IMenuTypeExtension.create(KartMenu::new));

	private MenuRegistry() {
	}

	public static void register(IEventBus modBus) {
		MENUS.register(modBus);
	}
}

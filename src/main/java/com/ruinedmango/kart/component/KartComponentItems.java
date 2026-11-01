package com.ruinedmango.kart.component;

import com.ruinedmango.kart.registries.ItemRegistry;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;

import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class KartComponentItems {
	private static final Map<Identifier, DeferredItem<KartComponentItem>> ITEMS = new LinkedHashMap<>();

	private KartComponentItems() {
	}

	public static void bootstrap() {
		if (!ITEMS.isEmpty()) {
			return;
		}

		for (ComponentDefinition definition : KartComponents.all()) {
			DeferredItem<KartComponentItem> item = ItemRegistry.ITEMS.registerItem(
					definition.id().getPath(),
					properties -> new KartComponentItem(definition, properties.stacksTo(1)));
			ITEMS.put(definition.id(), item);
		}
	}

	public static List<ItemStack> allStacks() {
		return ITEMS.values().stream().map(item -> new ItemStack(item.get())).toList();
	}

	public static @Nullable ComponentDefinition definitionFor(ItemStack stack) {
		if (stack.isEmpty()) {
			return null;
		}

		Item item = stack.getItem();
		for (Map.Entry<Identifier, DeferredItem<KartComponentItem>> entry : ITEMS.entrySet()) {
			if (entry.getValue().get() == item) {
				return KartComponents.byId(entry.getKey()).orElse(null);
			}
		}
		return null;
	}

	public static ItemStack stackFor(@Nullable Identifier id) {
		if (id == null) {
			return ItemStack.EMPTY;
		}

		DeferredItem<KartComponentItem> item = ITEMS.get(id);
		return item == null ? ItemStack.EMPTY : new ItemStack(item.get());
	}
}

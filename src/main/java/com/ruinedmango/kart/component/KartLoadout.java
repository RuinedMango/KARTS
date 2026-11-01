package com.ruinedmango.kart.component;

import com.ruinedmango.kart.component.KartComponentIds;

import net.minecraft.resources.Identifier;

import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public final class KartLoadout {
	private final EnumMap<ComponentSlot, Identifier> components = new EnumMap<>(ComponentSlot.class);

	public static KartLoadout defaultLoadout() {
		KartLoadout loadout = new KartLoadout();
		loadout.set(ComponentSlot.CHASSIS, KartComponentIds.SHOPPING_CART_FRAME);
		loadout.set(ComponentSlot.ENGINE, KartComponentIds.LAWNMOWER_ENGINE);
		loadout.set(ComponentSlot.WHEELS, KartComponentIds.STANDARD_KART_TIRES);
		return loadout;
	}

	public @Nullable Identifier get(ComponentSlot slot) {
		return this.components.get(slot);
	}

	public void set(ComponentSlot slot, @Nullable Identifier id) {
		if (id == null) {
			this.components.remove(slot);
		} else {
			this.components.put(slot, id);
		}
	}

	public boolean install(ComponentSlot slot, ComponentDefinition definition) {
		if (!slot.accepts(definition.type())) {
			return false;
		}
		this.set(slot, definition.id());
		return true;
	}

	public Optional<ComponentDefinition> definition(ComponentSlot slot) {
		Identifier id = this.get(slot);
		return id == null ? Optional.empty() : KartComponents.byId(id);
	}

	public Map<ComponentSlot, Identifier> asMap() {
		return Collections.unmodifiableMap(this.components);
	}

	public void clear() {
		this.components.clear();
	}
}

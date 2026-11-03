package com.ruinedmango.kart.menu;

import com.ruinedmango.kart.component.ComponentDefinition;
import com.ruinedmango.kart.component.ComponentSlot;
import com.ruinedmango.kart.component.KartComponentItem;
import com.ruinedmango.kart.component.KartComponentItems;
import com.ruinedmango.kart.registries.MenuRegistry;
import com.ruinedmango.kart.vehicle.KartEntity;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import org.jspecify.annotations.Nullable;

public final class KartMenu extends AbstractContainerMenu {
	private static final int INSTALL_SLOT = 0;
	private static final int GHOST_START = 1;
	private static final int GHOST_COUNT = 6;
	private static final int PLAYER_START = GHOST_START + GHOST_COUNT;
	private static final int PLAYER_END = PLAYER_START + 36;
	private static final int REMOVE_BUTTON_OFFSET = 100;

	private final @Nullable KartEntity kart;
	private final SimpleContainer installContainer = new SimpleContainer(1);
	private final SimpleContainer ghostContainer = new SimpleContainer(GHOST_COUNT);

	public KartMenu(int containerId, Inventory inventory, KartEntity kart) {
		this(containerId, inventory, kart, MenuRegistry.KART_MENU.get());
	}

	public KartMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf data) {
		this(containerId, inventory, findKart(inventory, data.readVarInt()), MenuRegistry.KART_MENU.get());
	}

	private KartMenu(int containerId, Inventory inventory, @Nullable KartEntity kart, MenuType<?> menuType) {
		super(menuType, containerId);
		this.kart = kart;

		this.addSlot(new Slot(this.installContainer, 0, 8, 20) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.getItem() instanceof KartComponentItem;
			}

			@Override
			public int getMaxStackSize() {
				return 1;
			}
		});

		for (int i = 0; i < GHOST_COUNT; i++) {
			this.addSlot(new Slot(this.ghostContainer, i, 8, 38 + i * 18) {
				@Override
				public boolean mayPlace(ItemStack stack) {
					return false;
				}

				@Override
				public boolean mayPickup(Player player) {
					return false;
				}

				@Override
				public int getMaxStackSize() {
					return 1;
				}
			});
		}

		this.addStandardInventorySlots(inventory, 8, 154);
		if (kart != null && !inventory.player.level().isClientSide()) {
			this.refreshGhostSlots();
		}
	}

	@Override
	public boolean stillValid(Player player) {
		return this.kart != null
				&& this.kart.isAlive()
				&& player.level() == this.kart.level()
				&& player.distanceToSqr(this.kart) <= 64.0D;
	}

	@Override
	public boolean clickMenuButton(Player player, int buttonId) {
		if (this.kart == null || !this.stillValid(player)) {
			return false;
		}

		boolean remove = buttonId >= REMOVE_BUTTON_OFFSET;
		int index = remove ? buttonId - REMOVE_BUTTON_OFFSET : buttonId;
		ComponentSlot[] slots = ComponentSlot.values();
		if (index < 0 || index >= slots.length) {
			return false;
		}
		ComponentSlot slot = slots[index];

		return remove
				? this.removeComponent(player, slot)
				: this.installComponent(player, slot);
	}

	private boolean installComponent(Player player, ComponentSlot slot) {
		ItemStack incoming = this.installContainer.getItem(0);
		ComponentDefinition definition = KartComponentItems.definitionFor(incoming);
		if (definition == null || !slot.accepts(definition.type())) {
			return false;
		}

		Identifier oldId = this.kart.getLoadout().get(slot);
		if (oldId != null && oldId.equals(definition.id())) {
			return false;
		}

		ItemStack oldStack = KartComponentItems.stackFor(oldId);
		if (!this.kart.installComponent(slot, definition)) {
			return false;
		}

		incoming.shrink(1);
		this.installContainer.setItem(0, incoming.isEmpty() ? ItemStack.EMPTY : incoming);
		giveOrDrop(player, oldStack);
		this.refreshGhostSlots();
		this.broadcastChanges();
		return true;
	}

	private boolean removeComponent(Player player, ComponentSlot slot) {
		Identifier oldId = this.kart.getLoadout().get(slot);
		ItemStack oldStack = KartComponentItems.stackFor(oldId);
		if (oldStack.isEmpty() || !this.kart.removeComponent(slot)) {
			return false;
		}

		giveOrDrop(player, oldStack);
		this.refreshGhostSlots();
		this.broadcastChanges();
		return true;
	}

	private void refreshGhostSlots() {
		ComponentSlot[] slots = ComponentSlot.values();
		for (int i = 0; i < GHOST_COUNT; i++) {
			this.ghostContainer.setItem(i, this.kart == null
					? ItemStack.EMPTY
					: KartComponentItems.stackFor(this.kart.getLoadout().get(slots[i])));
		}
	}

	private static void giveOrDrop(Player player, ItemStack stack) {
		if (stack.isEmpty()) {
			return;
		}
		if (!player.getInventory().add(stack)) {
			player.drop(stack, false);
		}
	}

	@Override
	public ItemStack quickMoveStack(Player player, int slotIndex) {
		Slot slot = this.slots.get(slotIndex);
		if (!slot.hasItem()) {
			return ItemStack.EMPTY;
		}

		ItemStack stack = slot.getItem();
		if (slotIndex == INSTALL_SLOT) {
			this.moveItemStackTo(stack, PLAYER_START, PLAYER_END, true);
			return ItemStack.EMPTY;
		}

		if (slotIndex >= PLAYER_START && stack.getItem() instanceof KartComponentItem) {
			this.moveItemStackTo(stack, INSTALL_SLOT, INSTALL_SLOT + 1, false);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void removed(Player player) {
		if (!player.level().isClientSide()) {
			ItemStack remaining = this.installContainer.removeItemNoUpdate(0);
			if (!remaining.isEmpty()) {
				giveOrDrop(player, remaining);
			}
		}
		super.removed(player);
	}

	private static @Nullable KartEntity findKart(Inventory inventory, int entityId) {
		return inventory.player.level().getEntity(entityId) instanceof KartEntity kart ? kart : null;
	}
}

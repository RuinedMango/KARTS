package com.ruinedmango.kart.client;

import com.ruinedmango.kart.KART;
import com.ruinedmango.kart.component.ComponentDefinition;
import com.ruinedmango.kart.component.ComponentSlot;
import com.ruinedmango.kart.component.KartComponentItems;
import com.ruinedmango.kart.menu.KartMenu;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import java.util.Locale;

public final class KartMenuScreen extends AbstractContainerScreen<KartMenu> {
	private static final int IMAGE_WIDTH = 176;
	private static final int IMAGE_HEIGHT = 232;
	private static final int ROW_START_Y = 38;
	private static final int ROW_STEP = 18;
	private static final int INSTALL_BUTTON_X = 72;
	private static final int REMOVE_BUTTON_X = 122;
	private static final int INSTALL_BUTTON_WIDTH = 46;
	private static final int REMOVE_BUTTON_WIDTH = 44;
	private static final int BUTTON_HEIGHT = 18;
	private static final int TOP_OFFSET = 6;
	private static final Identifier TEXTURE =
			Identifier.fromNamespaceAndPath(KART.MODID, "textures/gui/kart_workshop.png");

	private final Button[] addButtons = new Button[ComponentSlot.values().length];
	private final Button[] delButtons = new Button[ComponentSlot.values().length];

	public KartMenuScreen(KartMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title, IMAGE_WIDTH, IMAGE_HEIGHT);
	}

	@Override
	protected void init() {
		super.init();
		this.topPos = Math.max(0, this.topPos - TOP_OFFSET);

		for (int i = 0; i < ComponentSlot.values().length; i++) {
			final int index = i;
			String slotName = ComponentSlot.values()[i].serializedName().toUpperCase(Locale.ROOT);
			int y = this.topPos + ROW_START_Y + i * ROW_STEP;

			this.addButtons[i] = Button.builder(
							Component.literal("Install"),
							button -> this.sendButton(index))
					.bounds(this.leftPos + INSTALL_BUTTON_X, y, INSTALL_BUTTON_WIDTH, BUTTON_HEIGHT)
					.tooltip(Tooltip.create(Component.literal("Install into " + slotName)))
					.build();

			this.delButtons[i] = Button.builder(
							Component.literal("Remove"),
							button -> this.sendButton(100 + index))
					.bounds(this.leftPos + REMOVE_BUTTON_X, y, REMOVE_BUTTON_WIDTH, BUTTON_HEIGHT)
					.tooltip(Tooltip.create(Component.literal("Remove from " + slotName)))
					.build();

			this.addRenderableWidget(this.addButtons[i]);
			this.addRenderableWidget(this.delButtons[i]);
		}

		this.updateButtonStates();
	}

	@Override
	protected void containerTick() {
		super.containerTick();
		this.updateButtonStates();
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		super.extractBackground(graphics, mouseX, mouseY, partialTick);
		graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos, this.topPos,
				0.0F, 0.0F, IMAGE_WIDTH, IMAGE_HEIGHT, IMAGE_WIDTH, IMAGE_HEIGHT);
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		graphics.text(this.font, this.title, 8, 8, 0xFF404040, false);
		graphics.text(this.font, Component.literal("Component"), 30, 22, 0xFF404040, false);

		for (int i = 0; i < ComponentSlot.values().length; i++) {
			graphics.text(this.font, Component.literal(slotLabel(ComponentSlot.values()[i])),
					30, ROW_START_Y + i * ROW_STEP + 5, 0xFF404040, false);
		}
	}

	private void sendButton(int buttonId) {
		if (this.minecraft != null && this.minecraft.gameMode != null) {
			this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, buttonId);
		}
	}

	private void updateButtonStates() {
		for (int i = 0; i < ComponentSlot.values().length; i++) {
			if (this.addButtons[i] != null) {
				this.addButtons[i].active = this.isInstallEnabled(i);
			}
			if (this.delButtons[i] != null) {
				this.delButtons[i].active = this.isRemoveEnabled(i);
			}
		}
	}

	private boolean isInstallEnabled(int index) {
		ComponentDefinition definition = KartComponentItems.definitionFor(this.menu.getSlot(0).getItem());
		return definition != null && ComponentSlot.values()[index].accepts(definition.type());
	}

	private boolean isRemoveEnabled(int index) {
		return !this.menu.getSlot(1 + index).getItem().isEmpty();
	}

	private static String slotLabel(ComponentSlot slot) {
		return switch (slot) {
			case ACCESSORY_1 -> "ACC 1";
			case ACCESSORY_2 -> "ACC 2";
			default -> slot.serializedName().toUpperCase(Locale.ROOT);
		};
	}
}
